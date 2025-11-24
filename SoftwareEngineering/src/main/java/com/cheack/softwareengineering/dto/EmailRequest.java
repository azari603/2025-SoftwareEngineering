// src/main/java/com/cheack/softwareengineering/dto/EmailRequest.java
package com.cheack.softwareengineering.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    @NotBlank
    @Email
    private String email;
}