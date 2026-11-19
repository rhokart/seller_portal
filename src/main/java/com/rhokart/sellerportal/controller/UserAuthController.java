package com.rhokart.sellerportal.controller;

import com.rhokart.sellerportal.domain.Login;
import com.rhokart.sellerportal.models.AuthDetail;
import com.rhokart.sellerportal.models.SellerInfo;
import com.rhokart.sellerportal.service.JwtHelper;
import com.rhokart.sellerportal.service.PersonService;
import com.rhokart.sellerportal.util.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;


@Controller
@RequestMapping(value = "pages")
@Slf4j
public class UserAuthController {

    @Autowired
    private PersonService personService;

    @Autowired
    private JwtHelper jwtHelper;

    @GetMapping(value = "otplogin")
    public ModelMap mmDashboard() {
        ModelMap model = new ModelMap();
        Login login = Login.builder()
                .isOtp("false")
                .build();
        model.addAttribute("login",login);
        return model;
    }

    @PostMapping(value = "otpvalidation")
    public String otpValidation(Login login, BindingResult bindingResult, Model model, HttpServletResponse httpResponse) {
        if(bindingResult.hasErrors()){
            log.info("otp validation failed");
            return "/pages/otplogin";
        }
        log.info("otp validation payload :{}",login);

        if(Objects.isNull(login.getOtpValue())){
            String response = personService.sendOtpEmail(login.getEmail());
            Login otpLogin = Login.builder()
                    .email(login.getEmail())
                    .isOtp(response)
                    .build();
            log.info("login page called here...");
            model.addAttribute("login",otpLogin);
            return "/pages/otplogin";
        }

        if(personService.validateOtp(login.getEmail(),login.getOtpValue())) {
            SellerInfo info = personService.getLoginSellerDetailByEmail(login.getEmail());

            Date loginTime = new Date();
            String accessToken = jwtHelper.generateAccessToken(info.getId(), loginTime);

            AuthDetail authDetail = AuthDetail.builder()
                    .authToken(accessToken)
                    .sellerId(info.getId())
                    .sellerEmail(login.getEmail())
                    .sellerName(info.getSellerName())
                    .build();

            httpResponse.setHeader("authdata", String.valueOf(authDetail));

            if(info.isProfileCompleted() && info.isActive() && info.isProfileApproved()){
                log.info("seller profile is completed.. dashboard login.. seller id :{}",info.getId());
                return "/pages/dashboard";
            }
            return "/pages/registration";
        }
        else{
            Login errorLogin = Login.builder()
                    .email(login.getEmail())
                    .isOtp(GlobalConstants.TRUE_RESP)
                    .otpValue(login.getOtpValue())
                    .message("Otp did not Match !!!")
                    .build();  // setting otp error message a wrong otp...

            model.addAttribute("login",errorLogin);
        }
        return "/pages/otplogin";
    }

    @PostMapping(value = "registration")
    public String registrationSeller(SellerInfo sellerInfo, BindingResult bindingResult, Model model){
        return "/pages/registration";

    }
    @GetMapping(value = "dashboard")
    public String dashboard(SellerInfo sellerInfo, BindingResult bindingResult, Model model){
        log.info("Dashboard called...");
        return "/pages/dashboard";

    }

    @GetMapping("/registration_success")
    public String registrationSuccess(BindingResult bindingResult, Model model){
        return "/pages/registration_success";
    }


}
