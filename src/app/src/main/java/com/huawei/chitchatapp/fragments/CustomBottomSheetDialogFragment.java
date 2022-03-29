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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.utils.Constants;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private LinearLayout mDocLl;
    private LinearLayout mCameraLl;
    private LinearLayout mGalleryLl;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private LinearLayout llLocation;
    private CustomBottomSheetDialogListener customBottomSheetDialogListener;

    public static final String TAG = CustomBottomSheetDialogFragment.class.getCanonicalName();

    public void setCustomBottomSheetDialogListener(CustomBottomSheetDialogListener customBottomSheetDialogListener) {
        this.customBottomSheetDialogListener = customBottomSheetDialogListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_dialog_bottom_sheet, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedImageUri = data.getData();
                    }
                });


        mDocLl = view.findViewById(R.id.doc_ll);
        mCameraLl = view.findViewById(R.id.camera_ll);
        mGalleryLl = view.findViewById(R.id.gallery_ll);
        llLocation = view.findViewById(R.id.llLocation);

        mGalleryLl.setOnClickListener(view1 -> {
            customBottomSheetDialogListener.setMessageType(Constants.NAV_VIDEO);
        });

        mDocLl.setOnClickListener(view13 -> {
            customBottomSheetDialogListener.setMessageType(Constants.NAV_DOCUMENT);
        });

        llLocation.setOnClickListener(view12 -> customBottomSheetDialogListener.setMessageType(Constants.NAV_LOCATION));

        mCameraLl.setOnClickListener(view14 ->
                customBottomSheetDialogListener.setMessageType(Constants.NAV_CAMERA));
    }


    public interface CustomBottomSheetDialogListener {
        public void setMessageType(String type);
    }

}
