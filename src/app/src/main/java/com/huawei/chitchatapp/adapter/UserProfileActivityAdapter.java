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
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.activity.MessageActivity;
import com.huawei.chitchatapp.dbcloud.UserChat;
import com.huawei.chitchatapp.viewholder.UserProfileActivityViewHolder;

import java.util.List;

public class UserProfileActivityAdapter extends RecyclerView.Adapter<UserProfileActivityViewHolder> {
    private Context context;
    private List<UserChat> chatDocumentModelList;

    public UserProfileActivityAdapter(Context context, List<UserChat> chatDocumentModelList) {
        this.context = context;
        this.chatDocumentModelList = chatDocumentModelList;
    }

    @NonNull
    @Override
    public UserProfileActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemList = layoutInflater.inflate(R.layout.user_chat_document_item, parent, false);
        UserProfileActivityViewHolder viewHolder = new UserProfileActivityViewHolder(itemList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileActivityViewHolder holder, int position) {


        holder.itemView.setOnClickListener(view -> {
            Intent chatIntent = new Intent(context, MessageActivity.class);
            chatIntent.putExtra("imageUrl", chatDocumentModelList.get(position).getMessageData());
            context.startActivity(chatIntent);
        });

        if (chatDocumentModelList.get(position).getMessageData() != null) {
            Glide.with(context)
                    .load(chatDocumentModelList.get(position).getMessageData())
                    .error(R.drawable.profile)
                    .placeholder(R.drawable.profile)
                    .into(holder.mUserDocumentIv);
        }

    }


    @Override
    public int getItemCount() {
        return chatDocumentModelList.size();
    }


}
