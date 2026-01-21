package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.Role;
import gr.hua.dit.mycitygov.core.model.User;
import gr.hua.dit.mycitygov.core.port.GovIdentityPort;
import gr.hua.dit.mycitygov.core.repository.UserRepository;
import gr.hua.dit.mycitygov.core.service.UserService;
import gr.hua.dit.mycitygov.core.service.model.UserRegistrationDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GovIdentityPort govIdentityPort;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, GovIdentityPort govIdentityPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.govIdentityPort = govIdentityPort;
    }

    @Override
    @Transactional
    public void registerCitizen(UserRegistrationDto dto) {

        var trustedData = govIdentityPort.getCitizenIdentity(dto.getGovToken())
                .orElseThrow(() -> new RuntimeException("Security ERROR: wrong token tou TaxisNet!"));

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("email is already used.");
        }
        if (userRepository.existsByAfm(trustedData.afm())) {
            throw new RuntimeException("AFM is already used.");
        }
        if (userRepository.existsByAmka(trustedData.amka())) {
            throw new RuntimeException("AMKA is already used");
        }

        User user = new User();

        user.setAfm(trustedData.afm());
        user.setAmka(trustedData.amka());
        user.setFirstName(trustedData.firstName());
        user.setLastName(trustedData.lastName());

        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setRole(Role.CITIZEN);
        userRepository.save(user);
    }

    @Override
    public long countAllCitizens() {
        return userRepository.countByRole(Role.CITIZEN);
    }
}
