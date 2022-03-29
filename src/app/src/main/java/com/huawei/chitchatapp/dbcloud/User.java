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


@PrimaryKeys({"user_phone"})
@Indexes({"user_id:user_id", "user_phone_id:user_phone,user_id"})
public final class User extends CloudDBZoneObject {
    private Integer user_id;

    private String user_phone;

    private String user_name;

    private String user_status;

    private String user_login_status;

    private String user_push_token;

    private String user_profile_url;

    public User() {
        super(User.class);
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUserPhone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUserPhone() {
        return user_phone;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserStatus(String user_status) {
        this.user_status = user_status;
    }

    public String getUserStatus() {
        return user_status;
    }

    public void setUserLoginStatus(String user_login_status) {
        this.user_login_status = user_login_status;
    }

    public String getUserLoginStatus() {
        return user_login_status;
    }

    public void setUserPushToken(String user_push_token) {
        this.user_push_token = user_push_token;
    }

    public String getUserPushToken() {
        return user_push_token;
    }

    public void setUserProfileUrl(String user_profile_url) {
        this.user_profile_url = user_profile_url;
    }

    public String getUserProfileUrl() {
        return user_profile_url;
    }

}
