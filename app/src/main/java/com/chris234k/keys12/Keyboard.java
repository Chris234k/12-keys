package com.chris234k.keys12;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;


public class Keyboard extends ConstraintLayout {

    public static KeyListener listener;
    public static LinearLayout key_popup;
    public static PopupWindow popup_window;

    public Keyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Keyboard(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}

    public static void SetPopup(Key key, String keyText) {
        TextView text = key_popup.findViewById(R.id.key_popup_text);

        if(keyText == null) {
            popup_window.dismiss();
        } else {
            final float key_height = key.getHeight();
            final float Y_OFFSET = key_height * 1.5f; // give enough vertical space for an "up" input

            int x = (int) key.getX();
            int y = (int) (key.getY() - Y_OFFSET);

            popup_window.setWidth(key.getWidth());
            popup_window.showAtLocation(key, Gravity.NO_GRAVITY, x, y);
            text.setText(keyText);
        }
    }

    public void SetListener(KeyListener l) {
        listener = l;
    }
}