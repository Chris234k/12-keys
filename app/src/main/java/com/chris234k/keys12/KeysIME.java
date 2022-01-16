package com.chris234k.keys12;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class KeysIME extends InputMethodService implements KeyListener {
    public KeysIME() {}

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

        Log.d("chris", "key input: " + c + " " + ((int) c));

        ic.commitText(String.valueOf(c), 1);
    }

    public void onSpecial(String special) {
        InputConnection ic = getCurrentInputConnection();
        if(ic == null) {
            return;
        }

        // TODO TODO TODO this feels like a hack?
        switch (special) {
            case "shift":
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
    }
}