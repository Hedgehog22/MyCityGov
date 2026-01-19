package gr.hua.dit.mycitygov.core.model;

import jakarta.persistence.*;

/**
 * Entity RequestType
 */

@Entity
@Table( name = "request_types",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_request_type_name", columnNames = "name")
        },
        indexes = {
            @Index(name = "idx_request_type_active", columnList = "is_active"),
            @Index(name = "idx_request_type_department", columnList = "department_id")
        }
        )

public class RequestType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "sla_days", nullable = false)
    private Integer slaDays;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    // Which department is responsible for this type of requests
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    public RequestType() {}

    public RequestType(String name, String description, Integer slaDays, Boolean active, Department department) {
        this.name = name;
        this.description = description;
        this.slaDays = slaDays;
        this.active = active;
        this.department = department;
    }
    public RequestType(Long id, String name, String description, Integer slaDays, Boolean isActive, Department department) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slaDays = slaDays;
        this.active = isActive;
        this.department = department;
    }

    //Getters andSetters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSlaDays() {
        return slaDays;
    }

    public void setSlaDays(Integer slaDays) {
        this.slaDays = slaDays;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
