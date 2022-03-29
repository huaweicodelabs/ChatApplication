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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.viewholder.NearByLocationViewHolder;
import com.huawei.hms.site.api.model.Site;

import java.util.List;

public class NearByLocationAdapter extends RecyclerView.Adapter<NearByLocationViewHolder> {
    private Context context;
    private List<Site> siteList;
    private NearbyLocationListener nearbyLocationListener;


    public NearByLocationAdapter(Context context, List<Site> siteList) {
        this.context = context;
        this.siteList = siteList;
    }

    public void setNearbyLocationListener(NearbyLocationListener nearbyLocationListener) {
        this.nearbyLocationListener = nearbyLocationListener;
    }

    @NonNull
    @Override
    public NearByLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemList = layoutInflater.inflate(R.layout.nearby_location_item, parent, false);
        return new NearByLocationViewHolder(itemList);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NearByLocationViewHolder holder, int position) {

        holder.mName.setText(siteList.get(position).getName());
        holder.mChatText.setText(siteList.get(position).getFormatAddress());
        holder.mDistance.setText(siteList.get(position).getDistance() + " m");
        holder.itemView.setOnClickListener(view -> {
            nearbyLocationListener.nearbyAddress(siteList.get(position));
        });
    }


    @Override
    public int getItemCount() {
        return siteList.size();
    }

    public interface NearbyLocationListener {
        void nearbyAddress(Site site);
    }

}
