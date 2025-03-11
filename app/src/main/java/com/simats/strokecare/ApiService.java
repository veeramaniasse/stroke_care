package com.simats.strokecare;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("notify.php")  // Matches the PHP endpoint
    Call<ApiResponse> fetchNotifications(@Field("hospital_id") String HID);
}
