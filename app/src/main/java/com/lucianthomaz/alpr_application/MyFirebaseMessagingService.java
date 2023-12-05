package com.lucianthomaz.alpr_application;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final int REQUEST_LOCATION_PERMISSION = 123;
    FusedLocationProviderClient fusedLocationClient;
    Retrofit retrofit;
    UserService userService;

    @Override
    public void onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://curly-space-fishstick-7v4p7x9jw424pg-8080.app.github.dev")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userService = retrofit.create(UserService.class);

        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("Wooohhoooooo!!!");
        // Check if the message contains data
        if (remoteMessage.getData().size() > 0) {

            try {
                // Extract the JSON string
                String locationDetails = remoteMessage.getData().get("locationDetails"); // Replace "key" with your data key
                // Parse the JSON string
//                JSONObject locationDetailsJson = new JSONObject(locationDetails);
//                String message = locationDetailsJson.get("message").toString();
//                double latitude = Double.parseDouble(locationDetailsJson.get("latitude").toString());
//                double longitude = Double.parseDouble(locationDetailsJson.get("longitude").toString());
//                String address = locationDetailsJson.get("address").toString();
//                String direction = locationDetailsJson.get("direction").toString();

                Intent intent = new Intent(this, AlertActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("alert_details", locationDetails); // Pass data to the activity
                startActivity(intent);

                // Extract data using jsonObject.get("your_key") etc.
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Check if the app has location permissions
            if (hasLocationPermission()) {
                // Retrieve location when permission is granted
                retrieveLocation(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        // Now you have the latitude and longitude of the current location.
                        // You can handle the location data as needed, e.g., send it to your server.
                        SharedPreferences sharedPreferences = getSharedPreferences("ALPR_Application_Preferences", Context.MODE_PRIVATE);
                        String alpr_username = sharedPreferences.getString("alpr_username", "");
                        sendLocationToServer(alpr_username, latitude, longitude);
                    }
                });
            } else {
                // Request location permission from the user
                requestLocationPermission();
            }
        }

    }

    private boolean hasLocationPermission() {
        // Check for location permission
        return ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        // Request location permission from the user, typically by showing a dialog.
        // You can use ActivityCompat.requestPermissions here.
    }

    private void retrieveLocation(OnSuccessListener<Location> locationCallback) {
        // Use FusedLocationProviderClient to get the location
        if (hasLocationPermission()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(locationCallback)
                    .addOnFailureListener(e -> {
                        // Unable to get location.
                        System.out.println("Unable to get location");
                        e.printStackTrace();
                    });
        } else {
            // Permission was not granted.
            System.out.println("Location permission not granted.");
        }
    }

    public void sendLocationToServer(String username, double latitude, double longitude) {
        // Create an object containing location data.
        LocationData locationData = new LocationData(username, latitude, longitude);

        // Make the call to send the location.
        Call<Void> call = userService.sendLocation(locationData);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Location sent successfully.
                System.out.println("Location sent successfully");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Unable to send location.
                System.out.println("Unable to send location");
            }
        });
    }
}
