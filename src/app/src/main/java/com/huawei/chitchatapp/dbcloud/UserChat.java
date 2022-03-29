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

package com.huawei.chitchatapp.dbcloud;

import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.Text;
import com.huawei.agconnect.cloud.database.annotations.DefaultValue;
import com.huawei.agconnect.cloud.database.annotations.NotNull;
import com.huawei.agconnect.cloud.database.annotations.Indexes;
import com.huawei.agconnect.cloud.database.annotations.PrimaryKeys;

@PrimaryKeys({"chat_id"})
@Indexes({"room_id_index:room_id", "chat_id_index:chat_id", "chat_room_id_index:chat_id,room_id"})
public final class UserChat extends CloudDBZoneObject {
    private Integer chat_id;

    @NotNull
    @DefaultValue(stringValue = "null")
    private String room_id;

    @NotNull
    @DefaultValue(stringValue = "null")
    private String sender_phone;

    @NotNull
    @DefaultValue(stringValue = "null")
    private String receiver_phone;

    private String sender_name;

    private String receiver_name;

    @NotNull
    @DefaultValue(stringValue = "null")
    private String message_type;

    @NotNull
    @DefaultValue(stringValue = "null")
    private String message_data;

    private Long message_timestamp;

    public UserChat() {
        super(UserChat.class);
        this.room_id = "null";
        this.sender_phone = "null";
        this.receiver_phone = "null";
        this.message_type = "null";
        this.message_data = "null";
    }

    public void setChatId(Integer chat_id) {
        this.chat_id = chat_id;
    }

    public Integer getChatId() {
        return chat_id;
    }

    public void setRoomId(String room_id) {
        this.room_id = room_id;
    }

    public String getRoomId() {
        return room_id;
    }

    public void setSenderPhone(String sender_phone) {
        this.sender_phone = sender_phone;
    }

    public String getSenderPhone() {
        return sender_phone;
    }

    public void setReceiverPhone(String receiver_phone) {
        this.receiver_phone = receiver_phone;
    }

    public String getReceiverPhone() {
        return receiver_phone;
    }

    public void setSenderName(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSenderName() {
        return sender_name;
    }

    public void setReceiverName(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getReceiverName() {
        return receiver_name;
    }

    public void setMessageType(String message_type) {
        this.message_type = message_type;
    }

    public String getMessageType() {
        return message_type;
    }

    public void setMessageData(String message_data) {
        this.message_data = message_data;
    }

    public String getMessageData() {
        return message_data;
    }

    public void setMessageTimestamp(Long message_timestamp) {
        this.message_timestamp = message_timestamp;
    }

    public Long getMessageTimestamp() {
        return message_timestamp;
    }

}
