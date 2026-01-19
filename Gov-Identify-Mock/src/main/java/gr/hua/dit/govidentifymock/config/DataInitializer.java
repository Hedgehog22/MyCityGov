package gr.hua.dit.govidentifymock.config;

import gr.hua.dit.govidentifymock.core.model.GovCitizen;
import gr.hua.dit.govidentifymock.core.repository.GovCitizenRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(GovCitizenRepository repository) {
        return args -> {
            repository.save(new GovCitizen("valid-token-111", "123456789", "01019012345", "Nikoletta", "Papadopoulou"));
            repository.save(new GovCitizen("valid-token-222", "987654321", "02028554321", "Yiannis", "Ioannidis"));
            System.out.println("Token 1: valid-token-111");
            System.out.println("Token 2: valid-token-222");
        };
    }
}