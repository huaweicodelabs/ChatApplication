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

package com.huawei.chitchatapp.videoplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.videoplayer.contract.OnItemClickListener;
import com.huawei.chitchatapp.videoplayer.entity.PlayEntity;
import com.huawei.chitchatapp.videoplayer.utils.LogUtil;
import com.huawei.chitchatapp.videoplayer.utils.StringUtil;
import com.huawei.chitchatapp.viewholder.PlayViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SelectPlayDataAdapter extends RecyclerView.Adapter<PlayViewHolder> {
    private static final String TAG = "SelectPlayDataAdapter";

    private final List<PlayEntity> playList;
    private final Context context;
    private final OnItemClickListener onItemClickListener;

    public SelectPlayDataAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        playList = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectPlayList(List<PlayEntity> playList) {
        if (this.playList.size() > 0) {
            this.playList.clear();
        }
        this.playList.addAll(playList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.select_play_item, parent, false);
        return new PlayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (playList.size() > position) {
            PlayEntity playEntity = playList.get(position);
            if (playEntity == null) {
                LogUtil.i(TAG, "current item data is empty.");
                return;
            }
            StringUtil.setTextValue(holder.playName, playEntity.getName());
            StringUtil.setTextValue(holder.playUrl, playEntity.getUrl());
            StringUtil.setTextValue(holder.playType, String.valueOf(playEntity.getUrlType()));
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return playList.size();
    }


}
