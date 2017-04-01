package com.teamsynergy.cryptologue.UI;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.ParseException;
import com.teamsynergy.cryptologue.AccountManager;
import com.teamsynergy.cryptologue.R;


public class SettingsActivity extends AppCompatActivity {

    EditText _nameText;
    EditText _usernameText;
    EditText _passwordText;
    Button _saveButton;

    AccountManager manager = AccountManager.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        _nameText = ((EditText)findViewById(R.id.input_name));
        _usernameText = ((EditText)findViewById(R.id.input_username));
        _passwordText = ((EditText)findViewById(R.id.input_password));
        _saveButton = ((Button)findViewById(R.id.btn_save));

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

    }

    public void save() {
        if (!validate()) {
            onSaveFailed();
            return;
        }

        _saveButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving Settings...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        String phone = "9542549695";

        manager.updateAccount(username, name, password, phone, new AccountManager.onAccountStatus() {
            @Override
            public void onSave() {
                onSaveSuccess();
            };
            @Override
            public void onSaveError(ParseException e) {
                onSaveFailed(e);
            };
        });
    }

    public void onSaveSuccess() {
        Toast.makeText(getBaseContext(), "Settings saved.", Toast.LENGTH_LONG).show();
        _saveButton.setEnabled(true);
        setResult(RESULT_OK, null);
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
}


