package gr.hua.dit.mycitygov.core.port.impl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GovCitizenDto(
        String afm,
        String amka,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName
) {}
