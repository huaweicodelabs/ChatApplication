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

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.utils.Constants;

public class EditingFragment extends DialogFragment {

    private boolean type;
    private TextView heading;
    private EditText etUpdateStatus;
    private EditFragmentSetListener editFragmentSetListener;

    public static EditingFragment newInstance(boolean type) {
        Bundle args = new Bundle();
        EditingFragment fragment = new EditingFragment();
        args.putBoolean(Constants.TYPE_OF_DIALOG_FRAGMENT, type);
        fragment.setArguments(args);
        return fragment;
    }

    public void setEditFragmentSetListener(EditFragmentSetListener editFragmentSetListener) {
        this.editFragmentSetListener = editFragmentSetListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getBoolean(Constants.TYPE_OF_DIALOG_FRAGMENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_rounded, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        heading = view.findViewById(R.id.tvTitle);
        etUpdateStatus = view.findViewById(R.id.etUpdate);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);

        setHandling(type);

        btnUpdate.setOnClickListener(view1 -> {
            if (etUpdateStatus.getText() != null && etUpdateStatus.getText().toString().length() > 0) {
                editFragmentSetListener.setEditFragmentSetListener(etUpdateStatus.getText().toString(), type);
            }
            EditingFragment.this.dismiss();
        });
    }

    private void setHandling(boolean type) {
        if (type) {
            heading.setText(getResources().getText(R.string.edit_your_number));
        } else {
            heading.setText(getResources().getString(R.string.edit_your_status));
        }
    }

    public interface EditFragmentSetListener {
        public void setEditFragmentSetListener(String value, boolean isNumber);
    }


}
