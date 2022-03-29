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

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.videoplayer.adapter.SelectPlayDataAdapter;
import com.huawei.chitchatapp.videoplayer.contract.OnPlayWindowListener;
import com.huawei.chitchatapp.videoplayer.contract.OnWisePlayerListener;
import com.huawei.chitchatapp.videoplayer.entity.PlayEntity;
import com.huawei.chitchatapp.videoplayer.utils.Constants;
import com.huawei.chitchatapp.videoplayer.utils.DataFormatUtil;
import com.huawei.chitchatapp.videoplayer.utils.DeviceUtil;
import com.huawei.chitchatapp.videoplayer.utils.DialogUtil;
import com.huawei.chitchatapp.videoplayer.utils.LogUtil;
import com.huawei.chitchatapp.videoplayer.utils.PlayControlUtil;
import com.huawei.chitchatapp.videoplayer.utils.TimeUtil;
import com.huawei.hms.videokit.player.WisePlayer;

import java.util.List;

public class PlayView {

    private final Context context;

    private SurfaceView surfaceView;

    private TextureView textureView;

    private SeekBar seekBar;

    private TextView currentTimeTv;

    private TextView totalTimeTv;

    private ImageView playImg;

    private RelativeLayout videoBufferLayout;

    private TextView bufferPercentTv;

    private TextView currentPlayNameTv;

    private TextView speedTv;

    private Button fullScreenBt;

    private FrameLayout controlLayout;

    private LinearLayout contentLayout;

    private TextView videoNameTv;

    private TextView videoSizeTv;

    private TextView videoTimeTv;

    private TextView videoDownloadTv;

    private TextView videoBitrateTv;

    private SelectPlayDataAdapter selectPlayDataAdapter;

    private final OnPlayWindowListener onPlayWindowListener;

    private final OnWisePlayerListener onWisePlayerListener;

    private TextView switchBitrateTv;

    private TextView switchingBitrateTv;

    private RelativeLayout instreamContainer;

    private int maxAdDuration;

    private WisePlayer wisePlayer;

    private TextView countDown;

    public PlayView(Context context, OnPlayWindowListener onPlayWindowListener,
        OnWisePlayerListener onWisePlayerListener) {
        this.context = context;
        this.onPlayWindowListener = onPlayWindowListener;
        this.onWisePlayerListener = onWisePlayerListener;
    }

