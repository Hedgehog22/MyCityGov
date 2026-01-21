package gr.hua.dit.mycitygov.core.port.impl;

import gr.hua.dit.mycitygov.config.GovApiConfig;
import gr.hua.dit.mycitygov.core.port.GovIdentityPort;
import gr.hua.dit.mycitygov.core.port.impl.dto.GovCitizenDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class GovIdentityPortImpl implements GovIdentityPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(GovIdentityPortImpl.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;
    @Value("${gov.api.url}")
    private String govApiUrl;

    public GovIdentityPortImpl(RestTemplate govRestTemplate, GovApiConfig config) {
        this.restTemplate = govRestTemplate;
        this.baseUrl = config.getGovBaseUrl();
    }

    @Override
    public Optional<GovCitizenDto> getCitizenIdentity(String token) {
        String url = baseUrl + "/verify/" + token;
        LOGGER.info("GOV: Connecting: {}", url);

        try {
            GovCitizenDto response = restTemplate.getForObject(url, GovCitizenDto.class);

            LOGGER.info("GOV ADAPTER: Success!For AFM: {}", response.afm());
            return Optional.ofNullable(response);
        } catch (HttpClientErrorException.NotFound e) {
            LOGGER.warn("GOV ADAPTER: Token not found in external system.");
            return Optional.empty();
        }
    }

    @Override
    public boolean isServiceAvailable() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000); // 2000 ms
        factory.setReadTimeout(2000);

        RestTemplate checkTemplate = new RestTemplate(factory);

        try {
            String healthCheckUrl = "http://localhost:8081/auth/login";
            checkTemplate.getForEntity(healthCheckUrl, String.class);
            return true; //service alive
        } catch (RestClientException e) {
            LOGGER.error("Gov Service is down: {}", e.getMessage());
            return false;
        }
    }
}