package com.rhokart.sellerportal.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerAddress {
    private BillingAddress billingAddress;
    private PickupAddress pickupAddress;
}
