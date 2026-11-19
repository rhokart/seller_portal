package com.rhokart.sellerportal.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "seller_detail")
public class SellerDetail {
    private String _id;
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
    private List<Brand> brands;
}
