package com.chris234k.keys12;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;

import androidx.preference.PreferenceManager;

public class KeysIME extends InputMethodService implements KeyListener {
    public KeysIME() {}

    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = getLayoutInflater();

        // TODO TODO TODO @settings onCreateInputView isn't always called when shown, there's probably a better lifecycle callback
        int layout = R.layout.keyboard_usage_frequency_layout;
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_flip_phone_layout", false)) {
            layout = R.layout.keyboard_layout;
        }
        Keyboard keyboard = (Keyboard) getLayoutInflater().inflate(layout, null);
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
        if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_vibration", true)) {
            return;
        }

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // TODO support vibration on earlier versions of android?
            v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)); // TODO @settings
        }
    }
}