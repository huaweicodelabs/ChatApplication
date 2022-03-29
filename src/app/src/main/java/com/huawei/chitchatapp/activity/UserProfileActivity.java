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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.chitchatapp.ChitChatApplication;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.adapter.UserProfileActivityAdapter;
import com.huawei.chitchatapp.dbcloud.User;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.chitchatapp.utils.ChitChatSharedPref;
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.chitchatapp.utils.FileUtils;
import com.huawei.chitchatapp.utils.GetToken;
import com.huawei.chitchatapp.utils.GetTokenListener;
import com.huawei.chitchatapp.utils.LoginHelper;
import com.huawei.chitchatapp.viewmodel.ChitChatStorageViewModel;
import com.huawei.chitchatapp.viewmodel.UserProfile;

import java.io.File;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener, LoginHelper.OnLoginEventCallBack, GetTokenListener {

    private static final String TAG = UserProfileActivity.class.getSimpleName();

    private boolean isChatUserProfile;
    public TextView mPhoneNumber;
    public EditText mName;
    private EditText mStatus;
    public Button mSubmit;
    public ImageView mProfileImage;
    private ImageView imgUpdateUserPicture;
    public RecyclerView mDocumentRv;
    private UserProfileActivityAdapter userProfileActivityAdapter;

    private String phoneNumber;
    private UserProfile userProfile;
    private Handler mHandler;
    private String pushToken;
    private ChitChatStorageViewModel userProfileImage;
    private String mRoomId;


    ActivityResultLauncher<Intent> launcherForCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    Log.d(TAG, "image_data--->" + data.getData());
                    File file = FileUtils.bitmapToFile(UserProfileActivity.this,
                            (Bitmap) result.getData().getExtras().get("data"), phoneNumber + "userProfile.jpg");
                    uploadImage(file);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mHandler = new Handler(Looper.getMainLooper());
        if (AGConnectAuth.getInstance().getCurrentUser() == null) {
            mHandler.post(() -> {
                LoginHelper loginHelper = new LoginHelper(UserProfileActivity.this);
                loginHelper.addLoginCallBack(this);
                loginHelper.login();
            });
        } else {

        }
        initView();
        userProfile = new ViewModelProvider(UserProfileActivity.this).get(UserProfile.class);
        userProfileImage = new ViewModelProvider(UserProfileActivity.this).get(ChitChatStorageViewModel.class);

        userProfile.userLiveData.observe(this, user -> {
            setUserData(user);
        });

        userProfile.query(phoneNumber, UserProfileActivity.this);

        if (isChatUserProfile && mRoomId != null) {
            userProfile.userMediaLiveData.observe(this, userChats -> {

                userProfileActivityAdapter = new UserProfileActivityAdapter(this, userChats);
                LinearLayoutManager horizontalLayoutManagaer
                        = new LinearLayoutManager(UserProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
                mDocumentRv.setLayoutManager(horizontalLayoutManagaer);
                mDocumentRv.setAdapter(userProfileActivityAdapter);
            });

            userProfile.getUserSharedMedia(phoneNumber, mRoomId);
        }

    }

    private void setUserData(User user) {
        if (user == null) {
            return;
        }
        mName.setText(user.getUserName() != null ? user.getUserName() : "ChitChat App");
        mStatus.setText(user.getUserStatus() != null ? user.getUserStatus() : "ChitChat App");
        mPhoneNumber.setText(phoneNumber);
        if (user.getUserProfileUrl() != null) {
            Glide.with(UserProfileActivity.this)
                    .load(user.getUserProfileUrl())
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(mProfileImage);
        } else {
            mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        }
    }

    private void initView() {

        mPhoneNumber = findViewById(R.id.act_userprofile_phoneNumberTv);
        mName = findViewById(R.id.act_userprofile_name_rt);
        mStatus = findViewById(R.id.act_userprofile_aboutDetailsTv);
        mSubmit = findViewById(R.id.act_userprofile_submit_btn);
        mProfileImage = findViewById(R.id.act_userprofile_iv);
        imgUpdateUserPicture = findViewById(R.id.img_update_user_picture);
        mDocumentRv = findViewById(R.id.act_userprofile_document_rv);

        if (getIntent() != null && getIntent().getStringExtra(Constants.PHONE_NUMBER) != null) {
            phoneNumber = getIntent().getStringExtra(Constants.PHONE_NUMBER);
        }

        if (getIntent() != null && getIntent().getStringExtra(Constants.ROOM_ID) != null) {
            mRoomId = getIntent().getStringExtra(Constants.ROOM_ID);
        }

        if (getIntent() != null) {
            isChatUserProfile = getIntent().getBooleanExtra(Constants.IS_CHAT_USER_PROFILE, false);
        }

        if (isChatUserProfile) {
            mSubmit.setVisibility(View.GONE);
            imgUpdateUserPicture.setVisibility(View.GONE);
        }

        imgUpdateUserPicture.setOnClickListener(this);
        mSubmit.setOnClickListener(this);
        GetToken getToken = new GetToken(Constants.CLIENT_ID, UserProfileActivity.this);
        mPhoneNumber.setText(phoneNumber);
        getToken.setGetTokenListener(this);
        getToken.execute();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_update_user_picture:

                if (checkCameraPermission()) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    launcherForCamera.launch(takePicture);
                } else {
                    requestPermissionForCamera();
                }

                break;
            case R.id.act_userprofile_submit_btn:
                submitProfileData();
                break;

            default:
                break;
        }
    }

    private void submitProfileData() {
        try {
            String name = mName.getText().toString();
            String status = mStatus.getText().toString();
            if (!name.equalsIgnoreCase("") && !status.equalsIgnoreCase("")) {
                updateProfileData(name, status, null);
            } else {
                Toast.makeText(this, "Enter name and status", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            AppLog.logE(TAG, e.getMessage());
        }

    }

    private void requestPermissionForCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    Constants.CAMERA_PERMISSION);
        }
    }

    private boolean checkCameraPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onLogin(boolean showLoginUserInfo, SignInResult signInResult) {

    }

    @Override
    public void onLogOut(boolean showLoginUserInfo) {

    }


    @Override
    public void getToken(String token) {
        this.pushToken = token;
        ChitChatSharedPref.initializeInstance(UserProfileActivity.this);
        ChitChatSharedPref.getInstance().putString(Constants.PUSH_TOKEN, token);
    }

    private void updateProfileData(String name, String status, Uri profileUri) {

        ChitChatSharedPref.initializeInstance(UserProfileActivity.this);
        User user = new User();
        user.setUserId(2);
        user.setUserPushToken(pushToken);
        user.setUserPhone(phoneNumber);
        user.setUserName(name);
        user.setUserLoginStatus(Constants.STATUS_ONLINE);
        user.setUserStatus(status);
        user.setUserProfileUrl(String.valueOf(profileUri));

        userProfile.saveUser(user, UserProfileActivity.this);
        userProfile.userMutableLiveData.observe(UserProfileActivity.this, aBoolean -> {
            if (aBoolean) {
                ChitChatSharedPref.initializeInstance(getApplicationContext());
                ChitChatSharedPref.getInstance().putString(Constants.PHONE_NUMBER, phoneNumber);
                ChitChatSharedPref.getInstance().putString(Constants.USER_NAME, name);
                ChitChatSharedPref.getInstance().putString(Constants.ALREADY_LOGIN, "1");
                Toast.makeText(UserProfileActivity.this, getString(R.string.showMessageSuccess), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(UserProfileActivity.this, getString(R.string.showMessageFailed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadImage(File path) {
        userProfileImage.uploadFileLiveData().observe(UserProfileActivity.this, uri -> {
            updateProfileData(mName.getText().toString(), mStatus.getText().toString(), uri);
        });

        final String name = phoneNumber + ".png";
        final StorageReference storageReference = ChitChatApplication.getStorageManagement().getStorageReference("chitchatapp/profile_pic/" + name);


        userProfileImage.uploadFile(storageReference,
                phoneNumber + ".png", path,
                (errorMessage, e) -> AppLog.logE("ProfileFragment", "filePAth--->Error" + e));
    }


}