package com.qwest.backend.domain.geocoding;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LatLng {
    private double latitude;
    private double longitude;
}
