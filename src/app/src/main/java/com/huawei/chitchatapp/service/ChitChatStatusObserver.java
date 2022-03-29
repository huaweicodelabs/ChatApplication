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

package com.huawei.chitchatapp.service;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.Transaction;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.chitchatapp.ChitChatApplication;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.dao.CloudDBHelper;
import com.huawei.chitchatapp.dao.DBConstants;
import com.huawei.chitchatapp.dbcloud.User;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.chitchatapp.utils.ChitChatSharedPref;
import com.huawei.chitchatapp.utils.Constants;

import java.util.List;

public class ChitChatStatusObserver implements LifecycleObserver {

    private Context context;

    public ChitChatStatusObserver(Context context) {
        this.context = context;
    }

    private static final String TAG = "ChitChatStatusService";

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreateEvent() {
        updateUserLoginStatus(Constants.STATUS_ONLINE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResumeEvent() {
        updateUserLoginStatus(Constants.STATUS_ONLINE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onStopEvent() {
        updateUserLoginStatus(Constants.STATUS_OFFLINE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroyEvent() {
        updateUserLoginStatus(Constants.STATUS_OFFLINE);
    }

    public void updateUserLoginStatus(final String status) {


        CloudDBHelper.getInstance().openDb((isConnected, cloudDBZone) -> {
            if (cloudDBZone != null) {
                ChitChatSharedPref.initializeInstance(context);
                final String phoneNumber = ChitChatSharedPref.getInstance().getString(Constants.PHONE_NUMBER, null);
                if (phoneNumber == null) {
                    AppLog.logE(TAG, ChitChatApplication.getInstance().getString(R.string.err_login_user_mobile));
                    return;
                }

                CloudDBZoneQuery<User> query = CloudDBZoneQuery.where(User.class).equalTo(DBConstants.userNumber, phoneNumber);

                Transaction.Function updateStatus = transaction -> {
                    try {
                        List<User> user = transaction.executeQuery(query);
                        if (user != null && user.size() > 0) {
                            user.get(0).setUserLoginStatus(status);
                            user.get(0).setUserPhone(phoneNumber);
                            transaction.executeUpsert(user);
                        }
                    } catch (AGConnectCloudDBException agCEx) {
                        AppLog.logE(TAG, String.format(ChitChatApplication.getInstance().getString(R.string.err_cloud_exception), agCEx.getLocalizedMessage()));
                        return false;
                    }
                    return true;
                };
                cloudDBZone.runTransaction(updateStatus);
            } else {
                AppLog.logE(TAG, ChitChatApplication.getInstance().getString(R.string.err_db_zone));
            }
        });
    }

}
