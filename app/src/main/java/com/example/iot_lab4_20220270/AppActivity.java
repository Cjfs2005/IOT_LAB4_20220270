package com.example.iot_lab4_20220270;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavOptions;

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

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        binding.btnLocations.setOnClickListener(v -> navigateTopLevel(R.id.locationsFragment));
        binding.btnPronostico.setOnClickListener(v -> navigateTopLevel(R.id.forecastFragment));
        binding.btnFuturo.setOnClickListener(v -> navigateTopLevel(R.id.futureFragment));

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            updateNavigationHighlight(destination.getId());
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination() != null) {
                    int currentFragmentId = navController.getCurrentDestination().getId();
                    if (currentFragmentId == R.id.locationsFragment || 
                        currentFragmentId == R.id.forecastFragment || 
                        currentFragmentId == R.id.futureFragment) {
                        finish();
                        return;
                    }
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    public void navigateToForecast(Location location) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_location", location);
        NavOptions opts = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(navController.getGraph().getId(), false)
                .build();
        navController.navigate(R.id.forecastFragment, bundle, opts);
    }

    private void updateNavigationHighlight(int destinationId) {
        binding.btnLocations.setSelected(false);
        binding.btnPronostico.setSelected(false);
        binding.btnFuturo.setSelected(false);

        if (destinationId == R.id.locationsFragment) {
            binding.btnLocations.setSelected(true);
        } else if (destinationId == R.id.forecastFragment) {
            binding.btnPronostico.setSelected(true);
        } else if (destinationId == R.id.futureFragment) {
            binding.btnFuturo.setSelected(true);
        }
    }
    public NavController getNavController() {
        return navController;
    }

    private void navigateTopLevel(int destinationId) {
        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == destinationId) {
            return;
        }
        NavOptions opts = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(navController.getGraph().getId(), false)
                .build();
        navController.navigate(destinationId, null, opts);
    }
}