package com.lucianthomaz.alpr_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    Retrofit retrofit;
    UserService userService;
    private boolean authResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://curly-space-fishstick-7v4p7x9jw424pg-8080.app.github.dev") // Substitua pela URL do seu servidor
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userService = retrofit.create(UserService.class);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle login button click
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                // Perform authentication (e.g., check credentials against a database)
                if (authenticateUser(username, password)) {
                    // Successful login, navigate to the main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Optional: Finish the LoginActivity
                } else {
                    // Invalid credentials, show an error message
                    // You can display an error message or handle it as needed
//                    Toast.makeText(LoginActivity.this, "Incorrect username or password. Please, try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Perform user authentication here (e.g., check against a database)
    private boolean authenticateUser(String username, String password) {
        // Implement your authentication logic here
        // Return true if authentication is successful, false otherwise
        userService.login(username, password);
        // Faça a chamada para enviar a localização.
        Call<Boolean> call = userService.login(username, password);

//        final Boolean[] authResponse = new Boolean[1];

        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    authResult = response.body();
                    SharedPreferences sharedPreferences = getSharedPreferences("ALPR_Application_Preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("alpr_username", username);
                    editor.apply();
                } else authResult = false;
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                System.out.println("Não foi possível realizar o login");
                authResult = false;
            }
        });
        return authResult;
    }
}