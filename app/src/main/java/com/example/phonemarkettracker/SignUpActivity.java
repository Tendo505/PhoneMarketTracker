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

/** Front-end sign-up preview. The entered details are not persisted. */
public class SignUpActivity extends Activity {

    private static final String EXTRA_PREVIEW_EMAIL = "preview_email";
    private static final int MINIMUM_PASSWORD_LENGTH = 6;

    private EditText fullNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;

    // create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        connectViews();
        setUpActions();
    }

    // read
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

    // validate input
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

        openSignInScreen(emailAddress);
    }

    // read
    private String readFullName() {
        return fullNameInput.getText().toString().trim();
    }

    // read
    private String readEmailAddress() {
        return emailInput.getText().toString().trim();
    }

    // read
    private String readPassword() {
        return passwordInput.getText().toString();
    }

    // read
    private String readConfirmedPassword() {
        return confirmPasswordInput.getText().toString();
    }

    // display output
    private void displayInputError(EditText inputField, String errorMessage) {
        inputField.setError(errorMessage);
        inputField.requestFocus();
    }

    private void openSignInScreen(String emailAddress) {
        Intent signInIntent = new Intent(this, LoginActivity.class);
        signInIntent.putExtra(EXTRA_PREVIEW_EMAIL, emailAddress);
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signInIntent);
        finish();
    }
}
