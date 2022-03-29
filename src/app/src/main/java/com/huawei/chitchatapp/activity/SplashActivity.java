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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.utils.ChitChatSharedPref;
import com.huawei.chitchatapp.utils.Constants;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView[] dots;
    private Button next;
    private Button skip;
    private LinearLayout dotsLayout;
    private int[] layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        initView();
    }


    private void initView() {
        viewPager = findViewById(R.id.act_splash_view_pager);
        dotsLayout = findViewById(R.id.act_splash_layoutDots);
        skip = findViewById(R.id.act_splash_btn_skip);
        next = findViewById(R.id.act_splash_btn_next);

        skip.setOnClickListener(this);
        next.setOnClickListener(this);

        layouts = new int[]{R.layout.slider_1, R.layout.slider_2, R.layout.slider_3};

        addBottomDots(0);
        changeStatusBarColor();

        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewListener);
    }

    private void addBottomDots(int position) {
        dots = new TextView[layouts.length];
        int[] colorActive = getResources().getIntArray(R.array.dot_active);
        int[] colorInactive = getResources().getIntArray(R.array.dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorInactive[position]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(colorActive[position]);

        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addBottomDots(position);
            if (position == layouts.length - 1) {
                next.setText(getResources().getString(R.string.proceed));
                skip.setVisibility(View.GONE);
            } else {
                next.setText(getResources().getString(R.string.next));
                skip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_splash_btn_next:
                funNext();
                break;
            case R.id.act_splash_btn_skip:
                skitFun();
                break;
            default:
                break;
        }
    }

    private void skitFun() {
        ChitChatSharedPref.initializeInstance(SplashActivity.this);
        if (ChitChatSharedPref.getInstance().getString(Constants.ALREADY_LOGIN, "").equalsIgnoreCase("1")) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            requestPermission();
        }

        finish();
    }

    private void funNext() {
        int current = getItem(+1);
        if (current < layouts.length) {
            viewPager.setCurrentItem(current);
        } else {
            if (checkContactPermission()) {
                ChitChatSharedPref.initializeInstance(SplashActivity.this);
                if (ChitChatSharedPref.getInstance().getString(Constants.ALREADY_LOGIN, "").equalsIgnoreCase("1")) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            } else {
                requestPermission();
            }
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.CONTACT_PERMISSION);
        }
    }

    private boolean checkContactPermission() {
        int contactPermission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_CONTACTS);
        int readStoragePermission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return contactPermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
                &&writeStoragePermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.CONTACT_PERMISSION) {
            if (grantResults.length > 0) {
                boolean contactPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (contactPermission) {
                    ChitChatSharedPref.initializeInstance(SplashActivity.this);
                    if (ChitChatSharedPref.getInstance().getString(Constants.ALREADY_LOGIN, "").equalsIgnoreCase("1")) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                }
            }
        }
    }

    public class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @Override
        public Object instantiateItem(ViewGroup myContainer, int mPosition) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = layoutInflater.inflate(layouts[mPosition], myContainer, false);
            myContainer.addView(v);
            return v;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View mView, Object mObject) {
            return mView == mObject;
        }

        @Override
        public void destroyItem(ViewGroup mContainer, int mPosition, Object mObject) {
            View v = (View) mObject;
            mContainer.removeView(v);
        }
    }

}