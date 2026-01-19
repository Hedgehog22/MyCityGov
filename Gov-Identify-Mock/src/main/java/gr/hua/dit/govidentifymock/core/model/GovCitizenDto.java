package gr.hua.dit.govidentifymock.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GovCitizenDto(
        String afm,
        String amka,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName
) {}