package com.teamsynergy.cryptologue.UI;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.teamsynergy.cryptologue.AccountManager;
import com.teamsynergy.cryptologue.R;
import com.teamsynergy.cryptologue.UserAccount;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText _nameText;
    EditText _usernameText;
    EditText _passwordText;
    EditText _phoneNumberText;
    Button _signupButton;
    TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _nameText = ((EditText)findViewById(R.id.input_name));
        _usernameText = ((EditText)findViewById(R.id.input_username));
        _passwordText = ((EditText)findViewById(R.id.input_password));
        _phoneNumberText = ((EditText)findViewById(R.id.input_phonenumber));
        _signupButton = ((Button)findViewById(R.id.btn_signup));
        _loginLink = ((TextView)findViewById(R.id.link_login));

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        String phone = PhoneNumberUtils.formatNumberToE164(_phoneNumberText.getText().toString(), "US");

        // TODO: Implement your own signup logic here.
        AccountManager.getInstance().register(username, name, password, phone, new AccountManager.onAccountStatus() {
            @Override
            public void onRegistered(UserAccount account) {
                onSignupSuccess();
                progressDialog.dismiss();
            }
        });
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        String phone = PhoneNumberUtils.formatNumberToE164(_phoneNumberText.getText().toString(), "US");

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (phone == null) {
            _phoneNumberText.setError("invalid phone number");
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

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
