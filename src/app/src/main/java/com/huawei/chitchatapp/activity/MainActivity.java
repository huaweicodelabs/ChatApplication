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

package com.huawei.chitchatapp.activity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.adapter.ChitChatPagerAdapter;
import com.huawei.chitchatapp.dbcloud.User;
import com.huawei.chitchatapp.service.ChitChatStatusObserver;
import com.huawei.chitchatapp.utils.ChitChatSharedPref;
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.chitchatapp.utils.LoginHelper;
import com.huawei.chitchatapp.viewmodel.UserProfile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoginHelper.OnLoginEventCallBack {

    private ImageView toolBarProfileImageView;
    private UserProfile userProfile;
    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.pager);
        toolBarProfileImageView = findViewById(R.id.act_main_profile_iv);
        userName = findViewById(R.id.userName);
        ChitChatSharedPref.initializeInstance(MainActivity.this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ChitChatStatusObserver(MainActivity.this));
        userProfile = new ViewModelProvider(MainActivity.this).get(UserProfile.class);

        ChitChatPagerAdapter chitChatPagerAdapter = new ChitChatPagerAdapter(MainActivity.this);
        viewPager.setAdapter(chitChatPagerAdapter);
        setSupportActionBar(toolbar);


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(R.string.chats));
                    break;
                case 1:
                    tab.setText(getString(R.string.contacts));
                    break;
                case 2:
                    tab.setText(getString(R.string.profile));
                    break;
            }
        }).attach();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar
                .make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        initObserver();

    }

    private void initObserver() {
        userProfile.userLiveData.observe(MainActivity.this, this::updateUI);
    }

    private void updateUI(User user) {
        userName.setText(user.getUserName());
        Glide.with(this)
                .load(user.getUserProfileUrl())
                .placeholder(R.drawable.profile)
                .circleCrop()
                .into(toolBarProfileImageView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        userProfile.query(ChitChatSharedPref.getInstance().getString(Constants.PHONE_NUMBER, ""), MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLogin(boolean showLoginUserInfo, SignInResult signInResult) {
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLogOut(boolean showLoginUserInfo) {

    }


}