package com.rhokart.sellerportal.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthDetail {
    private String sellerEmail;
    private String sellerId;
    private String sellerName;
    private String authToken;
}
