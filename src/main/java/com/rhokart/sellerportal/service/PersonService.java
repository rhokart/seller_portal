package com.rhokart.sellerportal.service;

import com.rhokart.sellerportal.document.OtpDoc;
import com.rhokart.sellerportal.document.SellerDetail;
import com.rhokart.sellerportal.domain.EmailDetails;
import com.rhokart.sellerportal.domain.Person;
import com.rhokart.sellerportal.models.SellerInfo;
import com.rhokart.sellerportal.repositories.OtpDocRepo;
import com.rhokart.sellerportal.repositories.RefreshTokenRepo;
import com.rhokart.sellerportal.repositories.SellerDetailRepo;
import com.rhokart.sellerportal.util.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class PersonService {

    @Autowired
    private IEmailService emailService;

    @Autowired
    private OtpMongoDbImpl iOtpUtils;
    @Autowired
    private OtpDocRepo otpProcessRepository;
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;
    @Autowired
    private SellerDetailRepo sellerRepository;

    @Autowired
    private JwtHelper jwtHelper;


    public static List<Person> persons = new ArrayList<>();

    public void addPerson() {
        Person person = new Person();
        person.setId("1");
        person.setName("Amit Samanta");
        person.setAddress(" Kolkata-700136");
        persons.add(person);
    }


    public String sendOtpEmail(String email){
        OtpDoc getOtp = new OtpDoc(email, iOtpUtils.generateOtp(email, GlobalConstants.OTP_LENGTH), GlobalConstants.OTP_VALIDITY, System.currentTimeMillis());

        otpProcessRepository.save(getOtp);  //saving the otp for validation in future.

        EmailDetails detail = EmailDetails.builder()
                .msgBody("Your one time login password for Rhokart is :"+getOtp.getOtp()+".\n"+"Thank you for choosing Rhokart.")
                .subject("Alert: Rhokart Otp for login.")
                .toAddress(email.trim())
                .build();

        if(!emailService.sendEmailToUser(detail)) {
            log.error("Email send fail..");
            return GlobalConstants.FALSE_RESP;
        }
        return GlobalConstants.TRUE_RESP;
    }

    public boolean validateOtp(String email, String otp){

        return iOtpUtils.validateOtp(email,otp);
    }

    public List<Person> getAllPersons() {
        return persons;
    }

    public SellerInfo getLoginSellerDetailByEmail(String email){
        Optional<SellerDetail> sellerDetail = sellerRepository.findByEmail(email);
        if(sellerDetail.isPresent()){
            return SellerInfo.builder()
                    .id(sellerDetail.get().get_id())
                    .email(email)
                    .mobileNo(sellerDetail.get().getMobileNo())
                    .sellerName(sellerDetail.get().getSellerName())
                    .isActive(sellerDetail.get().isActive())
                    .isKycVerified(sellerDetail.get().isKycVerified())
                    .isProfileApproved(sellerDetail.get().isProfileApproved())
                    .isProfileCompleted(sellerDetail.get().isProfileCompleted())
                    .build();
        }

        return  SellerInfo.builder().build();
    }
}
