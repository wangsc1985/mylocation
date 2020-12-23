package com.wangsc.mylocation.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class _JsonUtils {
    public static JSONObject getJSONObjectByKey(JSONArray jsonArray, Object key, Object value) throws JSONException {
        JSONObject res = null;

        for (int i=0;i<jsonArray.length();i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if(obj.getString(key.toString()).equals(value)){
                res = obj;
            }
        }
        return res;
    }

    public static String getValueByKey(Object json, Object key) throws JSONException {
        JSONObject res = new JSONObject(json.toString());
        return res.get(key.toString()).toString();

    }

    public static boolean isContainsKey(Object json, Object key) throws JSONException {
        JSONObject res = new JSONObject(json.toString());
        return !res.isNull(key.toString());
    }

}
