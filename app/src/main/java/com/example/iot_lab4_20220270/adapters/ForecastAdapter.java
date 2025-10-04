package com.example.iot_lab4_20220270.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab4_20220270.R;
import com.example.iot_lab4_20220270.models.WeatherDay;
import com.example.iot_lab4_20220270.models.Location;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<WeatherDay> forecastList;
    private Location location;
    private Context context;

    public ForecastAdapter(Context context) {
        this.context = context;
    }

    public void setForecastList(List<WeatherDay> forecastList, Location location) {
        this.forecastList = forecastList;
        this.location = location;
        notifyDataSetChanged();
    }
    
    public void clearData() {
        if (forecastList != null) {
            forecastList.clear();
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        WeatherDay forecastDay = forecastList.get(position);

        holder.tvForecastDate.setText(forecastDay.getDate());
        
        if (location != null) {
            holder.tvForecastLocation.setText(location.getName());
            holder.tvForecastLocationId.setText("ID: " + location.getId());
        }

        // Usar métodos directos de WeatherDay
        holder.tvMaxTemp.setText(String.format("Máx: %.1f°C", forecastDay.getMaxTempC()));
        holder.tvMinTemp.setText(String.format("Mín: %.1f°C", forecastDay.getMinTempC()));
        
        if (forecastDay.getCondition() != null) {
            holder.tvCondition.setText(forecastDay.getCondition().getText());
        }
    }

    @Override
    public int getItemCount() {
        return forecastList != null ? forecastList.size() : 0;
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView tvForecastDate, tvForecastLocation, tvForecastLocationId;
        TextView tvMaxTemp, tvMinTemp, tvCondition;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            tvForecastDate = itemView.findViewById(R.id.tvForecastDate);
            tvForecastLocation = itemView.findViewById(R.id.tvForecastLocation);
            tvForecastLocationId = itemView.findViewById(R.id.tvForecastLocationId);
            tvMaxTemp = itemView.findViewById(R.id.tvMaxTemp);
            tvMinTemp = itemView.findViewById(R.id.tvMinTemp);
            tvCondition = itemView.findViewById(R.id.tvCondition);
        }
    }
}