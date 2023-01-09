package com.alissonbk.pilacoin.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class LoginDTO {
    @Email
    private String email;
    private String password;
}
