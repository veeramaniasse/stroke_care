
package com.simats.strokecare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

public class physio_adapter_patient extends RecyclerView.Adapter<physio_adapter_patient.ViewHolder> {

    private List<physio_patient_info> patientList;
    private Context context;

    public physio_adapter_patient(Context context, List<physio_patient_info> patientList) {
        this.context = context;
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.physio_pat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        physio_patient_info patient = patientList.get(position);
        holder.dayTextView.setText(patient.getDay());
        holder.monthTextView.setText(patient.getMonth());
        holder.yearTextView.setText(patient.getYear());

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem = patient.getDay() != null ? patient.getDay() : "";
                // Replace with your activity name
                Intent intent = new Intent(context, patient_details_doctor.class);
                intent.putExtra("Day", selectedItem);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;
        TextView monthTextView;
        TextView yearTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.day);
            monthTextView = itemView.findViewById(R.id.month);
            yearTextView = itemView.findViewById(R.id.year);
        }
    }

    public void filterList(List<physio_patient_info> filteredList) {
        patientList = filteredList;
        notifyDataSetChanged();
    }
}