package com.example.royalbeautyproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;
    private OnServiceSelectedListener onServiceSelectedListener;
    private List<Service> selectedServices = new ArrayList<>();

    public ServiceAdapter(List<Service> serviceList, OnServiceSelectedListener onServiceSelectedListener) {
        this.serviceList = serviceList;
        this.onServiceSelectedListener = onServiceSelectedListener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getName());
        holder.servicePrice.setText("Ksh " + service.getPrice());
        holder.serviceImage.setImageResource(service.getImageResId());
        holder.serviceCheckBox.setChecked(selectedServices.contains(service));

        holder.serviceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedServices.add(service);
            } else {
                selectedServices.remove(service);
            }
            onServiceSelectedListener.onServiceSelected(service, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public List<Service> getSelectedServices() {
        return selectedServices;
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        TextView serviceName, servicePrice;
        ImageView serviceImage;
        CheckBox serviceCheckBox;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.serviceName);
            servicePrice = itemView.findViewById(R.id.servicePrice);
            serviceImage = itemView.findViewById(R.id.serviceImage);
            serviceCheckBox = itemView.findViewById(R.id.serviceCheckBox);
        }
    }

    public interface OnServiceSelectedListener {
        void onServiceSelected(Service service, boolean isSelected);
    }
}
