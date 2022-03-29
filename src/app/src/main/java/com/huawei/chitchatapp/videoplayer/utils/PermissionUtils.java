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

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    public static boolean checkPermission(Context context, String permissionValue) {
        if (!isMNC()) {
            return true;
        }

        if (StringUtil.isEmpty(permissionValue)) {
            return false;
        }

        if (PermissionChecker.checkSelfPermission(context, permissionValue) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isMNC() {
        return Build.VERSION.SDK_INT >= 23;
    }

    public static boolean requestPermissionsIfNeed(Activity activity, String[] permissions, int code) {
        if (activity == null || permissions == null) {
            return false;
        }
        List<String> requestList = new ArrayList<>();
        for (String permission : permissions) {
            boolean request = !checkPermission(activity, permission);
            if (request) {
                requestList.add(permission);
            }
        }
        if (requestList.size() > 0) {
            if (isMNC()) {
                activity.requestPermissions(requestList.toArray(new String[requestList.size()]), code);
            }
            return true;
        }
        return false;
    }
}
