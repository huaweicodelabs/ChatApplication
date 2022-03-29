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
import com.huawei.chitchatapp.dao.DBConstants;
import com.huawei.chitchatapp.dbcloud.User;
import com.huawei.chitchatapp.dbcloud.UserChat;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;


public class MessageViewModel extends ViewModel {

    public MutableLiveData<ArrayList<UserChat>> userChatMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> userUpdatedSuccessfully = new MutableLiveData<>();
    public MutableLiveData<String> tokenMutableLiveData = new MutableLiveData<>();

    private static final String TAG = MessageViewModel.class.getSimpleName();


    public void getUserChatByRoomID(String roomId, Context context) {
        CloudDBZoneQuery<UserChat> query = CloudDBZoneQuery.where(UserChat.class).equalTo(DBConstants.roomId, roomId).orderByAsc(DBConstants.MESSAGE_TIMESTAMP);
        getUserChat(query, context);
    }

    private void getUserChat(CloudDBZoneQuery<UserChat> userQuery, Context context) {
        CloudDBHelper.getInstance().openDb((isConnected, cloudDBZone) -> {
            Task<CloudDBZoneSnapshot<UserChat>> queryTask = cloudDBZone.executeQuery(userQuery,
                    CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
            queryTask.addOnSuccessListener(userChatCloudDBZoneSnapshot -> {
                processSnapShot(userChatCloudDBZoneSnapshot.getSnapshotObjects(), context);
            });
        });
    }

    private void processSnapShot(CloudDBZoneObjectList<UserChat> userCloudDBZoneSnapshot, Context context) {
        if (userCloudDBZoneSnapshot != null) {
            ArrayList<UserChat> users = new ArrayList<>();
            while (userCloudDBZoneSnapshot.hasNext()) {
                UserChat user = null;
                try {
                    user = userCloudDBZoneSnapshot.next();
                    users.add(user);
                } catch (AGConnectCloudDBException e) {
                    e.printStackTrace();
                    CloudDBHelper.getInstance().closeDb(context);
                }

            }
            userChatMutableLiveData.setValue(users);
            CloudDBHelper.getInstance().closeDb(context);
        }
    }

    public void saveUserChat(UserChat userChat) {
        CloudDBHelper.getInstance().openDb((isConnected, cloudDBZone) -> {
            if (cloudDBZone != null) {
                Task<Integer> insertTask = cloudDBZone.executeUpsert(userChat);
                insertTask
                        .addOnSuccessListener(integer ->
                                userUpdatedSuccessfully.setValue(true))
                        .addOnFailureListener(e -> {
                            userUpdatedSuccessfully.setValue(false);
                            AppLog.logE(TAG, e.getMessage());
                        });

            } else {
                userUpdatedSuccessfully.setValue(false);
            }
        });
    }

    public void queryForToken(String phoneNumber, Context context) {
        CloudDBZoneQuery<User> query = CloudDBZoneQuery.where(User.class).equalTo(DBConstants.userNumber, phoneNumber);
        processNumberCheck(query, context);
    }

    private void processNumberCheck(CloudDBZoneQuery<User> query, Context context) {
        CloudDBHelper.getInstance().openDb((isConnected, cloudDBZone) -> {
            if (cloudDBZone != null) {
                Task<CloudDBZoneSnapshot<User>> queryTask = cloudDBZone.executeQuery(query,
                        CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
                queryTask.addOnSuccessListener(userCloudDBZoneSnapshot -> processSnapShot(userCloudDBZoneSnapshot, context));
            }
        });
    }

    private void processSnapShot(CloudDBZoneSnapshot<User> userCloudDBZoneSnapshot, Context context) {
        try {
            if (userCloudDBZoneSnapshot.getSnapshotObjects() != null &&
                    userCloudDBZoneSnapshot.getSnapshotObjects().size() >= 1) {
                CloudDBZoneObjectList<User> userCloudDBZoneObjectList = userCloudDBZoneSnapshot.getSnapshotObjects();
                User user = userCloudDBZoneObjectList.get(0);
                tokenMutableLiveData.postValue(user.getUserPushToken());
            } else {
                tokenMutableLiveData.setValue(null);
            }
        } catch (AGConnectCloudDBException e) {
            tokenMutableLiveData.setValue(null);
            e.printStackTrace();
        }
        CloudDBHelper.getInstance().closeDb(context);
    }


}
