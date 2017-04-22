package com.teamsynergy.cryptologue;

import android.graphics.Bitmap;

/**
 * Created by Sean on 4/21/2017.
 */

public class ImageUtil {

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
}
