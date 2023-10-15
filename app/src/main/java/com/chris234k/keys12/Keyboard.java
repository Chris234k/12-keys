package com.chris234k.keys12;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

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
    boolean is_shift, is_caps;
    Key shift_key;
    long shift_time = 0;

    // key repeat
    Handler handler;
    Runnable repeat_runnable;

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
        popup_text.setAutoSizeTextTypeUniformWithConfiguration(20, 40, 1, TypedValue.COMPLEX_UNIT_SP);

        // setup key visual state
        int childCount = getChildCount();
        keys = new ArrayList<Key>();

        for(int i = 0; i < childCount; i++) {
            View v = getChildAt(i);

            if(v instanceof Key) {
                Key key = (Key)v;
                keys.add(key);

                key.parent_keyboard = this;

                if(key.is_special) {
                    if(key.getTextForState(Key.TAP).equals("shift")) {
                        shift_key = key;
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
            if(key.is_special) {
                text = String.valueOf(key.getText());
            } else if (is_shift || is_caps) {
                text = text.toUpperCase();
            }
            
            final float key_height = key.getHeight();
            final float Y_OFFSET = key_height * 1.5f; // give enough vertical space for an "up" input

            int x = (int) key.getX();
            int y = (int) (key.getY() - Y_OFFSET);

            popup_window.setWidth(key.getWidth());
            popup_window.setHeight(key.getHeight());
            popup_window.showAtLocation(key, Gravity.NO_GRAVITY, x, y);
            popup_text.setText(text);
        }
    }

    public void OnKeyDown(Key key) {
        if(repeat_runnable != null) {
            handler.removeCallbacks(repeat_runnable);
        }

        if(key.can_repeat) {
            sendRepeat(key, key_repeat_initial_millis);
        }

        vibrate(true);
    }

    public void OnKeyUp(Key key) {
        if(key.is_special) {
            onSpecial(key);
        } else {
            char c = key.getCharForCurrentState();
            if(c == Character.MIN_VALUE) {
                return;
            }
            
            if(is_shift || is_caps) {
                is_shift = false;
                onShift(is_shift, is_caps);
                c = Character.toUpperCase(c);
            }

            listener.onKey(c);
        }

        if(repeat_runnable != null) {
            handler.removeCallbacks(repeat_runnable);
        }

        vibrate(false);
    }

    private void onSpecial(Key key) {
        String text = key.getTextForCurrentState();

        switch (text) {
            case "shift":
                if (is_shift) { // double tap to enable caps lock
                    long elapsed = System.currentTimeMillis() - shift_time;

                    if (elapsed < double_tap_window_millis) {
                        is_caps = true;
                    }

                    is_shift = false;
                } else if (is_caps) {
                    is_caps = false;
                } else {
                    is_shift = true;
                    shift_time = System.currentTimeMillis();
                }

                onShift(is_shift, is_caps);

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

            case "cursor_up":
                listener.onSpecial(KeyEvent.KEYCODE_DPAD_UP);
                break;

            case "cursor_down":
                listener.onSpecial(KeyEvent.KEYCODE_DPAD_DOWN);
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
            shift_key.setText("CAPS");
        } else if (shift) {
            shift_key.setText("SHIFT");
        } else {
            shift_key.setText("shift");
        }

        for(Key key : keys) {
            if(key.is_special) {
                continue;
            }

            key.onShift(shift || caps);
        }
    }

    private void sendRepeat(Key key, long delay) {
        // store ref to runnable to allow cancelling
        repeat_runnable = () -> {
            if(key.is_special) {
                onSpecial(key);
            } else {
                OnKeyUp(key);
            }

            sendRepeat(key, key_repeat_millis);
        };

        handler.postDelayed(repeat_runnable, delay);
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