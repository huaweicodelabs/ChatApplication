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

package com.huawei.chitchatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.dbcloud.User;
import com.huawei.chitchatapp.viewholder.ContactViewHolder;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    public Context context;
    public ArrayList<User> userArrayList;
    public ContactAdapterListener contactAdapterListener;

    public ContactAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    public void setContactAdapterListener(ContactAdapterListener contactAdapterListener) {
        this.contactAdapterListener = contactAdapterListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemList = layoutInflater.inflate(R.layout.adapter_contact, parent, false);
        return new ContactViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Glide.with(context)
                .load(userArrayList.get(position).getUserProfileUrl())
                .error(R.drawable.profile)
                .into(holder.profileImage);

        holder.numberTv.setText(userArrayList.get(position).getUserPhone());
        holder.nameTv.setText(userArrayList.get(position).getUserName());
        holder.btnInvite.setText(userArrayList.get(position).getUserLoginStatus());
        holder.itemView.setOnClickListener(view -> contactAdapterListener.setContactData(userArrayList.get(position)));
    }


    @Override
    public int getItemCount() {
        if (userArrayList != null) {
            return userArrayList.size();
        } else {
            return 0;
        }
    }

    public interface ContactAdapterListener {
        void setContactData(User user);
    }


}
