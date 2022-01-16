package com.chris234k.keys12;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
        up = a.getString(R.styleable.Key_up);
        down = a.getString(R.styleable.Key_down);
        left = a.getString(R.styleable.Key_left);
        right = a.getString(R.styleable.Key_right);
    }

    public Key(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isDown;
    public String tap, up, down, left, right;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                Log.d("chris", tap + " down");
                isDown = true;
                return true;

            case MotionEvent.ACTION_UP:
                if (isDown) {
                    isDown = false;

                    Log.d("chris", tap + " up");
                    Keyboard.listener.onKey(tap.charAt(0));


                    return true;
                }
        }

        return false;
    }
}