package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Optional<Attachment> findByStorageKey(String storageKey);
    List<Attachment> findByRequestId(Long requestId);
}