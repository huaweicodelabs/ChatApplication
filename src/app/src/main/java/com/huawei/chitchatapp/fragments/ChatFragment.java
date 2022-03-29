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

package com.huawei.chitchatapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.adapter.ChatHistoryAdapter;
import com.huawei.chitchatapp.model.ChatHistoryModel;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.chitchatapp.utils.ChitChatSharedPref;
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.chitchatapp.utils.OnApiError;
import com.huawei.chitchatapp.viewmodel.ChatViewModel;

import java.util.ArrayList;

public class ChatFragment extends BaseFragment {

    public View view;
    private RecyclerView mChatHistoryRv;
    private ChatHistoryAdapter chatHistoryAdapter;

    private ChatViewModel mChatViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mChatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mChatHistoryRv = view.findViewById(R.id.rvChatHistory);
        mChatHistoryRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        ChitChatSharedPref.initializeInstance(requireActivity());
        final String userPhone = ChitChatSharedPref.getInstance().getString(Constants.PHONE_NUMBER, "");
        mChatViewModel.getListOfChattedUser(onApiError).observe(requireActivity(), this::setListAdapter);
        mChatViewModel.initSnapShot(userPhone);

    }

    private void setListAdapter(ArrayList<ChatHistoryModel> chatHistoryModels){
        if(chatHistoryAdapter==null){
            chatHistoryAdapter = new ChatHistoryAdapter(getContext(),chatHistoryModels);
            mChatHistoryRv.setAdapter(chatHistoryAdapter);
        }else{
            chatHistoryAdapter.notifyDataSetChanged();
        }

    }

    OnApiError onApiError = (errorMessage, e) -> AppLog.logE(getTag(), errorMessage);

}
