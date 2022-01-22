package com.chris234k.keys12;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;


public class Keyboard extends ConstraintLayout {

    public Keyboard(Context context, AttributeSet attrs) {super(context, attrs);}
    public Keyboard(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}

    private KeyListener listener;
    private LinearLayout key_popup;
    private PopupWindow popup_window;

    private ArrayList<Key> keys;

    boolean isShift, isCaps; // TODO this may want to live in Key class for UX reasons
    final static long DOUBLE_TAP_THRESHOLD = 300; // in millis
    long shiftTime = 0;

    public void Init(KeyListener keyListener, LayoutInflater inflater) {
        listener = keyListener;

        key_popup = (LinearLayout) inflater.inflate(R.layout.key_popup_layout, null);

        popup_window = new PopupWindow(key_popup, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popup_window.setTouchable(false);


        int childCount = getChildCount();
        keys = new ArrayList<Key>();

        for(int i = 0; i < childCount; i++) {
            View v = getChildAt(i);

            if(v instanceof Key) {
                Key key = (Key)v;
                keys.add(key);

                key.keyboard = this;
            }
        }
    }

    public void SetPopup(Key key, String text) {
        TextView textView = key_popup.findViewById(R.id.key_popup_text);

        if(text == null) {
            popup_window.dismiss();
        } else {
            final float key_height = key.getHeight();
            final float Y_OFFSET = key_height * 1.5f; // give enough vertical space for an "up" input

            int x = (int) key.getX();
            int y = (int) (key.getY() - Y_OFFSET);

            popup_window.setWidth(key.getWidth());
            popup_window.showAtLocation(key, Gravity.NO_GRAVITY, x, y);
            textView.setText(text);
        }
    }

    public void onKey(char key) {
        if(isShift || isCaps) {
            isShift = false;
            key = Character.toUpperCase(key);
        }

        listener.onKey(key);
    }

    public void onSpecial(String special) {
        switch (special) {
            case "shift":
                if (isShift) { // double tap to enable caps lock
                    long elapsed = System.currentTimeMillis() - shiftTime;

                    if (elapsed < DOUBLE_TAP_THRESHOLD) {
                        isCaps = true;
                        isShift = false;
                    }
                } else if (isCaps) {
                    isCaps = false;
                } else {
                    isShift = true;
                    shiftTime = System.currentTimeMillis();
                }

                break;

            case "delete":
                listener.onSpecial(KeyEvent.KEYCODE_DEL);
                break;

            case "space":
                listener.onSpecial(KeyEvent.KEYCODE_SPACE);
                break;

            case "return":
                listener.onSpecial(KeyEvent.KEYCODE_ENTER);
                break;

            case "cursor_left":
                listener.onSpecial(KeyEvent.KEYCODE_DPAD_LEFT);
                break;

            case "cursor_right":
                listener.onSpecial(KeyEvent.KEYCODE_DPAD_RIGHT);
                break;
        }
    }
}