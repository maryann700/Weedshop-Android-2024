package com.weedshop.driver.utils;

/**
 * Created by MTPC-83 on 4/10/2017.
 */

public class Constant {
    public static final String ANDROID = "Android";
    public static String apikey = "582e7d442740f326cc32a5ade9ed92f1";
    //public static String BaseUrl = "http://192.168.0.160/dispobuds/api/";
    public static String BaseUrl = "http://high5delivery.com/weed/api/";
    public static String register = BaseUrl + "driver_register.php";
    public static String register_verification = BaseUrl + "driver_register_verification.php";
    public static String login_api = BaseUrl + "driver_login.php";
    public static String identificationPhoto_Upload = BaseUrl + "driver_id_upload.php";
    public static String driver_info = BaseUrl + "driver_info.php";
    public static String update_driver_location = BaseUrl + "update_driver_location.php";
    public static String driver_get_order_request = BaseUrl + "driver_get_order_request.php";
    public static String driver_request_action = BaseUrl + "driver_request_action.php";
    public static String driver_current_order_detail = BaseUrl + "driver_current_order_detail.php";
    public static String driver_profile = BaseUrl + "driver_profile.php";
    public static String driver_order_history = BaseUrl + "driver_order_history.php";
    public static String driver_order_detail = BaseUrl + "driver_order_detail.php";
    public static String driver_order_action = BaseUrl + "driver_order_action.php";
    public static String forgot_pass = BaseUrl + "driver_forgot.php";
    public static String driver_resend_verification = BaseUrl + "driver_resend_verification.php";
    public static String driver_device = BaseUrl + "driver_device.php";
    public static String logout = BaseUrl + "driver_logout.php";


    public static int statuscode_request_action_accept = 101;
    public static int statuscode_request_action_decline = 102;
    public static int statuscode_driver_current_order_detail = 103;
    public static int getStatuscode_driver_profile_edit = 104;
    public static int getStatuscode_driver_profile_list = 105;
    public static int getStatuscode_driver_order_pickup = 106;
    public static int getStatuscode_driver_order_deliver = 107;
    public static int status_driver_resend_verification = 108;
    public static int status_driver_verification = 109;

    public static String LOGIN_USER_PREF = "LoginUserPrefs";
    public static String PREF_userid = "userid";
    public static String PREF_userImage = "userImage";
    public static String PREF_NAME = "name";
    public static String PREF_carno = "carno";
    public static String ISLOGIN = "login";
    public static String ISVERIFYEMAIL = "verifyemail";
    public static String ISPHOTOUPLOAD = "photoupload";
    public static String ISDOCUMENTUPLOAD = "documentupload";
    public static String ISVERYFINEPERDETAIL = "veryfyperdetail"; // flag for admin verify
    public static String ISDRIVERORDERREQUEST = "driverorderrequest";
    public static String ISCURRENTORDER = "currentorder";
    public static String PREF_order_id = "order_id";
    public static String ISPICKUPORDER = "ispickuporder";

    public static String adminRejectReason = "adminRejectReason";
    public static String verifymsg = "verifymsg";
}