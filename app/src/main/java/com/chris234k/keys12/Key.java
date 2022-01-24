package com.chris234k.keys12;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

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

        isSpecial = a.getBoolean(R.styleable.Key_special, false);
        canRepeat = a.getBoolean(R.styleable.Key_repeats, false);

        numberKey = a.getString(R.styleable.Key_number_key);
        numberKeyColor = getResources().getColor(R.color.light_1);
        numKeyBounds = new Rect();
        numKeyPaint = new Paint();
    }

    public Key(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}

    public Keyboard keyboard;

    // input processing
    public boolean isSpecial;
    public boolean canRepeat;
    public static final int TAP = 0, LEFT = 1, UP = 2, RIGHT = 3, DOWN = 4;
    private int key_state = -1;
    public String tap, left, up, right, down;
    private String[] inputs;
    private int[] drawables;

    // number key drawn separately
    private String numberKey;
    private int numberKeyColor;
    private Rect numKeyBounds;
    private Paint numKeyPaint;


    // key state
    private static final float MIN_DIST = 30f; // TODO @settings
    boolean isDown;
    float startX, startY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(numberKey != null && !numberKey.isEmpty()) {
            numKeyPaint.set(getPaint());
            numKeyPaint.setColor(numberKeyColor);
            numKeyPaint.getTextBounds(numberKey, 0, numberKey.length(), numKeyBounds);

            float x = (getWidth() / 2.0f) - (numKeyBounds.width() / 2.0f);
            float y = (getHeight() * 3.0f / 4.0f) + (numKeyBounds.height() / 2.0f);

            canvas.drawText(numberKey, x, y, numKeyPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                setPressed(true);
                keyboard.SetPopup(this, inputs[TAP]);
                keyboard.onKeyDown(this);

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
                    keyboard.SetPopup(this, inputs[key_state]);

                    refreshDrawableState();
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (isDown) {
                    isDown = false;
                    setPressed(false);
                    keyboard.SetPopup(this, null);

                    processRelease(event.getX(), event.getY());
                    keyboard.onKeyUp(this);

                    setBackgroundResource(R.drawable.key_default);
                    return true;
                }
                
            case MotionEvent.ACTION_CANCEL:
                isDown = false;
                setPressed(false);
                keyboard.SetPopup(this, null);
                setBackgroundResource(R.drawable.key_default);

                return true;
        }

        return false;
    }

    public void onShift(boolean upper) {
        String text = (String)getText();
        if(upper) {
            setText(text.toUpperCase());
        } else {
            setText(text.toLowerCase());
        }
    }

    private void processRelease(float x, float y) {
        float dx = x - startX;
        float dy = y - startY;

        float sq_dist = Math.abs((x-startX) + (y-startY));

        key_state = -1;

        if(sq_dist > MIN_DIST) { // user has dragged
            // find dir
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
        } else {
            key_state = TAP;
        }
    }

    // Get character from the key's current state
    // processRelease must be called prior to this
    public char getCharFromState() {
        String str = inputs[key_state];
        if(str != null && str.length() > 0) {
            return str.charAt(0);
        }

        return Character.MIN_VALUE;
    }
}