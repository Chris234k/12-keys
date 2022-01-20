package com.chris234k.keys12;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import androidx.annotation.RequiresApi;

public class KeysIME extends InputMethodService implements KeyListener {
    public KeysIME() {}

    boolean isShift, isCaps; // TODO this may want to live in Key class for UX reasons
    final static long DOUBLE_TAP_THRESHOLD = 300; // in millis
    long shiftTime = 0;

    @Override
    public View onCreateInputView() {
        Keyboard inputView = (Keyboard) getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        inputView.SetListener(this);
        return inputView;
    }

    @Override
    public void onKey(char c) {
        InputConnection ic = getCurrentInputConnection();
        if(ic == null) {
            return;
        }

        if(isShift || isCaps) {
            isShift = false;
            c = Character.toUpperCase(c);
        }

        Log.d("chris", "key input: " + c + " " + ((int) c));

        ic.commitText(String.valueOf(c), 1);

        vibrate();
    }

    public void onSpecial(String special) {
        InputConnection ic = getCurrentInputConnection();
        if(ic == null) {
            return;
        }

        // TODO TODO TODO this feels like a hack?
        switch (special) {
            case "shift":
                if(isShift) { // double tap to enable caps lock
                    long elapsed = System.currentTimeMillis() - shiftTime;

                    if(elapsed < DOUBLE_TAP_THRESHOLD) {
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
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            break;

            case "space":
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE));
            break;

            case "return":
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
            break;

            case "cursor_left":
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT));
            break;

            case "cursor_right":
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT));
            break;
        }

        vibrate();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // TODO support vibration on earlier versions of android?
            v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)); // TODO @settings
        }
    }
}