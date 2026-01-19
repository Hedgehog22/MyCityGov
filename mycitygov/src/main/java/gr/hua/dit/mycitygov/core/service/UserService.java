package gr.hua.dit.mycitygov.core.service;

import gr.hua.dit.mycitygov.core.service.model.UserRegistrationDto;

public interface UserService {
    void registerCitizen(UserRegistrationDto registrationDto);
    long countAllCitizens();
}
