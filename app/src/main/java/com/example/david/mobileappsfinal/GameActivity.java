package com.example.david.mobileappsfinal;

import android.app.Activity;
import android.content.Context;
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
import java.util.List;
import java.util.Random;

public class GameActivity extends Activity {

    GameView gameView;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView (this);
        setContentView(gameView);

    }

    class GameView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder holder;
        volatile boolean isPlaying;

        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;

        Canvas canvas;
        Paint paint;
        long fps;
        private long timeThisFrame;

        int playerScore = 0;

        Bitmap playerbmp;
        int moveDirection = 0;
        //Units of movement and position are in percentages of the screen space
        float moveSpeed = 18;
        float playerSize = 13;
        float playerXpos = 50;
        float playerYpos = 70;
        Rect playerRect = new Rect((int)percentToWidth(50 - playerSize / 2f),
                (int)(percentToHeight(80) - percentToWidth(playerSize)),
                (int)percentToWidth(50 + playerSize / 2f),
                (int)percentToHeight(80));

        Bitmap laserbmp;
        float laserXpos = 52;
        float laserYpos = 800;
        Rect laserRect = new Rect((int)percentToWidth(50 - playerSize / 16f),
                (int)(percentToHeight(playerRect.top) - percentToWidth(playerSize * 3f/8f)),
                (int)percentToWidth(50 + playerSize / 16f),
                (int)percentToHeight(playerRect.top));
        float laserSpeed = 15;
        boolean laserActive = true;

        ArrayList<Rect> enemyRects = new ArrayList<Rect>();
        ArrayList<Float> enemyXpositions = new ArrayList<Float>();
        ArrayList<Float> enemyYpositions = new ArrayList<Float>();
        ArrayList<Boolean> enemiesActive = new ArrayList<Boolean>();
        float enemySpeed = 15;

        int numEnemiesAtOnce = 4;
        int pointsPerEnemy = 50;
        Bitmap enemy1bmp;

        Rect leftButtonRect = new Rect((int)percentToWidth(0), (int)percentToHeight(80), (int)percentToWidth(49), (int)percentToHeight(100));
        Rect rightButtonRect = new Rect((int)percentToWidth(51), (int)percentToHeight(80), (int)percentToWidth(100), (int)percentToHeight(100));
        //Rect fireButtonRect = new Rect((int)percentToWidth(65), (int)percentToHeight(80), (int)percentToWidth(100), (int)percentToHeight(100));

        public float percentToWidth (float value) {
            return (float)width * value / 100f;
        }
        public float percentToHeight (float value) {
            return (float)height * value / 100f;
        }

        public GameView(Context context) {
            super(context);
            holder = getHolder();
            paint = new Paint();
            playerbmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.player_ship);
            laserbmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.player_laser);
            enemy1bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy1);

            playerXpos = playerRect.left / (float)width * 100;
            playerYpos = playerRect.top / (float)height * 100;
            laserXpos = laserRect.left / (float)width * 100;
            laserYpos = laserRect.top / (float)height * 100;

            Random rng = new Random();
            for (int i = 0; i < numEnemiesAtOnce; i++) {
                enemyXpositions.add(rng.nextFloat() * 85f + 7.5f);
                enemyYpositions.add(0f);
                enemyRects.add(new Rect(
                        (int)percentToWidth(enemyXpositions.get(i)),
                        (int)percentToHeight(enemyYpositions.get(i)),
                        (int)percentToWidth(enemyXpositions.get(i) + playerSize),
                        (int)(percentToHeight(enemyYpositions.get(i)) + percentToWidth(playerSize))
                ));
                enemiesActive.add(true);
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
            final float playerXbefore = playerXpos;
            final float laserXbefore = laserXpos;
            final float laserYbefore = laserYpos;
            if (moveDirection != 0) {
                playerXpos += (percentToWidth(moveSpeed) / fps) * moveDirection;
                if (playerXpos < 3)
                    playerXpos = 3;
                if (playerXpos > 97 - playerSize)//20 is used as the player's size
                    playerXpos = 97 - playerSize;
            }
            laserYpos -= percentToHeight(laserSpeed) / fps;
            if (laserYpos < 1) {
                laserYpos = 68;
                laserXpos = playerXpos + playerSize * 7f/16f;
                laserActive = true;
            }
            playerRect.left = (int)percentToWidth(playerXpos);
            playerRect.right = (int)percentToWidth(playerXpos + playerSize);
            laserRect.offsetTo((int)percentToWidth(laserXpos), (int)percentToHeight(laserYpos));

            for (Rect r : enemyRects) {
                int index = enemyRects.indexOf(r);
                enemyYpositions.set(index, enemyYpositions.get(index) + percentToHeight(enemySpeed) / fps);
                r.offsetTo(r.left, Math.round(enemyYpositions.get(index)));
            }
        }

        public void draw() {
            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                canvas.drawColor(Color.argb(255, 0, 0 ,30));
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(45);
                canvas.drawText("FPS: " + fps, 20, 40, paint);

                paint.setAntiAlias(false);
                paint.setFilterBitmap(false);
                canvas.drawBitmap(playerbmp, null, playerRect, paint);

                if (laserActive) {
                    canvas.drawBitmap(laserbmp, null, laserRect, paint);
                }

                for (Rect r : enemyRects) {
                    int index = enemyRects.indexOf(r);
                    if (enemiesActive.get(index)) {
                        canvas.drawBitmap(enemy1bmp, null, r, paint);
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

}
