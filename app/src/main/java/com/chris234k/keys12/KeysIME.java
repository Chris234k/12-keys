package com.chris234k.keys12;

import android.inputmethodservice.InputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.preference.PreferenceManager;

public class KeysIME extends InputMethodService implements KeyListener {
    public KeysIME() {}

    // track current keyboard, update if preferences change
    Keyboard active_keyboard;
    int keyboard_id;

    @Override
    public View onCreateInputView() {
        active_keyboard = getInputView();
        return active_keyboard;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);

        active_keyboard = getInputView();
        setInputView(active_keyboard);
    }

    private Keyboard getInputView() {
        int layout = R.layout.keyboard_usage_frequency_layout;
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_flip_phone_layout", false)) {
            layout = R.layout.keyboard_layout;
        }

        // did preferences change?
        if(layout == keyboard_id) {
            return active_keyboard;
        } else {
            LayoutInflater inflater = getLayoutInflater();
            Keyboard newKeyboard = (Keyboard) getLayoutInflater().inflate(layout, null);
            newKeyboard.Init(this, inflater);

            return newKeyboard;
        }
    }

    @Override
    public void onKey(char c) {
        InputConnection ic = getCurrentInputConnection();
        if(ic == null) {
            return;
        }

        ic.commitText(String.valueOf(c), 1);
    }

    public void onSpecial(int keyEvent) {
        InputConnection ic = getCurrentInputConnection();
        if(ic == null) {
            return;
        }

        sendDownUpKeyEvents(keyEvent);
    }
}