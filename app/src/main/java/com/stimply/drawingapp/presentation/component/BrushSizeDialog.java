package com.stimply.drawingapp.presentation.component;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.stimply.drawingapp.R;
import com.stimply.drawingapp.databinding.DialogBrushSizeBinding;

public class BrushSizeDialog extends Dialog {

    private OnBrushSelected onBrushSelected;

    private final DialogBrushSizeBinding binding;


    public BrushSizeDialog(@NonNull Context context) {
        super(context);
        binding = DialogBrushSizeBinding.inflate(getLayoutInflater());
        setTitle(context.getString(R.string.brush_size));
        setContentView(binding.getRoot());
        setClickListeners();
    }

    private void setClickListeners() {
        binding.ibSmallBrush.setOnClickListener(view -> {
            if (onBrushSelected != null) {
                onBrushSelected.run(10.f);
                dismiss();
            }
        });

        binding.ibMediumBrush.setOnClickListener(view -> {
            if (onBrushSelected != null) {
                onBrushSelected.run(20.f);
                dismiss();
            }
        });

        binding.ibLargeBrush.setOnClickListener(view -> {
            if (onBrushSelected != null) {
                onBrushSelected.run(30.f);
                dismiss();
            }
        });
    }

    public void setOnBrushSelected(OnBrushSelected onBrushSelected) {
        this.onBrushSelected = onBrushSelected;
    }

    public void removeOnBrushSelected() {
        this.onBrushSelected = null;
    }

    public interface OnBrushSelected {
        void run(float selectedBrushSize);
    }
}
