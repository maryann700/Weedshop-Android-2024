package com.weedshop.driver.utils;

/**
 * Created by Ravi Tamada on 28/09/16.
 * www.androidhive.info
 */

public class Config {
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray

    public static final int NOTIFICATION_ID = 98;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 99;
    public static final int ACCOUNT_APPROVED_ID = 100;/* Account Approved (Send to Home)*/
    public static final int ORDER_AVAILABLE_ID = 101;/* Order Accept/Decline View)*/

    public static final String SHARED_PREF = "ah_firebase";
}
