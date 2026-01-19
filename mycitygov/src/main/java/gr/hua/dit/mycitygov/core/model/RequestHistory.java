package gr.hua.dit.mycitygov.core.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "request_history")
public class RequestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @CreationTimestamp
    @Column(name = "action_date", nullable = false)
    private Instant actionDate;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "action_details")
    private String actionDetails;

    public RequestHistory() {}

    public RequestHistory(Request request, String actionType, String actionDetails) {
        this.request = request;
        this.actionType = actionType;
        this.actionDetails = actionDetails;
        this.actionDate = Instant.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Instant getActionDate() {
        return actionDate;
    }

    public void setActionDate(Instant actionDate) {
        this.actionDate = actionDate;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(String actionDetails) {
        this.actionDetails = actionDetails;
    }
}