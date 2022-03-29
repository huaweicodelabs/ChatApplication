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

package com.huawei.chitchatapp.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.OnSnapshotListener;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.chitchatapp.dbcloud.User;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class NumberCheck extends ViewModel {

    public MutableLiveData<Boolean> isUserMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<List<User>> userMutableLiveData = new MutableLiveData<>();
    private static final String TAG = NumberCheck.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    public Context context;

    private OnSnapshotListener<User> mSnapshotListener = (cloudDBZoneSnapshot, e) -> {
        if (e != null) {
            AppLog.logD(TAG, "onSnapshot: " + e.getMessage());
            return;
        }
        CloudDBZoneObjectList<User> snapshotObjects = cloudDBZoneSnapshot.getSnapshotObjects();
        List<User> users = new ArrayList<>();
        try {
            if (snapshotObjects != null) {
                while (snapshotObjects.hasNext()) {
                    User user = snapshotObjects.next();
                    users.add(user);
                    AppLog.logD(TAG, "onSnapshot: " + user);
                }
            }
            userMutableLiveData.postValue(users);
        } catch (AGConnectCloudDBException snapshotException) {
            Log.w(TAG, "onSnapshot:(getObject) " + snapshotException.getMessage());
        } finally {
            cloudDBZoneSnapshot.release();
        }
    };
}