package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.AppointmentSlot;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    @Query("SELECT DISTINCT s.startTime FROM AppointmentSlot s " +
            "WHERE s.employee.department.id = :deptId " +
            "AND s.date = :date " +
            "AND s.isAvailable = true " +
            "ORDER BY s.startTime")
    List<LocalTime> findUniqueAvailableTimes(@Param("deptId") Long deptId, @Param("date") LocalDate date);

    List<AppointmentSlot> findByEmployee_Department_IdAndDateAndStartTimeAndIsAvailableTrue(Long deptId, LocalDate date, LocalTime startTime);

    @Query("SELECT DISTINCT s.date FROM AppointmentSlot s " +
            "WHERE s.employee.department.id = :deptId " +
            "AND s.isAvailable = true " +
            "AND s.date >= CURRENT_DATE " +
            "ORDER BY s.date ASC")
    List<LocalDate> findDistinctAvailableDates(@Param("deptId") Long deptId);

    @Query("SELECT s FROM AppointmentSlot s " +
            "WHERE s.employee.department.id = :deptId " +
            "AND s.date = :date " +
            "AND s.isAvailable = true " +
            "ORDER BY s.startTime ASC")
    List<AppointmentSlot> findAvailableSlotsByDepartmentAndDate(@Param("deptId") Long deptId,
                                                                @Param("date") LocalDate date);

    @NotNull
    @Lock(LockModeType.OPTIMISTIC)
    Optional<AppointmentSlot> findById(@NotNull Long id);
    List<AppointmentSlot> findAllByEmployeeIdAndDate(Long employeeId, LocalDate date);
    boolean existsByEmployeeIdAndDateAndStartTime(Long employeeId, LocalDate date, LocalTime startTime);
}
