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

package com.huawei.chitchatapp.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.huawei.chitchatapp.model.MapModel;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.HttpUrl;

public class StaticMapUrl {

    private static final String TAG = StaticMapUrl.class.getSimpleName();

    public static String getStaticMapWithUrlSignature(MapModel map) {

        String mLocation = map.getLatitude() + "," + map.getLongitude();
        HttpUrl httpUrl = new HttpUrl.Builder().scheme("https")
                .host("mapapi.cloud.huawei.com")
                .encodedPath("/mapApi/v1/mapService/getStaticMap")
                .addQueryParameter("width", "200")
                .addQueryParameter("height", "200")
                .addQueryParameter("location", mLocation)
                .addQueryParameter("scale", "1")
                .addQueryParameter("zoom", "15")
                .addQueryParameter("language", "en")
                .addQueryParameter("logo", "size:logo_normal|logoAnchor:bottomleft")
                .addQueryParameter("markers", map.getMarkers())
                .addQueryParameter("markerStyles", "size:tiny|color:blue|label:p")
                .build();
        String urlToSign = httpUrl.toString();
        String secret = Constants.MAP_VIEW_BUNDLE_KEY;
        String signature = null;
        signature = sign(urlToSign, secret);
        String url = urlToSign + "&key=" + getApiKey() + "&signature=" + signature;
        return url;
    }

    private static String sign(String urlToSign, String secret) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            URL url = null;
            String signature = null;
            try {
                url = new URL(urlToSign);
            } catch (MalformedURLException e) {
                AppLog.logE(TAG, e.getMessage());
            }
            if (url != null && url.getPath() != null) {
                String canonicalUrl = url.getPath() + "?" + new CanonicalQueryString(url.getQuery()).toString();
                SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
                Mac mac = null;
                try {
                    mac = Mac.getInstance("HmacSHA256");
                    mac.init(secretKey);
                    byte[] sigBytes = canonicalUrl.getBytes(Charset.defaultCharset());
                    signature = Base64.getEncoder().encodeToString(sigBytes);
                    signature = signature.replace('+', '-');
                    signature = signature.replace('/', '_');


                } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                    AppLog.logE(TAG, e.getMessage());
                }
                return signature;

            } else {
                return null;
            }
        }
        return null;
    }

    static class CanonicalQueryString {
        private static final int INIT_BUFFER_SIZE = 128;
        public static final String PARAMETER_EQ = "=";
        public static final String PARAMETER_SEP = "&";
        private SortedMap<String, List<String>> params = new TreeMap<>();

        @RequiresApi(api = Build.VERSION_CODES.N)
        public CanonicalQueryString(String queryString) {
            if (queryString == null || queryString.length() == 0) {
                return;
            }

            for (String pair : queryString.split(PARAMETER_SEP)) {
                int idx = pair.indexOf(PARAMETER_EQ);
                if (idx != -1) {
                    String value = pair.substring(idx + 1);
                    if (value.length() == 0) {
                        continue;
                    }
                    String paramKey = pair.substring(0, idx);
                    List<String> values = params.computeIfAbsent(paramKey, key -> new ArrayList<>());
                    try {
                        values.add(URLDecoder.decode(value, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        AppLog.logE(TAG, e.getMessage());
                    }
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(INIT_BUFFER_SIZE);
            for (SortedMap.Entry<String, List<String>> item : params.entrySet()) {
                String key = item.getKey();
                for (String value : item.getValue()) {
                    if (sb.length() > 0) {
                        sb.append(PARAMETER_SEP);
                    }
                    sb.append(key).append(PARAMETER_EQ).append(value);
                }
            }

            return sb.toString();
        }
    }

    private static String getApiKey() {
        String apiKey = Constants.MAP_VIEW_API_KEY;
        try {
            return URLEncoder.encode(apiKey, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            AppLog.logE(TAG, e.getMessage());
            return null;
        }
    }
}
