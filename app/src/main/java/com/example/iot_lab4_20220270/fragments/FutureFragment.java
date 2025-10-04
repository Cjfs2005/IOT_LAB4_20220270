package com.example.iot_lab4_20220270.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.iot_lab4_20220270.adapters.HourAdapter;
import com.example.iot_lab4_20220270.databinding.FragmentFutureBinding;
import com.example.iot_lab4_20220270.models.WeatherResponse;
import com.example.iot_lab4_20220270.WeatherApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FutureFragment extends Fragment {

    private FragmentFutureBinding binding;
    private HourAdapter adapter;
    private WeatherApiService weatherApiService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        weatherApiService = retrofit.create(WeatherApiService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFutureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSearchButton();
    }

    private void setupRecyclerView() {
        adapter = new HourAdapter(getContext());
        binding.rvFutureHours.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFutureHours.setAdapter(adapter);
    }

    private void setupSearchButton() {
        binding.btnBuscarFuturo.setOnClickListener(v -> {
            String locationQuery = binding.etIdLocacionFuture.getText().toString().trim();
            String dateStr = binding.etFechaInteres.getText().toString().trim();
            
            if (!locationQuery.isEmpty() && !dateStr.isEmpty()) {
                if (isValidDateFormat(dateStr)) {
                    searchWeatherForDate(locationQuery, dateStr);
                } else {
                    Toast.makeText(getContext(), "Formato de fecha incorrecto. Use YYYY-MM-DD", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidDateFormat(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void searchWeatherForDate(String locationQuery, String dateStr) {
        // Determinar si la fecha es futura o pasada
        Calendar today = Calendar.getInstance();
        Calendar searchDate = Calendar.getInstance();
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = sdf.parse(dateStr);
            searchDate.setTime(date);
            
            // Calcular diferencia en días
            long diffInMillis = searchDate.getTimeInMillis() - today.getTimeInMillis();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
            
            if (diffInDays >= 14 && diffInDays <= 300) {
                // Usar Future API
                getFutureWeather(locationQuery, dateStr);
            } else if (diffInDays < 0 && diffInDays >= -365) {
                // Usar History API
                getHistoryWeather(locationQuery, dateStr);
            } else {
                Toast.makeText(getContext(), "La fecha debe estar entre 1 año atrás y 300 días en el futuro", Toast.LENGTH_LONG).show();
            }
            
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Error procesando la fecha", Toast.LENGTH_SHORT).show();
        }
    }

    private void getFutureWeather(String locationQuery, String date) {
        Call<WeatherResponse> call = weatherApiService.getFutureWeather(
                WeatherApiService.API_KEY, 
                locationQuery, 
                date
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                handleWeatherResponse(response, "Future");
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                handleNetworkError(t);
            }
        });
    }

    private void getHistoryWeather(String locationQuery, String date) {
        Call<WeatherResponse> call = weatherApiService.getHistoryWeather(
                WeatherApiService.API_KEY, 
                locationQuery, 
                date
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                handleWeatherResponse(response, "History");
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                handleNetworkError(t);
            }
        });
    }

    private void handleWeatherResponse(Response<WeatherResponse> response, String apiType) {
        if (response.isSuccessful() && response.body() != null) {
            WeatherResponse weatherResponse = response.body();
            
            if (weatherResponse.getForecast() != null && 
                weatherResponse.getForecast().getForecastday() != null &&
                !weatherResponse.getForecast().getForecastday().isEmpty()) {
                
                // Obtener las horas del primer (y único) día
                if (weatherResponse.getForecast().getForecastday().get(0).getHour() != null) {
                    adapter.setHourList(
                        weatherResponse.getForecast().getForecastday().get(0).getHour(),
                        weatherResponse.getLocation()
                    );
                    
                    Log.d("FutureFragment", apiType + " weather loaded: " + 
                          weatherResponse.getForecast().getForecastday().get(0).getHour().size() + " hours");
                } else {
                    Toast.makeText(getContext(), "No se encontraron datos por horas", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "No se encontraron datos para la fecha especificada", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Error obteniendo datos: " + response.code(), Toast.LENGTH_SHORT).show();
            Log.e("FutureFragment", "Error: " + response.code() + " " + response.message());
        }
    }

    private void handleNetworkError(Throwable t) {
        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e("FutureFragment", "Network error", t);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}