package com.qwest.backend.domain.geocoding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.qwest.backend.configuration.AppConfig;

@Service
public class GeocodingService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LatLng getLatLngForAddress(String address) {
        String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";
        String finalUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("address", address)
                .queryParam("key", apiKey)
                .toUriString();

        ResponseEntity<GeocodeResponse> response = restTemplate.getForEntity(finalUrl, GeocodeResponse.class);

        if (response.getBody() != null && !response.getBody().getResults().isEmpty()) {
            GeocodeResult result = response.getBody().getResults().get(0);
            GeocodeLocation location = result.getGeometry().getLocation();
            return new LatLng(location.getLat(), location.getLng());
        } else {
            throw new IllegalStateException("Could not fetch location for address: " + address);
        }
    }
}
