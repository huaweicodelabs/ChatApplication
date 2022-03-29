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

import java.util.Locale;

public class TimeUtil {

    public static final int MS_TO_SECOND = 1000;

    private static final int SECOND_TO_MINUTE = 60;

    private static final int SECOND_TO_HOUR = 60 * 60;

    public static String formatLongToTimeStr(int time) {
        int totalSeconds = time / MS_TO_SECOND;
        int seconds = totalSeconds % SECOND_TO_MINUTE;
        int minutes = totalSeconds / SECOND_TO_MINUTE;
        int hours = totalSeconds / SECOND_TO_HOUR;

        if (hours > 0) {
            minutes %= SECOND_TO_MINUTE;
            return String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
        }
    }
}
