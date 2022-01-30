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

        String tap = a.getString(R.styleable.Key_tap);
        String left = a.getString(R.styleable.Key_left);
        String up = a.getString(R.styleable.Key_up);
        String right = a.getString(R.styleable.Key_right);
        String down = a.getString(R.styleable.Key_down);

        inputs = new String[] {tap, left, up, right, down};
        drawables = new int[] {R.drawable.key_pressed, R.drawable.key_left, R.drawable.key_up, R.drawable.key_right, R.drawable.key_down};

        is_special = a.getBoolean(R.styleable.Key_special, false);
        can_repeat = a.getBoolean(R.styleable.Key_repeats, false);

        number_key = a.getString(R.styleable.Key_number_key);
        number_key_color = getResources().getColor(R.color.light_1);
        number_key_bounds = new Rect();
        number_key_paint = new Paint();
    }

    public Key(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}

    public Keyboard parent_keyboard;

    // modifiers
    public boolean is_special;
    public boolean can_repeat;

    // key state
    public static final int NONE = -1, TAP = 0, LEFT = 1, UP = 2, RIGHT = 3, DOWN = 4;
    private int key_state = NONE;
    private String[] inputs;
    private int[] drawables;

    // number key drawn separately
    private String number_key;
    private int number_key_color;
    private Rect number_key_bounds;
    private Paint number_key_paint;


    // key state
    private static final float MIN_DIST = 30f; // TODO @settings
    boolean isDown;
    float startX, startY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(number_key != null && !number_key.isEmpty()) {
            number_key_paint.set(getPaint());
            number_key_paint.setColor(number_key_color);
            number_key_paint.getTextBounds(number_key, 0, number_key.length(), number_key_bounds);

            float x = (getWidth() / 2.0f) - (number_key_bounds.width() / 2.0f);
            float y = (getHeight() * 3.0f / 4.0f) + (number_key_bounds.height() / 2.0f);

            canvas.drawText(number_key, x, y, number_key_paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                key_state = TAP;
                isDown = true;
                setPressed(true);
                parent_keyboard.SetPopup(this, inputs[TAP]);
                parent_keyboard.OnKeyDown(this);

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
                    parent_keyboard.SetPopup(this, inputs[key_state]);

                    refreshDrawableState();
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (isDown) {
                    isDown = false;
                    setPressed(false);
                    parent_keyboard.SetPopup(this, null);

                    processRelease(event.getX(), event.getY());
                    parent_keyboard.OnKeyUp(this);

                    setBackgroundResource(R.drawable.key_default);

                    key_state = NONE;
                    return true;
                }
                
            case MotionEvent.ACTION_CANCEL:
                key_state = NONE;
                isDown = false;
                setPressed(false);
                parent_keyboard.SetPopup(this, null);
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
    public char getCharForCurrentState() {
        String str = getTextForCurrentState();
        if(str != null && str.length() > 0) {
            return str.charAt(0);
        }

        return Character.MIN_VALUE;
    }

    public String getTextForCurrentState() {
        String text = "";

        if(key_state != NONE) {
            text = getTextForState(key_state);
        }

        if(text == null) {
            text = "";
        }

        return text;
    }

    public String getTextForState(int state) {
        return inputs[state];
    }
}