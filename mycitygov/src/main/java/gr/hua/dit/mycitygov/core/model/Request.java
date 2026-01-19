package gr.hua.dit.mycitygov.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Request Entity
 */
@Entity
@Table(
        name = "requests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_request_protocol", columnNames = "protocol_number")
        },
        indexes = {
                @Index(name="idx_request_status", columnList = "status"),
                @Index(name = "idx_request_citizen", columnList = "citizen_id"),
                @Index(name = "idx_request_employee", columnList = "employee_id"),
                @Index(name = "idx_request_submission_date", columnList = "submission_date")
        }
)
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "protocol_number", nullable = false, length = 64)
    private String protocolNumber;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status;

    @CreationTimestamp
    @Column(name = "submission_date", nullable = false, updatable = false)
    private Instant submissionDate;

    @Column(name = "due_date", nullable = false, updatable = false)
    private Instant dueDate;

    @Column(name = "comments", length = 4000)
    private String comments;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    //RELATIONSHIPS

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "citizen_id", nullable = false)
    private User citizen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_type_id", nullable = false)
    private RequestType requestType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private User employee;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Attachment> attachments = new ArrayList<>();

    @Column(name = "result_document_key")
    private String resultDocumentKey;


    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = RequestStatus.SUBMITTED;
        }
        if (this.protocolNumber == null) {
            this.protocolNumber = UUID.randomUUID().toString();
        }
        if (this.submissionDate == null) {
            this.submissionDate = Instant.now();
        }
        if (requestType != null && this.dueDate == null) {
            this.dueDate = this.submissionDate.plus(this.requestType.getSlaDays(), ChronoUnit.DAYS);
        }
    }

    public Request() {
    }

    public Request(User citizen, RequestType requestType, String description) {
        this.citizen = citizen;
        this.requestType = requestType;
        this.description = description;
    }
    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocolNumber() {
        return protocolNumber;
    }

    public void setProtocolNumber(String protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Instant getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Instant submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getCitizen() {
        return citizen;
    }

    public void setCitizen(User citizen) {
        this.citizen = citizen;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getResultDocumentKey() {
        return resultDocumentKey;
    }

    public void setResultDocumentKey(String resultDocumentKey) {
        this.resultDocumentKey = resultDocumentKey;
    }
}