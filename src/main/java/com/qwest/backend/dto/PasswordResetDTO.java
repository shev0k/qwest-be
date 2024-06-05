package com.qwest.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDTO {
    @Email(message = "Email should be valid.")
    @NotBlank(message = "Email must not be empty.")
    private String email;

    @NotBlank(message = "New password must not be empty.")
    private String newPassword;

    @NotBlank(message = "Confirm new password must not be empty.")
    private String confirmNewPassword;
}
