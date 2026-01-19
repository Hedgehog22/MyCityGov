package gr.hua.dit.mycitygov.core.security;

import gr.hua.dit.mycitygov.core.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AppUserDetails extends User {

    private final Long id;
    private final Role role;
    private final String fullName;

    public AppUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
                          Long id, Role role, String fullName) {
        super(username, password, authorities);
        this.id = id;
        this.role = role;
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }
}