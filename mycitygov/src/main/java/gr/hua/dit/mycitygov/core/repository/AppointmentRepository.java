package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
  //  List<Appointment> findAllBySlotEmployeeIdAndSlotDateOrderBySlotStartTimeAsc(Long employeeId, LocalDate date);
    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.slot s " +
            "JOIN FETCH s.employee e " +
            "LEFT JOIN FETCH e.department " +
            "WHERE a.citizen.id = :citizenId " +
            "ORDER BY s.date DESC, s.startTime DESC")
    List<Appointment> findByCitizenId(@Param("citizenId") Long citizenId);
    
    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.slot s " +
            "JOIN FETCH a.citizen c " +
            "WHERE s.employee.id = :employeeId " +
            "ORDER BY s.date ASC, s.startTime ASC")
    List<Appointment> findAllForEmployee(@Param("employeeId") Long employeeId);

    List<Appointment> findAllByStatusAndSlotDateBefore(AppointmentStatus status, LocalDate date);

}