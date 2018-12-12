package com.example.david.mobileappsfinal;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class GameObject {
    protected float Xpos = 0;
    protected float Ypos = 0;
    private float size = 13;
    protected boolean isActive = true;
    protected Bitmap bmp;
    Rect rect;

    protected void movePosition(float Xdelta, float Ydelta) {
        Xpos += Xdelta;
        Ypos += Ydelta;
        rect.offset((int)GameActivity.percentToWidth(Xdelta), (int)GameActivity.percentToHeight(Ydelta));
    }

    protected GameObject (float Xpos, float Ypos, float size, boolean isActive, Bitmap bmp) {
        this.Xpos = Xpos;
        this.Ypos = Ypos;
        this.isActive = isActive;
        this.bmp = bmp;
        rect = new Rect(
                (int)GameActivity.percentToWidth(Xpos),
                (int)GameActivity.percentToHeight(Ypos),
                (int)GameActivity.percentToWidth(Xpos + size),
                (int)(GameActivity.percentToHeight(Ypos) + GameActivity.percentToWidth(size))
        );
    }

}
