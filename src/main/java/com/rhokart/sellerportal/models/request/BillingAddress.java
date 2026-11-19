package com.rhokart.sellerportal.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingAddress {
    private String addressLine1;
    private String addressLine2;
    private String pincode;
    private String city;
    private String district;
    private String state;
    private String country;
    private String landmark;
}
