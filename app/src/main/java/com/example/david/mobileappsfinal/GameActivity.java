package com.example.david.mobileappsfinal;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

        Canvas canvas;
        Paint paint;
        long fps;
        private long timeThisFrame;
        Bitmap playerbmp;
        int moveDirection = 0;
        //Units of movement and position are in percentages of the screen space
        float moveSpeed = 15;
        float playerXPos = 50;
        float playerYPos = 70;

        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;

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
            if (moveDirection != 0) {
                playerXPos += (percentToWidth(moveSpeed) / fps) * moveDirection;
                if (playerXPos < 5)
                    playerXPos = 5;
                if (playerXPos > 95 - 20)//20 is used as the player's size
                    playerXPos = 95 - 20;
            }
        }

        public void draw() {
            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                canvas.drawColor(Color.argb(255, 0, 0 ,30));
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(45);
                canvas.drawText("FPS: " + fps, 20, 40, paint);

                Rect pDest = new Rect((int)percentToWidth(playerXPos), (int)(percentToHeight(80) - percentToWidth(20)), (int)(percentToWidth(playerXPos + 20)), (int)percentToHeight(80));
                paint.setAntiAlias(false);
                paint.setFilterBitmap(false);
                canvas.drawBitmap(playerbmp, null, pDest, paint);

                paint.setAntiAlias(true);
                paint.setColor(Color.argb(200, 120, 120, 120));
                canvas.drawRect(leftButtonRect, paint);
                canvas.drawRect(rightButtonRect, paint);
                //canvas.drawRect(fireButtonRect, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(60);


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
                    if (motionEvent.getY() >= leftButtonRect.top) {
                        if (motionEvent.getX() <= leftButtonRect.right) {
                            moveDirection = -1;
                        } else if (motionEvent.getX() >= rightButtonRect.left) {
                            moveDirection = 1;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
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
