package com.teamsynergy.cryptologue.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.parse.ParseException;
import com.teamsynergy.cryptologue.AccountManager;
import com.teamsynergy.cryptologue.ImageUtil;
import com.teamsynergy.cryptologue.Manifest;
import com.teamsynergy.cryptologue.R;
import com.teamsynergy.cryptologue.UserAccount;

import java.io.File;
import java.io.FileOutputStream;


public class SettingsActivity extends AppCompatActivity {
    String imgDecodableString = null;
    File _userAvatar = null;

    EditText _nameText;
    EditText _usernameText;
    EditText _passwordText;
    Button _saveButton;
    ImageView _uploadUserAvatar;

    AccountManager manager = AccountManager.getInstance();

    ProgressDialog progressDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressDialog = new ProgressDialog(SettingsActivity.this, R.style.AppTheme_Dark_Dialog);

        _nameText = ((EditText) findViewById(R.id.input_name));
        _usernameText = ((EditText) findViewById(R.id.input_username));
        _passwordText = ((EditText) findViewById(R.id.input_password));
        _saveButton = ((Button) findViewById(R.id.btn_save));

        _usernameText.setText(manager.getCurrentAccount().getUsername());
        _nameText.setText(manager.getCurrentAccount().getDisplayName());

        _usernameText.setOnKeyListener(mValidateListener);
        _nameText.setOnKeyListener(mValidateListener);
        _passwordText.setOnKeyListener(mValidateListener);
        _saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        _uploadUserAvatar = ((ImageView) findViewById(R.id.userAvatar));

        _uploadUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicker();
            }
        });


        AccountManager.getInstance().getCurrentAccount().getImage(new UserAccount.Callbacks() {
            @Override
            public void onGotProfilePicture(File image) {
                ImageView imgView = (ImageView) findViewById(R.id.userAvatar);
                if (image != null)
                    // Set the Image in ImageView after decoding the String
                    imgView.setImageBitmap(BitmapFactory.decodeFile(image.getAbsolutePath()));
            }
        });
    }

    public void save() {
        if (!validate()) {
            onSaveFailed();
            return;
        }

        _saveButton.setEnabled(false);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving Settings...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        String phone = "9542549695";

        manager.updateAccount(username, name, password, phone, _userAvatar, new AccountManager.onAccountStatus() {
            @Override
            public void onSave() {
                onSaveSuccess();
            }

            ;

            @Override
            public void onSaveError(ParseException e) {
                onSaveFailed(e);
            }

            ;
        });
    }

    public void onSaveSuccess() {
        Toast.makeText(getBaseContext(), "Settings saved.", Toast.LENGTH_LONG).show();
        _saveButton.setEnabled(true);
        setResult(RESULT_OK, null);
        progressDialog.dismiss();
        finish();
    }

    public void onSaveFailed() {
        Toast.makeText(getBaseContext(), "Save failed.", Toast.LENGTH_LONG).show();
        _saveButton.setEnabled(true);
    }

    public void onSaveFailed(ParseException e) {
        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        _saveButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (username.isEmpty()) {
            _usernameText.setError("enter a valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if ((password.length() < 4 && !password.isEmpty()) || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private EditText.OnKeyListener mValidateListener = new EditText.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            validate();

            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == ImageUtil.RESULT_LOAD_IMG) {
                if (resultCode == RESULT_OK && null != data) {

                    Bitmap scaledBmp = ImageUtil.imagePickerResult(this, data, 128);
                    ImageView imgView = (ImageView) findViewById(R.id.userAvatar);
                    imgView.setImageBitmap(scaledBmp);

                    _userAvatar = ImageUtil.tempSaveCompressedBitmap(this, scaledBmp);
                } else {
                    Toast.makeText(this, "You haven't picked an image",
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();

            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void showImagePicker() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ImageUtil.READ_IMAGE_PERMISSION);
        } else {
            ImageUtil.showImagePicker(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ImageUtil.READ_IMAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImageUtil.showImagePicker(this);
                } else {

                }
                break;
            }

        }
    }

}