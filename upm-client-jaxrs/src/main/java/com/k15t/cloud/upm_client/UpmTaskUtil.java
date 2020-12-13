package com.k15t.cloud.upm_client;


import org.json.JSONObject;

import java.util.Optional;


public class UpmTaskUtil {


    private UpmTaskUtil() {
    }


    public static void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public static int getPollDelay(JSONObject jsonObject) {
        if (jsonObject.keySet().contains("pollDelay")) {
            return jsonObject.getInt("pollDelay");
        } else if (jsonObject.keySet().contains("pingAfter")) {
            return jsonObject.getInt("pingAfter");
        }
        throw new IllegalArgumentException("pingAfter or pollDelay are not contained in " + jsonObject);
    }


    public static String getInstallTaskLink(JSONObject jsonObject) {
        return jsonObject.getJSONObject("links").getString("alternate");
    }


    public static boolean getTaskDone(JSONObject jsonObject) {
        return jsonObject.getBoolean("done");
    }


    public static Optional<String> getErrorCode(JSONObject jsonObject) {
        if (jsonObject.keySet().contains("error")) {
            return Optional.of(jsonObject.getJSONObject("error").getString("code"));
        }
        return Optional.empty();
    }


    public static String substringAfter(String str, String separator) {
        if (str == null || str.length() == 0) {
            return str;
        } else if (separator == null) {
            return "";
        } else {
            int pos = str.indexOf(separator);
            return pos == -1 ? "" : str.substring(pos + separator.length());
        }
    }


}
