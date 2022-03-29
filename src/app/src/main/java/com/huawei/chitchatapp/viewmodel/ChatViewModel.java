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

import com.google.gson.Gson;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.ListenerHandler;
import com.huawei.agconnect.cloud.database.OnSnapshotListener;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.chitchatapp.ChitChatApplication;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.dao.CloudDBHelper;
import com.huawei.chitchatapp.dao.DBConstants;
import com.huawei.chitchatapp.dbcloud.ChatRoomId;
import com.huawei.chitchatapp.dbcloud.User;
import com.huawei.chitchatapp.dbcloud.UserChat;
import com.huawei.chitchatapp.model.ChatHistoryModel;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.chitchatapp.utils.OnApiError;
import com.huawei.chitchatapp.utils.OnDBZoneOpen;
import com.huawei.hmf.tasks.OnCanceledListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChatViewModel extends ViewModel {

    private static final String TAG = "ChatViewModel";

    private String userPhone;
    private ListenerHandler mRegister;
    private Integer mRoomDataIndex = 0;
    private final ReadWriteLock mReadWriteLock = new ReentrantReadWriteLock();
    private MutableLiveData<ArrayList<ChatHistoryModel>> mListofChattedUser;
    private OnApiError mOnApiError;
    private int counter;
    private static final int CALL_ROOM_ID_QUERY = 10001;
    private static final int CALL_LAST_MESSAGE_QUERY = 10002;
    private static final int CALL_GET_USER_QUERY = 10003;
    private ArrayList<ChatHistoryModel> mChatHistoryModelList = new ArrayList<>();

    public MutableLiveData<ArrayList<ChatHistoryModel>> getListOfChattedUser(OnApiError onApiError) {
        this.mOnApiError = onApiError;
        if (mListofChattedUser == null) {
            mListofChattedUser = new MutableLiveData<>();
        }
        return mListofChattedUser;
    }


    private void showZoneOpenError() {
        if (mOnApiError != null) {
            mOnApiError.onError(ChitChatApplication.getInstance().getString(R.string.err_db_zone), new Throwable(ChitChatApplication.getInstance().getString(R.string.err_db_zone)));
        }

    }

    private OnFailureListener getOnFailureListener(int queryCode) {
        return e -> {
            switch (queryCode) {

                case CALL_ROOM_ID_QUERY:
                    mOnApiError.onError(ChitChatApplication.getInstance().getString(R.string.err_room_id_list) + e.getLocalizedMessage(), e);
                    break;

                case CALL_LAST_MESSAGE_QUERY:
                    mOnApiError.onError(ChitChatApplication.getInstance().getString(R.string.err_getting_last_message) + e.getLocalizedMessage(), e);
                    break;

                case CALL_GET_USER_QUERY:
                    mOnApiError.onError(ChitChatApplication.getInstance().getString(R.string.err_getting_user_data) + e.getLocalizedMessage(), e);
                    break;

            }

        };
    }

    private OnCanceledListener getOnCancelListener(int queryCode) {
        return () -> {
            mOnApiError.onError("Task is cancelled by user", new Throwable("task cancelled"));

            switch (queryCode) {

                case CALL_ROOM_ID_QUERY:
                    mOnApiError.onError("Get list of chatted user task is killed by user", new Throwable("task cancelled while CALL_ROOM_ID_QUERY"));
                    break;

                case CALL_LAST_MESSAGE_QUERY:
                    mOnApiError.onError("Getting last message task cancel by user", new Throwable("task cancelled while CALL_LAST_MESSAGE_QUERY"));
                    break;

                case CALL_GET_USER_QUERY:
                    mOnApiError.onError("Getting user data task is cancel by user", new Throwable("task cancelled while CALL_GET_USER_QUERY"));
                    break;

            }
        };
    }

    private CloudDBZoneQuery<UserChat> getLastMessageQuery(String roomId) {
        return CloudDBZoneQuery.where(UserChat.class)
                .equalTo(DBConstants.ROOM_ID, roomId).limit(1).orderByDesc(DBConstants.MESSAGE_TIMESTAMP);
    }

    private CloudDBZoneQuery<User> getUserNameFromMobileQuery(String mobileNo) {
        return CloudDBZoneQuery.where(User.class)
                .equalTo(DBConstants.USER_PHONE, mobileNo).limit(1);
    }

    private void getLastMessageData() {

        for (int i=0;i<mChatHistoryModelList.size();i++) {

            int pos = i;
            CloudDBHelper.getInstance().openDb(new OnDBZoneOpen() {
                @Override
                public void isDBZoneOpen(boolean isConnected, CloudDBZone cloudDBZone) {
                    AppLog.logE(getClass().getName(), "We are inside ");
                    if (cloudDBZone != null) {
                        final CloudDBZoneQuery<UserChat> query = getLastMessageQuery(mChatHistoryModelList.get(pos).getRoomId());
                        cloudDBZone.executeQuery(query, CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY)
                                .addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<UserChat>>() {
                                    @Override
                                    public void onSuccess(CloudDBZoneSnapshot<UserChat> userChatCloudDBZoneSnapshot) {
                                        if (userChatCloudDBZoneSnapshot != null
                                                && userChatCloudDBZoneSnapshot.getSnapshotObjects() != null
                                                && userChatCloudDBZoneSnapshot.getSnapshotObjects().size() > 0) {
                                            try {
                                                UserChat userChat = userChatCloudDBZoneSnapshot.getSnapshotObjects().get(0);
                                                if (userChat.getMessageType().equals("image")) {
                                                    mChatHistoryModelList.get(pos).setChats("Image");
                                                } else if (userChat.getMessageType().equals("map")) {
                                                    mChatHistoryModelList.get(pos).setChats("Location");
                                                } else if(userChat.getMessageType().equals("text")){
                                                    if(userChat.getMessageData()!=null){
                                                        mChatHistoryModelList.get(pos).setChats(userChat.getMessageData());
                                                    }else{
                                                        mChatHistoryModelList.get(pos).setChats(null);
                                                    }
                                                }else{
                                                    mChatHistoryModelList.get(pos).setChats(userChat.getMessageData());
                                                }
                                            } catch (AGConnectCloudDBException e) {
                                                    AppLog.logE(TAG,e.getMessage());
                                            }
                                        }

                                        AppLog.logE(getClass().getName(), "History model JSON ===> "+ new Gson().toJson(mChatHistoryModelList));

                                    }
                                })
                                .addOnFailureListener(getOnFailureListener(CALL_GET_USER_QUERY))
                                .addOnCanceledListener(getOnCancelListener(CALL_GET_USER_QUERY));
                    } else {
                        showZoneOpenError();
                    }
                }
            });
        }
        mListofChattedUser.postValue(mChatHistoryModelList);
    }

    private void getChattedUserName() {

        for (int i=0;i<mChatHistoryModelList.size();i++) {
            int pos = i;
            CloudDBHelper.getInstance().openDb((isConnected, cloudDBZone) -> {
                if (cloudDBZone != null) {
                    final CloudDBZoneQuery<User> query = getUserNameFromMobileQuery(mChatHistoryModelList.get(pos).getNumber());
                    cloudDBZone.executeQuery(query, CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY)
                            .addOnSuccessListener(userCloudDBZoneSnapshot -> {
                                if (userCloudDBZoneSnapshot != null
                                        && userCloudDBZoneSnapshot.getSnapshotObjects() != null
                                        && userCloudDBZoneSnapshot.getSnapshotObjects().size() > 0) {
                                    try {
                                        mChatHistoryModelList.get(pos).setName(userCloudDBZoneSnapshot.getSnapshotObjects().get(0).getUserName());
                                        mChatHistoryModelList.get(pos).setUserProfile(userCloudDBZoneSnapshot.getSnapshotObjects().get(0).getUserProfileUrl());
                                        AppLog.logE(getClass().getName(), "History model JSON ===> "+ new Gson().toJson(mChatHistoryModelList));
                                    } catch (AGConnectCloudDBException e) {
                                        AppLog.logE(getClass().getName(), "AGConnectCloudDBException ==> " + e.getLocalizedMessage());
                                    }
                                } else {
                                    AppLog.logE(getClass().getName(), "We are inside user success and we got nothing ==> ");
                                }
                                mListofChattedUser.postValue(mChatHistoryModelList);
                                AppLog.logE(getClass().getName(), "History model JSON ===> "+ new Gson().toJson(mChatHistoryModelList));

                            })
                            .addOnFailureListener(getOnFailureListener(CALL_LAST_MESSAGE_QUERY))
                            .addOnCanceledListener(getOnCancelListener(CALL_LAST_MESSAGE_QUERY));
                } else {
                    AppLog.logE(getClass().getName(), "DB ZONE ERROR");
                    showZoneOpenError();
                }
            });
        }
        getLastMessageData();

    }

    public void clearChatHistoryList() {
        if (mChatHistoryModelList != null) {
            mChatHistoryModelList.clear();
        }
    }

    private final OnSnapshotListener<ChatRoomId> mSnapshotListener = (cloudDBZoneSnapshot, e) -> {
        if (e != null) {
            AppLog.logE(getClass().getName(), "Snap shot is null ===>");
            return;
        }
        CloudDBZoneObjectList<ChatRoomId> snapshotObjects = cloudDBZoneSnapshot.getSnapshotObjects();
        try {
            if (snapshotObjects != null) {
                while (snapshotObjects.hasNext()) {
                    ChatRoomId chatRoomId = snapshotObjects.next();
                    AppLog.logE(getClass().getName(), "We got room id ===>" + chatRoomId.getRoomId());
                    if (chatRoomId.getUserMobileTo().equalsIgnoreCase(userPhone)) {
                        AppLog.logE(getClass().getName(), "WE GOT ROOM ID AS " + chatRoomId.getRoomId());
                        ChatHistoryModel model = new ChatHistoryModel();
                        model.setRoomId(chatRoomId.getRoomId());
                        model.setNumber(chatRoomId.getUserMobileFrom());
                        mChatHistoryModelList.add(model);
                    }else if(chatRoomId.getUserMobileFrom().equalsIgnoreCase(userPhone)){
                        ChatHistoryModel model = new ChatHistoryModel();
                        model.setRoomId(chatRoomId.getRoomId());
                        model.setNumber(chatRoomId.getUserMobileTo());
                        mChatHistoryModelList.add(model);
                    }

                }
                getChattedUserName();
            }
        } catch (AGConnectCloudDBException snapshotException) {
            AppLog.logE(getClass().getName(), "Snap shot exception");
        } finally {
            cloudDBZoneSnapshot.release();
        }
    };

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
                    AppLog.logE(getClass().getName(), "Exception in getting snapshot register");
                }

            }
        });

    }

    public void initSnapShot(String userPhone) {
        setUserPhone(userPhone);
        subscribeSnapShot();
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
