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

package com.huawei.chitchatapp.pushutils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.activity.MessageActivity;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;

public class ChitChatPushService extends HmsMessageService {

    private static final String TAG = ChitChatPushService.class.getSimpleName();
    private static final String CHANNEL1 = "Push_Channel_01";
    private Data data = null;
    private Bitmap mBitmap;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        AppLog.logE(TAG, remoteMessage.getData());
        try {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            data = new Gson().fromJson(jsonObject.toString(), Data.class);
        } catch (JSONException e) {
            AppLog.logE(TAG,e.getMessage());
        }

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL1, CHANNEL1, importance);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
                if (Objects.requireNonNull(remoteMessage.getDataOfMap().get("message")).equalsIgnoreCase(Constants.MESSAGE_TYPE_TEXT)) {
                    Intent notificationIntent = new Intent(getApplicationContext(), MessageActivity.class);
                    notificationIntent.putExtra("roomId", remoteMessage.getDataOfMap().get("roomId"));
                    notificationIntent.putExtra("name",remoteMessage.getDataOfMap().get("sender_name"));
                    notificationIntent.putExtra("phone",remoteMessage.getDataOfMap().get("sender_phone"));
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                            notificationIntent, 0);
                    NotificationCompat.Builder mBuilder;
                    mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL1)
                            .setSmallIcon(R.drawable.profile)
                            .setContentTitle(remoteMessage.getDataOfMap().get("sender_name"))
                            .setContentText(remoteMessage.getDataOfMap().get("message_data"));

                    mBuilder.setContentIntent(intent);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                } else if (Objects.requireNonNull(remoteMessage.getDataOfMap().get("message")).equalsIgnoreCase(Constants.MESSAGE_TYPE_IMAGE)) {
                    new GeneratePictureStyleNotification(this, data).execute();
                } else if (Objects.requireNonNull(remoteMessage.getDataOfMap().get("message")).equalsIgnoreCase(Constants.MESSAGE_TYPE_MAP)) {
                    Intent notificationIntent = new Intent(getApplicationContext(), MessageActivity.class);
                    Bundle passBundle = new Bundle();
                    passBundle.putString("roomId", remoteMessage.getDataOfMap().get("roomId"));
                    PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                            notificationIntent, 0);
                    NotificationCompat.Builder mBuilder;
                    mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL1)
                            .setSmallIcon(R.drawable.profile)
                            .setContentTitle(remoteMessage.getDataOfMap().get("sender_name"))
                            .setContentText(Constants.LOCATION_DATA);

                    mBuilder.setContentIntent(intent);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(0, mBuilder.build());
                }
            }

        }
    }

    private class GeneratePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {
        Context mContext;
        private Data data;

        public GeneratePictureStyleNotification(Context context, Data data) {
            super();
            this.mContext = context;
            this.data = data;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(data.messageData)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mBitmap = resource;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                showNotification(data);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);


        }
    }

    private void showNotification(Data data) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;

        // String channelName = CHANNEL1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL1, CHANNEL1, importance);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);

                Intent notificationIntent = new Intent(getApplicationContext(), MessageActivity.class);
                Bundle passValue = new Bundle();
                passValue.putString("roomId", data.getRoomId());
                notificationIntent.setAction("android.intent.action.MAIN");
                notificationIntent.addCategory("android.intent.category.LAUNCHER");
                notificationIntent.putExtras(passValue);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder;

                if (mBitmap != null) {
                    NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                            .bigPicture(mBitmap);

                    mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL1)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(getResources().getString(R.string.app_name))
                            .setLargeIcon(mBitmap)
                            .setStyle(style)
                            .setContentText(data.getMessageData());
                } else {
                    mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL1)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(getResources().getString(R.string.app_name))
                            .setContentText(data.getMessageData());
                }

                mBuilder.setContentIntent(intent);
                mBuilder.setAutoCancel(true);
                mNotificationManager.notify(notificationId, mBuilder.build());
            }


        } else {
            Intent notificationIntent = new Intent(getApplicationContext(), MessageActivity.class);
            Bundle passValue = new Bundle();
            passValue.putString("roomId", data.getRoomId());
            notificationIntent.setAction("android.intent.action.MAIN");
            notificationIntent.addCategory("android.intent.category.LAUNCHER");
            notificationIntent.putExtras(passValue);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (mBitmap != null) {
                NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                        .bigPicture(mBitmap);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.app_name))
                        .setStyle(style)
                        .setLargeIcon(mBitmap).setAutoCancel(true).setSound(soundUri);
            } else {
                mBuilder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(data.message)).setContentText(data.getMessage()).setAutoCancel(true).setSound(soundUri);
            }

            mBuilder.setAutoCancel(true);
            NotificationCompat.InboxStyle inBoxStyle = new NotificationCompat.InboxStyle();
            inBoxStyle.setBigContentTitle(getString(R.string.app_name));

            mBuilder.setContentIntent(intent);
            mBuilder.setStyle(inBoxStyle);
            Notification notification = mBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(notificationId, notification);
        }


    }
}

