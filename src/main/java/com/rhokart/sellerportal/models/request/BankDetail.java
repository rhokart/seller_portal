package com.rhokart.sellerportal.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankDetail {
    private String accName;
    private String accNo;
    private String cnfAccName;
    private String ifsc;
    private String bankName;
    private String branchName;
    private String branchCityName;
    private String accountType;
}
