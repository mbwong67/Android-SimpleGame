package com.example.phoenix.simplegame.drawing.models;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Phoenix on 14-Feb-18.
 */

public class Sprite {

    private Point point;
    private Rect bounds;
    private Bitmap bm;

    public Sprite(int x, int y, Resources resources, int bitmapResID) {
        point = new Point(x, y);
        bm = BitmapFactory.decodeResource(resources, bitmapResID);
        bounds = new Rect(0, 0, bm.getWidth(), bm.getHeight());
    }

    public Sprite() {
        point = null;
        bm = null;
        bounds = null;
    }

    synchronized public int getX() {
        return point.x;
    }

    synchronized public int getY() {
        return point.y;
    }

    synchronized public void setPoint(int x, int y) {
        point = new Point(x, y);
    }

    synchronized public void setX(int x) {
        point.x = x;
    }

    synchronized public void setY(int y) {
        point.y = y;
    }

    synchronized public int getWidth() {
        return bounds.width();
    }

    synchronized public int getHeight() {
        return bounds.height();
    }

    public void setBounds(int width, int height) {
        bounds = new Rect(0, 0, width, height);
    }

    public Bitmap getBitmap() {
        return bm;
    }

    public void setBitmap(Resources resources, int resourceID) {
        bm = BitmapFactory.decodeResource(resources, resourceID);
    }

    public boolean didCollideWith(Sprite sprite) {

        if (point.x < 0 && sprite.getX() < 0 && sprite.getY() < 0 && sprite.getY() <
                0) {
            return false;
        }


        Rect r1 = new Rect(point.x, point.y, point.x + bounds.width(), point.y + bounds.height());
        Rect r2 = new Rect(sprite.getX(), sprite.getY(), sprite.getX() + sprite.getWidth(),
                sprite.getY() +
                sprite.getHeight());
        Rect r3 = new Rect(r1);

        if (r1.intersect(r2)) {
            for (int i = r1.left; i < r1.right; i++) {
                for (int j = r1.top; j < r1.bottom; j++) {
                    if (bm.getPixel(i - r3.left, j - r3.top) != Color
                            .TRANSPARENT) {
                        if (sprite.getBitmap().getPixel(i - r2.left, j - r2.top) !=
                                Color.TRANSPARENT) {
//                            lastCollision = new Point(collidedSprite.x + i -
//                                    r2.left, collidedSprite.y + j - r2.top);
                            return true;
                        }
                    }
                }
            }
        }
//        lastCollision = new Point(-1, -1);

        return false;
    }
}
