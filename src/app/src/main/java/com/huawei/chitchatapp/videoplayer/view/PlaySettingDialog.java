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

package com.huawei.chitchatapp.videoplayer.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Pair;
import android.view.Window;
import android.view.WindowManager;

import com.huawei.chitchatapp.videoplayer.contract.OnPlaySettingListener;

import java.util.ArrayList;
import java.util.List;


public class PlaySettingDialog implements OnClickListener {

    private AlertDialog.Builder builder;

    private OnPlaySettingListener onPlaySettingListener;

    private List<Pair<String, String>> showTextList = new ArrayList<Pair<String, String>>();

    private int playSettingType;

    private int selectIndex;

    public PlaySettingDialog(Context context) {
        builder = new AlertDialog.Builder(context);
    }

    public PlaySettingDialog setTitle(CharSequence title) {
        builder.setTitle(title);
        return this;
    }
    public PlaySettingDialog setList(List<String> strList) {
        showTextList = new ArrayList<Pair<String, String>>();
        if (strList != null) {
            for (String temp : strList) {
                showTextList.add(new Pair<String, String>(temp, temp));
            }
        }
        return this;
    }

    public PlaySettingDialog setSelectValue(String value) {
        for (int iLoop = 0; iLoop < showTextList.size(); iLoop++) {
            if (showTextList.get(iLoop).first.equals(value)) {
                selectIndex = iLoop;
                break;
            }
        }
        return this;
    }

    public PlaySettingDialog setSelectIndex(int index) {
        selectIndex = index;
        return this;
    }

    public PlaySettingDialog initDialog(OnPlaySettingListener playSettingListener, int playSettingType) {
        this.onPlaySettingListener = playSettingListener;
        this.playSettingType = playSettingType;
        return this;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (onPlaySettingListener != null) {
            onPlaySettingListener.onSettingItemClick(showTextList.get(which).first, playSettingType);
        }
        dialog.dismiss();
    }

    public PlaySettingDialog setNegativeButton(String text, OnClickListener listener) {
        builder.setNegativeButton(text, listener);
        return this;
    }

    public PlaySettingDialog show() {
        String[] items = new String[showTextList.size()];

        for (int iLoop = 0; iLoop < items.length; iLoop++) {
            items[iLoop] = showTextList.get(iLoop).second;
        }
        builder.setSingleChoiceItems(items, selectIndex, this);
        AlertDialog dialog = builder.create();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.7f;
        window.setAttributes(lp);
        dialog.show();
        return this;
    }
}
