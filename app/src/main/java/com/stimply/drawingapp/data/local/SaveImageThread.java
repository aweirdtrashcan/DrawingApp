package com.stimply.drawingapp.data.local;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class SaveImageThread extends Thread {

    private final Bitmap bitmap;

    private final SaveImageRunnable whenReadyRunnable;

    public SaveImageThread(Bitmap bitmap, SaveImageRunnable whenReadyRunnable) {
        super();
        this.bitmap = bitmap;
        this.whenReadyRunnable = whenReadyRunnable;
    }

    @Override
    public void run() {
        super.run();

        try {
            // Get DCIM path
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +
                    File.separator +
                    "KidsDrawingApp/";

            // Create a file that'll represent the absolute directory
            File directory = new File(filePath);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("failed to create directory");
                }
            }

            String fileName = "KidsDrawingApp_" +
                            System.currentTimeMillis() / 1000 +
                            ".png";

            File file = new File(directory, fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            whenReadyRunnable.run(fileName);
        } catch (SecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isExternalStorageWriteable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public interface SaveImageRunnable {
        public void run(String savedPath);
    }
}
