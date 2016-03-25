package com.example.dacnaviapp.util;

import android.nfc.Tag;
import android.util.Log;

import com.example.dacnaviapp.bean.DA2Response;
import com.example.dacnaviapp.bean.DAObject;
import com.example.dacnaviapp.bean.DAType1;
import com.example.dacnaviapp.bean.DAType3;
import com.example.dacnaviapp.bean.DATypePostal;
import com.example.dacnaviapp.bean.Vertex;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONDecorder {

    public final static String TAG = "JsonDecorder";

    public static <T> T decodeStatusFromJson(String jsonResult) {
        try {

            Log.d(TAG, "JSON RESULT AT JSON DECODER - " + jsonResult);

            JSONObject reader = new JSONObject(jsonResult);
            String status = reader.getString("Status");

            if(status.equals("Unregistered DA")){
                String type = reader.getString("rtype");
                if (type.equals("DA")) {
                    DAType1 type1Response = decodeDA1ObjectFromResponse(status, jsonResult);
                    return (T) type1Response;
                } else if (type.equals("location")) {
                    DAType3 type3Response = decodeDA3ObjectFromResponse(status, jsonResult);
                    return (T) type3Response;
                }
            } else if(status.equals("Success")){

                if(reader.has("DAList")){
                    MultipleDAResponse multipleDAResponse = decodeMultipleDAFromResponse(status,jsonResult);
                    return (T) multipleDAResponse;
                } else {
                    DA2Response type2Response = decodeDA2ObjectFromResponse(status, jsonResult);
                    return (T) type2Response;
                }
            } else if (status.equals("location")){
                DATypePostal typePostal = decodeDAPostalObjectFromResponse(jsonResult);
                return  (T) typePostal;
            } else if(status.equals("noDA")){
                return (T)status;
            } else if(status.equals("error")){
                return (T)status;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    private static DAType1 decodeDA1ObjectFromResponse(
            String status, String jsonResult) {
        Gson gson = new Gson();
        DAResponse response = gson.fromJson(
                jsonResult, DAResponse.class);
        DAType1 daType1 = new DAType1();

        if (response != null) {
            List<Vertex> vertices = new ArrayList<>();
            for (int i = 0; i < response.Polygon.length; i++) {
                Vertex cur = new Vertex(response.Polygon[i].latitude,response.Polygon[i].longtitude);
                vertices.add(cur);
            }
            com.example.dacnaviapp.bean.Polygon curPolygon = new
                    com.example.dacnaviapp.bean.Polygon(vertices);
            daType1.setPolygon(curPolygon);
            daType1.setTag1(response.tag1);
            daType1.setTag2(response.tag2);
            daType1.setTag3(response.tag3);
        }
        return daType1;
    }

    private static DAType3 decodeDA3ObjectFromResponse(
            String status, String jsonResult) {
        Gson gson = new Gson();
        DAResponseLoc response = gson.fromJson(
                jsonResult, DAResponseLoc.class);
        DAType3 daType3 = new DAType3();

        if (response != null) {
            daType3.setTag1(response.tag1);
            daType3.setTag2(response.tag2);
            daType3.setTag3(response.tag3);
            daType3.setLatitude(response.latitude);
            daType3.setLongtitude(response.longtitude);
        }

        return daType3;
    }

    private static MultipleDAResponse decodeMultipleDAFromResponse(
            String status, String jsonResult){

        Gson gson = new Gson();
        MultipleDAResponse response = gson.fromJson(
                jsonResult, MultipleDAResponse.class);
        return  response;
    }

    private static DA2Response decodeDA2ObjectFromResponse(
            String status, String jsonResult) {

        Gson gson = new Gson();
        DA2Response response = gson.fromJson(
                jsonResult, DA2Response.class);

        return response;
    }

    private static DATypePostal decodeDAPostalObjectFromResponse(String jsonResult){
        Gson gson = new Gson();
        DATypePostal response = gson.fromJson(
                jsonResult, DATypePostal.class);
        return response;
    }

    public class DAResponse {
        private String Status;
        private Polygon[] Polygon;
        private String tag1;
        private String tag2;
        private String tag3;

        public DAResponse() {

        }
    }

    public class MultipleDAResponse{

        private String Status;
        private DAObject[] DAList;

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        public DAObject[] getDAList() {
            return DAList;
        }

        public void setDAList(DAObject[] DAList) {
            this.DAList = DAList;
        }

        public MultipleDAResponse(){

        }
    }

    public class DAResponseLoc {
        private String Status;
        private String tag1;
        private String tag2;
        private String tag3;
        private double longtitude;
        private double latitude;

        public DAResponseLoc() {

        }
    }

    public static class Polygon {
        private double latitude;
        private double longtitude;

    }


}
