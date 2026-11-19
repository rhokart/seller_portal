package com.rhokart.sellerportal.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerInfo {
    private BusinessDetail businessDetail;
    private BankDetail bankDetail;
    private Documents documents;
    private SellerAddress sellerAddress;
    private Boolean isTnc;  
    private Boolean isPermission;
    private List<Brand> brands;
}
