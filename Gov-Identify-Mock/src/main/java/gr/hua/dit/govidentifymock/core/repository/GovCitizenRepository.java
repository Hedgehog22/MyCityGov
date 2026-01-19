package gr.hua.dit.govidentifymock.core.repository;

import gr.hua.dit.govidentifymock.core.model.GovCitizen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GovCitizenRepository extends JpaRepository<GovCitizen, Long> {
    Optional<GovCitizen> findByGovToken(String govToken);
}
