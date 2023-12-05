package com.lucianthomaz.alpr_application;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    @PUT("/api/user/update-location")
    Call<Void> sendLocation(@Body LocationData locationData);

    @PUT("/api/user/{id}/update-fcmtoken/{fcmtoken}")
    Call<Void> sendFcmToken(@Path("id") int id, @Path("fcmtoken") String fcmtoken);

    @POST("/api/user/login/")
    Call<Boolean> login(@Query("username") String username, @Query("password") String password);

    @PUT("/api/alert/alert-action/")
    Call<Void> alertAction(@Body AlertActionDto alertAction);

    @PUT("/api/alert/finish")
    Call<Void> alertCompletion(@Body AlertActionDto alertAction);

}
