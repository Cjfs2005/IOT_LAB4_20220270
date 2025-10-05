package com.example.iot_lab4_20220270.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.iot_lab4_20220270.R;
import com.example.iot_lab4_20220270.adapters.ForecastAdapter;
import com.example.iot_lab4_20220270.databinding.FragmentForecastBinding;
import com.example.iot_lab4_20220270.models.WeatherResponse;
import com.example.iot_lab4_20220270.models.Location;
import com.example.iot_lab4_20220270.WeatherApiService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastFragment extends Fragment implements SensorEventListener {

    private FragmentForecastBinding binding;
    private ForecastAdapter adapter;
    private WeatherApiService weatherApiService;
    
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final double SHAKE_THRESHOLD = 14.0;
    private long lastShakeTime = 0;
    private static final int SHAKE_DELAY = 2500;
    private static final float ALPHA = 0.8f;
    private float[] gravity = new float[3];
    private Integer lastSearchedId = null;

/*
Modelo: GPT-5 (en modo Ask usando Github Copilot para que reciba contexto del proyecto)
Prompt: "Necesito el código para manejar el acelerómetro en un Fragment (Forecast) que detecte una agitación por encima de un umbral y pida confirmación antes de limpiar la lista del RecyclerView. Debe usar únicamente sensores básicos (SensorManager, TYPE_ACCELEROMETER), ajustar un umbral suficientemente alto para evitar falsos positivos y tomar las consideraciones adicionales necesarias para que funcione correctamente"
Correcciones: "Se ajustó el umbral a 14.0 tras pruebas. Se agregó un SHAKE_DELAY para evitar múltiples apariciones del diálogo de confirmación. Se simplificó el filtrado manteniendo ALPHA=0.8." 
*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(com.example.iot_lab4_20220270.models.WeatherDay.class, new com.example.iot_lab4_20220270.models.WeatherDay.Deserializer())
        .create();

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(WeatherApiService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
        
        weatherApiService = retrofit.create(WeatherApiService.class);
        
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSearchButton();
        
        Bundle args = getArguments();
        if (args != null) {
            Location selectedLocation = (Location) args.getSerializable("selected_location");
            if (selectedLocation != null) {
                fetchForecast(selectedLocation);
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void setupRecyclerView() {
        adapter = new ForecastAdapter(getContext());
        binding.rvForecast.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvForecast.setAdapter(adapter);
    }

    private void setupSearchButton() {
        binding.btnBuscarPronostico.setOnClickListener(v -> {
            String rawInput = binding.etIdLocacion.getText().toString().trim();
            if (rawInput.isEmpty()) {
                Toast.makeText(getContext(), "Ingrese ID de locación", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!rawInput.matches("\\d+")) {
                Toast.makeText(getContext(), "Sólo se acepta ID numérico", Toast.LENGTH_SHORT).show();
                return;
            }
            String locationQuery = "id:" + rawInput;
            try { lastSearchedId = Integer.parseInt(rawInput); } catch (NumberFormatException ignored) {}

            String daysStr = binding.etDiasForecast.getText().toString().trim();
            if (!locationQuery.isEmpty() && !daysStr.isEmpty()) {
                try {
                    int days = Integer.parseInt(daysStr);
                    if (days >= 1 && days <= 14) {
                        getForecast(locationQuery, days);
                    } else {
                        Toast.makeText(getContext(), "Los días deben estar entre 1 y 14", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Ingrese un número válido de días", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchForecast(Location location) {
        if (location != null) {
            binding.etIdLocacion.setText(String.valueOf(location.getId()));
            binding.etDiasForecast.setText("14");
            String normalized = "id:" + location.getId();
            lastSearchedId = location.getId();
            getForecast(normalized, 14);
        }
    }

    private void getForecast(String locationQuery, int days) {
        String queryToSend = locationQuery.trim();
        if (queryToSend.matches("\\d+")) {
            queryToSend = "id:" + queryToSend;
        }

        Call<WeatherResponse> call = weatherApiService.getForecast(
                WeatherApiService.API_KEY,
                queryToSend,
                days
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse.getForecast() != null && 
                        weatherResponse.getForecast().getForecastday() != null) {
                        if (weatherResponse.getLocation() != null && weatherResponse.getLocation().getId() == 0 && lastSearchedId != null) {
                            weatherResponse.getLocation().setId(lastSearchedId);
                        }
                        
                        adapter.setForecastList(
                            weatherResponse.getForecast().getForecastday(),
                            weatherResponse.getLocation()
                        );
                        
                    }
                } else {
                    Toast.makeText(getContext(), "Error obteniendo pronóstico: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
            gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
            gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

            float linearX = x - gravity[0];
            float linearY = y - gravity[1];
            float linearZ = z - gravity[2];

            double linearAcceleration = Math.sqrt(linearX * linearX + linearY * linearY + linearZ * linearZ);

            if (linearAcceleration > SHAKE_THRESHOLD) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShakeTime > SHAKE_DELAY) {
                    lastShakeTime = currentTime;
                    showShakeConfirmationDialog();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    
    private void showShakeConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Borrar Pronósticos")
                .setMessage(R.string.confirmacion_borrar)
                .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.clearData();
                        Toast.makeText(getContext(), "Pronósticos eliminados", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}