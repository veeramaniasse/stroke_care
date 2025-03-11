package com.simats.strokecare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class ToastUtils {
    public static void showToast(Context context, String message) {
        // Inflate the custom layout for the toast message
        View view = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(message);

        // Create and display the toast
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
