package com.stimply.drawingapp.presentation.util;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class AppUtil {

    public static void showSnackbar(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }
}
