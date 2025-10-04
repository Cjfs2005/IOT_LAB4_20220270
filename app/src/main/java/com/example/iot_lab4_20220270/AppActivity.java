package com.example.iot_lab4_20220270;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.iot_lab4_20220270.databinding.ActivityAppBinding;
import com.example.iot_lab4_20220270.models.Location;

public class AppActivity extends AppCompatActivity {

    private ActivityAppBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar Navigation Controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Configurar botones de navegación
        binding.btnLocations.setOnClickListener(v -> {
            navController.navigate(R.id.locationsFragment);
        });

        binding.btnPronostico.setOnClickListener(v -> {
            navController.navigate(R.id.forecastFragment);
        });

        binding.btnFuturo.setOnClickListener(v -> {
            navController.navigate(R.id.futureFragment);
        });

        // Configurar el manejo del botón de retroceso
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Si estamos en un fragmento específico, volver al MainActivity
                if (navController.getCurrentDestination() != null) {
                    int currentFragmentId = navController.getCurrentDestination().getId();
                    if (currentFragmentId == R.id.locationsFragment || 
                        currentFragmentId == R.id.forecastFragment || 
                        currentFragmentId == R.id.futureFragment) {
                        finish(); // Volver al MainActivity
                        return;
                    }
                }
                // Si no manejamos el evento, permitir el comportamiento por defecto
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }
    
    // Métodos públicos para comunicación entre fragments (según Clase 6.1)
    
    /**
     * Método llamado desde LocationsFragment al seleccionar una ubicación
     * Navega a ForecastFragment pasando la location por Bundle
     */
    public void navigateToForecast(Location location) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_location", location);
        navController.navigate(R.id.forecastFragment, bundle);
    }
    
    /**
     * Método llamado desde cualquier fragment para obtener el NavController
     */
    public NavController getNavController() {
        return navController;
    }
}