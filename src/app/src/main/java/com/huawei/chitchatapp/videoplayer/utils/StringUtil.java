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

package com.huawei.chitchatapp.videoplayer.utils;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.text.TextUtils;
import android.widget.TextView;

public class StringUtil {

    public static String emptyStringValue() {
        return "";
    }

    public static boolean isEmpty(String value) {
        return TextUtils.isEmpty(value) || value.trim().length() == 0;
    }

    public static String getNotEmptyString(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        } else {
            return value;
        }
    }

    public static String[] getStringArray(String value, String split) {
        return value.split(split);
    }

    public static String getStringFromResId(Context context, int resId) {
        String stringValue = "";
        try {
            stringValue = context.getResources().getString(resId);
        } catch (NotFoundException e) {
            LogUtil.i("get String from resId fail:" + e.getMessage());
        }
        return stringValue;
    }

    public static float valueOf(String value) {
        float intValue = 0f;
        if (TextUtils.isEmpty(value)) {
            return intValue;
        }
        try {
            intValue = Float.valueOf(value);
        } catch (Exception e) {
            LogUtil.i("Integer parseInt error :" + e.getMessage());
        }
        return intValue;
    }
    public static void setTextValue(TextView textView, String value) {
        if (textView != null) {
            textView.setText(value);
        }
    }
}
