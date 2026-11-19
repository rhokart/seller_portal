package com.rhokart.sellerportal.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerInfo {
    private String id;
    private String sellerName;
    private String email;
    private String mobileNo;
    private boolean isKycVerified;
    private boolean isProfileCompleted;
    private boolean isProfileApproved;
    private boolean isActive;
    private BusinessDetail businessDetail;
    private BankDetail bankDetail;
    private Documents documents;
    private SellerAddress sellerAddress;
}
