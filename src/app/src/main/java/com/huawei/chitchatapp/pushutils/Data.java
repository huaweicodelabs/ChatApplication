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

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("title")
    public String title;

    @SerializedName("roomId")
    public String roomId;

    @SerializedName("message")
    public String message;

    @SerializedName("sender_phone")
    public String sender_phone;

    @SerializedName("sender_name")
    public String sender_name;

    @SerializedName("message_data")
    public String messageData;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderPhone() {
        return sender_phone;
    }

    public void setSenderPhone(String sender_phone) {
        this.sender_phone = sender_phone;
    }

    public String getSenderName() {
        return sender_name;
    }

    public void setSenderName(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("{")
                .append("'").append("title").append("'").append("=").append("'").append(title).append("'").append(",")
                .append("'").append("roomId").append("'").append("=").append("'").append(roomId).append("'").append(",")
                .append("'").append("message").append("'").append("=").append("'").append(message).append("'").append(",")
                .append("'").append("message_data").append("'").append("=").append("'").append(messageData).append("'").append(",")
                .append("'").append("sender_name").append("'").append("=").append("'").append(sender_name).append("'").append(",")
                .append("'").append("sender_phone").append("'").append("=").append("'").append(sender_phone).append("'").append("}").toString();

    }


}