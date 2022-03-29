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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.annotation.NonNull;

import com.huawei.chitchatapp.fragments.ChatFragment;
import com.huawei.chitchatapp.fragments.ContactFragment;
import com.huawei.chitchatapp.fragments.ProfileFragment;

public class ChitChatPagerAdapter extends FragmentStateAdapter {


    public ChitChatPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ChatFragment();
                break;
            case 1:
                fragment = new ContactFragment();
                break;
            case 2:
                fragment = new ProfileFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
