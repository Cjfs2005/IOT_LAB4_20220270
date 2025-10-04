package com.example.iot_lab4_20220270;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.iot_lab4_20220270.models.Location;

public class SharedViewModel extends ViewModel {
    
    private MutableLiveData<Location> selectedLocation = new MutableLiveData<>();
    
    public MutableLiveData<Location> getSelectedLocation() {
        return selectedLocation;
    }
    
    public void setSelectedLocation(Location location) {
        selectedLocation.setValue(location);
    }
}