    public View getContentView() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.play_view, null);
        initView(view);
        initRecycleView(view);
        showDefaultValueView();
        return view;
    }

    private void initView(View view) {
        if (view != null) {
            surfaceView = view.findViewById(R.id.surface_view);
            textureView = view.findViewById(R.id.texture_view);
            if (PlayControlUtil.isSurfaceView()) {
                SurfaceHolder surfaceHolder = surfaceView.getHolder();
                surfaceHolder.addCallback(onPlayWindowListener);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                textureView.setVisibility(View.GONE);
                surfaceView.setVisibility(View.VISIBLE);
            } else {
                textureView.setSurfaceTextureListener(onPlayWindowListener);
                textureView.setVisibility(View.VISIBLE);
                surfaceView.setVisibility(View.GONE);
            }
            seekBar = view.findViewById(R.id.seek_bar);
            seekBar.setOnSeekBarChangeListener(onWisePlayerListener);
            currentTimeTv = view.findViewById(R.id.current_time_tv);
            totalTimeTv = view.findViewById(R.id.total_time_tv);
            playImg = view.findViewById(R.id.play_btn);
            playImg.setOnClickListener(onPlayWindowListener);
            ImageView playRefreshImg = view.findViewById(R.id.play_refresh);
            playRefreshImg.setOnClickListener(onPlayWindowListener);
            TextView backTv = view.findViewById(R.id.back_tv);
            backTv.setOnClickListener(onPlayWindowListener);
            fullScreenBt = view.findViewById(R.id.fullscreen_btn);
            fullScreenBt.setOnClickListener(onPlayWindowListener);
            videoBufferLayout = view.findViewById(R.id.buffer_rl);
            videoBufferLayout.setVisibility(View.GONE);
            controlLayout = view.findViewById(R.id.control_layout);
            bufferPercentTv = view.findViewById(R.id.play_process_buffer);
            contentLayout = view.findViewById(R.id.content_layout);
            currentPlayNameTv = view.findViewById(R.id.video_name_tv);
            speedTv = view.findViewById(R.id.play_speed_btn);
            speedTv.setOnClickListener(onPlayWindowListener);
            TextView settingsTv = view.findViewById(R.id.setting_tv);
            settingsTv.setOnClickListener(onPlayWindowListener);
            videoNameTv = view.findViewById(R.id.tv_video_name);
            videoSizeTv = view.findViewById(R.id.video_width_and_height);
            videoTimeTv = view.findViewById(R.id.video_time);
            videoDownloadTv = view.findViewById(R.id.video_download_speed);
            videoBitrateTv = view.findViewById(R.id.video_bitrate);
            switchBitrateTv = view.findViewById(R.id.switch_bitrate_tv);
            switchBitrateTv.setOnClickListener(onPlayWindowListener);
            switchBitrateTv.setVisibility(View.GONE);
            switchingBitrateTv = view.findViewById(R.id.switching_bitrate_tv);
            switchingBitrateTv.setVisibility(View.GONE);

            TextView skipAd = view.findViewById(R.id.instream_skip);
            countDown = view.findViewById(R.id.instream_count_down);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateCountDown(long playTime) {
        final String time = String.valueOf(Math.round((maxAdDuration - playTime) / 1000));
        countDown.setText(time + "s");
    }

    private void initRecycleView(View view) {
        RecyclerView playRecyclerView = view.findViewById(R.id.player_recycler_view);
        playRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        selectPlayDataAdapter = new SelectPlayDataAdapter(context, onPlayWindowListener);
        playRecyclerView.setAdapter(selectPlayDataAdapter);
    }

    public void setRecycleData(List<PlayEntity> list) {
        selectPlayDataAdapter.setSelectPlayList(list);
    }

    public void updatePlayView(WisePlayer wisePlayer) {
        if (wisePlayer != null) {
            int totalTime = wisePlayer.getDuration();
            LogUtil.i(String.valueOf(totalTime));
            seekBar.setMax(totalTime);
            totalTimeTv.setText(TimeUtil.formatLongToTimeStr(totalTime));
            currentTimeTv.setText(TimeUtil.formatLongToTimeStr(0));
            seekBar.setProgress(0);
            contentLayout.setVisibility(View.GONE);
            this.wisePlayer = wisePlayer;
            configAdLoader(totalTime);

        }
    }

    private void configAdLoader(int totalTime) {
        int maxCount = 1;
        if(totalTime > 30000){
            maxCount = 2;
        }
        totalTime = totalTime / 1000;
    }

    @SuppressLint("SetTextI18n")
    public void updateBufferingView(int percent) {
        LogUtil.d("show buffering view loading");
        if (videoBufferLayout.getVisibility() == View.GONE) {
            videoBufferLayout.setVisibility(View.VISIBLE);
        }
        bufferPercentTv.setText(percent + "%");
    }

    public void showBufferingView() {
        videoBufferLayout.setVisibility(View.VISIBLE);
        bufferPercentTv.setText("0%");
    }

    public void dismissBufferingView() {
        LogUtil.d("dismiss buffering view loading");
        videoBufferLayout.setVisibility(View.GONE);
    }

    public void setPauseView() {
        playImg.setImageResource(R.drawable.ic_full_screen_suspend_normal);
    }

    public void setPlayView() {
        playImg.setImageResource(R.drawable.ic_play);
    }

    public void updatePlayProgressView(int progress, int bufferPosition, long bufferingSpeed, int bitrate) {
        seekBar.setProgress(progress);
        seekBar.setSecondaryProgress(bufferPosition);
        currentTimeTv.setText(TimeUtil.formatLongToTimeStr(progress));
        videoDownloadTv.setText(context.getResources().getString(R.string.video_download_speed, bufferingSpeed));
        if (bitrate == 0) {
            videoBitrateTv.setText(context.getResources().getString(R.string.video_bitrate_empty));
        } else {
            videoBitrateTv.setText(context.getResources().getString(R.string.video_bitrate, bitrate));
        }
    }

    public void setFullScreenView(String name) {
        fullScreenBt.setVisibility(View.GONE);
        contentLayout.setVisibility(View.GONE);
        currentPlayNameTv.setVisibility(View.VISIBLE);
        surfaceView.setLayoutParams(
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        currentPlayNameTv.setText(name);
    }

    public void setPortraitView() {
        surfaceView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            DeviceUtil.dp2px(context, Constants.HEIGHT_DP)));
        fullScreenBt.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        currentPlayNameTv.setVisibility(View.INVISIBLE);
        currentPlayNameTv.setText(null);
    }

    public void updatePlayCompleteView() {
        playImg.setImageResource(R.drawable.ic_play);
        controlLayout.setVisibility(View.VISIBLE);
    }

    public void onPause() {
        dismissBufferingView();
    }

    public void setContentView(WisePlayer wisePlayer, String name) {
        if (wisePlayer != null) {
            videoNameTv.setText(context.getResources().getString(R.string.video_name, name));
            videoSizeTv.setText(context.getResources()
                .getString(R.string.video_width_and_height, wisePlayer.getVideoWidth(), wisePlayer.getVideoHeight()));
            videoTimeTv.setText(context.getResources()
                .getString(R.string.video_time, TimeUtil.formatLongToTimeStr(wisePlayer.getDuration())));
        }
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public TextureView getTextureView() {
        return textureView;
    }

    public void showSettingDialog(int settingType, List<String> showTextList, int selectIndex) {
        DialogUtil.onSettingDialogSelectIndex(context, settingType, showTextList, selectIndex, onPlayWindowListener);
    }

    public void showSettingDialogValue(int settingType, List<String> showTextList, String selectValue) {
        DialogUtil.onSettingDialogSelectValue(context, settingType, showTextList, selectValue, onPlayWindowListener);
    }

    public void showGettingDialog(int gettingType, List<String> showTextList, int selectIndex) {
        DialogUtil.onGettingDialogSelectIndex(context, gettingType, showTextList, selectIndex, onPlayWindowListener);
    }

    public void setSpeedButtonText(String speedText) {
        speedTv.setText(speedText);
    }

    public void showDefaultValueView() {
        currentTimeTv.setText(TimeUtil.formatLongToTimeStr(0));
        totalTimeTv.setText(TimeUtil.formatLongToTimeStr(0));
        speedTv.setText(context.getString(R.string._1_0x));
        videoNameTv.setText(context.getResources().getString(R.string.video_name, ""));
        videoSizeTv.setText(context.getResources().getString(R.string.video_width_and_height, 0, 0));
        videoTimeTv.setText(context.getResources().getString(R.string.video_time, TimeUtil.formatLongToTimeStr(0)));
        videoDownloadTv.setText(context.getResources().getString(R.string.video_download_speed, 0));
        videoBitrateTv.setText(context.getResources().getString(R.string.video_bitrate_empty));
    }

    public void reset() {
        showBufferingView();
        playImg.setImageResource(R.drawable.ic_play);
        showDefaultValueView();
        hiddenSwitchingBitrateTextView();
        hiddenSwitchBitrateTextView();
        setSwitchBitrateTv(0);
    }

    public void showSwitchBitrateTextView() {
        if (switchBitrateTv.getVisibility() == View.GONE) {
            switchBitrateTv.setVisibility(View.VISIBLE);
        }
    }

    public void hiddenSwitchBitrateTextView() {
        if (switchBitrateTv.getVisibility() == View.VISIBLE) {
            switchBitrateTv.setVisibility(View.GONE);
        }
    }

    public void setSwitchBitrateTv(int videoHeight) {
        switchBitrateTv.setText(DataFormatUtil.getVideoQuality(context, videoHeight));
    }

    public void showSwitchingBitrateTextView(String textValue) {
        if (switchingBitrateTv.getVisibility() == View.GONE) {
            switchingBitrateTv.setVisibility(View.VISIBLE);
        }
        switchingBitrateTv.setText(Html.fromHtml(context.getString(R.string.resolution_switching, textValue)));
    }

    public void updateSwitchingBitrateTextView(String textValue) {
        switchingBitrateTv.setText(textValue);
    }

    public void hiddenSwitchingBitrateTextView() {
        if (switchingBitrateTv.getVisibility() == View.VISIBLE) {
            switchingBitrateTv.setVisibility(View.GONE);
        }
    }

}
