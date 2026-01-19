package gr.hua.dit.mycitygov.core.repository;


import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByEmployeeIdOrderBySubmissionDateDesc(Long employeeId);

    List<Request> findByRequestTypeDepartmentIdAndEmployeeIsNullOrderBySubmissionDateDesc(Long departmentId);

    List<Request> findByCitizenIdOrderBySubmissionDateDesc(Long citizenId);

    List<Request> findByRequestTypeDepartmentIdOrderBySubmissionDateDesc(Long departmentId);

    List<Request> findByEmployeeId(Long employeeId);

    long countByStatus(RequestStatus status); // TO DO: for admin statistic

    Request findByProtocolNumber(String protocolNumber); // TO DO: searching of request
}
