package com.alrosyid.notula.api;

public class Constant {
    public static final String URL = "https://app.jeevva.my.id/";
    public static final String HOME = URL+"api";
    //auth
    public static final String LOGIN = HOME+"/login";
    public static final String LOGOUT = HOME+"/logout";
    public static final String REGISTER = HOME+"/register";
    public static final String ACCOUNT = HOME+"/myAccount";


    //Notula
    public static final String NOTULA = HOME+"/notulas";
    public static final String MY_NOTULA = NOTULA+"/myNotulas";
    public static final String DELETE_NOTULA = NOTULA+"/delete";
    public static final String SAVE_USER_INFO = HOME+"/save_user_info";
}
