package com.chris234k.keys12;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
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
    public void onKey(int key) {
        InputConnection ic = getCurrentInputConnection();
        if(ic == null) {
            return;
        }

        Log.e("chris", "key input: " + key);

        char code = (char) key;
        ic.commitText(String.valueOf(code), 1);
    }
}