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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.chitchatapp.ChitChatApplication;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.adapter.MessageAdapter;
import com.huawei.chitchatapp.dbcloud.UserChat;
import com.huawei.chitchatapp.fragments.CustomBottomSheetDialogFragment;
import com.huawei.chitchatapp.model.MapModel;
import com.huawei.chitchatapp.pushutils.PushApis;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.chitchatapp.utils.ChitChatSharedPref;
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.chitchatapp.utils.FileUtils;
import com.huawei.chitchatapp.utils.Util;
import com.huawei.chitchatapp.videoplayer.PlayActivity;
import com.huawei.chitchatapp.videoplayer.control.HomePageControl;
import com.huawei.chitchatapp.viewmodel.ChitChatStorageViewModel;
import com.huawei.chitchatapp.viewmodel.MessageViewModel;
import com.huawei.hms.site.api.model.Site;

import java.io.File;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener, CustomBottomSheetDialogFragment.CustomBottomSheetDialogListener, MessageAdapter.MessageAdapterSetListener, Constants {

    private static final String TAG = MessageActivity.class.getSimpleName();
    private CustomBottomSheetDialogFragment customBottomSheetDialogFragment;
    private EditText textSend;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private MessageViewModel messageViewModel;
    private ChitChatStorageViewModel chitChatStorageViewModel;
    private String messageType;
    private String receiverText;
    private String receiverPhoneNumber;
    private String receiverImageUrl;
    private String roomId;
    private File imageFile;
    private String imageData;
    private String jsonMapModel;

    ActivityResultLauncher<Intent> launcherForLocation = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::onActivityResult);

    ActivityResultLauncher<Intent> launcherForCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageFile = FileUtils.bitmapToFile(MessageActivity.this, (Bitmap) result.getData().getExtras().get("data"), "image.jpg");
                    AppLog.logD(TAG, imageFile.toString());
                    messageType = Constants.MESSAGE_TYPE_IMAGE;
                    setMessage(messageType);
                }
            });

    ActivityResultLauncher<Intent> launcherForGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Uri imageUri = result.getData().getData();
            messageType = Constants.MESSAGE_TYPE_VIDEO;
            setMessage(messageType);
            AppLog.logD(TAG, imageUri.toString());
        }
    });

    ActivityResultLauncher<Intent> launcherForDoc = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Uri imageUri = result.getData().getData();
            messageType = Constants.MESSAGE_TYPE_DOC;
            setMessage(messageType);
            AppLog.logD(TAG, imageUri.toString());
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null
                && (getIntent().getStringExtra("name") != null)
                && (getIntent().getStringExtra("phone") != null)
                && (getIntent().getStringExtra("imageUrl") != null)) {
            receiverText = getIntent().getStringExtra("name");
            receiverPhoneNumber = getIntent().getStringExtra("phone");
            receiverImageUrl = getIntent().getStringExtra("imageUrl");
            roomId = getIntent().getStringExtra("roomId");
        }

        initView();

        initObserver();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
        ImageButton btnAttach = findViewById(R.id.btn_attach);
        ImageButton btnSend = findViewById(R.id.btn_send);
        textSend = findViewById(R.id.text_send);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> MessageActivity.super.onBackPressed());
        ImageView profile = findViewById(R.id.profile_image);
        TextView username = findViewById(R.id.username);

        if (receiverImageUrl != null) {
            Glide.with(MessageActivity.this)
                    .load(receiverImageUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(profile);
        } else {
            profile.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        }

        username.setText(receiverText);

        textSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                messageType = Constants.MESSAGE_TYPE_TEXT;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnAttach.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        profile.setOnClickListener(this);
        username.setOnClickListener(this);
        messageViewModel = new ViewModelProvider(MessageActivity.this).get(MessageViewModel.class);
        chitChatStorageViewModel = new ViewModelProvider(MessageActivity.this).get(ChitChatStorageViewModel.class);
        getChatList();


    }

    private void initObserver() {
        messageViewModel.userChatMutableLiveData.observe(MessageActivity.this, userChats -> {
            if (userChats != null) {
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                messageAdapter = new MessageAdapter(MessageActivity.this, userChats, receiverImageUrl);
                messageAdapter.setMessageAdapterSetListener(this);
                recyclerView.scrollToPosition(userChats.size() - 1);
                recyclerView.setAdapter(messageAdapter);
            }
        });

        messageViewModel.tokenMutableLiveData.observe(MessageActivity.this, s -> {
            PushApis pushApis = new PushApis(MessageActivity.this);
            if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_MAP)) {
                pushApis.sendPushNotification(roomId, messageType, Constants.CLIENT_ID, jsonMapModel, s);
            } else if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_VIDEO)) {
                pushApis.sendPushNotification(roomId, messageType, Constants.CLIENT_ID, MessageActivity.this.imageData, s);
            } else if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_IMAGE)) {
                pushApis.sendPushNotification(roomId, messageType, Constants.CLIENT_ID, MessageActivity.this.imageData, s);
            } else if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_TEXT)) {
                pushApis.sendPushNotification(roomId, messageType, Constants.CLIENT_ID, MessageActivity.this.textSend.getText().toString(), s);
                textSend.setText("");
            }
        });
    }

    public void getChatList() {
        messageViewModel.getUserChatByRoomID(roomId, MessageActivity.this);
    }

    private void setMessage(String messageType) {
        ChitChatSharedPref.initializeInstance(MessageActivity.this);
        Util.showProgressBar(MessageActivity.this);
        UserChat userChat = new UserChat();
        userChat.setRoomId(roomId);
        userChat.setMessageTimestamp(Long.parseLong(Util.getTimeStamp()));
        userChat.setChatId(Util.getRandomNumber());
        userChat.setReceiverName(receiverText);
        userChat.setReceiverPhone(receiverPhoneNumber);
        userChat.setSenderName(ChitChatSharedPref.getInstance().getString(Constants.USER_NAME, ""));
        userChat.setSenderPhone(ChitChatSharedPref.getInstance().getString(Constants.PHONE_NUMBER, ""));

        userChat.setMessageType(messageType);

        switch (messageType) {

            case Constants.MESSAGE_TYPE_VIDEO:
            case Constants.MESSAGE_TYPE_IMAGE:
            case Constants.MESSAGE_TYPE_DOC:

                chitChatStorageViewModel.uploadFileLiveData().observe(MessageActivity.this, uri -> {
                    userChat.setMessageData(String.valueOf(uri));
                    messageViewModel.saveUserChat(userChat);
                    MessageActivity.this.imageData = String.valueOf(uri);
                    messageViewModel.userUpdatedSuccessfully.observe(MessageActivity.this, aBoolean -> {
                        if (aBoolean) {
                            textSend.setText("");
                            Util.stopProgressBar();
                            getChatList();
                            Toast.makeText(MessageActivity.this, getString(R.string.showMessageSuccess), Toast.LENGTH_SHORT).show();
                        } else {
                            Util.stopProgressBar();
                            Toast.makeText(MessageActivity.this, getString(R.string.showMessageFailed), Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                StorageReference mediaReference = ChitChatApplication.getStorageManagement().getStorageReference(Constants.chatScreen + "/" + roomId + "/" + Util.getTimeStamp() + ".png");
                chitChatStorageViewModel.uploadFile(mediaReference,
                        Util.getTimeStamp() + ".png", imageFile,
                        (errorMessage, e) -> AppLog.logE("ProfileFragment", "filePAth--->Error" + e));
                break;

            case Constants.MESSAGE_TYPE_MAP:

                userChat.setMessageData(jsonMapModel);
                messageViewModel.saveUserChat(userChat);
                messageViewModel.userUpdatedSuccessfully.observe(MessageActivity.this, aBoolean -> {
                    if (aBoolean) {
                        Util.stopProgressBar();
                        getChatList();
                        Toast.makeText(MessageActivity.this, getString(R.string.showMessageSuccess), Toast.LENGTH_SHORT).show();
                    } else {
                        Util.stopProgressBar();
                        Toast.makeText(MessageActivity.this, getString(R.string.showMessageFailed), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case Constants.MESSAGE_TYPE_TEXT:

                userChat.setMessageData(textSend.getText().toString());
                messageViewModel.saveUserChat(userChat);
                messageViewModel.userUpdatedSuccessfully.observe(MessageActivity.this, aBoolean -> {
                    if (aBoolean) {
                        Util.stopProgressBar();
                        getChatList();
                    } else {
                        Util.stopProgressBar();
                    }
                });

                break;

        }
        messageViewModel.queryForToken(receiverPhoneNumber, MessageActivity.this);

    }

    private boolean checkStoragePermission() {
        int readPermission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkLocationPermission() {
        int locationPermission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int coursePermission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        return locationPermission == PackageManager.PERMISSION_GRANTED && coursePermission == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkCameraPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            }, Constants.LOCATION_PERMISSION);
        }
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
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION:
                if (grantResults.length > 0) {
                    boolean fineLoc = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarseLoc = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (fineLoc && coarseLoc) {
                        Intent intent = new Intent(MessageActivity.this, LocationActivity.class);
                        launcherForLocation.launch(intent);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.allow_location_permission), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case Constants.CAMERA_PERMISSION:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraPermission) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        launcherForCamera.launch(takePicture);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.allow_camera_permission), Toast.LENGTH_SHORT).show();

                    }
                }
                break;

            case Constants.STORAGE_PERMISSION:
                if (grantResults.length > 0) {
                    boolean storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storagePermission) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        launcherForCamera.launch(takePicture);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.allow_storage_permission), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @SuppressLint("IntentReset")
    @Override
    public void setMessageType(String type) {

        switch (type) {

            case Constants.NAV_DOCUMENT:
                if (checkStoragePermission()) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    launcherForDoc.launch(intent);
                } else {
                    requestPermissionForStorage();
                }
                customBottomSheetDialogFragment.dismiss();
                break;

            case Constants.NAV_LOCATION:
                customBottomSheetDialogFragment.dismiss();
                if (checkLocationPermission()) {
                    Intent intent = new Intent(MessageActivity.this, LocationActivity.class);
                    launcherForLocation.launch(intent);
                } else {
                    requestPermissionForLocation();
                }
                break;

            case Constants.NAV_CAMERA:
                customBottomSheetDialogFragment.dismiss();
                if (checkCameraPermission() && checkStoragePermission()) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    launcherForCamera.launch(takePicture);
                } else {
                    requestPermissionForCamera();
                    requestPermissionForStorage();
                }
                break;

            case Constants.NAV_GALLERY:
                if (checkStoragePermission()) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    launcherForGallery.launch(intent);
                } else {
                    requestPermissionForStorage();
                }
                customBottomSheetDialogFragment.dismiss();
                break;

            case Constants.NAV_VIDEO:
                if (checkStoragePermission()) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024);
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60000);
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    launcherForGallery.launch(intent);
                } else {
                    requestPermissionForStorage();
                }
                customBottomSheetDialogFragment.dismiss();
                break;
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_attach:
                customBottomSheetDialogFragment = new CustomBottomSheetDialogFragment();
                customBottomSheetDialogFragment.setCustomBottomSheetDialogListener(this);
                customBottomSheetDialogFragment.show(getSupportFragmentManager(), CustomBottomSheetDialogFragment.TAG);
                break;
            case R.id.btn_send:
                setMessage(messageType);
                break;

            case R.id.userName:
            case R.id.profile_image:
                openUserProfile();
                break;
            default:
                break;
        }
    }

    private void openUserProfile() {
        Intent userProfile = new Intent();
        userProfile.setClass(MessageActivity.this, UserProfileActivity.class);
        userProfile.putExtra(PHONE_NUMBER, receiverPhoneNumber);
        userProfile.putExtra(ROOM_ID, roomId);
        userProfile.putExtra(IS_CHAT_USER_PROFILE, true);
        startActivity(userProfile);
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Intent data = result.getData();
            messageType = Constants.MESSAGE_TYPE_MAP;
            Site site = data.getParcelableExtra(Constants.SITE_MAP);
            Location location = data.getParcelableExtra(Constants.CURRENT_LOCATION);
            String markers = "{" + location.getLatitude() + "," + location.getLongitude() + "}|{" + site.getLocation().getLat() + "," + site.getLocation().getLng() + "}";
            MapModel map = new MapModel(site.getFormatAddress(), String.valueOf(site.getLocation().getLat()), String.valueOf(site.getLocation().getLng()), markers);
            Gson gson = new Gson();
            jsonMapModel = gson.toJson(map);
            setMessage(messageType);
        }

    }

    @Override
    public void setMessageAdapterSetListener(String dataType, String url) {
        if (dataType.equalsIgnoreCase(Constants.MESSAGE_TYPE_IMAGE)) {
            Intent intent = new Intent(MessageActivity.this, FullImageActivity.class);
            intent.putExtra(Constants.DATA_TYPE, url);
            startActivity(intent);
        } else if (dataType.equalsIgnoreCase(Constants.MESSAGE_TYPE_MAP)) {
            Uri contentUrl = Uri.parse("petalmaps://navigation?saddr=25.102916,55.165363&daddr=25.164610000000,55.228869000000&type=drive&utm_source=fb");
            Intent intent = new Intent(Intent.ACTION_VIEW, contentUrl);
            startActivity(intent);
        } else if (dataType.equalsIgnoreCase(Constants.MESSAGE_TYPE_VIDEO)) {
            HomePageControl homePageControl = new HomePageControl(MessageActivity.this);
            PlayActivity.startPlayActivity(MessageActivity.this, homePageControl.getInputPlay(url));
        }
    }

}
