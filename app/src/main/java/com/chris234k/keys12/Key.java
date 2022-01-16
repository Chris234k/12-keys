package com.chris234k.keys12;

import android.content.Context;
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
    }

    public Key(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}

    public Key(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {super(context, attrs, defStyleAttr, defStyleRes);}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("chris", "touch!");
        return super.onTouchEvent(event);
    }
}