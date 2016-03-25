package com.example.dacnaviapp.util;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

public class JSONConverter {

    public final static String TAG = "JsonConverter";

    public static String getSearchDARequestAsJson(HashMap keyVal){
        JSONObject obj = new JSONObject();

        try {
            Log.d(TAG, "rType" + (String) keyVal.get(1));

            if((keyVal.get(1).equals("1"))){

                obj.put("rType", (String) keyVal.get(1));
                obj.put("tag1", (String) keyVal.get(2));

                if(!keyVal.get(3).equals(""))
                    obj.put("tag2", (String) keyVal.get(3));

                if(!keyVal.get(4).equals(""))
                    obj.put("tag3", (String) keyVal.get(4));

            } else if((keyVal.get(1).equals("2"))){

                obj.put("rType", (String) keyVal.get(1));
                obj.put("latitude", new Double((String) keyVal.get(2)));
                obj.put("longtitude", new Double((String) keyVal.get(3)));
            }

        } catch (Exception e) {
            Log.e(TAG, "Could not retrieve the GPS location: " + e.getMessage());
        }

        return obj.toString();
    }
}

