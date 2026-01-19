package gr.hua.dit.mycitygov.core.repository;


import gr.hua.dit.mycitygov.core.model.Role;
import gr.hua.dit.mycitygov.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByAfm(String afm);
    boolean existsByAmka(String amka);
    long countByRole(Role role);
    List<User> findByRole(Role role);
}