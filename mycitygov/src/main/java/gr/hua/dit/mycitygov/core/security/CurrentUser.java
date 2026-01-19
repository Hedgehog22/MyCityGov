package gr.hua.dit.mycitygov.core.security;

import gr.hua.dit.mycitygov.core.model.Role;

public record CurrentUser(
        Long id,
        String email,
        String fullName,
        Role role
) {}