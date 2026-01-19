package gr.hua.dit.mycitygov.core.model;

import gr.hua.dit.mycitygov.core.model.Appointment;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Appointment Entity
 */

@Entity
@Table(
        name = "appointments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_appointment_ref_code", columnNames = "reference_code"),
            },
        indexes = {
                @Index(name = "idx_appointment_citizen", columnList = "citizen_id"),
                @Index(name = "idx_appointment_status", columnList = "status")
        }
)

public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "reference_code", nullable = false, length = 36)
    private String referenceCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status;

    @CreationTimestamp
    @Column(name = "booked_at", nullable = false, updatable = false)
    private Instant bookedAt;

    //relationships

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "citizen_id", nullable = false)
    private User citizen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "slot_id", nullable = false)
    private AppointmentSlot slot;

    public Appointment () {}

    public Appointment (String referenceCode, AppointmentStatus status, User citizen, AppointmentSlot slot) {
        this.referenceCode = referenceCode;
        this.status = status;
        this.citizen = citizen;
        this.slot = slot;
    }

    public Appointment (Long id, String referenceCode, AppointmentStatus status, User citizen, AppointmentSlot slot) {
        this.id = id;
        this.referenceCode = referenceCode;
        this.status = status;
        this.citizen = citizen;
        this.slot = slot;
    }

    //Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public Instant getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(Instant bookedAt) {
        this.bookedAt = bookedAt;
    }

    public User getCitizen() {
        return citizen;
    }

    public void setCitizen(User citizen) {
        this.citizen = citizen;
    }

    public AppointmentSlot getSlot() {
        return slot;
    }

    public void setSlot(AppointmentSlot slot) {
        this.slot = slot;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", referenceCode='" + referenceCode + '\'' +
                ", status=" + status +
                ", bookedAt=" + bookedAt +
                '}';
    }
}
