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
import com.huawei.chitchatapp.model.ChatHistoryModel;
import com.huawei.chitchatapp.viewholder.ChatHistoryViewHolder;

import java.util.List;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryViewHolder> {
    private Context context;
    private List<ChatHistoryModel> chatHistoryModelList;

    public ChatHistoryAdapter(Context context, List<ChatHistoryModel> chatHistoryModelList) {
        this.context = context;
        this.chatHistoryModelList = chatHistoryModelList;
    }

    @NonNull
    @Override
    public ChatHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemList = layoutInflater.inflate(R.layout.chat_history_item, parent, false);
        ChatHistoryViewHolder viewHolder = new ChatHistoryViewHolder(itemList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHistoryViewHolder holder, int position) {

        holder.mName.setText(chatHistoryModelList.get(position).getName());
        holder.mChatText.setText(chatHistoryModelList.get(position).getChats());

        holder.itemView.setOnClickListener(view -> {
            Intent chatIntent = new Intent(context, MessageActivity.class);
            chatIntent.putExtra("name", chatHistoryModelList.get(position).getName());
            chatIntent.putExtra("phone", chatHistoryModelList.get(position).getNumber());
            chatIntent.putExtra("imageUrl", chatHistoryModelList.get(position).getUserProfile());
            chatIntent.putExtra("roomId", chatHistoryModelList.get(position).getRoomId());
            context.startActivity(chatIntent);
        });

        if (chatHistoryModelList.get(position).getUserProfile() != null) {
            Glide.with(context)
                    .load(chatHistoryModelList.get(position).getUserProfile())
                    .error(R.drawable.profile)
                    .placeholder(R.drawable.profile)
                    .into(holder.mProfileImg);
        }

    }


    @Override
    public int getItemCount() {
        return chatHistoryModelList.size();
    }


}
