package com.example.phonemarkettracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//creates a user account in the local sqlite database.
public class SignUpActivity extends Activity {

    private static final String EXTRA_PREVIEW_EMAIL = "preview_email";
    private static final int MINIMUM_PASSWORD_LENGTH = 6;

    private EditText fullNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private DatabasePMT databasePMT;

    //1.screen setup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        databasePMT = new DatabasePMT(this);
        databasePMT.getWritableDatabase();

        connectViews();
        setUpActions();
    }

    private void connectViews() {
        fullNameInput = findViewById(R.id.editSignUpName);
        emailInput = findViewById(R.id.editSignUpEmail);
        passwordInput = findViewById(R.id.editSignUpPassword);
        confirmPasswordInput = findViewById(R.id.editConfirmPassword);
    }

    private void setUpActions() {
        Button createAccountButton = findViewById(R.id.buttonCreateAccount);
        TextView signInLink = findViewById(R.id.textBackToSignIn);

        createAccountButton.setOnClickListener(view -> validateInputAndReturnToSignIn());
        signInLink.setOnClickListener(view -> finish());
        confirmPasswordInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateInputAndReturnToSignIn();
                return true;
            }

            return false;
        });
    }

    //2.input
    private String readFullName() {
        return fullNameInput.getText().toString().trim();
    }

    private String readEmailAddress() {
        return emailInput.getText().toString().trim();
    }

    private String readPassword() {
        return passwordInput.getText().toString();
    }

    private String readConfirmedPassword() {
        return confirmPasswordInput.getText().toString();
    }

    //3.process: validate and register
    private void validateInputAndReturnToSignIn() {
        String fullName = readFullName();
        String emailAddress = readEmailAddress();
        String password = readPassword();
        String confirmedPassword = readConfirmedPassword();

        if (TextUtils.isEmpty(fullName)) {
            displayInputError(fullNameInput, "Enter your full name");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            displayInputError(emailInput, "Enter a valid email address");
            return;
        }

        if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            displayInputError(passwordInput, "Use at least 6 characters");
            return;
        }

        if (!password.equals(confirmedPassword)) {
            displayInputError(confirmPasswordInput, "Passwords do not match");
            return;
        }

        if (databasePMT.emailExists(emailAddress)) {
            displayInputError(emailInput, "Email already registered");
            return;
        }

        boolean accountCreated = databasePMT.addUser(
                fullName,
                emailAddress,
                password
        );

        if (!accountCreated) {
            Toast.makeText(
                    this,
                    "Unable to create account",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Toast.makeText(
                this,
                "Account created successfully",
                Toast.LENGTH_SHORT
        ).show();

        openSignInScreen(emailAddress);
    }

    //4.output
    private void displayInputError(EditText inputField, String errorMessage) {
        inputField.setError(errorMessage);
        inputField.requestFocus();
    }

    //5.navigation
    private void openSignInScreen(String emailAddress) {
        Intent signInIntent = new Intent(this, LoginActivity.class);
        signInIntent.putExtra(EXTRA_PREVIEW_EMAIL, emailAddress);
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signInIntent);
        finish();
    }
}
