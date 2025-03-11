package com.simats.strokecare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class MissingDaysAdapter extends RecyclerView.Adapter<MissingDaysAdapter.ViewHolder> {

    private ArrayList<String> missingDays;

    public MissingDaysAdapter(ArrayList<String> missingDays) {
        this.missingDays = missingDays;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_patient_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] dayDetails = missingDays.get(position).split(",");
        holder.bind(dayDetails);
    }

    @Override
    public int getItemCount() {
        return missingDays.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dayTextView;
        private TextView monthTextView;
        private TextView yearTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.day);
            monthTextView = itemView.findViewById(R.id.month);
            yearTextView = itemView.findViewById(R.id.year);

        }

        public void bind(String[] dayDetails) {
            dayTextView.setText(dayDetails[0]);
            monthTextView.setText(dayDetails[1]);
            yearTextView.setText(dayDetails[2]);

        }
    }
}
