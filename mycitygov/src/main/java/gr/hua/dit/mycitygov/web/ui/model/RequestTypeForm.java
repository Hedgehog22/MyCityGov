package gr.hua.dit.mycitygov.web.ui.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestTypeForm(
        @NotBlank(message = "Name cannot be empty")
        @Size(max = 100, message = "Name too long")
        String name,

        @Size(max = 500, message = "Description too long")
        String description,

        @NotNull(message = "SLA Days is required")
        @Min(value = 1, message = "Minimum 1 day")
        Integer slaDays,

        @NotNull(message = "Department is required")
        Long departmentId
) {}