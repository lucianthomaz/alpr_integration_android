package com.lucianthomaz.alpr_application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlertActivity extends Activity {

    private TextView tvMessage;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvAddress;
    private TextView tvDirection;
    private Button acceptBtn;
    private Button rejectBtn;
    private Button finishBtn;
    Retrofit retrofit;
    UserService userService;
    TextView tvUser;
    TextView tvDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        Intent intent = getIntent();
        String data = intent.getStringExtra("alert_details");
        tvUser = findViewById(R.id.tvUser);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://curly-space-fishstick-7v4p7x9jw424pg-8080.app.github.dev") // Substitua pela URL do seu servidor
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userService = retrofit.create(UserService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("ALPR_Application_Preferences", Context.MODE_PRIVATE);
        String alpr_username = sharedPreferences.getString("alpr_username", "");
        tvUser.setText(alpr_username);

        try {
            JSONObject locationDetailsJson = new JSONObject(data);

            tvMessage = findViewById(R.id.tvMessage);
            tvLatitude = findViewById(R.id.tvLatitude);
            tvLongitude = findViewById(R.id.tvLongitude);
            tvAddress = findViewById(R.id.tvAddress);
            tvDirection = findViewById(R.id.tvDirection);
            tvDetails = findViewById(R.id.tvDetails);
            acceptBtn = findViewById(R.id.acceptBtn);
            rejectBtn = findViewById(R.id.rejectBtn);
            finishBtn = findViewById(R.id.finishBtn);

//            tvMessage.setText(locationDetailsJson.get("message").toString());
//            tvMessage.setText("Um veículo em situação irregular acabou de ser detectado no local" +
//                    " abaixo, e você é o policial mais próximo dele!");
            tvLatitude.setText(locationDetailsJson.get("latitude").toString());
            tvLongitude.setText(locationDetailsJson.get("longitude").toString());
            tvAddress.setText(locationDetailsJson.get("address").toString());
            tvDirection.setText(locationDetailsJson.get("direction").toString());
            tvDetails.setText(locationDetailsJson.get("details").toString());
            int alertId = locationDetailsJson.getInt("alertId");
            int userId = locationDetailsJson.getInt("userId");

            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptBtn.setEnabled(false);
                    rejectBtn.setEnabled(false);
                    finishBtn.setEnabled(true);
                    alertAction(alertId, userId, true);
                    Toast.makeText(AlertActivity.this,
                            "Alerta aceito!", Toast.LENGTH_LONG).show();
                }
            });

            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptBtn.setEnabled(false);
                    rejectBtn.setEnabled(false);
                    finishBtn.setEnabled(false);
                    alertAction(alertId, userId, false);
                    Toast.makeText(AlertActivity.this,
                            "Alerta rejeitado!", Toast.LENGTH_LONG).show();
                }
            });

            finishBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptBtn.setEnabled(false);
                    rejectBtn.setEnabled(false);
                    finishBtn.setEnabled(false);
                    finishAlert(alertId, userId);
                    Toast.makeText(AlertActivity.this,
                            "Alerta concluído!", Toast.LENGTH_LONG).show();
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void alertAction(int alertId, int userId, boolean accepted) {

        // Crie um objeto de modelo que contenha os dados de localização.
        AlertActionDto alertActionDto = new AlertActionDto(alertId, userId, accepted);

        // Faça a chamada para enviar a localização.
        Call<Void> call = userService.alertAction(alertActionDto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // A localização foi enviada com sucesso.
                System.out.println("Status do alerta alterado com sucesso para: " + accepted);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Não foi possível enviar a localização.
                System.out.println("Não foi possível atualizar o status do alerta!!!");
            }
        });
    }
    public void finishAlert(int alertId, int userId) {

        // Crie um objeto de modelo que contenha os dados de localização.
        AlertActionDto alertActionDto = new AlertActionDto(alertId, userId, null);

        // Faça a chamada para enviar a localização.
        Call<Void> call = userService.alertCompletion(alertActionDto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // A localização foi enviada com sucesso.
                System.out.println("Alerta concluído com sucesso!");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Não foi possível enviar a localização.
                System.out.println("Não foi possível concluir o alerta!!!");
            }
        });
    }
}
