package com.teamsynergy.cryptologue.UI;

    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.view.View;
    import android.widget.Button;
    import android.widget.TextView;

    import com.teamsynergy.cryptologue.R;

/**
 * Created by jasonpinlac on 3/26/17.
 */

public class CreateChatRoomActivity extends AppCompatActivity {

    Button _createButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createchatroom);


        _createButton = ((Button)findViewById(R.id.button_confirmCreate));

        _createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }
}
