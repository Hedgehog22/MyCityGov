package gr.hua.dit.govidentifymock.core.impl;

import gr.hua.dit.govidentifymock.core.GovService;
import gr.hua.dit.govidentifymock.core.model.GovCitizen;
import gr.hua.dit.govidentifymock.core.repository.GovCitizenRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class GovServiceImpl implements GovService {

    private final GovCitizenRepository repository;

    public GovServiceImpl(GovCitizenRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<GovCitizen> findCitizenByToken(String token) {
        return repository.findByGovToken(token);
    }
}
