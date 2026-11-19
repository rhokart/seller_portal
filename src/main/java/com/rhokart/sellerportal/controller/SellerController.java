package com.rhokart.sellerportal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhokart.datamodel.dto.SuccessInfo;
import com.rhokart.sellerportal.domain.Person;
import com.rhokart.sellerportal.models.RegistrationResponse;
import com.rhokart.sellerportal.models.SellerRegisterResponse;
import com.rhokart.sellerportal.models.request.SellerInfo;
import com.rhokart.sellerportal.service.PersonService;
import com.rhokart.sellerportal.service.SellerService;
import com.rhokart.sellerportal.util.GlobalConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "api/v1")
@Slf4j
public class SellerController {

    @Autowired
    private PersonService personService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private MongoTemplate mongoTemplate;



    @GetMapping(value = "persons")
    public List<Person> findAll() {
        personService.addPerson();
        return personService.getAllPersons();
    }

    @PostMapping("/seller/register")
    @SneakyThrows
    public ResponseEntity<RegistrationResponse> registerSeller(@RequestBody String sellerInfo) {
        // Process the sellerInfo and save it to the database
        log.info("Seller Info:{}",sellerInfo);
        ObjectMapper mapper = new ObjectMapper();
        SellerInfo info = mapper.readValue(sellerInfo,SellerInfo.class);
        log.info("Inside seller info :: {}", info);
        String refId = sellerService.registerNewSeller(info);
        if(!refId.isEmpty()) {
            return ResponseEntity.ok(RegistrationResponse.builder()
                    .refId(refId)
                    .sellerId(refId.substring(3))
                    .sellerName(info.getBusinessDetail().getEntityName())
                    .build());
        }
        return ResponseEntity.badRequest().body(RegistrationResponse.builder().build());

    }


    // API to send OTP and verify OTP
    @GetMapping("/getOtpAndValidate")
    public ResponseEntity<String> getOtpAndValidate(@RequestParam("email") String email, @RequestParam(value = "otpValue", required = false) String otpValue) {

        // If OTP value is not provided, it means the user wants to send OTP
        if (otpValue == null || otpValue.isEmpty()) {
            // Generate and send OTP
            String otp = personService.sendOtpEmail(email);
            if (otp != null) {
                return ResponseEntity.ok("OTP Sent");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP.");
            }
        } else {
            // Validate the OTP
            boolean isValidOtp = personService.validateOtp(email, otpValue);
            if (isValidOtp) {
                return ResponseEntity.ok("OTP Verified Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
            }
        }
    }

    @PostMapping(value = "/seller/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SellerRegisterResponse> registerSeller(
            @RequestPart("sellerInfo") String sellerInfo,
            @RequestPart(value = "uploadOwnerPan", required = false) MultipartFile uploadOwnerPan,
            @RequestPart(value = "uploadGstin", required = false) MultipartFile uploadGstin,
            @RequestPart(value = "incCertificate", required = false) MultipartFile incCertificate,
            @RequestPart(value = "businessAddressProof", required = false) MultipartFile businessAddressProof,
            @RequestPart(value = "signature", required = false) MultipartFile signature,
            @RequestParam(value = "brandCertificate", required = false) List<MultipartFile> brandCertificates) throws JsonProcessingException {

        log.info("Inside seller info-1 :: {}", sellerInfo);
        ObjectMapper mapper = new ObjectMapper();
        SellerInfo info = mapper.readValue(sellerInfo,SellerInfo.class);
        String sellerId = sellerService.registerNewSeller(info);
       // uploadOwnerPan.transferTo(new File());
        // all mandatory files are added here.
        List<MultipartFile> mfiles = new ArrayList<>();
        mfiles.add(uploadOwnerPan);
        mfiles.add(uploadGstin);
        mfiles.add(incCertificate);
        mfiles.add(businessAddressProof);
        mfiles.add(signature);
        // depending on brand certificate files are added to target files.
        for(MultipartFile file: brandCertificates){
            if(!Objects.isNull(file))
                mfiles.add(file);
        }
        sellerService.storeFileToS3(sellerId,mfiles);

        return ResponseEntity.ok(SellerRegisterResponse.builder().refId(GlobalConstants.RETURN_INITIAL +sellerId).build());
    }


    @PostMapping(value = "/multipart",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadMultipart(@RequestPart(value = "mfile", required = false) List<MultipartFile> mfile){
        return  sellerService.storeFileToS3("100",mfile);
    }

    @GetMapping("/brandlist")
    public List<String> getTotalBrandList() {
        List<String> itr = mongoTemplate
                .getCollection("listing_details")
                .distinct("brandName", String.class)
                .into(new ArrayList<>());


        SuccessInfo successInfo = new SuccessInfo();
        log.info("band list :{}",itr);

        return itr;

    }



}
