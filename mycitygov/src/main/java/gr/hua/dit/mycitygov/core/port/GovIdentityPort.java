package gr.hua.dit.mycitygov.core.port;

import gr.hua.dit.mycitygov.core.port.impl.dto.GovCitizenDto;

import java.util.Optional;

public interface GovIdentityPort {
    Optional<GovCitizenDto> getCitizenIdentity(String token);
    boolean isServiceAvailable();
}
