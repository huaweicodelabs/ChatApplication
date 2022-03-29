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

/**
 * Definition of ObjectType ChatRoomId.
 *
 * @since 2021-10-25
 */
@PrimaryKeys({"room_id"})
@Indexes({"index_room_id_from:room_id,user_mobile_from", "index_room_id_to:room_id,user_mobile_from"})
public final class ChatRoomId extends CloudDBZoneObject {
    private String room_id;

    @NotNull
    @DefaultValue(stringValue = "null")
    private String user_mobile_to;

    @NotNull
    @DefaultValue(stringValue = "null")
    private String user_mobile_from;

    @DefaultValue(booleanValue = true)
    private Boolean update_shadow_flag;

    public ChatRoomId() {
        super(ChatRoomId.class);
        this.user_mobile_to = "null";
        this.user_mobile_from = "null";
        this.update_shadow_flag = true;
    }

    public void setRoomId(String room_id) {
        this.room_id = room_id;
    }

    public String getRoomId() {
        return room_id;
    }

    public void setUserMobileTo(String user_mobile_to) {
        this.user_mobile_to = user_mobile_to;
    }

    public String getUserMobileTo() {
        return user_mobile_to;
    }

    public void setUserMobileFrom(String user_mobile_from) {
        this.user_mobile_from = user_mobile_from;
    }

    public String getUserMobileFrom() {
        return user_mobile_from;
    }

    public void setUpdateShadowFlag(Boolean update_shadow_flag) {
        this.update_shadow_flag = update_shadow_flag;
    }

    public Boolean getUpdateShadowFlag() {
        return update_shadow_flag;
    }

}
