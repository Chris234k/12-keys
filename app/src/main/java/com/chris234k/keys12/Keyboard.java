package com.chris234k.keys12;

import android.content.Context;
import android.util.AttributeSet;
import androidx.constraintlayout.widget.ConstraintLayout;


public class Keyboard extends ConstraintLayout {

    public static KeyListener listener;

    public Keyboard(Context context) { super(context); }
    public Keyboard(Context context, AttributeSet attrs) {super(context, attrs);}
    public Keyboard(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}


    public void SetListener(KeyListener l) {
        listener = l;
    }
}