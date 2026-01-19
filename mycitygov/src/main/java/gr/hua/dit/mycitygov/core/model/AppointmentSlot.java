package gr.hua.dit.mycitygov.core.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Appointment Slot entity
 */
@Entity
@Table(
        name = "appointment_slots",
        uniqueConstraints = {
                //employee mustn't have 2+ slots for the same time
                @UniqueConstraint(name = "uk_slot_emp_date_time",
                                  columnNames = {"employee_id", "date", "start_time"})
        },
        indexes = {
                @Index(name = "idx_slot_date_available", columnList = "date, is_available"),
                @Index(name = "idx_slot_employee", columnList = "employee_id")
        }
)

public class AppointmentSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "slot_id")
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column (name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    public AppointmentSlot(LocalDate date, LocalTime startTime, LocalTime endTime, User employee) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.employee = employee;
        this.isAvailable = true;
    }

    public AppointmentSlot() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {return date;}

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() { return startTime; }

    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }

    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Boolean getIsAvailable() { return isAvailable; }

    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Integer getVersion() { return version; }

    public void setVersion(Integer version) { this.version = version; }

    public User getEmployee() { return employee; }

    public void setEmployee(User employee) { this.employee = employee; }

    @Override
    public String toString() {
        return "AppointmentSlot{" + "id = " + id + ", date =" + date + ", startTime = " + startTime
                + ", endTime = " + endTime + ", isAvailable = " + isAvailable + '}';
    }
}
