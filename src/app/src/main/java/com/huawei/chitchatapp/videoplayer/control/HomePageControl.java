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

package com.huawei.chitchatapp.videoplayer.control;

import android.content.Context;
import android.widget.Toast;

import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.videoplayer.entity.PlayEntity;
import com.huawei.chitchatapp.videoplayer.utils.Constants;
import com.huawei.chitchatapp.videoplayer.utils.DataFormatUtil;
import com.huawei.chitchatapp.videoplayer.utils.PlayControlUtil;

import java.util.ArrayList;
import java.util.List;

public class HomePageControl {

    private List<PlayEntity> playEntityList;
    private final Context context;

    public HomePageControl(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        playEntityList = new ArrayList<>();
    }

    public void loadPlayList() {
        playEntityList.clear();
        playEntityList.addAll(DataFormatUtil.getPlayList(context));
    }

    public List<PlayEntity> getPlayList() {
        return playEntityList;
    }

    private boolean isPlayListEmpty() {
        return playEntityList == null || playEntityList.size() == 0;
    }

    private boolean isPlayEffective(int position) {
        return !isPlayListEmpty() && playEntityList.size() > position;
    }

    public PlayEntity getPlayFromPosition(int position) {
        if (isPlayEffective(position)) {
            return playEntityList.get(position);
        }
        return null;
    }

    public void setVideoType(int videoType) {
        PlayControlUtil.setVideoType(videoType);
    }


    public void setSurfaceViewView(boolean isSurfaceView) {
        PlayControlUtil.setIsSurfaceView(isSurfaceView);
    }

    public void setMute(boolean status) {
        PlayControlUtil.setIsMute(status);
    }

    public void setPlayMode(int playMode) {
        PlayControlUtil.setPlayMode(playMode);
    }

    public void setBandwidthSwitchMode(int mod) {
        PlayControlUtil.setBandwidthSwitchMode(mod);
    }

    public void setInitBitrateEnable(boolean initBitrateEnable) {
        PlayControlUtil.setInitBitrateEnable(initBitrateEnable);
    }

    public void setBandwidthSwitchMode(boolean takeEffectOfAll) {
        PlayControlUtil.setTakeEffectOfAll(takeEffectOfAll);
    }

    public void setCloseLogo(boolean closeLogo) {
        PlayControlUtil.setCloseLogo(closeLogo);
    }

    public PlayEntity getInputPlay(String inputUrl) {
        PlayEntity playEntity = new PlayEntity();
        playEntity.setUrl(inputUrl);
        playEntity.setUrlType(Constants.UrlType.URL);
        return playEntity;
    }

    public void pauseAllTasks() {
        if (PlayControlUtil.getPreloader() != null) {
            PlayControlUtil.getPreloader().pauseAllTasks();
        } else {
            showPreloadFailToast();
        }
    }

    public void resumeAllTasks() {
        if (PlayControlUtil.getPreloader() != null) {
            PlayControlUtil.getPreloader().resumeAllTasks();
        } else {
            showPreloadFailToast();
        }
    }

    public void removeAllCache() {
        if (PlayControlUtil.getPreloader() != null) {
            PlayControlUtil.getPreloader().removeAllCache();
        } else {
            showPreloadFailToast();
        }
    }

    public void removeAllTasks() {
        if (PlayControlUtil.getPreloader() != null) {
            PlayControlUtil.getPreloader().removeAllTasks();
        } else {
            showPreloadFailToast();
        }
    }

    private void showPreloadFailToast() {
        Toast.makeText(context, context.getString(R.string.video_add_single_cache_fail), Toast.LENGTH_SHORT).show();
    }

    public void setDownloadLink(boolean isDownloadLinkSingle) {
        PlayControlUtil.setIsDownloadLinkSingle(isDownloadLinkSingle);
    }


    public void setWakeMode(boolean isWakeOn) {
        PlayControlUtil.setWakeOn(isWakeOn);
    }


    public void setSubtitleRenderByDemo(boolean isRenderByDemo) {
        PlayControlUtil.setSubtitleRenderByDemo(isRenderByDemo);
    }
}
