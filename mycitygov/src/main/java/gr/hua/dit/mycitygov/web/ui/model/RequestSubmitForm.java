package gr.hua.dit.mycitygov.web.ui.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record RequestSubmitForm(
        @NotNull(message = "Please, choose the tyype of service")
        Long requestTypeId,

        @NotBlank(message = "Description cannot be empty!")
        @Size(max = 500, message = "too long (max 500 symb.)")
        String description,

        MultipartFile attachment
) {}