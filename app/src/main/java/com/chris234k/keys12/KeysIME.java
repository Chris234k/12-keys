package com.chris234k.keys12;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class KeysIME extends InputMethodService implements KeyListener {
    public KeysIME() {}

    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = getLayoutInflater();
        Keyboard keyboard = (Keyboard) getLayoutInflater().inflate(R.layout.keyboard_usage_frequency_layout, null);
        keyboard.Init(this, inflater);

        return keyboard;
    }

    @Override
    public void onKey(char c) {
        InputConnection ic = getCurrentInputConnection();
        if(ic == null) {
            return;
        }

        ic.commitText(String.valueOf(c), 1);
        vibrate();
    }

    public void onSpecial(int keyEvent) {
        InputConnection ic = getCurrentInputConnection();
        if(ic == null) {
            return;
        }

        sendDownUpKeyEvents(keyEvent);
        vibrate();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // TODO support vibration on earlier versions of android?
            v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)); // TODO @settings
        }
    }
}