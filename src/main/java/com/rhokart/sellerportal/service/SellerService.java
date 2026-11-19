package com.rhokart.sellerportal.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.rhokart.sellerportal.document.*;
import com.rhokart.sellerportal.models.request.SellerInfo;
import com.rhokart.sellerportal.repositories.SellerDetailRepo;
import com.rhokart.sellerportal.util.GlobalConstants;
import com.rhokart.sellerportal.util.ValidationUtility;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class SellerService {

    @Autowired
    private SellerDetailRepo repo;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${application.aws.s3.kyc.bucket.name}")
    private String bucketName;

    @SneakyThrows
    public String storeFileToS3(String sellerId, List<MultipartFile> multipartFiles){
        // provide bucket to public access as it might be required for verifications.
        BucketPolicy policy = amazonS3.getBucketPolicy(bucketName);
        AccessControlList acl = new AccessControlList();
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        String rootPath = System.getProperty("user.dir");
        log.info("root path :{}",rootPath);
        multipartFiles.forEach( multipartFile -> {
            String s3FileName = sellerId+"_"+multipartFile.getOriginalFilename();
            File conFile = new File(rootPath+"/upload/" +s3FileName);
            try {
                FileOutputStream fos = new FileOutputStream(conFile);
                fos.write(multipartFile.getBytes());
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            amazonS3.putObject(bucketName,s3FileName,conFile);
        });

        return GlobalConstants.EMPTY_STRING;
    }
    @SneakyThrows
    public String registerNewSeller(SellerInfo sellerInfo) {
        if(ValidationUtility.isValidBankDetail(sellerInfo.getBankDetail())
                && ValidationUtility.isValidSellerInfo(sellerInfo)
                && ValidationUtility.isValidBillingAddress(sellerInfo.getSellerAddress().getBillingAddress())
                && ValidationUtility.isValidPickupAddress(sellerInfo.getSellerAddress().getPickupAddress())
                && ValidationUtility.isValidBusinessDetail(sellerInfo.getBusinessDetail())
                && ValidationUtility.isValidSellerDocuments(sellerInfo.getDocuments())) {
            String sellerId = generateSellerId();
            SellerDetail detail = sellerDetailMapper(sellerInfo,sellerId);
            try {
                repo.save(detail);
            }catch (Exception ex){
                log.info("Exceptions occurs at the time of saving seller.. check log");
            }
            return sellerId;
        }
        return GlobalConstants.EMPTY_STRING;
    }

    private String generateSellerId(){
        List<SellerDetail> sellerDetails = repo.findAll();
        SellerDetail maxIdSeller = sellerDetails.stream().max(Comparator.comparing(SellerDetail::get_id)).get();
        log.info("Max - id seller :{}",maxIdSeller);
        Integer generatedSellerId = Integer.parseInt(maxIdSeller.get_id()) + 1;   // Generated new seller id will be max_id + 1;
        log.info("Generated Seller Id:{}",generatedSellerId);
        return String.valueOf(generatedSellerId);

    }

    private SellerDetail sellerDetailMapper(SellerInfo info, String id){

        BusinessDetail businessDetail = BusinessDetail.builder()
                .alternateEmail(info.getBusinessDetail().getAltEmail())
                .city(info.getBusinessDetail().getCity())
                .alternatePhoneNo(info.getBusinessDetail().getAltPhoneNo())
                .contactName(info.getBusinessDetail().getContactName())
                .country(info.getBusinessDetail().getCountry())
                .entityName(info.getBusinessDetail().getEntityName())
                .gstin(info.getBusinessDetail().getGstin())
                .email(info.getBusinessDetail().getEmail())
                .isAlternateEmailVerified(Boolean.TRUE)
                .isAlternatePhoneNoVerified(Boolean.FALSE)
                .isEmailVerified(Boolean.TRUE)
                .isGstinVerified(Boolean.TRUE)
                .isPanVerified(Boolean.TRUE)
                .isPhoneNoVerified(Boolean.TRUE)
                .pan(info.getBusinessDetail().getPan())
                .phoneNo(info.getBusinessDetail().getPhoneNo())
                .pincode(info.getBusinessDetail().getPincode())
                .state(info.getBusinessDetail().getState())
                .tan(info.getBusinessDetail().getTan())
                .tradeName(info.getBusinessDetail().getTradeName())
                .build();
        log.info("Seller business details:{}",businessDetail);

        BankDetail bankDetail = BankDetail.builder()
                .accountHolderName(info.getBankDetail().getAccName())
                .accountNo(info.getBankDetail().getAccNo())
                .accountType(info.getBankDetail().getAccountType())
                .bankName(info.getBankDetail().getBankName())
                .branchCityName(info.getBankDetail().getBranchCityName())
                .branchName(info.getBankDetail().getBranchName())
                .ifscCode(info.getBankDetail().getIfsc())
                .isIFscVerified(Boolean.FALSE)
                .build();
        log.info("Seller bank details:{}",bankDetail);

        Documents documents = Documents.builder()
                .businessAddressProof(info.getDocuments().getBusinessAddressProof())
                .cancelledCheque("")
                .gstinCertificate(info.getDocuments().getGstin())
                .incorporationCertificate(info.getDocuments().getIncCertificate())
                .ownerPanCard(info.getDocuments().getOwnerPan())
                .signature(info.getDocuments().getSignature())
                .build();
        log.info("Seller document details:{}",documents);

        BillingAddress billingAddress = BillingAddress.builder()
                .addressLine1(info.getSellerAddress().getBillingAddress().getAddressLine1())
                .addressLine2(info.getSellerAddress().getBillingAddress().getAddressLine2())
                .city(info.getSellerAddress().getBillingAddress().getCity())
                .country(info.getSellerAddress().getBillingAddress().getCountry())
                .district(info.getSellerAddress().getBillingAddress().getDistrict())
                .pincode(info.getSellerAddress().getBillingAddress().getPincode())
                .landmark(info.getSellerAddress().getBillingAddress().getLandmark())
                .state(info.getSellerAddress().getBillingAddress().getState())
                .build();
        log.info("Seller Billing address:{}",billingAddress);

        PickupAddress pickupAddress = PickupAddress.builder()
                .addressLine1(info.getSellerAddress().getPickupAddress().getAddressLine1())
                .addressLine2(info.getSellerAddress().getPickupAddress().getAddressLine2())
                .city(info.getSellerAddress().getPickupAddress().getCity())
                .country(info.getSellerAddress().getPickupAddress().getCountry())
                .district(info.getSellerAddress().getPickupAddress().getDistrict())
                .pincode(info.getSellerAddress().getPickupAddress().getPincode())
                .state(info.getSellerAddress().getPickupAddress().getState())
                .landmark(info.getSellerAddress().getPickupAddress().getPicklandmark())
                .build();
        log.info("Seller pickup address:{}",pickupAddress);

        List<Brand> brands = new ArrayList<>();
        info.getBrands().forEach( brand -> brands.add(Brand.builder()
                        .associationType(brand.getAssociationType())
                        .brandName(brand.getBrandName())
                        .certificate(brand.getCertificate())
                .build()));

        SellerDetail sellerDetail = SellerDetail.builder()
                ._id(id)
                .email(info.getBusinessDetail().getEmail())
                .mobileNo(info.getBusinessDetail().getPhoneNo())
                .sellerName(info.getBusinessDetail().getEntityName())
                .businessDetail(businessDetail)
                .bankDetail(bankDetail)
                .documents(documents)
                .sellerAddress(SellerAddress.builder()
                        .billingAddress(billingAddress)
                        .pickupAddress(pickupAddress)
                        .build())
                .isActive(Boolean.FALSE)             // default we are making seller as active but profile approved as false...
                .isKycVerified(Boolean.TRUE)
                .isProfileApproved(Boolean.FALSE)
                .isProfileCompleted(Boolean.TRUE)
                .brands(brands)
                .build();
        log.info("Seller details:{}",sellerDetail);

        return sellerDetail;
    }
}
