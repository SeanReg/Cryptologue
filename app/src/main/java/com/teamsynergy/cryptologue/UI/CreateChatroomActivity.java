package com.teamsynergy.cryptologue.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.teamsynergy.cryptologue.AccountManager;
import com.teamsynergy.cryptologue.Chatroom;
import com.teamsynergy.cryptologue.ImageUtil;
import com.teamsynergy.cryptologue.ObjectPasser;
import com.teamsynergy.cryptologue.R;
import com.teamsynergy.cryptologue.User;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateChatroomActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMG = 1;
    private static int RESULT_SELECT_CONTACTS = 2;
    File imgDecodableFile = null;

    EditText _nameText;
    Button _uploadChatAvatar;
    Button _nextButton;

    AccountManager manager = AccountManager.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chatroom);

        _nameText = ((EditText)findViewById(R.id.input_name));
        _uploadChatAvatar = ((Button)findViewById(R.id.btn_upload));
        _nextButton = ((Button)findViewById(R.id.btn_next));

        _uploadChatAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        _nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

    }

    public void next() {
        Intent contactsIntent = new Intent(this, SelectContactsActivity.class);
        contactsIntent.putExtra("Chatroom name", _nameText.getText().toString());
        startActivityForResult(contactsIntent, RESULT_SELECT_CONTACTS);
        // send name and photo
    }

    public void upload() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG) {
                if (resultCode == RESULT_OK && null != data) {

                    // Get the Image from data
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgFile = cursor.getString(columnIndex);

                    cursor.close();
                    ImageView imgView = (ImageView) findViewById(R.id.chatAvatar);
                    // Set the Image in ImageView after decoding the
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable=true;
                    Bitmap imgBitmap = BitmapFactory
                            .decodeFile(imgFile, options);

                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    // path to /data/data/yourapp/app_data/imageDir
                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

                    Bitmap scaledBmp = ImageUtil.uniformScale(imgBitmap, 128, true);//Bitmap.createScaledBitmap(imgBitmap, bW, bH, false);
                    imgView.setImageBitmap(scaledBmp);
                    imgDecodableFile = new File(directory, "temp1.jpg");
                    scaledBmp.compress(Bitmap.CompressFormat.JPEG, 90,
                            new FileOutputStream(imgDecodableFile));

                } else {
                    Toast.makeText(this, "You haven't picked an image",
                            Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == RESULT_SELECT_CONTACTS) {
                if (resultCode == RESULT_OK) {
                    String[] numbers = data.getStringArrayExtra("selectedNumbers");
                    User.findByPhoneNumber(Arrays.asList(numbers), new User.UsersFoundListener() {
                        @Override
                        public void onUsersFound(List<User> users) {
                            Chatroom.Builder builder = new Chatroom.Builder();
                            builder.setName(_nameText.getText().toString());
                            if (imgDecodableFile != null)
                                builder.setImage(imgDecodableFile);
                            for (User usr : users) {
                                builder.addMember(usr);
                            }
                            builder.build(true, new Chatroom.BuiltListener() {
                                @Override
                                public void onChatroomBuilt(Chatroom room) {
                                    ObjectPasser.putObject(room.getId(), room);

                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("chatroom", room.getId());
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                }
                            });

                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }
}
