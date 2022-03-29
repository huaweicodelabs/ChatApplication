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

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.chitchatapp.dao.CloudDBHelper;
import com.huawei.chitchatapp.dbcloud.User;
import com.huawei.chitchatapp.utils.ChitChatSharedPref;
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;

public class ContactViewModel extends ViewModel {

    private static final String TAG = ContactViewModel.class.getSimpleName();

    public MutableLiveData<ArrayList<User>> userMutableLiveData = new MutableLiveData<>();

    public void getContact(Context context) {
        CloudDBHelper.getInstance().openDb((isConnected, cloudDBZone) -> {
            if (isConnected) {
                CloudDBZoneQuery<User> snapshotQuery = CloudDBZoneQuery.where(User.class);
                Task<CloudDBZoneSnapshot<User>> queryTask = cloudDBZone.executeQuery(snapshotQuery,
                        CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
                queryTask.addOnSuccessListener(userCloudDBZoneSnapshot -> {
                    checkRetrieveProcess(userCloudDBZoneSnapshot.getSnapshotObjects(), context);
                });
            }
        });
    }

    public void checkRetrieveProcess(CloudDBZoneObjectList<User> snapshotObjects, Context context) {
        if (snapshotObjects != null) {
            ArrayList<User> users = new ArrayList<>();
            while (snapshotObjects.hasNext()) {
                User user = null;
                try {
                    user = snapshotObjects.next();
                    if(!ChitChatSharedPref.getInstance().getString(Constants.PHONE_NUMBER, "").equals(user.getUserPhone())) {
                        users.add(user);
                    }
                } catch (AGConnectCloudDBException e) {
                    e.printStackTrace();
                }

            }
            userMutableLiveData.postValue(users);
        }

    }
}
