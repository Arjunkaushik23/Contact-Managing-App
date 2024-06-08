package com.scm.forms;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@ToString
@Builder
public class ContactForm {

    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Min 3 Characters is required")
    private String name;

    @Email(message = "Invalid Email Address")
    @NotBlank(message = "Email Address Required")
    private String email;

    @NotBlank(message = "Invalid Phone Number")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid Phone Number")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    // create annotation
    // size
    // Resolution
    
    private MultipartFile contactImage;

   
    private String description;

    private boolean favorite;

    
    private String websiteLink;

    
    private String linkedInLink;


}
