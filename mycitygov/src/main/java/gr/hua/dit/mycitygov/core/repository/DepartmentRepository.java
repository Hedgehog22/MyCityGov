package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    long countByActiveTrue();
    Optional<Department> findByName(String name);
}