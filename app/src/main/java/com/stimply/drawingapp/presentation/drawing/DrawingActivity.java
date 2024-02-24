package com.stimply.drawingapp.presentation.drawing;

import static com.stimply.drawingapp.presentation.util.AppUtil.showSnackbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.stimply.drawingapp.R;
import com.stimply.drawingapp.data.local.SaveImageThread;
import com.stimply.drawingapp.databinding.ActivityMainBinding;
import com.stimply.drawingapp.presentation.component.BrushSizeDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import kotlinx.coroutines.Job;

public class DrawingActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ImageButton lastPressedButton;

    private final ArrayList<String> cameraPermissions = new ArrayList<>();

    private final ActivityResultLauncher<Intent> openGalleryResult =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResult -> {
            if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() != null) {
                Uri imageUri = activityResult.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    binding.drawingView.setBackgroundBitmap(bitmap);
                } catch (IOException e) {
                    showSnackbar(binding.getRoot(), "Failed to select image from gallery");
                }
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        lastPressedButton = (ImageButton)binding.llPaintColors.getChildAt(0);
        binding.drawingView.setColor((String)lastPressedButton.getTag());
        requestGalleryPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();

        listenForColors();
        binding.btnShowDialog.setOnClickListener(view -> showBrushSizeDialog());
        binding.btnOpenGallery.setOnClickListener(view -> openGallery());
        binding.btnUndo.setOnClickListener(view -> binding.drawingView.undo());
        binding.btnSave.setOnClickListener(view -> saveBitmap(binding.drawingView.getViewAsBitmap()));
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

    private void requestGalleryPermission() {
        if (cameraPermissions.isEmpty()) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                cameraPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                cameraPermissions.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
        }
        requestPermissions(cameraPermissions.toArray(new String[0]), 3);
    }

    private boolean hasGalleryPermissions() {
        for (String permission : cameraPermissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void openGallery() {
        if (!hasGalleryPermissions()) {
            showSnackbar(binding.getRoot(), "This app requires camera permission");
        }
        showSnackbar(binding.getRoot(), "Opening gallery...");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        openGalleryResult.launch(pickIntent);
    }

    private void saveBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            System.out.println("Can't save an empty bitmap");
            return;
        }
        try {
            Thread saveThread = new SaveImageThread(bitmap, new SaveImageThread.SaveImageRunnable() {
                @Override
                public void run(String savedPath) {
                    showSnackbar(binding.getRoot(), "Saved as: " + savedPath);
                }
            });
            saveThread.start();
        } catch (RuntimeException exception) {
            System.out.println("Failed to save bitmap: " + exception.getMessage());
        }
    }
}