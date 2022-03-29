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

import com.huawei.agconnect.common.api.Logger;
import com.huawei.chitchatapp.BuildConfig;

public class AppLog {

    public static void logD(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Logger.d(tag, message);
        }
    }

    public static void logW(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Logger.w(tag, message);
        }
    }

    public static void logV(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Logger.v(tag, message);
        }
    }

    public static void logE(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Logger.e(tag, message);
        }
    }

    public static void logI(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Logger.i(tag, message);
        }
    }
}
