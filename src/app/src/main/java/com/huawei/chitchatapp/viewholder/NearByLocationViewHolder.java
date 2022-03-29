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

package com.huawei.chitchatapp.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.chitchatapp.R;

public class NearByLocationViewHolder extends RecyclerView.ViewHolder {

    public TextView mName;
    public TextView mChatText;
    public TextView mDistance;

    public NearByLocationViewHolder(@NonNull View itemView) {
        super(itemView);
        mName = itemView.findViewById(R.id.tvContactNameHistory);
        mChatText = itemView.findViewById(R.id.tvContactNumberHistory);
        mDistance = itemView.findViewById(R.id.tvDistance);
    }
}