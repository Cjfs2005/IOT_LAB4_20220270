package com.example.iot_lab4_20220270.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FutureFragment extends Fragment {

    private FragmentFutureBinding binding;
    private HourAdapter adapter;
    private WeatherApiService weatherApiService;
    private Integer lastSearchedId = null;

/*
Modelo: GPT-5 (en modo Ask usando Github Copilot para que reciba contexto del proyecto)
Prompt: "Necesito la lógica y código en un Fragment para decidir entre llamar future.json (14 a 300 días en el futuro) o history.json (1 a 365 días en el pasado) de WeatherAPI, validando que 0 días (hoy) y 1-13 días futuros se indiquen como caso de Forecast existente. Incluir validaciones con y mostrar mensajes Toast claros en español y sólo proceder a la llamada correcta." 
Correcciones: "Se mantuvo la estructura propuesta casi intacta. Se ajustó el mensaje para rango inválido consolidando ambos límites (pasado 365, futuro 14-300). Se añadió el ID si la respuesta no lo trae y se forzó Input numérico en validación." 
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
    setupButtons();
    }

    private void setupRecyclerView() {
        adapter = new HourAdapter(getContext());
        binding.rvFutureHours.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFutureHours.setAdapter(adapter);
    }

    private void setupButtons() {
        binding.etFechaInteres.setFocusable(true);
        binding.etFechaInteres.setFocusableInTouchMode(true);
        View.OnClickListener dateClickListener = v -> {
            java.time.LocalDate today = java.time.LocalDate.now(java.time.ZoneId.of("America/Lima"));
            DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                int realMonth = month + 1;
                String formatted = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, realMonth, dayOfMonth);
                binding.etFechaInteres.setText(formatted);
            }, today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth());
            dialog.show();
        };
        binding.etFechaInteres.setOnClickListener(dateClickListener);
        binding.etFechaInteres.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) dateClickListener.onClick(v); });
        binding.btnBuscarFuturo.setOnClickListener(v -> {
            String idInput = binding.etIdLocacionFuture.getText().toString().trim();
            String dateStr = binding.etFechaInteres.getText().toString().trim();

            if (idInput.isEmpty()) {
                Toast.makeText(getContext(), "Ingrese ID de locación", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!idInput.matches("\\d+")) {
                Toast.makeText(getContext(), "Sólo se acepta ID numérico", Toast.LENGTH_SHORT).show();
                return;
            }
            String locationQuery = "id:" + idInput;
            try { lastSearchedId = Integer.parseInt(idInput); } catch (NumberFormatException ignored) {}

            if (!dateStr.isEmpty()) {
                searchWeatherForDate(locationQuery, dateStr);
            } else {
                Toast.makeText(getContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchWeatherForDate(String locationQuery, String dateStr) {
        try {
            LocalDate today = LocalDate.now(java.time.ZoneId.of("America/Lima"));
            LocalDate target = LocalDate.parse(dateStr);
            long diffInDays = ChronoUnit.DAYS.between(today, target);

            if (diffInDays == 0) {
                Toast.makeText(getContext(), "Hoy: use Pronóstico (Forecast)", Toast.LENGTH_LONG).show();
            } else if (diffInDays > 0 && diffInDays < 14) {
                Toast.makeText(getContext(), "1-13 días: use Pronóstico (Forecast)", Toast.LENGTH_LONG).show();
            } else if (diffInDays >= 14 && diffInDays <= 300) {
                getFutureWeather(locationQuery, dateStr);
            } else if (diffInDays < 0 && diffInDays >= -365) {
                getHistoryWeather(locationQuery, dateStr);
            } else {
                Toast.makeText(getContext(), "Rango válido: Pasado hasta 365 días | Futuro 14-300 días", Toast.LENGTH_LONG).show();
            }
        } catch (DateTimeParseException e) {
            Toast.makeText(getContext(), "Formato de fecha incorrecto (YYYY-MM-DD)", Toast.LENGTH_SHORT).show();
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
                
                if (weatherResponse.getForecast().getForecastday().get(0).getHour() != null) {
                    if (weatherResponse.getLocation() != null && weatherResponse.getLocation().getId() == 0 && lastSearchedId != null) {
                        weatherResponse.getLocation().setId(lastSearchedId);
                    }
                    adapter.setHourList(
                        weatherResponse.getForecast().getForecastday().get(0).getHour(),
                        weatherResponse.getLocation()
                    );
                    
                } else {
                    Toast.makeText(getContext(), "No se encontraron datos por horas", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "No se encontraron datos para la fecha especificada", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Error obteniendo datos: " + response.code(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNetworkError(Throwable t) {
        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}