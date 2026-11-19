package com.rhokart.sellerportal.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessDetail {
    private String entityName;
    private String gstin;
    private String contactName;
    private String tradeName;
    private String country;
    private String pincode;
    private String state;
    private String city;
    private String tan;
    private String pan;
    private String phoneNo;
    private String email;
    private String alternateEmail;
    private String alternatePhoneNo;
    private boolean isPanVerified;
    private boolean isGstinVerified;
    private boolean isEmailVerified;
    private boolean isAlternateEmailVerified;
    private boolean isAlternatePhoneNoVerified;
    private boolean isPhoneNoVerified;
}

