package com.qwest.backend.domain.geocoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class GeocodingService {

    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);

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
        GeocodeResponse responseBody = response.getBody();

        if (responseBody == null || responseBody.getResults().isEmpty()) {
            logger.error("No results found for address '{}'. Response: {}", address, responseBody);
            throw new IllegalStateException("No geocoding results for address: " + address);
        }

        GeocodeResult result = responseBody.getResults().get(0);
        if (result.getGeometry() == null || result.getGeometry().getLocation() == null) {
            logger.error("Invalid geometry data for address '{}'.", address);
            throw new IllegalStateException("Invalid geometry data for address: " + address);
        }

        GeocodeLocation location = result.getGeometry().getLocation();
        return new LatLng(location.getLat(), location.getLng());
    }
}
