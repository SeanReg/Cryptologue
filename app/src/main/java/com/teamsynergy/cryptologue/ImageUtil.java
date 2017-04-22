package com.teamsynergy.cryptologue;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Sean on 4/21/2017.
 */

public class ImageUtil {
    public static final int READ_IMAGE_PERMISSION = 1;


    public static final int RESULT_LOAD_IMG = 1;

    public static Bitmap uniformScale(Bitmap scale, int size, boolean destroy) {
        int bW, bH;
        float fSize = size;
        if (scale.getWidth() > scale.getHeight()) {
            bH = ((int)(scale.getHeight() / (float)scale.getWidth()
                    * fSize));
            bW = size;
        } else {
            bW = ((int)(scale.getWidth() / (float)scale.getHeight()
                    * fSize));
            bH = 128;
        }

        Bitmap scaled =  Bitmap.createScaledBitmap(scale, bW, bH, false);
        scale.recycle();

        return scaled;
    }

    public static void showImagePicker(Activity act) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        act.startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public static Bitmap imagePickerResult(Context context, Intent data, @Nullable Integer maxSize) {
        // Get the Image from data
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        // Get the cursor
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        // Set the Image in ImageView after decoding the
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap imgBitmap = BitmapFactory.decodeFile(filePath, options);

        if (maxSize!= null) {
            imgBitmap = uniformScale(imgBitmap, maxSize, true);
        }

        return imgBitmap;
    }

    public static File tempSaveCompressedBitmap(Context con, Bitmap bmp) {
        ContextWrapper cw = new ContextWrapper(con.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File temp = new File(directory, "temp.jpg");

        try {
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(temp));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return temp;
    }
}
