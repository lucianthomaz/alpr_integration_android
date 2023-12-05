package com.lucianthomaz.alpr_application;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 123;
    FusedLocationProviderClient fusedLocationClient;
    Button locationButton;
    Retrofit retrofit;
    UserService userService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fusedLocationClient =  LocationServices.getFusedLocationProviderClient(this);
        locationButton = findViewById(R.id.locationButton);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://curly-space-fishstick-7v4p7x9jw424pg-8080.app.github.dev") // Substitua pela URL do seu servidor
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userService = retrofit.create(UserService.class);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationPermission();
            }
        });
    }

    private void checkLocationPermission() {
        SharedPreferences sharedPreferences = getSharedPreferences("ALPR_Application_Preferences", Context.MODE_PRIVATE);
        String alpr_username = sharedPreferences.getString("alpr_username", "");
        System.out.println("ALPR_USERNAME: " + alpr_username);
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // The permission is already granted, you can proceed to get the location.
            getToken();
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        System.out.println("Location: "+ location);
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            // Agora você tem a latitude e longitude da localização atual.
                            // Você pode enviar esses dados para o seu servidor backend.
                            sendLocationToServer(alpr_username, latitude, longitude);
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        // Não foi possível obter a localização.
                        System.out.println("Não foi possível obter a localização");
                        e.printStackTrace();
                    });
            getLocation();
        } else {
            // Request location permission from the user.
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    public void sendLocationToServer(String username, double latitude, double longitude) {

        // Crie um objeto de modelo que contenha os dados de localização.
        LocationData locationData = new LocationData(username, latitude, longitude);

        // Faça a chamada para enviar a localização.
        Call<Void> call = userService.sendLocation(locationData);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // A localização foi enviada com sucesso.
                System.out.println("A localização foi enviada com sucesso");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Não foi possível enviar a localização.
                System.out.println("Não foi possível enviar a localização");
            }
        });
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = "Token: " + token;
                        System.out.println("!!!Token: " + token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}