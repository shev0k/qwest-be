package com.qwest.backend.domain.geocoding;

import lombok.Data;
import java.util.List;

@Data
public class GeocodeResponse {
    private List<GeocodeResult> results;
}
