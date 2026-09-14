package com.example.phonemarkettracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//signs in an existing user stored in the local sqlite database.
public class LoginActivity extends Activity {

    private static final String EXTRA_PREVIEW_EMAIL = "preview_email";

    private EditText emailInput;
    private EditText passwordInput;
    private DatabasePMT databasePMT;

    //1.screen setup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databasePMT = new DatabasePMT(this);
        databasePMT.getWritableDatabase();

        connectViews();
        displayPreviewEmail();
        setUpActions();
    }

    private void connectViews() {
        emailInput = findViewById(R.id.editEmail);
        passwordInput = findViewById(R.id.editPassword);
    }

    private void setUpActions() {
        Button signInButton = findViewById(R.id.buttonLogin);
        TextView signUpLink = findViewById(R.id.textSignUp);

        signInButton.setOnClickListener(view -> validateInputAndOpenProductMenu());
        signUpLink.setOnClickListener(view -> openSignUpScreen());
        passwordInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateInputAndOpenProductMenu();
                return true;
            }

            return false;
        });
    }

    //2.input
    private String readEmailAddress() {
        return emailInput.getText().toString().trim();
    }

    private String readPassword() {
        return passwordInput.getText().toString();
    }

    //3.process: validate and sign in
    private void validateInputAndOpenProductMenu() {
        String emailAddress = readEmailAddress();
        String password = readPassword();

        if (TextUtils.isEmpty(emailAddress)) {
            displayInputError(emailInput, "Enter your email address");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            displayInputError(passwordInput, "Enter your password");
            return;
        }

        int userId = databasePMT.getUserId(
                emailAddress,
                password
        );

        if (userId == -1) {
            Toast.makeText(
                    this,
                    "Invalid email or password",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        UserSession.signIn(userId);
        openProductMenu();
    }

    //4.output
    private void displayPreviewEmail() {
        String previewEmailAddress = getIntent().getStringExtra(EXTRA_PREVIEW_EMAIL);

        if (previewEmailAddress != null) {
            emailInput.setText(previewEmailAddress);
            passwordInput.requestFocus();
        }
    }

    private void displayInputError(EditText inputField, String errorMessage) {
        inputField.setError(errorMessage);
        inputField.requestFocus();
    }

    //5.navigation
    private void openSignUpScreen() {
        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        startActivity(signUpIntent);
    }

    private void openProductMenu() {
        Intent productMenuIntent = new Intent(this, ProductActivity.class);
        startActivity(productMenuIntent);
    }
}
