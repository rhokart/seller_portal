package com.rhokart.sellerportal.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documents {
    private String ownerPan;
    private String gstin;
    private String incCertificate;
    private String businessAddressProof;
    private String signature;
}
