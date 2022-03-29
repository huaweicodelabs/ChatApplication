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

package com.huawei.chitchatapp.model;

public class ContactModel {

    public String contactName;
    public String profilePic;
    public int contactId;
    public int isContactThere;
    public String phoneNumber;


    public ContactModel() {
    }

    public ContactModel(String contactName, String profilePic, int contactId, int isContactThere, String phoneNumber) {
        this.contactName = contactName;
        this.profilePic = profilePic;
        this.contactId = contactId;
        this.isContactThere = isContactThere;
        this.phoneNumber = phoneNumber;
    }


    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getProfilePic() {
        return profilePic;
    }


    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public boolean getIsContactThere() {
        if (isContactThere == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void setIsContactThere(int isContactThere) {
        this.isContactThere = isContactThere;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
