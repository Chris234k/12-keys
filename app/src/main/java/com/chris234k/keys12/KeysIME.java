package com.chris234k.keys12;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.View;

public class KeysIME extends InputMethodService {
    public KeysIME() {
        Log.e("chrissupersecret", "hello from ime");
    }

    @Override
    public View onCreateInputView() {
        KeysView inputView = (KeysView) getLayoutInflater().inflate(R.layout.keys_view, null);
        return inputView;
    }
}