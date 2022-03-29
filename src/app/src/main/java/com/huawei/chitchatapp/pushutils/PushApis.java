/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.chitchatapp.pushutils;

import android.content.Context;
import android.os.StrictMode;

import com.google.gson.Gson;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.model.BearerRequest;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.chitchatapp.utils.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PushApis {

    private Context context;
    private static final String TAG = PushApis.class.getSimpleName();

    public PushApis(Context context) {
        this.context = context;
    }

    public void sendPushNotification(String chatId, String message, String appId, String messageData, String userPushTokens) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String response = "";
            URL url = new URL(Constants.TOKEN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("POST", "/oauth2/v3/token   HTTP/1.1");
            connection.setRequestProperty("Host", "oauth-login.cloud.huawei.com");
            HashMap<String, String> params = new HashMap<>();
            params.put("grant_type", "client_credentials");
            params.put("client_secret", Constants.CLIENT_SECRET);
            params.put("client_id", Constants.CLIENT_ID);
            String postDataLength = getDataString(params);
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(postDataLength);
            writer.flush();
            writer.close();
            os.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }
            AppLog.logE("Response", response);
            Gson gson = new Gson();
            BearerRequest bearerRequest = gson.fromJson(response, BearerRequest.class);
            triggerPush(bearerRequest.getAccessToken(), appId, chatId, message, messageData, userPushTokens);
        } catch (Exception e) {
            AppLog.logE(TAG, e.getMessage());
        }
    }

    private String getDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private void triggerPush(String bearer, String appId, String chatId, String messageType, String messageData, String userPushTokens) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String response = null;
            URL url = new URL("https://push-api.cloud.huawei.com/v1/" + appId + "/messages:send");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Bearer " + bearer);
            connection.setRequestProperty("Host", "oauth-login.cloud.huawei.com");
            connection.setRequestProperty("POST", "/oauth2/v2/token   HTTP/1.1");
            OutputStream os = connection.getOutputStream();
            Data data = new Data();
            data.message = messageType;
            data.roomId = chatId;
            data.messageData = messageData;
            data.sender_name = "Garbad Zala";
            data.sender_phone = "9099903898";
            data.title = context.getResources().getString(R.string.app_name);

            ArrayList<String> token = new ArrayList<>();
            token.add(userPushTokens);

            Message message = new Message();
            message.tokens = token;
            message.data = data.toString();

            PushMessageRequest pushMessageRequest = new PushMessageRequest();

            pushMessageRequest.message = message;
            pushMessageRequest.validate_only = false;

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(gson.toJson(pushMessageRequest, PushMessageRequest.class));
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();
            int responseCode = connection.getResponseCode();
            String line = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
            AppLog.logE(TAG, response);
        } catch (Exception e) {
            AppLog.logE(TAG, e.getMessage());
        }
    }
}
