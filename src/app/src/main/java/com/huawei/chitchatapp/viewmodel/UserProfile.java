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
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    public MutableLiveData<Boolean> userMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<User> userLiveData = new MutableLiveData<>();
    public MutableLiveData<List<UserChat>> userMediaLiveData = new MutableLiveData<>();

    public void saveUser(User user, Context context) {
        CloudDBHelper.getInstance().openDb((isConnected, cloudDBZone) -> {
            if (isConnected && cloudDBZone != null) {
                if (cloudDBZone == null) {
                    return;
                } else {
                    Task<Integer> insertTask = cloudDBZone.executeUpsert(user);
                    insertTask.addOnSuccessListener(integer -> {
                        userMutableLiveData.setValue(true);
                        CloudDBHelper.getInstance().closeDb(context);
                    }).addOnFailureListener(e -> {
                        userMutableLiveData.setValue(false);
                        CloudDBHelper.getInstance().closeDb(context);
                    });
                }
            }
        });
    }

    public void query(String phoneNumber, Context context) {
        this.context = context;
        CloudDBZoneQuery<User> query = CloudDBZoneQuery.where(User.class).equalTo(DBConstants.userNumber, phoneNumber);
        processNumberCheck(query);
    }

    private void processNumberCheck(CloudDBZoneQuery<User> query) {
        CloudDBHelper.getInstance().openDb((isConnected, cloudDBZone) -> {
            if (cloudDBZone != null) {
                Task<CloudDBZoneSnapshot<User>> queryTask = cloudDBZone.executeQuery(query,
                        CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
                queryTask.addOnSuccessListener(this::processSnapShot);
            }
        });
    }

    private void processSnapShot(CloudDBZoneSnapshot<User> userCloudDBZoneSnapshot) {
        try {
            if (userCloudDBZoneSnapshot.getSnapshotObjects() != null &&
                    userCloudDBZoneSnapshot.getSnapshotObjects().size() >= 1) {
                CloudDBZoneObjectList<User> userCloudDBZoneObjectList = userCloudDBZoneSnapshot.getSnapshotObjects();
                User user = userCloudDBZoneObjectList.get(0);
                userLiveData.setValue(user);
            } else {
                userLiveData.setValue(null);
            }
        } catch (AGConnectCloudDBException e) {
            userLiveData.setValue(null);
            e.printStackTrace();
        }
        CloudDBHelper.getInstance().closeDb(context);
    }

    public void getUserSharedMedia(String userMobile, String roomId){

        CloudDBZoneQuery<UserChat> query = CloudDBZoneQuery.where(UserChat.class)
                .equalTo(DBConstants.ROOM_ID, roomId)
                .equalTo(DBConstants.MESSAGE_TYPE, Constants.MESSAGE_TYPE_IMAGE);

        CloudDBHelper.getInstance().openDb((isConnected, cloudDBZone) -> {

            if(isConnected){
                Task<CloudDBZoneSnapshot<UserChat>> queryTask = cloudDBZone.executeQuery(query,
                        CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
                queryTask.addOnSuccessListener(this::processUserMediaSnapShot);
            }

        });


    }

    List<UserChat> userMediaChat = new ArrayList<>();

    private void processUserMediaSnapShot(CloudDBZoneSnapshot<UserChat> userCloudDBZoneSnapshot) {

        try {
            if (userCloudDBZoneSnapshot.getSnapshotObjects() != null &&
                    userCloudDBZoneSnapshot.getSnapshotObjects().size() >= 1) {
                CloudDBZoneObjectList<UserChat> userCloudDBZoneObjectList = userCloudDBZoneSnapshot.getSnapshotObjects();
                while(userCloudDBZoneObjectList.hasNext()){
                    UserChat userChat = userCloudDBZoneObjectList.next();
                    AppLog.logE(getClass().getName(), Constants.ROOM_ID+userChat.getRoomId());
                    AppLog.logE(getClass().getName(), DBConstants.SENDER_PHONE+userChat.getSenderPhone());
                    AppLog.logE(getClass().getName(), DBConstants.RECEIVER_PHONE+userChat.getReceiverPhone());
                    AppLog.logE(getClass().getName(), "MESSAGE DATA"+userChat.getMessageData());
                    userMediaChat.add(userChat);
                }
                userMediaLiveData.postValue(userMediaChat);
            } else {
                userMediaLiveData.setValue(null);
            }
        } catch (AGConnectCloudDBException e) {
            userMediaLiveData.setValue(null);
            e.printStackTrace();
        }
        CloudDBHelper.getInstance().closeDb(context);
    }

}
