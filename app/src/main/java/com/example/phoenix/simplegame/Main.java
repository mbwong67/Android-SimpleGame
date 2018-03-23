package com.example.phoenix.simplegame;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.phoenix.simplegame.drawing.GameBoard;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Random;

public class Main extends AppCompatActivity implements View.OnClickListener {

    private Handler frame = new Handler();

    //Velocity includes the speed and the direction of our sprite motion
    private Point asteroidVelocity;
    private Point ufoVelocity;
    private Point sunMoonVelocity;

    private int asteroidMaxX;
    private int asteroidMaxY;
    private int ufoMaxX;
    private int ufoMaxY;
    private int sunMoonMaxX;
    private int sunMoonMaxY;

    public int customX = 0, customY = 0;
    
    private GameBoard canvas;

    private boolean didShowGameOver = false;

    //Divide the frame by 1000 to calculate how many times per second the
    // screen will update.
    private static final int FRAME_RATE = 10; //50 frames per second

    private Switch autoSwitch;
    private ImageButton collisionButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setToFullScreen();
        Handler h = new Handler();
        ((Button) findViewById(R.id.button_reset)).setOnClickListener(this);
        collisionButton = (ImageButton) findViewById(R.id.button_collision);
        collisionButton.setOnClickListener(this);
        autoSwitch = (Switch) findViewById(R.id.auto_switch);
        autoSwitch.setOnClickListener(this);
        canvas = findViewById(R.id.the_canvas);
        //We can't initialize the graphics immediately because the layout
        // manager needs to run first, thus we call back in a sec.
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                initGfx();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }

    private void setToFullScreen() {
        ViewGroup rootLayout = findViewById(R.id.main);
        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    boolean didTouchUfo = false;
    boolean didTouchAsteroid = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)(event.getX() - canvas.getLeft() - canvas.ufo.getWidth()/2);
        int y = (int)(event.getY() - canvas.getTop() - canvas.ufo.getHeight()/2);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (x >= canvas.ufo.getX() - 40 && x <= canvas.ufo.getX() + canvas.ufo.getWidth()
                        + 40 && y >= canvas.ufo.getY() - 40 && y <= canvas.ufo.getY() + canvas.ufo
                        .getHeight() + 40) {
                    didTouchUfo = true;
                } else if (x >= canvas.asteroid.getX() - 40 && x <= canvas.asteroid.getX() + canvas.asteroid
                        .getWidth()
                        + 40 && y >= canvas.asteroid.getY() - 40 && y <= canvas.asteroid.getY() + canvas.asteroid
                        .getHeight() + 40) {
                    didTouchAsteroid = true;
                }
            case MotionEvent.ACTION_MOVE:
                customX = x;
                customY = y;
                break;
            case MotionEvent.ACTION_UP:
                didTouchUfo = false;
                didTouchAsteroid = false;
        }
        return true;
    }

    private Point getRandomVelocity() {
        Random r = new Random();
        int min = 6;
        int max = 12;
        int x = r.nextInt(max - min + 1) + min;
        int y = r.nextInt(max - min + 1) + min;
        return new Point(x, y);
    }

    private Point getRandomPoint() {
        Random r = new Random();
        int maxX = canvas.getWidth();
        int x = 0;
        int maxY = canvas.getHeight();
        int y = 0;
        x = r.nextInt(maxX + 1);
        y = r.nextInt(maxY + 1);
        return new Point(x, y);
    }

    synchronized public void initGfx() {
        canvas.resetStarField();

        //Select two random points for our initial sprite placement.
        //The loop is just to make sure we don't accidentally pick
        //two points that overlap.
        Point p1, p2;
        canvas.station.setPoint(((canvas.getWidth() - canvas.station.getWidth())/2), ((canvas.getHeight() - canvas.station
                .getHeight()) / 2));
        do {
            p1 = getRandomPoint();
            p2 = getRandomPoint();
        } while (Math.abs(p1.x - p2.x) < canvas.asteroid.getWidth());

        canvas.asteroid.setPoint(p1.x, p1.y);
        canvas.ufo.setPoint(p2.x, p2.y);
        ufoVelocity = new Point(1, 1);
        ufoMaxX = canvas.getWidth() - canvas.ufo.getWidth();
        ufoMaxY = canvas.getHeight() - canvas.ufo.getHeight();
        

        canvas.sunMoon.setPoint((canvas.getWidth() - canvas.sunMoon.getWidth())/2, (canvas.getHeight() - canvas.sunMoon
                .getHeight())/14);

        asteroidVelocity = getRandomVelocity();
        sunMoonVelocity = new Point(4, 4);

        //Set our boundaries for the sprites
        asteroidMaxX = canvas.getWidth() - canvas.asteroid.getWidth();
        asteroidMaxY = canvas.getHeight() - canvas.asteroid.getHeight();

        sunMoonMaxX = canvas.getWidth();
        sunMoonMaxY = canvas.getHeight();
        (findViewById(R.id.button_reset)).setEnabled(true);
        autoSwitch.setChecked(true);
        autoSwitch.setEnabled(true);
        collisionButton.setEnabled(true);

        //It's a good idea to remove any existing callbacks to keep
        //them from inadvertently stacking up.
        frame.removeCallbacks(frameUpdate);
        canvas.invalidate();
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }

    @Override
    synchronized public void onClick(View v) {

        switch (v.getId()){
            case R.id.button_reset:
                didShowGameOver = false;
                canvas.didAllCollide = false;
                autoSwitch.setEnabled(true);
                collisionButton.setEnabled(false);
                collisionButton.setImageResource(R.drawable.collision1);
                canvas.isCollisionType2 = false;
                initGfx();
                break;

            case R.id.button_collision:
                if (canvas.isCollisionType2) {
                    collisionButton.setImageResource(R.drawable.collision1);
                    canvas.isCollisionType2 = false;
                } else {
                    collisionButton.setImageResource(R.drawable.collision2);
                    canvas.isCollisionType2 = true;
                }
        }

    }

    private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {

            frame.removeCallbacks(frameUpdate);

            if (!canvas.didAllCollide) {

                if (autoSwitch.isChecked()) {
                    Point asteroidPoint = new Point(canvas.asteroid.getX(), canvas.asteroid.getY());
                    asteroidPoint.x = asteroidPoint.x + asteroidVelocity.x;
                    if (asteroidPoint.x > asteroidMaxX || asteroidPoint.x < 5) {
                        asteroidVelocity.x *= -1;
                    }
                    asteroidPoint.y = asteroidPoint.y + asteroidVelocity.y;
                    if (asteroidPoint.y > asteroidMaxY || asteroidPoint.y < 5) {
                        asteroidVelocity.y *= -1;
                    }
                    canvas.asteroid.setPoint(asteroidPoint.x, asteroidPoint.y);

                    Point ufoPoint = new Point(canvas.ufo.getX(), canvas.ufo.getY());
                    ufoPoint.x = ufoPoint.x + ufoVelocity.x;
                    if (ufoPoint.x > ufoMaxX || ufoPoint.x < 5) {
                        ufoVelocity.x *= -1;
                    }
                    ufoPoint.y = ufoPoint.y + ufoVelocity.y;
                    if (ufoPoint.y > ufoMaxY || ufoPoint.y < 5) {
                        ufoVelocity.y *= -1;
                    }
                    canvas.ufo.setPoint(ufoPoint.x, ufoPoint.y);
                } else {
                    if (didTouchUfo) {
                        canvas.ufo.setPoint(customX, customY);
                    }
                    if (didTouchAsteroid) {
                        canvas.asteroid.setPoint(customX, customY);
                    }
                }
            } else {
                didTouchUfo = false;
                didTouchAsteroid = false;
                if (!didShowGameOver) {
                    Toast.makeText(Main.this, "Game Over", Toast.LENGTH_LONG).show();
                    didShowGameOver = true;
                }
            }

            Point sunMoonPoint = new Point(canvas.sunMoon.getX(), canvas.sunMoon.getY());

            //Now calculate the new positions.
            //Note if we exceed a boundary the direction of the velocity
            // gets reversed.



            sunMoonPoint.x = sunMoonPoint.x + sunMoonVelocity.x;
            if (sunMoonPoint.x > sunMoonMaxX) {
                sunMoonPoint.x = 0 - canvas.sunMoon.getWidth();
                if (canvas.isSun){
                    canvas.isSun = false;
                } else {
                    canvas.isSun = true;
                }
            }
            canvas.sunMoon.setPoint(sunMoonPoint.x, sunMoonPoint.y);

            //make any updates to on screen objects here
            //then invoke the on draw by invalidating the canvas
            canvas.invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };
}
