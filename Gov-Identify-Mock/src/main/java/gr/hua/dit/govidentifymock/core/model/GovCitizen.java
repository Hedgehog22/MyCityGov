package gr.hua.dit.govidentifymock.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "citizens")
public class GovCitizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String govToken;

    private String afm;
    private String amka;
    private String firstName;
    private String lastName;

    public GovCitizen() {}

    public GovCitizen(String govToken, String afm, String amka, String firstName, String lastName) {
        this.govToken = govToken;
        this.afm = afm;
        this.amka = amka;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getGovToken() {
        return govToken;
    }

    public String getAfm() {
        return afm;
    }

    public String getAmka() {
        return amka;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
