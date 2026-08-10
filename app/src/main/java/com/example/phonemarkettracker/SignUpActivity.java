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

public class SignUpActivity extends Activity {

    private DatabaseSQL databaseSQL;
    private EditText editUsername;
    private EditText editPassword;
    private EditText editConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        databaseSQL = new DatabaseSQL(this);
        editUsername = findViewById(R.id.editSignUpUsername);
        editPassword = findViewById(R.id.editSignUpPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        TextView textBackToSignIn = findViewById(R.id.textBackToSignIn);

        buttonCreateAccount.setOnClickListener(view -> createAccount());
        textBackToSignIn.setOnClickListener(view -> finish());
        editConfirmPassword.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                createAccount();
                return true;
            }
            return false;
        });
    }

    private void createAccount() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString();
        String confirmation = editConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            editUsername.setError("Enter a username");
            editUsername.requestFocus();
            return;
        }
        if (!username.matches("[A-Za-z0-9_]{3,20}")) {
            editUsername.setError("Use 3–20 letters, numbers, or underscores");
            editUsername.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editPassword.setError("Password must contain at least 6 characters");
            editPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmation)) {
            editConfirmPassword.setError("Passwords do not match");
            editConfirmPassword.requestFocus();
            return;
        }
        if (databaseSQL.usernameExists(username)) {
            editUsername.setError("This username is already registered");
            editUsername.requestFocus();
            return;
        }
        if (!databaseSQL.registerUser(username, password)) {
            Toast.makeText(this, "Account could not be created", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Account created. You can sign in now.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("registered_username", username);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        databaseSQL.close();
        super.onDestroy();
    }
}
