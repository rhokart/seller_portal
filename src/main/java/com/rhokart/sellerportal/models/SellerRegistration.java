package com.rhokart.sellerportal.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerRegistration {

    private SellerInfo sellerInfo;
    private Integer returnCode;
    private String returnStatus;
    private String returnMessage;
    private String returnDescription;

}
