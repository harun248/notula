package com.alrosyid.notula.api;

public class Constant {
//    public static final String URL = "https://app.jeevva.my.id/";
//    public static final String URL = "http://10.0.2.2:81/";
    public static final String URL = "http://192.168.100.4:81/";


    public static final String HOME = URL+"api";
    //auth
    public static final String LOGIN = HOME+"/login";
    public static final String LOGOUT = HOME+"/logout";
    public static final String REGISTER = HOME+"/register";
    public static final String ACCOUNT = HOME+"/myAccount";

    //Meetings
    public static final String MEETING = HOME+"/meetings";
    public static final String MY_MEETING = MEETING+"/myMeetings";
    public static final String DETAIL_MEETING = MEETING+"/detailMeetings/";
    public static final String MY_NOTULA_BY_MEETING = MEETING+"/myNotulas/";
    public static final String DELETE_MEETINGS = MEETING+"/delete";
    public static final String EDIT_MEETINGS = MEETING+"/editMeetings/";
    public static final String CREATE_MEETINGS = MEETING+"/create";
    public static final String UPDATE_MEETINGS = MEETING+"/update";


    //Notula
    public static final String NOTULA = HOME+"/notulas";
    public static final String MY_NOTULA = NOTULA+"/detailNotulas/";
    public static final String DELETE_NOTULA = NOTULA+"/delete";
    public static final String SAVE_USER_INFO = HOME+"/save_user_info";

    //Attendances
    public static final String ATTENDANCES =HOME+"/attendances";
    public static final String LIST_ATTENDANCES =ATTENDANCES+"/listAttendances/";
    public static final String DELETE_ATTENDANCES = ATTENDANCES+"/delete";
    public static final String CREATE_ATTENDANCES = ATTENDANCES+"/create";
    public static final String DETAIL_ATTENDANCES = ATTENDANCES+"/detailAttendances/";
    public static final String UPDATE_ATTENDANCES = ATTENDANCES+"/update";




}
