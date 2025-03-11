package com.simats.strokecare;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class notificationTask extends AsyncTask<String, Void, ArrayList<String>> {

    private static final String TAG = "notificationTask";
    private OnNotificationsFetchedListener mListener;

    public notificationTask(OnNotificationsFetchedListener listener) {
        this.mListener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> notifications = new ArrayList<>();

        try {
            // Create URL
            URL url = new URL("http://10.0.2.2/php/fetching_notification.php");

            // Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // Prepare the data to be sent
            String postData = "hospital_id=" + params[0]; // Assuming params[0] contains the HID

            // Write data to the connection
            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            // Get response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            String status = jsonResponse.getString("status");
            if (status.equals("success")) {
                JSONArray notificationsArray = jsonResponse.getJSONArray("notifications");
                for (int i = 0; i < notificationsArray.length(); i++) {
                    String notification = notificationsArray.getString(i);
                    notifications.add(notification);
                }
            } else {
                // Handle failure case
                Log.e(TAG, "Failed to fetch notifications: " + jsonResponse.optString("message", "Unknown error"));
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }

        return notifications;
    }

    @Override
    protected void onPostExecute(ArrayList<String> notifications) {
        mListener.onNotificationsFetched(notifications);
    }

    public interface OnNotificationsFetchedListener {
        void onNotificationsFetched(ArrayList<String> notifications);
    }
}
