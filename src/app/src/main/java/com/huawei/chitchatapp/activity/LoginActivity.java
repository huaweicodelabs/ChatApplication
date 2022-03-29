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

package com.huawei.chitchatapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.fragments.LoginPhoneFragment;
import com.huawei.chitchatapp.fragments.LoginWithOTPFragment;
import com.huawei.chitchatapp.utils.Constants;

public class LoginActivity extends BaseActivity implements LoginPhoneFragment.LoginPhoneFragmentListener,
        LoginWithOTPFragment.LoginWithOTPFragmentListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginPhoneFragment loginPhoneFragment = new LoginPhoneFragment();
        loginPhoneFragment.setLoginPhoneFragmentListener(this);
        addFragmentToWithoutBackStack(loginPhoneFragment);
    }

    @Override
    public void setLoginPhoneFragmentListener(Bundle bundle) {
        LoginWithOTPFragment loginWithOTPFragment = new LoginWithOTPFragment();
        loginWithOTPFragment.setArguments(bundle);
        loginWithOTPFragment.setLoginWithOTPFragmentListener(this);
        addFragmentOnStack(loginWithOTPFragment, LoginWithOTPFragment.TAG);
    }

    @Override
    public void setLoginWithOTPFragmentListener(Bundle bundle) {
        String number = bundle.getString(Constants.PHONE_NUMBER);
        Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
        intent.putExtra(Constants.PHONE_NUMBER, number);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        ActivityCompat.finishAffinity(LoginActivity.this);
    }

}