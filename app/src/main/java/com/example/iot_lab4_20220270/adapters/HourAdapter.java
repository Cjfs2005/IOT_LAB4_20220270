package com.example.iot_lab4_20220270.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab4_20220270.R;
import com.example.iot_lab4_20220270.models.Hour;
import com.example.iot_lab4_20220270.models.Location;

import java.util.List;

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

    private List<Hour> hourList;
    private Location location;
    private Context context;

    public HourAdapter(Context context) {
        this.context = context;
    }

    public void setHourList(List<Hour> hourList, Location location) {
        this.hourList = hourList;
        this.location = location;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hour, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        Hour hour = hourList.get(position);

        holder.tvHourTime.setText(hour.getTime());
        holder.tvHourTemp.setText(String.format("%.1f°C", hour.getTempC()));
        
        if (location != null) {
            holder.tvHourLocationInfo.setText(location.getName() + " - ID: " + location.getId());
        }
        
        if (hour.getCondition() != null) {
            holder.tvHourCondition.setText(hour.getCondition().getText());
        }
        
        holder.tvHourHumidity.setText(String.format("Humedad: %d%%", hour.getHumidity()));
        holder.tvHourRain.setText(String.format("Lluvia: %d%%", hour.getChanceOfRain()));
        holder.tvHourWind.setText(String.format("Viento: %.1f km/h", hour.getWindKph()));
    }

    @Override
    public int getItemCount() {
        return hourList != null ? hourList.size() : 0;
    }

    public class HourViewHolder extends RecyclerView.ViewHolder {
        TextView tvHourTime, tvHourTemp, tvHourLocationInfo;
        TextView tvHourCondition, tvHourHumidity, tvHourRain, tvHourWind;

        public HourViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHourTime = itemView.findViewById(R.id.tvHourTime);
            tvHourTemp = itemView.findViewById(R.id.tvHourTemp);
            tvHourLocationInfo = itemView.findViewById(R.id.tvHourLocationInfo);
            tvHourCondition = itemView.findViewById(R.id.tvHourCondition);
            tvHourHumidity = itemView.findViewById(R.id.tvHourHumidity);
            tvHourRain = itemView.findViewById(R.id.tvHourRain);
            tvHourWind = itemView.findViewById(R.id.tvHourWind);
        }
    }
}