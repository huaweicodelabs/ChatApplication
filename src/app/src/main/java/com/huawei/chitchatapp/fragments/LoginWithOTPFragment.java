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

package com.huawei.chitchatapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.chitchatapp.utils.Util;
import com.huawei.chitchatapp.view.OTPEditText;
import com.huawei.chitchatapp.viewmodel.AuthServiceViewModel;

public class LoginWithOTPFragment extends Fragment {

    public static final String TAG = LoginWithOTPFragment.class.getSimpleName();
    private Button verifyBtn;
    private String strPhone;
    private String strCountryCode;
    private LoginWithOTPFragmentListener loginWithOTPFragmentListener;
    private AuthServiceViewModel authServiceViewModel;
    private OTPEditText otpEditText;


    public void setLoginWithOTPFragmentListener(LoginWithOTPFragmentListener loginWithOTPFragmentListener) {
        this.loginWithOTPFragmentListener = loginWithOTPFragmentListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strPhone = getArguments().getString(Constants.PHONE_NUMBER);
            strCountryCode = getArguments().getString(Constants.COUNTRY_CODE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        authServiceViewModel = new ViewModelProvider(requireActivity()).get(AuthServiceViewModel.class);
        return inflater.inflate(R.layout.fragment_login_with_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        verifyBtn = view.findViewById(R.id.fragment_otp_verify_btn);
        otpEditText = view.findViewById(R.id.etOTP);
        verifyBtn.setOnClickListener(view1 -> {
            Util.showProgressBar(getActivity());
            Bundle bundle = new Bundle();
            bundle.putString(Constants.COUNTRY_CODE, strCountryCode);
            bundle.putString(Constants.PHONE_NUMBER, strPhone);
            if (otpEditText.length() == 6) {
                authServiceViewModel.verifyContactDetails(strCountryCode, strPhone, otpEditText.getText().toString());
                authServiceViewModel.userMutableLiveData.observe(requireActivity(), user -> {
                    if (user != null) {
                        loginWithOTPFragmentListener.setLoginWithOTPFragmentListener(bundle);
                    }
                    Util.stopProgressBar();
                });

            }
        });
    }

    public interface LoginWithOTPFragmentListener {
        void setLoginWithOTPFragmentListener(Bundle bundle);
    }
}