package gr.hua.dit.govidentifymock.web.rest;

import gr.hua.dit.govidentifymock.core.GovService;
import gr.hua.dit.govidentifymock.core.model.GovCitizen;
import gr.hua.dit.govidentifymock.core.model.GovCitizenDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/external/gov-login")
public class GovResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(GovResource.class);
    private final GovService govService;

    public GovResource(GovService govService) {
        this.govService = govService;
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<GovCitizenDto> verifyToken(@PathVariable String token) {
        LOGGER.info("Received verification request for token: {}", token);
        return govService.findCitizenByToken(token)
                .map(citizen -> {
                    LOGGER.info("Token found. Returning citizen: {}", citizen.getAfm());
                    return ResponseEntity.ok(mapToDto(citizen));
                })
                .orElseGet(() -> {
                    LOGGER.warn("Token NOT found: {}", token);
                    return ResponseEntity.notFound().build();
                });
    }

    private GovCitizenDto mapToDto(GovCitizen citizen) {
        return new GovCitizenDto(
                citizen.getAfm(),
                citizen.getAmka(),
                citizen.getFirstName(),
                citizen.getLastName()
        );
    }
}