package gr.hua.dit.mycitygov.core.port.impl;

import gr.hua.dit.mycitygov.core.port.NotificationProviderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Service
public class TwilioSmsImpl implements NotificationProviderPort {

    private final RestClient restClient;

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String fromPhoneNumber;

    private static final String TWILIO_URL = "https://api.twilio.com/2010-04-01/Accounts/{sid}/Messages.json";
    public TwilioSmsImpl(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public boolean sendNotification(String recipient, String message) {
        try {
            String authStr = accountSid + ":" + authToken;
            String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("To", recipient);
            formData.add("From", fromPhoneNumber);
            formData.add("Body", message);

            String response = restClient.post()
                    .uri(TWILIO_URL, accountSid)
                    .header("Authorization", "Basic " + base64Creds)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(String.class);

            System.out.println("SMS SENT! Response: " + response);
            return true;
        } catch (Exception e) {
            System.err.println("FAILED to send SMS via Twilio REST: " + e.getMessage());
            return false;
        }
    }
}