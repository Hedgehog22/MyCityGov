package gr.hua.dit.mycitygov.core.repository;
import gr.hua.dit.mycitygov.core.model.RequestType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestTypeRepository extends JpaRepository<RequestType, Long> {
    @EntityGraph(attributePaths = "department")
    List<RequestType> findAll();
    List<RequestType> findAllByActiveTrue();
    Optional<RequestType> findByName(String name);
}