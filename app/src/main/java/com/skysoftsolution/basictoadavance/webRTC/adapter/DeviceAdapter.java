package com.skysoftsolution.basictoadavance.webRTC.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skysoftsolution.basictoadavance.R;
import com.skysoftsolution.basictoadavance.webRTC.entity.DeviceItem;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private List<DeviceItem> devices;
    private DeviceClickListener clickListener;

    public interface DeviceClickListener {
        void onDeviceClick(String endpointId);
    }

    public DeviceAdapter(List<DeviceItem> devices, DeviceClickListener listener) {
        this.devices = devices;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        DeviceItem device = devices.get(position);
        holder.txtDeviceName.setText(device.getDeviceName());
        holder.btnConnect.setOnClickListener(v -> clickListener.onDeviceClick(device.getEndpointId()));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView txtDeviceName;
        Button btnConnect;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDeviceName = itemView.findViewById(R.id.txtDeviceName);
            btnConnect = itemView.findViewById(R.id.btnConnect);
        }
    }
}

