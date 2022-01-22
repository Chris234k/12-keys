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
    private PopupWindow popup_window;
    private TextView popup_text;

    private ArrayList<Key> keys;

    boolean isShift, isCaps;
    Key shiftKey;
    final static long DOUBLE_TAP_THRESHOLD = 300; // in millis
    long shiftTime = 0;

    public void Init(KeyListener keyListener, LayoutInflater inflater) {
        listener = keyListener;

        LinearLayout key_popup = (LinearLayout) inflater.inflate(R.layout.key_popup_layout, null);

        popup_window = new PopupWindow(key_popup, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popup_window.setTouchable(false);

        popup_text = key_popup.findViewById(R.id.key_popup_text);

        int childCount = getChildCount();
        keys = new ArrayList<Key>();

        for(int i = 0; i < childCount; i++) {
            View v = getChildAt(i);

            if(v instanceof Key) {
                Key key = (Key)v;
                keys.add(key);

                key.keyboard = this;

                if(key.isSpecial) {
                    if(key.tap.equals("shift")) {
                        shiftKey = key;
                    }
                }
            }
        }

        onShift(false, false);
    }

    public void SetPopup(Key key, String text) {
        if(text == null) {
            popup_window.dismiss();
        } else {
            if(key.isSpecial) {
                text = String.valueOf(key.getText());
            } else if (isShift || isCaps) {
                text = text.toUpperCase();
            }
            
            final float key_height = key.getHeight();
            final float Y_OFFSET = key_height * 1.5f; // give enough vertical space for an "up" input

            int x = (int) key.getX();
            int y = (int) (key.getY() - Y_OFFSET);

            popup_window.setWidth(key.getWidth());
            popup_window.showAtLocation(key, Gravity.NO_GRAVITY, x, y);
            popup_text.setText(text);
        }
    }

    public void onKey(char key) {
        if(isShift || isCaps) {
            isShift = false;
            onShift(isShift, isCaps);
            key = Character.toUpperCase(key);
        }

        listener.onKey(key);
    }

    public void onSpecial(Key key) {
        String text = key.tap.toLowerCase();

        switch (text) {
            case "shift":
                if (isShift) { // double tap to enable caps lock
                    long elapsed = System.currentTimeMillis() - shiftTime;

                    if (elapsed < DOUBLE_TAP_THRESHOLD) {
                        isCaps = true;
                    }

                    isShift = false;
                } else if (isCaps) {
                    isCaps = false;
                } else {
                    isShift = true;
                    shiftTime = System.currentTimeMillis();
                }

                onShift(isShift, isCaps);

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

    private void onShift(boolean shift, boolean caps) {
        if(caps) {
            shiftKey.setText("CAPS");
        } else if (shift) {
            shiftKey.setText("SHIFT");
        } else {
            shiftKey.setText("shift");
        }

        for(Key key : keys) {
            if(key.isSpecial) {
                continue;
            }

            key.onShift(shift || caps);
        }
    }
}