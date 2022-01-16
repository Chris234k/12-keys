package com.chris234k.keys12;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class KeysIME extends InputMethodService implements KeyListener {
    public KeysIME() {
        Log.e("chrissupersecret", "hello from ime");
    }

    @Override
    public View onCreateInputView() {
        KeysView inputView = (KeysView) getLayoutInflater().inflate(R.layout.keys_layout, null);
        inputView.SetListener(this);
        return inputView;
    }

    @Override
    public void onKey(int key) {
        InputConnection ic = getCurrentInputConnection();
        if(ic == null) {
            return;
        }

        char code = (char) key;
        ic.commitText(String.valueOf(code), 1);
    }
}