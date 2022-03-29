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
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

    public static final String PLAY_FILE_NAME = "video_kit_demo.txt";

    public static final String ENCODE_UTF_8 = "utf-8";


    public static String parseAssetsFile(Context context, String charsetName) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(context.getAssets().open(PLAY_FILE_NAME), charsetName);
            char[] buffer = new char[1024];
            int count = 0;
            StringBuilder builder = new StringBuilder();
            while ((count = reader.read(buffer, 0, 1024)) > 0) {
                builder.append(buffer, 0, count);
            }
            return builder.toString();
        } catch (IOException e) {
            LogUtil.i("get assets file error :" + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                LogUtil.i("close InputStreamReader error :" + e.getMessage());
            }
        }
        return StringUtil.emptyStringValue();
    }


    public static boolean createFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (!file.exists()) {
                return file.mkdirs();
            } else {
                return true;
            }
        }
        return false;
    }
}
