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

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.PhoneUser;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.chitchatapp.model.User;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.util.Locale;

public class AuthServiceViewModel extends ViewModel {

    private static final String TAG = AuthServiceViewModel.class.getSimpleName();
    public MutableLiveData<VerifyCodeResult> verifyCodeResultMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    public void getOTP(String countryCodeStr, String phoneNumberStr) {
        VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                .sendInterval(30)
                .locale(Locale.getDefault())
                .build();

        Task<VerifyCodeResult> task = AGConnectAuth.getInstance().requestVerifyCode(countryCodeStr, phoneNumberStr, settings);
        task.addOnSuccessListener(TaskExecutors.immediate(), verifyCodeResult -> {
            if (null != verifyCodeResult) {
                verifyCodeResultMutableLiveData.postValue(verifyCodeResult);
            }
        });
        task.addOnFailureListener(e ->
                AppLog.logE(TAG, "onFailure: " + e.getCause()));
    }

    public void verifyContactDetails(String countryCodeStr, String phoneNumberStr, String code) {
        PhoneUser phoneUser = new PhoneUser.Builder()
                .setCountryCode(countryCodeStr)
                .setPhoneNumber(phoneNumberStr)
                .setVerifyCode(code)
                .build();
        AGConnectAuth.getInstance().createUser(phoneUser)
                .addOnSuccessListener(signInResult -> {
                    if (signInResult != null) {
                        User user = new User();
                        user.setUsername(signInResult.getUser().getDisplayName());
                        user.setPhoneNumber(phoneNumberStr);
                        userMutableLiveData.postValue(user);
                    }
                })
                .addOnFailureListener(e -> {
                    AppLog.logE(TAG, "verifyContactDetails: " + e.getStackTrace());
                    User user = new User();
                    user.setPhoneNumber(phoneNumberStr);
                    userMutableLiveData.setValue(user);

                });
    }


}
