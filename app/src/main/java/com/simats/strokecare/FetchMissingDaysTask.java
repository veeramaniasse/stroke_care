package com.simats.strokecare;

import static com.simats.strokecare.API.Track_Patient_URL;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

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

public class FetchMissingDaysTask extends AsyncTask<String, Void, ArrayList<String>> {

    private static final String TAG = "FetchMissingDaysTask";
    private OnMissingDaysFetchedListener mListener;

//    public FetchMissingDaysTask(OnMissingDaysFetchedListener listener) {
//        this.mListener = listener;
//    }
    private Context mContext;

    public FetchMissingDaysTask(Context context, OnMissingDaysFetchedListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> missingDays = new ArrayList<>();

        try {
            // Create URL
            URL url = new URL(Track_Patient_URL);

            // Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // Create the POST data
            String postData = "hospital_id=" + params[0];

            // Send POST data
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
            JSONObject dataObject = jsonResponse.optJSONObject("data");

            if (dataObject != null) {
                JSONArray missingDaysArray = dataObject.getJSONArray("missing_days");

                // Extract missing days
                for (int i = 0; i < missingDaysArray.length(); i++) {
                    JSONObject missingDayObject = missingDaysArray.getJSONObject(i);
                    String day = missingDayObject.getString("day");
                    String monthName = missingDayObject.getString("month_name");
                    String year = missingDayObject.getString("year");

                    // Create a string representing each missing day in the format: "day,month,year,additional_info"
                    String missingDay = day + "," + monthName + "," + year + "," ;
                    missingDays.add(missingDay);
                }
            }

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }

        return missingDays;
    }

//    @Override
//    protected void onPostExecute(ArrayList<String> missingDays) {
//        mListener.onMissingDaysFetched(missingDays);
//    }

    public interface OnMissingDaysFetchedListener {
        void onMissingDaysFetched(ArrayList<String> missingDays);
    }
    @Override
    protected void onPostExecute(ArrayList<String> missingDays) {
        if (missingDays.isEmpty()) {
            mListener.onMissingDaysFetched(missingDays);
            // Show alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Alert");
            builder.setMessage("No missing days found ");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            mListener.onMissingDaysFetched(missingDays);
        }
    }

}
