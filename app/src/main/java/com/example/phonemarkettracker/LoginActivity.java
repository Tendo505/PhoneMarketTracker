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

public class LoginActivity extends Activity {

    private EditText editUsername;
    private EditText editPassword;
    private DatabaseSQL databaseSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseSQL = new DatabaseSQL(this);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        TextView textSignUp = findViewById(R.id.textSignUp);

        String registeredUsername = getIntent().getStringExtra("registered_username");
        if (registeredUsername != null) {
            editUsername.setText(registeredUsername);
            editPassword.requestFocus();
        }

        buttonLogin.setOnClickListener(view -> attemptLogin());
        textSignUp.setOnClickListener(view ->
                startActivity(new Intent(this, SignUpActivity.class)));
        editPassword.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });
    }

    private void attemptLogin() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            editUsername.setError("Enter your username");
            editUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Enter your password");
            editPassword.requestFocus();
            return;
        }

        if (databaseSQL.authenticate(username, password)) {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CartActivity.class));
            finish();
        } else {
            editPassword.setText("");
            editPassword.setError("Incorrect username or password");
            editPassword.requestFocus();
        }
    }

    @Override
    protected void onDestroy() {
        databaseSQL.close();
        super.onDestroy();
    }
}
