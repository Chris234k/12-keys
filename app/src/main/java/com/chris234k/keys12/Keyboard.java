package com.chris234k.keys12;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;


public class Keyboard extends ConstraintLayout {

    public Keyboard(Context context, AttributeSet attrs) {super(context, attrs);}
    public Keyboard(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}

    private KeyListener listener;
    private PopupWindow popup_window;
    private TextView popup_text;
    private ArrayList<Key> keys;

    // shift state
    boolean isShift, isCaps;
    Key shiftKey;
    long shiftTime = 0;

    // key repeat
    Handler handler;
    Runnable repeatRunnable;

    // user preferences
    int key_repeat_initial_millis = 200; // how long you have to hold to trigger a repeat
    int key_repeat_millis = 50; // rate of subsequent repeats
    int double_tap_window_millis = 300;
    boolean vibration_enabled = true;

    public void Init(KeyListener keyListener, LayoutInflater inflater) {
        listener = keyListener;

        LinearLayout key_popup = (LinearLayout) inflater.inflate(R.layout.key_popup_layout, null);
        popup_window = new PopupWindow(key_popup, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popup_window.setTouchable(false);
        popup_text = key_popup.findViewById(R.id.key_popup_text);

        // setup key visual state
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

        // key repeat message callbacks
        if(handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        key_repeat_initial_millis = prefs.getInt("key_repeat_initial", 200);
        key_repeat_millis = prefs.getInt("key_repeat_rate", 50);
        double_tap_window_millis = prefs.getInt("double_tap_window", 300);
        vibration_enabled = prefs.getBoolean("use_vibration", true);
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

    public void onKeyDown(Key key) {
        if(repeatRunnable != null) {
            handler.removeCallbacks(repeatRunnable);
        }

        if(key.canRepeat) {
            sendRepeat(key, key_repeat_initial_millis);
        }

        vibrate(true);
    }

    public void onKeyUp(Key key) {
        if(key.isSpecial) {
            onSpecial(key);
        } else {
            char c = key.getCharFromState();
            if(isShift || isCaps) {
                isShift = false;
                onShift(isShift, isCaps);
                c = Character.toUpperCase(c);
            }

            listener.onKey(c);
        }

        if(repeatRunnable != null) {
            handler.removeCallbacks(repeatRunnable);
        }

        vibrate(false);
    }

    private void onSpecial(Key key) {
        String text = key.tap.toLowerCase();

        switch (text) {
            case "shift":
                if (isShift) { // double tap to enable caps lock
                    long elapsed = System.currentTimeMillis() - shiftTime;

                    if (elapsed < double_tap_window_millis) {
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


            case "swap_symbols":
                listener.onSwitchLayout(false);
                    break;
            case "swap_letters":
                listener.onSwitchLayout(true);
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

    private void sendRepeat(Key key, long delay) {
        // store ref to runnable to allow cancelling
        repeatRunnable = () -> {
            if(key.isSpecial) {
                onSpecial(key);
            } else {
                onKeyUp(key);
            }

            sendRepeat(key, key_repeat_millis);
        };

        handler.postDelayed(repeatRunnable, delay);
    }

    private void vibrate(boolean keyDown) {
        if(vibration_enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                if (keyDown) {
                    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS);
                } else {
                    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_RELEASE);
                }
            } else {
                if(keyDown) { // UX: this feels too strong for both up and down key states
                    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                }
            }
        }
    }
}