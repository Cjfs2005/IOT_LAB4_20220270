package com.example.iot_lab4_20220270.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab4_20220270.R;
import com.example.iot_lab4_20220270.models.Location;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationViewHolder> {

    private List<Location> locationList;
    private Context context;
    private OnLocationClickListener listener;

    public interface OnLocationClickListener {
        void onLocationClick(Location location);
    }

    public LocationsAdapter(Context context) {
        this.context = context;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
        notifyDataSetChanged();
    }

    public void setOnLocationClickListener(OnLocationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locationList.get(position);
        holder.location = location;

        holder.tvLocationName.setText(location.getName());
        holder.tvLocationDetails.setText(location.getRegion() + ", " + location.getCountry());
        holder.tvLocationId.setText("ID: " + location.getId());
        holder.tvLocationCoords.setText(String.format("Lat: %.2f, Lon: %.2f", 
                location.getLat(), location.getLon()));
    }

    @Override
    public int getItemCount() {
        return locationList != null ? locationList.size() : 0;
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        Location location;
        TextView tvLocationName, tvLocationDetails, tvLocationId, tvLocationCoords;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvLocationDetails = itemView.findViewById(R.id.tvLocationDetails);
            tvLocationId = itemView.findViewById(R.id.tvLocationId);
            tvLocationCoords = itemView.findViewById(R.id.tvLocationCoords);

            itemView.setOnClickListener(v -> {
                if (listener != null && location != null) {
                    listener.onLocationClick(location);
                }
            });
        }
    }
}