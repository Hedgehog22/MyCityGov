package gr.hua.dit.mycitygov.core.port.impl;

import gr.hua.dit.mycitygov.core.port.HolidayProviderPort;
import gr.hua.dit.mycitygov.core.port.impl.dto.PublicHolidayDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

@Service
public class HolidayProviderPortImpl implements HolidayProviderPort {

    private final RestClient restClient;
    private static final String API_URL = "https://date.nager.at/api/v3/publicholidays/{year}/{countryCode}";

    public HolidayProviderPortImpl(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public boolean isHoliday(LocalDate date){
        try {
            List<PublicHolidayDto> holidays = restClient.get()
                    .uri(API_URL, date.getYear(), "GR")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<PublicHolidayDto>>() {});

            //our date in the list?
            if (holidays != null){
                for (PublicHolidayDto holiday : holidays) {
                    if (holiday.getDate().equals(date)) {
                        System.out.println("HOLIDAY DETECTED: " + holiday.getName() + " on " + date);
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("⚠WARNING: Failed to check external holiday API: " + e.getMessage());
            return false;
        }
    }
}