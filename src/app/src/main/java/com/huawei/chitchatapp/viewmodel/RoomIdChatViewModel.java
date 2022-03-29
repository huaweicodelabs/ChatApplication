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

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.ListenerHandler;
import com.huawei.agconnect.cloud.database.OnSnapshotListener;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.chitchatapp.dao.CloudDBHelper;
import com.huawei.chitchatapp.dao.DBConstants;
import com.huawei.chitchatapp.dbcloud.ChatRoomId;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.chitchatapp.utils.OnApiError;
import com.huawei.chitchatapp.utils.OnDBZoneOpen;
import com.huawei.hmf.tasks.OnCanceledListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RoomIdChatViewModel extends ViewModel {

    private static final String TAG = "ChatViewModel";

    private ArrayList<ChatRoomId> chatRoomIdList = new ArrayList<>();
    private MutableLiveData<String> getRoomId;
    private OnApiError mOnApiError;
    private int counter;
    private ListenerHandler mRegister;
    private Integer mRoomDataIndex = 0;
    private ReadWriteLock mReadWriteLock = new ReentrantReadWriteLock();

    public void initSnapShot() {
        subscribeSnapShot();
    }

    public MutableLiveData<String> getRoomId(OnApiError onApiError) {
        mOnApiError = onApiError;
        if (getRoomId == null) {
            getRoomId = new MutableLiveData<>();
        }
        return getRoomId;
    }

    public void callGetRoomId(final String userMobileTo, final String userMobileFrom, final boolean isFrom) {
        String roomId = null;
        if (chatRoomIdList != null && chatRoomIdList.size() > 0) {
            for(ChatRoomId chatRoomId : chatRoomIdList){
                if (chatRoomId.getUserMobileFrom().equalsIgnoreCase(userMobileFrom)
                        && chatRoomId.getUserMobileTo().equalsIgnoreCase(userMobileTo)) {
                    roomId = chatRoomId.getRoomId();
                    break;
                } else if (chatRoomId.getUserMobileFrom().equalsIgnoreCase(userMobileTo)
                        && chatRoomId.getUserMobileTo().equalsIgnoreCase(userMobileFrom)) {
                    roomId = chatRoomId.getRoomId();
                    break;
                }
            }

            if(roomId==null){
                AppLog.logE(getClass().getName(), "WE ARE INSIDE CALLCREATEROOMID");
                callCreateRoomId(userMobileTo, userMobileFrom);
            }else{
                AppLog.logE(getClass().getName(), "WE GOT ROOM ID AS ===> "+ roomId);
                getRoomId.postValue(roomId);
            }

        }
    }

    private CloudDBZoneQuery<ChatRoomId> createQuery(String userMobileTo, String userMobileFrom, boolean isFrom) {

        if (isFrom) {
            return CloudDBZoneQuery.where(ChatRoomId.class)
                    .equalTo(DBConstants.USER_MOBILE_TO, userMobileFrom)
                    .equalTo(DBConstants.USER_MOBILE_FROM, userMobileTo);
        } else {
            return CloudDBZoneQuery.where(ChatRoomId.class)
                    .equalTo(DBConstants.USER_MOBILE_TO, userMobileTo)
                    .equalTo(DBConstants.USER_MOBILE_FROM, userMobileFrom);
        }

    }

    private OnSuccessListener<CloudDBZoneSnapshot<ChatRoomId>> getSuccessListener(String mobileTo, String mobileFrom) {
        counter = counter + 1;
        return new OnSuccessListener<CloudDBZoneSnapshot<ChatRoomId>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<ChatRoomId> snapshot) {
                if (snapshot != null && snapshot.getSnapshotObjects().size() > 0) {
                    for (int i = 0; i < snapshot.getSnapshotObjects().size(); i++) {
                        try {
                            ChatRoomId chatRoomId = snapshot.getSnapshotObjects().get(i);
                            if (chatRoomId.getUserMobileFrom().equalsIgnoreCase(mobileFrom) && chatRoomId.getUserMobileTo().equalsIgnoreCase(mobileTo)) {
                                getRoomId.postValue(chatRoomId.getRoomId());
                                return;
                            } else if (chatRoomId.getUserMobileFrom().equalsIgnoreCase(mobileTo) && chatRoomId.getUserMobileTo().equalsIgnoreCase(mobileFrom)) {
                                getRoomId.postValue(chatRoomId.getRoomId());
                                return;
                            }
                        } catch (AGConnectCloudDBException e) {
                            mOnApiError.onError(e.getLocalizedMessage(), e);
                            e.printStackTrace();
                            return;
                        }
                    }
                } else {

                    if (counter <= 1) {
                        callGetRoomId(mobileTo, mobileFrom, true);
                    } else {
                        counter = 0;
                        callCreateRoomId(mobileTo, mobileFrom);
                    }
                }
            }
        };
    }

    OnFailureListener roomIdFailureListener = new OnFailureListener() {

        @Override
        public void onFailure(Exception e) {
            mOnApiError.onError(e.getLocalizedMessage(), e);
        }
    };

    OnCanceledListener roomIdCancelListener = new OnCanceledListener() {
        @Override
        public void onCanceled() {
            mOnApiError.onError("Task is cancelled by user", new Throwable("task cancelled"));
        }
    };

    public void callCreateRoomId(final String userMobileTo, final String userMobileFrom) {

        CloudDBHelper.getInstance().openDb(new OnDBZoneOpen() {
            @Override
            public void isDBZoneOpen(boolean isConnected, CloudDBZone cloudDBZone) {
                if (cloudDBZone != null) {
                    ChatRoomId roomId = new ChatRoomId();
                    mRoomDataIndex = mRoomDataIndex + 1;
                    AppLog.logE(TAG, "New ROOM IS WILL BE ===> " + mRoomDataIndex);
                    roomId.setRoomId(String.valueOf(mRoomDataIndex));
                    roomId.setUserMobileTo(userMobileTo);
                    roomId.setUserMobileFrom(userMobileFrom);
                    roomId.setUpdateShadowFlag(true);
                    Task<Integer> insertTask = cloudDBZone.executeUpsert(roomId);
                    insertTask.addOnSuccessListener(insertSuccessListener(roomId))
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    AppLog.logE(TAG, "Exception in creating room id " + e.getLocalizedMessage());
                                }
                            }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            AppLog.logE(TAG, "Inside on cancel listener");
                        }
                    });
                } else {
                    if (mOnApiError != null) {
                        mOnApiError.onError("Cloud database zone is null", new Throwable("CloudDBZone is null"));
                    }
                }
            }
        });

    }

    private OnSuccessListener<Integer> insertSuccessListener(ChatRoomId roomId) {

        return new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                getRoomId.postValue(roomId.getRoomId());
            }
        };

    }

    private void subscribeSnapShot() {

        CloudDBHelper.getInstance().openDb(new OnDBZoneOpen() {
            @Override
            public void isDBZoneOpen(boolean isConnected, CloudDBZone cloudDBZone) {
                if (cloudDBZone == null) {
                    return;
                }
                try {
                    CloudDBZoneQuery<ChatRoomId> snapshotQuery = CloudDBZoneQuery.where(ChatRoomId.class).equalTo(DBConstants.UPDATE_SHADOW_FLAG, true);
                    mRegister = cloudDBZone.subscribeSnapshot(snapshotQuery,
                            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY, mSnapshotListener);
                } catch (AGConnectCloudDBException e) {
                    AppLog.logW(TAG, "Exception in getting snapshot register");
                }

            }
        });

    }

    private OnSnapshotListener<ChatRoomId> mSnapshotListener = (cloudDBZoneSnapshot, e) -> {
        if (e != null) {
            AppLog.logE(getClass().getName(), "Snap shot is null ===>");
            return;
        }
        chatRoomIdList.clear();
        CloudDBZoneObjectList<ChatRoomId> snapshotObjects = cloudDBZoneSnapshot.getSnapshotObjects();
        try {
            if (snapshotObjects != null) {
                while (snapshotObjects.hasNext()) {
                    ChatRoomId chatRoomId = snapshotObjects.next();
                    chatRoomIdList.add(chatRoomId);
                    AppLog.logE(TAG, "We got room id ===>" + chatRoomId.getRoomId());
                    updateChatRoomIndex(chatRoomId);
                }
            }
        } catch (AGConnectCloudDBException snapshotException) {
            AppLog.logW(TAG, "Snap shot exception");
        } finally {
            cloudDBZoneSnapshot.release();
        }
    };

    private void updateChatRoomIndex(ChatRoomId chatRoomId) {
        try {
            mReadWriteLock.writeLock().lock();
            int roomId = Integer.parseInt(chatRoomId.getRoomId());
            if (mRoomDataIndex < roomId) {
                mRoomDataIndex = roomId;
            }
        } finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

}
