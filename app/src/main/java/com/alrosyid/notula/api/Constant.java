package com.alrosyid.notula.api;

public class Constant {
//    public static final String URL = "https://app.jeevva.my.id/";
    public static final String URL = "http://10.0.2.2:81/";
//    public static final String URL = "http://192.168.100.4:81/";
    //Root
    public static final String HOME = URL+"api";
    //auth
    public static final String LOGIN = HOME+"/login";
    public static final String LOGOUT = HOME+"/logout";
    public static final String REGISTER = HOME+"/register";
    public static final String ACCOUNT = HOME+"/myAccount";
    public static final String UPDATE_USER = HOME+"/user/update";
    //Meetings
    public static final String MEETING = HOME+"/meetings";
    public static final String MY_MEETING = MEETING+"/myMeetings";
    public static final String DETAIL_MEETING = MEETING+"/detailMeetings/";
    public static final String DELETE_MEETINGS = MEETING+"/delete";
    public static final String EDIT_MEETINGS = MEETING+"/editMeetings/";
    public static final String CREATE_MEETINGS = MEETING+"/create";
    public static final String UPDATE_MEETINGS = MEETING+"/update";
    //Attendances
    public static final String ATTENDANCES =HOME+"/attendances";
    public static final String LIST_ATTENDANCES =ATTENDANCES+"/listAttendances/";
    public static final String DELETE_ATTENDANCES = ATTENDANCES+"/delete";
    public static final String CREATE_ATTENDANCES = ATTENDANCES+"/create";
    public static final String DETAIL_ATTENDANCES = ATTENDANCES+"/detailAttendances/";
    public static final String UPDATE_ATTENDANCES = ATTENDANCES+"/update";
    //Notula
    public static final String NOTULA = HOME+"/notulas";
    public static final String EDIT_NOTULA = NOTULA+"/editNotulas/";
    public static final String  DETAIL_NOTULA = NOTULA+"/detailNotulas/";
    public static final String CREATE_NOTULA = NOTULA+"/create";
    public static final String DELETE_NOTULA = NOTULA+"/delete";
    public static final String LIST_NOTULA = NOTULA+"/listNotulas/";
    public static final String UPDATE_NOTULA = NOTULA+"/update";
    //Points
    public static final String POINTS = HOME+"/points";
    public static final String LIST_POINTS = POINTS+"/listPoints/";
    public static final String DETAIL_POINTS = POINTS+"/detailPoints/";
    public static final String CREATE_POINTS = POINTS+"/create";
    public static final String DELETE_POINTS = POINTS+"/delete";
    public static final String UPDATE_POINTS = POINTS+"/update";
    //FollowUP
    public static final String FOLLOW_UP = HOME+"/followUp";
    public static final String LIST_FOLLOW_UP = FOLLOW_UP+"/listFollowUp/";
    public static final String DETAIL_FOLLOW_UP = FOLLOW_UP+"/detailFollowUp/";
    public static final String CREATE_FOLLOW_UP = FOLLOW_UP+"/create";
    public static final String DELETE_FOLLOW_UP = FOLLOW_UP+"/delete";
    public static final String UPDATE_FOLLOW_UP = FOLLOW_UP+"/update";


}
