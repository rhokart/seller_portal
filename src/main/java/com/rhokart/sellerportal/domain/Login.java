package com.rhokart.sellerportal.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Login {

    private String isOtp;
    private String otpValue;
    private String email;
    private String message;
}
