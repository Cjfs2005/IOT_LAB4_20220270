package com.example.iot_lab4_20220270.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "ForecastFragment";
    private FragmentForecastBinding binding;
    private ForecastAdapter adapter;
    private WeatherApiService weatherApiService;
    
    // Sensor variables
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final double SHAKE_THRESHOLD = 10.0;
    private long lastShakeTime = 0;
    private static final int SHAKE_DELAY = 2000; // 2 seconds

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        weatherApiService = retrofit.create(WeatherApiService.class);
        
        // Inicializar sensor
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
        
        // Obtener Location del Bundle y cargar pronóstico
        Bundle args = getArguments();
        if (args != null) {
            Location selectedLocation = (Location) args.getSerializable("selected_location");
            if (selectedLocation != null) {
                Log.d(TAG, "Ubicación recibida: " + selectedLocation.getName());
                fetchForecast(selectedLocation);
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Registrar el sensor solo cuando el fragmento está activo
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Desregistrar el sensor cuando el fragmento no está activo
        sensorManager.unregisterListener(this);
    }

    private void setupRecyclerView() {
        adapter = new ForecastAdapter(getContext());
        binding.rvForecast.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvForecast.setAdapter(adapter);
    }

    private void setupSearchButton() {
        binding.btnBuscarPronostico.setOnClickListener(v -> {
            String locationQuery = binding.etIdLocacion.getText().toString().trim();
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
            binding.tvLocationInfo.setText("Locación: " + location.getName() + " - ID: " + location.getId());
            binding.etIdLocacion.setText("id:" + location.getId());
            binding.etDiasForecast.setText("7"); // Default 7 días
            
            // Obtener pronóstico automáticamente
            getForecast("id:" + location.getId(), 7);
        }
    }

    private void getForecast(String locationQuery, int days) {
        Call<WeatherResponse> call = weatherApiService.getForecast(
                WeatherApiService.API_KEY, 
                locationQuery, 
                days
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse.getForecast() != null && 
                        weatherResponse.getForecast().getForecastday() != null) {
                        
                        adapter.setForecastList(
                            weatherResponse.getForecast().getForecastday(),
                            weatherResponse.getLocation()
                        );
                        
                        Log.d("ForecastFragment", "Forecast loaded: " + 
                              weatherResponse.getForecast().getForecastday().size() + " days");
                    }
                } else {
                    Toast.makeText(getContext(), "Error obteniendo pronóstico: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("ForecastFragment", "Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ForecastFragment", "Network error", t);
            }
        });
    }

    // Implementación del SensorEventListener
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            
            // Calcular la magnitud del vector de aceleración
            double acceleration = Math.sqrt(x * x + y * y + z * z);
            
            // Detectar agitación
            if (acceleration > SHAKE_THRESHOLD) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShakeTime > SHAKE_DELAY) {
                    lastShakeTime = currentTime;
                    showShakeConfirmationDialog();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesitamos implementar esto
    }
    
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