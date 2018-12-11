package com.example.david.mobileappsfinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends Activity {

    GameView gameView;
    protected static int height;
    protected static int width;
    protected int previousBest = 0;
    protected int playerScore = 0;

    public final static String NEW_BEST = "newBestKey";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        height = Resources.getSystem().getDisplayMetrics().heightPixels;
        width = Resources.getSystem().getDisplayMetrics().widthPixels;

        Intent gameIntent = getIntent();
        previousBest = gameIntent.getIntExtra(MainActivity.PREVIOUS_BEST, 0);

        gameView = new GameView (this);
        setContentView(gameView);

    }

    protected static float percentToWidth(float value) {return value * width / 100f;}
    protected static float percentToHeight(float value) {return value * height / 100f;}

    class GameView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder holder;
        volatile boolean isPlaying;

        Canvas canvas;
        Paint paint;
        long fps = 10000;
        private long timeThisFrame;

        boolean gameOver = false;

        Bitmap playerbmp;
        int moveDirection = 0;
        //Units of movement and position are in percentages of the screen space
        float moveSpeed = 75;
        float playerSize = 13;
        float playerXpos = 50;
        float playerYpos = 70;
        Rect playerRect = new Rect((int)percentToWidth(50 - playerSize / 2f),
                (int)(percentToHeight(80) - percentToWidth(playerSize)),
                (int)percentToWidth(50 + playerSize / 2f),
                (int)percentToHeight(80));

        Bitmap laserbmp;
        float laserXpos = 52;
        float laserYpos = 100;
        Rect laserRect = new Rect((int)percentToWidth(50 - playerSize / 16f),
                (int)(playerRect.top - percentToWidth(playerSize * 3f/8f)),
                (int)percentToWidth(50 + playerSize / 16f),
                (int)playerRect.top);
        float laserSpeed = 100;
        boolean laserActive = true;

        ArrayList<GameObject> enemies = new ArrayList<GameObject>();
        float enemySpeed = 20;

        int numStartingEnemies = 6;
        int pointsPerEnemy = 50;
        Bitmap enemy1bmp;

        Rect leftButtonRect = new Rect((int)percentToWidth(0), (int)percentToHeight(80), (int)percentToWidth(49), (int)percentToHeight(100));
        Rect rightButtonRect = new Rect((int)percentToWidth(51), (int)percentToHeight(80), (int)percentToWidth(100), (int)percentToHeight(100));

        Random rng = new Random();

        public GameView(Context context) {
            super(context);
            holder = getHolder();
            paint = new Paint();
            playerbmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.player_shipv2);
            laserbmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.player_laser);
            enemy1bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy1);

            playerXpos = playerRect.left / (float)width * 100;
            playerYpos = playerRect.top / (float)height * 100;
            laserXpos = laserRect.left / (float)width * 100;
            laserYpos = laserRect.top / (float)height * 100;

            for (int i = 0; i < numStartingEnemies; i++) {
                enemies.add(new GameObject(
                        rng.nextFloat() * (85 - playerSize) + 7.5f,
                        rng.nextFloat() * -10f - 10f,
                        playerSize,
                        true,
                        enemy1bmp
                ));
            }
        }

        @Override
        public void run() {
            while (isPlaying) {
                long startFrameTime = System.currentTimeMillis();
                update();
                draw();

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        public void update() {
            if (gameOver) {
                isPlaying = false;
                return;
            }
            for (GameObject obj : enemies) {
                if (obj != null && obj.isActive) {
                    if (Rect.intersects(obj.rect, playerRect)) {
                        gameOver = true;
                        return;
                    } else if (laserActive && Rect.intersects(obj.rect, laserRect)) {
                        obj.isActive = false;
                        playerScore += pointsPerEnemy;
                        laserActive = false;
                    }
                }
            }
            if (playerScore / 400 > enemies.size() - numStartingEnemies) {
                enemies.add(new GameObject(
                        rng.nextFloat() * (85 - playerSize) + 7.5f,
                        rng.nextFloat() * -10f - 10f,
                        playerSize,
                        true,
                        enemy1bmp
                ));
                laserSpeed += 6;
            }

            final float playerXbefore = playerXpos;
            final float laserXbefore = laserXpos;
            final float laserYbefore = laserYpos;

            if (moveDirection != 0) {
                playerXpos += moveSpeed / fps * moveDirection;
                if (playerXpos < 3)
                    playerXpos = 3;
                if (playerXpos > 97 - playerSize)//20 is used as the player's size
                    playerXpos = 97 - playerSize;
            }
            laserYpos -= laserSpeed / fps;
            if (laserYpos < 1) {
                laserYpos = 68;
                laserXpos = playerXpos + playerSize * 7f/16f;
                laserActive = true;
            }
            playerRect.left = (int)percentToWidth(playerXpos);
            playerRect.right = (int)percentToWidth(playerXpos + playerSize);
            laserRect.offsetTo((int)percentToWidth(laserXpos), (int)percentToHeight(laserYpos));

            for (GameObject obj : enemies) {
                obj.movePosition(0, enemySpeed / fps);
                if (obj.rect.top > height) {
                    obj.Xpos = rng.nextFloat() * (85 - playerSize) + 7.5f;
                    obj.movePosition(0, -100f + (rng.nextFloat() * -25f) - obj.rect.height() / (float)height * 100f);
                    obj.isActive = true;
                }
            }
        }

        public void draw() {
            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                canvas.drawColor(Color.argb(255, 0, 0 ,30));
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(40);
                canvas.drawText("FPS: " + fps, 20, 40, paint);

                paint.setAntiAlias(false);
                paint.setFilterBitmap(false);
                canvas.drawBitmap(playerbmp, null, playerRect, paint);

                if (laserActive) {
                    canvas.drawBitmap(laserbmp, null, laserRect, paint);
                }
                GameObject firstEnemy = null;
                for (GameObject obj : enemies) {
                    if (obj != null && obj.isActive) {
                        canvas.drawBitmap(obj.bmp, null, obj.rect, paint);
                        if (enemies.indexOf(obj) == 0)
                            firstEnemy = obj;
                    }
                }

                paint.setAntiAlias(true);
                paint.setColor(Color.argb(200, 130, 130, 130));
                canvas.drawRect(leftButtonRect, paint);
                canvas.drawRect(rightButtonRect, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(40);
                /*canvas.drawText(laserRect.flattenToString(), 20, 80, paint);
                canvas.drawText("laserXpos: " + laserXpos, 20, 120, paint);*/
                /*if (firstEnemy != null) {
                    canvas.drawText(firstEnemy.rect.flattenToString(), 20, 80, paint);
                    canvas.drawText("Ypos: " + firstEnemy.Ypos, 20, 120, paint);
                }*/
                canvas.drawText("Score: " + playerScore, 20, 80, paint);
                if (gameOver) {
                    canvas.drawText("GAME OVER", 50, 200, paint);
                    canvas.drawText("Press back to return", 20, 300, paint);
                    if (playerScore > previousBest) {
                        canvas.drawText("NEW HIGH SCORE!", 30, 250, paint);
                    }
                }

                holder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            isPlaying = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        public void resume() {
            isPlaying = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (motionEvent.getY() >= leftButtonRect.top) {
                        if (motionEvent.getX() <= leftButtonRect.right) {
                            moveDirection = -1;
                        } else if (motionEvent.getX() >= rightButtonRect.left) {
                            moveDirection = 1;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    moveDirection = 0;
                    break;
            }
            return true;
        }
    }//End GameView inner class

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
    @Override
    public void onBackPressed() {
        //Allow back press with closing the activity + intent when game is over
        if (gameView.gameOver) {
            Intent endGameIntent = new Intent(GameActivity.this, MainActivity.class);
            endGameIntent.putExtra(NEW_BEST, playerScore);
            startActivity(endGameIntent);
            this.finish();
            super.onBackPressed();
        }
    }

}
