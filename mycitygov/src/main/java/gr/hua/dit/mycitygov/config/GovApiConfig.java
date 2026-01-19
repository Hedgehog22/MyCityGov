package gr.hua.dit.mycitygov.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GovApiConfig {
    @Value("${gov.api.url}")
    private String govBaseUrl;
    @Bean
    public RestTemplate govRestTemplate() {
        return new RestTemplate();
    }

    public String getGovBaseUrl() {
        return govBaseUrl;
    }
}