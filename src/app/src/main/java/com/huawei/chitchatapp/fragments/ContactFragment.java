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

import android.content.Intent;
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
import com.huawei.chitchatapp.activity.MessageActivity;
import com.huawei.chitchatapp.adapter.ContactAdapter;
import com.huawei.chitchatapp.dbcloud.User;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.chitchatapp.utils.ChitChatSharedPref;
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.chitchatapp.utils.Util;
import com.huawei.chitchatapp.viewmodel.ContactViewModel;
import com.huawei.chitchatapp.viewmodel.RoomIdChatViewModel;

public class ContactFragment extends BaseFragment implements ContactAdapter.ContactAdapterListener {


    private RecyclerView contactRecyclerView;
    private ContactAdapter contactAdapter;
    private ContactViewModel contactViewModel;
    private RoomIdChatViewModel roomIdChatViewModel;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        initObserver();
    }

    private void initObserver(){
        contactViewModel.userMutableLiveData.observe(requireActivity(), users -> {
            if (users != null) {
                contactAdapter = new ContactAdapter(requireActivity(), users);
                contactAdapter.setContactAdapterListener(ContactFragment.this);
                contactRecyclerView.setAdapter(contactAdapter);
                Util.stopProgressBar();
            }
        });

        roomIdChatViewModel.getRoomId((errorMessage, e) -> requireActivity().runOnUiThread(() -> {
            AppLog.logE(ContactFragment.class.getSimpleName(),errorMessage);
        })).observe(requireActivity(), this::startMainActivity);
    }

    public void initView(View view) {
        ChitChatSharedPref.initializeInstance(requireActivity());
        Util.showProgressBar(requireActivity());
        contactRecyclerView = view.findViewById(R.id.rvContactAdapter);
        roomIdChatViewModel = new ViewModelProvider(this).get(RoomIdChatViewModel.class);
        roomIdChatViewModel.initSnapShot();
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        contactViewModel = new ViewModelProvider(requireActivity()).get(ContactViewModel.class);
        contactViewModel.getContact(requireContext());
    }

    private void startMainActivity(String roomId){
        Intent intent = new Intent(requireContext(), MessageActivity.class);
        intent.putExtra("name", user.getUserName());
        intent.putExtra("phone", user.getUserPhone());
        intent.putExtra("imageUrl", user.getUserProfileUrl());
        intent.putExtra("roomId", roomId);
        startActivity(intent);
    }

    @Override
    public void setContactData(User user) {
        setUser(user);
        ChitChatSharedPref.initializeInstance(requireActivity());
        roomIdChatViewModel.callGetRoomId(user.getUserPhone(), ChitChatSharedPref.getInstance().getString(Constants.PHONE_NUMBER, ""), false);
    }

    private void setUser(User user) {
        this.user = user;
    }


}
