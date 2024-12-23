package com.sedulous.obhsadi.service;

import org.json.JSONObject;

public class WebServicesURLClass {

    public static final String BASE_URL="http://obhsadi.projectrailway.in/api/User/";

    public static final String LOGIN_URL=BASE_URL+"login";
    public static final String PENALTY_LIST_URL=BASE_URL+"penaltylist";
    public static final String INSPECTOR_FEEDBACK_URL=BASE_URL+"inspectorfeedback";
    public static final String PASSENGER_QUESTION_URL=BASE_URL+"questionlist";
    public static final String PASSENGER_FEEDBACK_URL=BASE_URL+"passengerfeedback";
    public static final String TTE_QUESTIONS_URL=BASE_URL+"ttquestionlist";
    public static final String TTE_FEEDBACK_URL=BASE_URL+"ttfeedback";
    public static final String OTP_VERIFY_URL=BASE_URL+"sendpassengerotp";
    public static final String FILE_UPLOAD_URL =BASE_URL+"savepassengerfiles";
    public static final String GET_TRAIN_WISE_COACH_URL =BASE_URL+"getcoachlist";
    public static final String GET_TASK_CAT =BASE_URL+"getpcatList";

    HttpResponseClass httpResponseClass=new HttpResponseClass();

    public JSONObject userLoginMethod(JSONObject userdata)
    {
        return httpResponseClass.getResponseByPut(LOGIN_URL,userdata);
    }
    public JSONObject getPenaltyListMethod(JSONObject userdata)
    {
        return httpResponseClass.getResponseByPut(PENALTY_LIST_URL,userdata);
    }

    public JSONObject submitInspectorFeedbackMethod(JSONObject userdata)
    {
        return httpResponseClass.getResponseByPut(INSPECTOR_FEEDBACK_URL,userdata);
    }

    public JSONObject getPassengerQuestionMethod(JSONObject userdata)
    {
        return httpResponseClass.getResponseByPut(PASSENGER_QUESTION_URL,userdata);
    }

    public JSONObject submitPassengerFeedbackMethod(JSONObject userdata)
    {
        return httpResponseClass.getResponseByPut(PASSENGER_FEEDBACK_URL,userdata);
    }

    public JSONObject otpVerificationMethod(JSONObject userdata)
    {
        return httpResponseClass.getResponseByPut(OTP_VERIFY_URL,userdata);
    }

    public JSONObject getTteQuestionsListMethod(JSONObject userdata)
    {
        return httpResponseClass.getResponseByPut(TTE_QUESTIONS_URL,userdata);
    }

    public JSONObject getTrainWiseCoachListMethod(JSONObject userdata)
    {
        return httpResponseClass.getResponseByPut(GET_TRAIN_WISE_COACH_URL,userdata);
    }

}
