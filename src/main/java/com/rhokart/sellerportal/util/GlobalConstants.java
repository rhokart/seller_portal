package com.rhokart.sellerportal.util;

import java.util.Arrays;
import java.util.List;

public class GlobalConstants {
    public static final String TRUE_RESP = "true";
    public static final String FALSE_RESP = "false";
    public static final int OTP_LENGTH = 5;
    public static final long OTP_VALIDITY = 120;
    public static final String RETURN_INITIAL = "RF-";
    public static final String EMPTY_STRING = "";
    public static final List<String> PUBLIC_URL = Arrays.asList("/pages/otplogin","/pages/otpvalidation","/pages/oauth_login"
                                                                ,"/login/oauth2/code/google"
                                                                ,"/css/registration.css","/css/login.css"
                                                                ,"/js/registration.js"
                                                                ,"/images/google_icon.png"
                                                                 ,"/api/v1/brandlist");
}
