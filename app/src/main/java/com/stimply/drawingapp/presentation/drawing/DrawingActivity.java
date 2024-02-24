package com.stimply.drawingapp.presentation.drawing;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.stimply.drawingapp.R;
import com.stimply.drawingapp.databinding.ActivityMainBinding;
import com.stimply.drawingapp.presentation.component.BrushSizeDialog;

public class DrawingActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ImageButton lastPressedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        lastPressedButton = (ImageButton)binding.llPaintColors.getChildAt(0);
        binding.drawingView.setColor((String)lastPressedButton.getTag());
    }

    @Override
    protected void onStart() {
        super.onStart();

        listenForColors();
        binding.btnShowDialog.setOnClickListener(view -> showBrushSizeDialog());
    }

    private void showBrushSizeDialog() {
        BrushSizeDialog dialog = new BrushSizeDialog(this);
        dialog.setOnBrushSelected(selectedBrushSize ->
                binding.drawingView.setSizeForBrush(selectedBrushSize)
        );
        dialog.show();
    }

    private void listenForColors() {
        for (int i = 0; i < binding.llPaintColors.getChildCount(); i++) {
            View view = binding.llPaintColors.getChildAt(i);
            view.setOnClickListener(this::onPaintClicked);
        }
    }

    private void onPaintClicked(View view) {
        ImageButton imgBtn = (ImageButton)view;

        if (imgBtn.equals(lastPressedButton)) return;
        lastPressedButton.setImageDrawable(
                ContextCompat.getDrawable(DrawingActivity.this, R.drawable.pallete_normal)
        );

        binding.drawingView.setColor(imgBtn.getTag().toString());

        imgBtn.setImageDrawable(
                ContextCompat.getDrawable(DrawingActivity.this, R.drawable.pallete_pressed)
        );

        lastPressedButton = imgBtn;
    }
}