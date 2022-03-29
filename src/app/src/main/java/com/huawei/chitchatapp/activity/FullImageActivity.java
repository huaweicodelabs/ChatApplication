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
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.utils.Constants;

public class FullImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 0.1f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_full_image);
        imageView = findViewById(R.id.fullImage);
        if(getIntent().getStringExtra(Constants.DATA_TYPE)!=null){

            Glide.with(this)
                    .load(getIntent().getStringExtra(Constants.DATA_TYPE))
                    .placeholder(R.drawable.profile)
                    .into(imageView);
        }

        mScaleGestureDetector = new ScaleGestureDetector(this, new FullImageActivity.ScaleListener());


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mScaleGestureDetector.onTouchEvent(event);
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        // when a scale gesture is detected, use it to resize the image
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.75f, Math.min(mScaleFactor, 5.0f));
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }
}
