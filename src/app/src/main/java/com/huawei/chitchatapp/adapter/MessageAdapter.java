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
import android.graphics.Typeface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.dbcloud.UserChat;
import com.huawei.chitchatapp.model.MapModel;
import com.huawei.chitchatapp.utils.ChitChatSharedPref;
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.chitchatapp.utils.StaticMapUrl;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Typeface MR;
    private Typeface MRR;


    private Context mContext;
    private List<UserChat> mChat;
    private String imageurl;

    public MessageAdapterSetListener messageAdapterSetListener;

    public MessageAdapter(Context mContext, List<UserChat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }


    public void setMessageAdapterSetListener(MessageAdapterSetListener messageAdapterSetListener) {
        this.messageAdapterSetListener = messageAdapterSetListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserChat chat = mChat.get(position);
        holder.showMessage.setTypeface(MRR);
        holder.txtSeen.setTypeface(MRR);

        if (chat.getMessageType().equals("image")) {

            holder.showMessage.setVisibility(View.GONE);
            holder.showDocument.setVisibility(View.GONE);
            holder.showMessage.setVisibility(View.GONE);
            holder.itemCardViewLocation.setVisibility(View.GONE);
            holder.itemCardViewDocument.setVisibility(View.GONE);
            holder.itemCardView.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(mChat.get(position).getMessageData())
                    .placeholder(R.drawable.avatar)
                    .into(holder.showImage);

        } else if (chat.getMessageType().equals("map")) {
            holder.showMessage.setVisibility(View.GONE);
            holder.showDocument.setVisibility(View.GONE);
            Gson gson = new Gson();
            MapModel model = gson.fromJson(mChat.get(position).getMessageData(), MapModel.class);
            holder.showMessage.setVisibility(View.GONE);
            holder.itemCardViewLocation.setVisibility(View.VISIBLE);
            holder.itemCardViewDocument.setVisibility(View.GONE);
            holder.itemCardView.setVisibility(View.GONE);
            holder.showLocationWv.getSettings().setLoadWithOverviewMode(true);
            holder.showLocationWv.getSettings().setUseWideViewPort(true);
            holder.showLocationWv.loadUrl(StaticMapUrl.getStaticMapWithUrlSignature(model));
            holder.showLocationTv.setText(model.getTitle());


        } else if (chat.getMessageType().equals("document")) {
            holder.showMessage.setVisibility(View.GONE);
            holder.showDocument.setVisibility(View.VISIBLE);
            holder.showDocument.setImageDrawable(mContext.getResources().getDrawable(R.drawable.doc_preview));
            holder.itemCardViewLocation.setVisibility(View.GONE);
            holder.itemCardViewDocument.setVisibility(View.VISIBLE);
            holder.itemCardView.setVisibility(View.GONE);
            holder.showDocumentNameTv.setText(chat.getMessageData());
        } else if (chat.getMessageType().equals("video")) {
            holder.showDocument.setVisibility(View.VISIBLE);
            holder.showDocument.setImageDrawable(mContext.getResources().getDrawable(R.drawable.videoicon));
            holder.showMessage.setVisibility(View.GONE);
            holder.itemCardViewLocation.setVisibility(View.GONE);
            holder.itemCardViewDocument.setVisibility(View.VISIBLE);
            holder.itemCardView.setVisibility(View.GONE);
            Glide.with(mContext)
                    .load(R.drawable.ic_default_video)
                    .placeholder(R.drawable.ic_default_video)
                    .into(holder.showImage);
            holder.showDocumentNameTv.setText(chat.getMessageData());
        } else {
            holder.showMessage.setVisibility(View.VISIBLE);
            holder.itemCardView.setVisibility(View.GONE);
            holder.showMessage.setText(chat.getMessageData());
            holder.itemCardViewLocation.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            messageAdapterSetListener.setMessageAdapterSetListener(mChat.get(position).getMessageType(), mChat.get(position).getMessageData());
        });

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView showMessage;
        public TextView showLocationTv;
        public ImageView profileImage;
        public ImageView showImage;
        public ImageView showDocument;
        public TextView txtSeen;
        public TextView timeTv;
        public TextView showDocumentNameTv;
        public CardView itemCardView;
        public CardView itemCardViewLocation;
        public CardView itemCardViewDocument;
        public WebView showLocationWv;

        public ViewHolder(View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.show_message);
            showLocationTv = itemView.findViewById(R.id.show_location_tv);
            showDocumentNameTv = itemView.findViewById(R.id.show_document_name_tv);
            profileImage = itemView.findViewById(R.id.profile_image);
            showImage = itemView.findViewById(R.id.show_image);
            txtSeen = itemView.findViewById(R.id.txt_seen);
            timeTv = itemView.findViewById(R.id.time_tv);
            itemCardView = itemView.findViewById(R.id.item_card_view);
            itemCardViewLocation = itemView.findViewById(R.id.item_card_view_location);
            itemCardViewDocument = itemView.findViewById(R.id.item_card_view_document);
            showLocationWv = itemView.findViewById(R.id.show_location_wv);
            showDocument = itemView.findViewById(R.id.show_document_iv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChitChatSharedPref.initializeInstance(mContext);
        if (mChat.get(position).getSenderPhone().equals(ChitChatSharedPref.getInstance().getString(Constants.PHONE_NUMBER, ""))) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public interface MessageAdapterSetListener {
        public void setMessageAdapterSetListener(String url, String imageType);
    }
}