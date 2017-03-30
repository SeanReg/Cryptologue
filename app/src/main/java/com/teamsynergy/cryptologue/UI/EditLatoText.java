package com.teamsynergy.cryptologue.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by nadeen on 3/29/17.
 */

public class EditLatoText extends EditText {

    public EditLatoText(Context context) {
        super(context);
        setFontLato();
    }

    public EditLatoText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFontLato();
    }

    public EditLatoText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFontLato();
    }

    public EditLatoText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFontLato();
    }

    private void setFontLato() {
        Typeface mLato = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Lato-Regular.ttf");
        setTypeface(mLato);
    }
}
