package gr.hua.dit.mycitygov.core.security;

import gr.hua.dit.mycitygov.core.model.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentUserProvider {

    public Optional<CurrentUser> getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof AppUserDetails userDetails) {
            return Optional.of(new CurrentUser(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getFullName(),
                    userDetails.getRole()
            ));
        }

        return Optional.empty();
    }

    public CurrentUser requireCurrentUser() {
        return this.getCurrentUser().orElseThrow(() -> new SecurityException("User not authenticated"));
    }

    public Long requireCitizenId() {
        final var currentUser = this.requireCurrentUser();
        if (currentUser.role() != Role.CITIZEN) {
            throw new SecurityException("Access denied: Citizen role required");
        }
        return currentUser.id();
    }
}