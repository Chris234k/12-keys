package com.chris234k.keys12;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;

/**
 * TODO: document your custom view class.
 */
public class KeysView extends FrameLayout {

    private KeyListener listener;

    public KeysView(Context context) { super(context); }
    public KeysView(Context context, AttributeSet attrs) {super(context, attrs);}

    public void SetListener(KeyListener l) {
        listener = l;
    }
}