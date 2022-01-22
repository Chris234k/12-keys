package com.chris234k.keys12;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Key extends Button {
    public Key(Context context) {
        super(context);
    }

    public Key(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Key,
                0, 0);

        tap = a.getString(R.styleable.Key_tap);
        left = a.getString(R.styleable.Key_left);
        up = a.getString(R.styleable.Key_up);
        right = a.getString(R.styleable.Key_right);
        down = a.getString(R.styleable.Key_down);

        inputs = new String[] {tap, left, up, right, down};
        drawables = new int[] {R.drawable.key_pressed, R.drawable.key_left, R.drawable.key_up, R.drawable.key_right, R.drawable.key_down};

        special = a.getBoolean(R.styleable.Key_special, false);
    }

    public Key(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static final float MIN_DIST = 30f; // TODO @settings

    // input processing
    public boolean special;
    private static final int TAP = 0, LEFT = 1, UP = 2, RIGHT = 3, DOWN = 4;
    public String tap, left, up, right, down;
    private String[] inputs;
    private int[] drawables;

    // key state
    boolean isDown;
    float startX, startY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                setPressed(true);
                Keyboard.SetPopup(this, inputs[TAP]);

                startX = event.getX();
                startY = event.getY();

                setBackgroundResource(R.drawable.key_pressed);

                return true;

            case MotionEvent.ACTION_MOVE:
                if(isDown) {
                    float x = event.getX();
                    float y = event.getY();
                    float sq_dist = Math.abs((x-startX) + (y-startY));

                    float dx = x - startX;
                    float dy = y - startY;

                    int key_state = 0;

                    if(sq_dist > MIN_DIST) { // TODO TODO TODO duplicate code of process inputs
                        if(Math.abs(dx) >= Math.abs(dy)) {
                            if(dx > 0) {
                                key_state = RIGHT;
                            } else {
                                key_state = LEFT;
                            }
                        } else {
                            if(dy > 0) { // (0,0) is top left
                                key_state = DOWN;

                            } else {
                                key_state = UP;
                            }
                        }
                    }

                    setBackgroundResource(drawables[key_state]);
                    Keyboard.SetPopup(this, inputs[key_state]);

                    refreshDrawableState();
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (isDown) {
                    isDown = false;
                    setPressed(false);
                    Keyboard.SetPopup(this, null);

                    if(special) {
                        processSpecial();
                    } else {
                        processRelease(event.getX(), event.getY());
                    }

                    setBackgroundResource(R.drawable.key_default);
                    return true;
                }
        }

        return false;
    }

    private void processSpecial() {
        Keyboard.listener.onSpecial(tap);
    }

    private void processRelease(float x, float y) {
        float dx = x - startX;
        float dy = y - startY;

        float sq_dist = Math.abs((x-startX) + (y-startY));

        char c;

        if(sq_dist > MIN_DIST) {
            // find dir
            if(Math.abs(dx) >= Math.abs(dy)) {
                if(dx > 0) {
                    c = getChar(RIGHT);
                } else {
                    c = getChar(LEFT);
                }
            } else {
                if(dy > 0) { // (0,0) is top left
                    c = getChar(DOWN);
                } else {
                    c = getChar(UP);
                }
            }
        } else {
            c = getChar(TAP);
        }

        if(c == Character.MIN_VALUE) {
            return;
        }

        Keyboard.listener.onKey(c);
    }

    private char getChar(int index) {
        String str = inputs[index];
        if(str != null && str.length() > 0) {
            return str.charAt(0);
        }

        return Character.MIN_VALUE;
    }
}