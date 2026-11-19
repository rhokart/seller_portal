package com.rhokart.sellerportal.util;


import com.rhokart.sellerportal.models.request.*;

public class ValidationUtility {

    public static boolean isValidSellerInfo(SellerInfo sellerInfo){

        return Boolean.TRUE;
    };

    public static boolean isValidBusinessDetail(BusinessDetail businessDetail){
        return Boolean.TRUE;
    }

    public static boolean isValidBankDetail(BankDetail bankDetail){
        return Boolean.TRUE;
    }

    public static boolean isValidBillingAddress(BillingAddress billingAddress){
        return Boolean.TRUE;
    }

    public static boolean isValidSellerDocuments(Documents documents){
        return Boolean.TRUE;
    }

    public static boolean isValidPickupAddress(PickupAddress pickupAddress){
        return Boolean.TRUE;
    }
}
