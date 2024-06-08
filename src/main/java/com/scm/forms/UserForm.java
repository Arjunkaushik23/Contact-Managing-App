package com.scm.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserForm {

    @NotBlank(message = "Username is required")
    @Size(min = 3, message = "Min 3 Characters is required")
    private String name;

    @Email(message = "Invalid Email Address")
    @NotBlank(message = "Email Address Required")
    private String email;

    @NotBlank(message = "Invalid Password")
    @Size(min = 6, message = "Min 6 Characters required")
    private String password;

    @Size(min = 10, max = 10, message = "Invalid Phone Number")
    private String phoneNumber;

    @NotBlank(message = "About is required")
    private String about;

}
