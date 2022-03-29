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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.chitchatapp.ChitChatApplication;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.dbcloud.User;
import com.huawei.chitchatapp.utils.ChitChatSharedPref;
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.chitchatapp.utils.GetToken;
import com.huawei.chitchatapp.utils.GetTokenListener;
import com.huawei.chitchatapp.utils.Util;
import com.huawei.chitchatapp.viewmodel.ChitChatStorageViewModel;
import com.huawei.chitchatapp.viewmodel.UserProfile;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ProfileFragment extends BaseFragment implements View.OnClickListener, EditingFragment.EditFragmentSetListener, GetTokenListener {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private ImageView profileIv;
    private ImageView ivNameEdit;
    private TextView tvName;
    private TextView aboutDetailsTv;
    private TextView phoneNumberTv;
    private UserProfile userProfile;
    private ChitChatStorageViewModel userProfileImage;
    private String pushToken;
    private String number;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            number = getArguments().getString(Constants.PHONE_NUMBER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GetToken getToken = new GetToken(Constants.CLIENT_ID, requireContext());
        getToken.setGetTokenListener(this);
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userProfile = new ViewModelProvider(requireActivity()).get(UserProfile.class);
        userProfileImage = new ViewModelProvider(requireActivity()).get(ChitChatStorageViewModel.class);

        profileIv = view.findViewById(R.id.profile_iv);
        tvName = view.findViewById(R.id.nameTv);
        aboutDetailsTv = view.findViewById(R.id.aboutDetailsTv);
        phoneNumberTv = view.findViewById(R.id.phoneNumberTv);
        ivNameEdit = view.findViewById(R.id.ivNameEdit);

        ivNameEdit.setOnClickListener(this);
        profileIv.setOnClickListener(this);

        getUserFromCloud();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivNameEdit:
                editDetailsDialogBox();
                break;
            case R.id.profile_iv:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkCameraPermission() && checkStoragePermission()) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    } else {
                        requestPermissionForCamera();
                        requestPermissionForStorage();
                    }
                }
                break;
        }
    }

    private boolean checkStoragePermission() {
        int readPermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkCameraPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.STORAGE_PERMISSION);
        }
    }

    private void requestPermissionForCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    Constants.CAMERA_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri tempUri = getImageUri(getContext(), photo);
            File finalPath = new File(getRealPathFromURI(tempUri));
            uploadImage(finalPath);
            Glide.with(requireActivity())
                    .load(photo)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .circleCrop()
                    .into(profileIv);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContext().getContentResolver() != null) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }


    public void uploadImage(File path) {
        userProfileImage.uploadFileLiveData().observe(getActivity(), uri -> {
            updateProfileData(tvName.getText().toString(), aboutDetailsTv.getText().toString(), uri);
        });

        final String name = number + ".png";
        final StorageReference storageReference = ChitChatApplication.getStorageManagement().getStorageReference(Constants.PIC_PATH + name);
        userProfileImage.uploadFile(storageReference,
                number + ".png", path,
                (errorMessage, e) -> Log.d(TAG, "filePAth--->Error" + e));
    }

    private void editDetailsDialogBox() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ViewGroup viewGroup = getView().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.customview, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        EditText editTextNameValue = dialogView.findViewById(R.id.custom_box_name_edittext);
        EditText editTextStatusValue = dialogView.findViewById(R.id.custom_box_status_edittext);
        Button btnOk = dialogView.findViewById(R.id.buttonOk);
        Button btnCancel = dialogView.findViewById(R.id.buttonCancel);

        btnOk.setOnClickListener(view -> {

            updateProfileData(editTextNameValue.getText().toString(), editTextStatusValue.getText().toString(), null);
            alertDialog.dismiss();

        });
        btnCancel.setOnClickListener(view -> alertDialog.dismiss());

    }

    private void updateProfileData(String name, String status, Uri profileUri) {

        Util.showProgressBar(getContext());
        ChitChatSharedPref.initializeInstance(requireActivity());
        User user = new User();
        user.setUserId(2);
        user.setUserPushToken(ChitChatSharedPref.getInstance().getString(Constants.PUSH_TOKEN, ""));
        user.setUserPhone(getContact());
        user.setUserName(name);
        user.setUserLoginStatus("Online");
        user.setUserStatus(status);
        user.setUserProfileUrl(String.valueOf(profileUri));

        userProfile.saveUser(user, requireActivity());
        userProfile.userMutableLiveData.observe(requireActivity(), aBoolean -> {
            if (aBoolean) {
                Util.stopProgressBar();
                getUserFromCloud();
                Toast.makeText(requireActivity(), getString(R.string.showMessageSuccess), Toast.LENGTH_SHORT).show();
            } else {
                Util.stopProgressBar();
                Toast.makeText(requireActivity(), getString(R.string.showMessageFailed), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void setEditFragmentSetListener(String value, boolean isType) {
        if (isType) {
            tvName.setText(value);
        } else {
            aboutDetailsTv.setText(value);
        }
    }


    private void updateProfile(User user) {

        tvName.setText(user.getUserName());
        aboutDetailsTv.setText(user.getUserStatus());
        phoneNumberTv.setText(user.getUserPhone());

        Glide.with(requireActivity())
                .load(user.getUserProfileUrl())
                .placeholder(R.drawable.avatar)
                .circleCrop()
                .into(profileIv);

    }

    private void updateProfileWithNull() {

        ChitChatSharedPref.initializeInstance(getActivity());
        String contactNumber = ChitChatSharedPref.getInstance().getString(getContact(), "");
        tvName.setText(requireContext().getString(R.string.enter_your_name));
        aboutDetailsTv.setText(requireContext().getString(R.string.enter_your_status));
        phoneNumberTv.setText(contactNumber);

        Glide.with(requireActivity())
                .load(R.drawable.avatar)
                .placeholder(R.drawable.avatar)
                .circleCrop()
                .into(profileIv);
    }

    private void getUserFromCloud() {
        Util.showProgressBar(getActivity());
        userProfile.query(getContact(), requireContext());

        userProfile.userLiveData.observe(requireActivity(), user -> {

            Util.stopProgressBar();
            if (user != null) {
                updateProfile(user);
            } else {
                updateProfileWithNull();
            }
        });
    }

    @Override
    public void getToken(String token) {
        this.pushToken = token;
    }
}
