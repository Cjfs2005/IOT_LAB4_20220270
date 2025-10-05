package com.example.iot_lab4_20220270.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.iot_lab4_20220270.AppActivity;
import com.example.iot_lab4_20220270.R;
import com.example.iot_lab4_20220270.adapters.LocationsAdapter;
import com.example.iot_lab4_20220270.databinding.FragmentLocationsBinding;
import com.example.iot_lab4_20220270.models.Location;
import com.example.iot_lab4_20220270.WeatherApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationsFragment extends Fragment {

    private FragmentLocationsBinding binding;
    private LocationsAdapter adapter;
    private WeatherApiService weatherApiService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        weatherApiService = retrofit.create(WeatherApiService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLocationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSearchButton();
    }

    private void setupRecyclerView() {
        adapter = new LocationsAdapter(getContext());
        binding.rvLocations.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvLocations.setAdapter(adapter);

        adapter.setOnLocationClickListener(location -> {
            AppActivity appActivity = (AppActivity) getActivity();
            if (appActivity != null) {
                appActivity.navigateToForecast(location);
            }
        });
    }

    private void setupSearchButton() {
        binding.btnBuscarLocacion.setOnClickListener(v -> {
            String query = binding.etBuscarLocacion.getText().toString().trim();
            if (!query.isEmpty()) {
                searchLocations(query);
            } else {
                Toast.makeText(getContext(), "Ingrese una ubicación para buscar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchLocations(String query) {
        Call<List<Location>> call = weatherApiService.searchLocations(
                WeatherApiService.API_KEY, 
                query
        );

        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Location> locations = response.body();
                    if (!locations.isEmpty()) {
                        adapter.setLocationList(locations);
                    } else {
                        Toast.makeText(getContext(), "No se encontraron ubicaciones", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error en la búsqueda: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}