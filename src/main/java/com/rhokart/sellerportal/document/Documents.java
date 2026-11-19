package com.rhokart.sellerportal.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Documents {
    private String ownerPanCard;
    private String gstinCertificate;
    private String cancelledCheque;
    private String incorporationCertificate;
    private String businessAddressProof;
    private String signature;
}
