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

package com.huawei.chitchatapp;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.chitchatapp.dao.CloudDBHelper;
import com.huawei.chitchatapp.service.ChitChatStatusObserver;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.hms.videokit.player.InitFactoryCallback;
import com.huawei.hms.videokit.player.LogConfigInfo;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.WisePlayerFactoryOptionsExt;

public class ChitChatApplication extends Application {

    private static ChitChatApplication mInstance;

    private static AGCStorageManagement storageManagement;

    private static final String TAG = ChitChatApplication.class.getSimpleName();

    private static WisePlayerFactory wisePlayerFactory = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        CloudDBHelper.getInstance().init(getApplicationContext());
        storageManagement = AGCStorageManagement.getInstance();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ChitChatStatusObserver(getApplicationContext()));
        initPlayer();
    }

    public static ChitChatApplication getInstance() {
        return mInstance;
    }


    public static AGCStorageManagement getStorageManagement() {
        return storageManagement;
    }

    public static StorageReference getDefaultStorageRef() {
        return getStorageManagement().getStorageReference("chitchatapp/");
    }

    public static StorageReference getProfilePicStorageRef() {
        return getDefaultStorageRef().child("profile_pic/");
    }

    private void initPlayer() {
        WisePlayerFactoryOptionsExt.Builder factoryOptions =
                new WisePlayerFactoryOptionsExt.Builder().setDeviceId("xxx");
        LogConfigInfo logCfgInfo = new LogConfigInfo(1, "", 20, 1024);
        factoryOptions.setLogConfigInfo(logCfgInfo);

        WisePlayerFactory.initFactory(this, factoryOptions.build(), new InitFactoryCallback() {
            @Override
            public void onSuccess(WisePlayerFactory wisePlayerFactory) {
                AppLog.logD(TAG, "onSuccess wise PlayerFactory:" + wisePlayerFactory);
                setWisePlayerFactory(wisePlayerFactory);
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                AppLog.logE(TAG, "onFailure error code:" + errorCode + " reason:" + msg);
            }
        });
    }

    public static WisePlayerFactory getWisePlayerFactory() {
        return wisePlayerFactory;
    }

    private static void setWisePlayerFactory(WisePlayerFactory wisePlayerFactory) {
        ChitChatApplication.wisePlayerFactory = wisePlayerFactory;
    }
}
