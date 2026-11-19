package com.rhokart.sellerportal.controller;

import com.rhokart.sellerportal.models.AuthDetail;
import com.rhokart.sellerportal.models.SellerInfo;
import com.rhokart.sellerportal.service.JwtHelper;
import com.rhokart.sellerportal.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
@RequestMapping(value = "pages")
@Slf4j
public class OAuthController {

    @Autowired
    private PersonService personService;

    @Autowired
    private JwtHelper jwtHelper;

    @GetMapping(value = "oauth_login")
    public String oauthLogin(@AuthenticationPrincipal OAuth2User principal, Model model, HttpServletResponse response) {
        String username = principal.getAttribute("name");
        String email = principal.getAttribute("email");
        log.info("username ={} , email ={}",username,email);

        SellerInfo info = personService.getLoginSellerDetailByEmail(email);

        Date loginTime = new Date();
        String accessToken = jwtHelper.generateAccessToken(info.getId(), loginTime);

        AuthDetail authDetail = AuthDetail.builder()
                .sellerEmail(email)
                .authToken(accessToken)
                .sellerId(info.getId())
                .sellerName(info.getSellerName())
                .build();

            response.setHeader("authdata", String.valueOf(authDetail));

        if(info.isProfileApproved() && info.isActive() && info.isProfileCompleted()) {
            log.info("profile is completed and approved for the seller , going to dashboard");
            return "/pages/dashboard";
        }
        if(info.isProfileCompleted() && !info.isProfileApproved() && !info.isActive()){
            log.info("Profile completed but not approved.");
            model.addAttribute("referenceId","RF-"+info.getId());
            return "/pages/registration_success";
        }
        return "/pages/registration";
    }

}
