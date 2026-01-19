package gr.hua.dit.govidentifymock.core;

import gr.hua.dit.govidentifymock.core.model.GovCitizen;

import java.util.Optional;

public interface GovService {
    Optional<GovCitizen> findCitizenByToken(String token);
}
