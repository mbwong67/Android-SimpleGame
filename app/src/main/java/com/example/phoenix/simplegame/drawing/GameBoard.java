package com.example.phoenix.simplegame.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.phoenix.simplegame.R;
import com.example.phoenix.simplegame.drawing.models.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Phoenix on 11-Feb-18.
 */

public class GameBoard extends View {

    private static final int NUM_OF_STARS = 35;
    private Paint p;
    private List<Point> starField = null;
    private int starAlpha = 80;

    //Add private variables to keep up with sprite position and size
    private int starFade = 2;


    public Sprite asteroid, ufo, sunMoon, station;//SM - Sun Moon
    //Bitmaps that hold the actual sprite images

    public boolean isSun = false;

    private int dayAlpha = 0;

    private Matrix m = null;
    private int asteroidRotation = 0;

    //Collision flag and point
    public boolean didAllCollide = false;
    public boolean isCollisionType2 = false;

    //Allow our controller to get and set the sprite positions

    public GameBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        p = new Paint();

        //Load our bitmaps and set the bounds for the controller
        asteroid = new Sprite(-1, -1, getResources(), R.drawable.asteroid);
        ufo = new Sprite(-1, -1, getResources(), R.drawable.ufo);
        sunMoon = new Sprite(-1000, -1000, getResources(), R.drawable.moon);
        station = new Sprite(-1, -1, getResources(), R.drawable.space_station);

        m = new Matrix();
        p = new Paint();
//        Resources resources = context.getResources();
//        int resourceId = resources.getIdentifier("asteroid", "drawable", context.getPackageName());
////        bm1 = BitmapFactory.decodeResource(getResources(), R.drawable
////                .asteroid);
    }

    synchronized public void resetStarField() {
        starField = null;
    }

    //return the point of the last collision

    private void initializeStars(int maxX, int maxY) {
        starField = new ArrayList<Point>();

        for (int i = 0; i < NUM_OF_STARS; i++) {
            Random r = new Random();
            int x = r.nextInt(maxX - 5 + 1) + 5;
            int y = r.nextInt(maxY - 5 + 1) + 5;
            starField.add(new Point(x, y));
        }
        //collisionDetected = false;
    }


    @Override
    synchronized public void onDraw(Canvas canvas) {
        canvas.save();
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);

        if (isSun) {
            sunMoon.setBitmap(getResources(), R.drawable.sun);
            if (sunMoon.getX() >= canvas.getWidth() - sunMoon.getWidth()) {
                if (dayAlpha > 0) {
                    dayAlpha -= 2;
                }
            }else if (dayAlpha < 254) {
                dayAlpha += 2;
            }
        } else {
            sunMoon.setBitmap(getResources(), R.drawable.moon);
        }

        p.setColor(Color.CYAN);
        p.setAlpha(dayAlpha);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);

        //initialize the starfield if needed
        if (starField == null) {
            initializeStars(canvas.getWidth(), canvas.getHeight());
        }

        //draw the stars
        p.setColor(Color.CYAN);
        p.setAlpha(starAlpha = starAlpha + starFade);

        //fade them in and out
        if (starAlpha >= 252 || starAlpha <= 80) {
            starFade = starFade * -1;
        }

        p.setStrokeWidth(7);
        for (int i = 0; i < NUM_OF_STARS; i++) {
            canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
        }

        //Now we draw our sprites. Items drawn in this function are stacked.
        //The items drawn at the top of the loop are on the bottom of the
        // z-order.

        //Therefore we draw our set, then our actors, and finally any fx.


        if (sunMoon.getY() != -1000) {
            canvas.drawBitmap(sunMoon.getBitmap(), sunMoon.getX(), sunMoon.getY(), null);
        }


        if (station.getX() >= 0) {
            canvas.drawBitmap(station.getBitmap(), station.getX(), station.getY(), null);
        }


        if (asteroid.getX() >= 0) {
            m.reset();
            m.postTranslate((float) (asteroid.getX()), (float) (asteroid.getY()));
            m.postRotate(asteroidRotation, (float) (asteroid.getX() + asteroid.getWidth()/2.0),
                    (float) (asteroid.getY() + asteroid.getWidth() / 2.0));

            canvas.drawBitmap(asteroid.getBitmap(), m, null);
            asteroidRotation += 5;
            if (asteroidRotation >= 360) {
                asteroidRotation = 0;
            }
        }


        if (ufo.getX() >= 0) {
            canvas.drawBitmap(ufo.getBitmap(), ufo.getX(), ufo.getY(), null);
        }


        if (!isCollisionType2) {
            if (asteroid.didCollideWith(ufo) && ufo.didCollideWith(station) || ufo.didCollideWith
                    (asteroid) && asteroid.didCollideWith(station) || ufo.didCollideWith(station) &&
                    station.didCollideWith(asteroid)) {
                didAllCollide = true;
                asteroid.setPoint(-1000, -1000);
                ufo.setPoint(-1000,-1000);
                station.setPoint(-1000,-1000);
            } else {
                didAllCollide = false;
            }
        } else {
            if (asteroid.didCollideWith(ufo) && ufo.didCollideWith(station) && station
                    .didCollideWith(asteroid)) {
                didAllCollide = true;
                asteroid.setPoint(-1000, -1000);
                ufo.setPoint(-1000,-1000);
                station.setPoint(-1000,-1000);
            } else {
                didAllCollide = false;
            }
        }
        canvas.restore();

    }
}
