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
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.TextureView;

import com.huawei.chitchatapp.ChitChatApplication;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.chitchatapp.videoplayer.contract.OnWisePlayerListener;
import com.huawei.chitchatapp.videoplayer.entity.BitrateInfo;
import com.huawei.chitchatapp.videoplayer.entity.PlayEntity;
import com.huawei.chitchatapp.videoplayer.utils.Constants;
import com.huawei.chitchatapp.videoplayer.utils.DataFormatUtil;
import com.huawei.chitchatapp.videoplayer.utils.PlayControlUtil;
import com.huawei.chitchatapp.videoplayer.utils.StringUtil;
import com.huawei.hms.videokit.player.AudioTrackInfo;
import com.huawei.hms.videokit.player.InitBitrateParam;
import com.huawei.hms.videokit.player.StreamInfo;
import com.huawei.hms.videokit.player.VideoInfo;
import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.common.PlayerConstants;
import com.huawei.hms.videokit.player.common.PlayerConstants.CycleMode;
import com.huawei.hms.videokit.player.common.PlayerConstants.ScenarioType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayControl {
    private static final String TAG = "PlayControl";

    private final Context context;

    private WisePlayer wisePlayer;

    private PlayEntity currentPlayData;

    private final OnWisePlayerListener onWisePlayerListener;

    private List<PlayEntity> playEntityList;

    private boolean isHttpVideo = true;

    private List<BitrateInfo> bitrateRangeList;

    private List<String> switchBitrateList;

    public PlayControl(Context context, OnWisePlayerListener onWisePlayerListener) {
        this.context = context;
        this.onWisePlayerListener = onWisePlayerListener;
        init();
    }

    public void init() {
        initPlayer();
        setPlayListener();
    }

    private void setPlayListener() {
        if (wisePlayer != null) {
            wisePlayer.setErrorListener(onWisePlayerListener);
            wisePlayer.setEventListener(onWisePlayerListener);
            wisePlayer.setResolutionUpdatedListener(onWisePlayerListener);
            wisePlayer.setReadyListener(onWisePlayerListener);
            wisePlayer.setLoadingListener(onWisePlayerListener);
            wisePlayer.setPlayEndListener(onWisePlayerListener);
            wisePlayer.setSeekEndListener(onWisePlayerListener);
            if (PlayControlUtil.isSubtitleRenderByDemo()) {
                wisePlayer.setSubtitleUpdateListener(onWisePlayerListener);
            } else {
                wisePlayer.setSubtitleUpdateListener(null);
            }
        }
    }

    public boolean initPlayFail() {
        return wisePlayer == null;
    }

    private void initPlayer() {
        if (ChitChatApplication.getWisePlayerFactory() == null) {
            return;
        }
        wisePlayer = ChitChatApplication.getWisePlayerFactory().createWisePlayer();
    }

    public void setCurrentPlayData(Serializable serializable) {
        if (serializable instanceof PlayEntity) {
            currentPlayData = (PlayEntity) serializable;
        }
    }

    public void ready() {
        if (currentPlayData != null && wisePlayer != null) {
            AppLog.logD(TAG, "current play video url is :" + currentPlayData.getUrl());
            if (currentPlayData.getUrlType() == Constants.UrlType.URL) {
                setHttpVideo(true);
                wisePlayer.setPlayUrl(new String[]{currentPlayData.getUrl()});
            } else if (currentPlayData.getUrlType() == Constants.UrlType.URL_JSON) {
                setHttpVideo(false);
                wisePlayer.setPlayUrl(currentPlayData.getUrl(), currentPlayData.getAppId(), ScenarioType.ONLINE);
            } else if (currentPlayData.getUrlType() == Constants.UrlType.URL_MULTIPLE) {
                setHttpVideo(true);
                String[] strings = StringUtil.getStringArray(currentPlayData.getUrl(), "-SPAD-");
                wisePlayer.setPlayUrl(strings);
            } else if (currentPlayData.getUrlType() == Constants.UrlType.URL_JSON_FORMAT) {
                setHttpVideo(false);
                wisePlayer.setPlayUrl(currentPlayData.getUrl(), currentPlayData.getAppId(), ScenarioType.ONLINE, currentPlayData.getVideoFormat());
            } else {
                setHttpVideo(true);
                wisePlayer.setPlayUrl(new String[]{currentPlayData.getUrl()});
            }
            setProxyInfo();
            setDownloadLink();
            setBookmark();
            setPlayMode(PlayControlUtil.getPlayMode(), false);
            setMute(PlayControlUtil.isMute());
            setVideoType(PlayControlUtil.getVideoType(), false);
            setBandwidthSwitchMode(PlayControlUtil.getBandwidthSwitchMode(), false);
            setInitBitrateEnable();
            setInitBufferTimeStrategy();
            setBitrateRange();
            setSubtitlePresetLanguage();
            setCloseLogo();
            setPreferAudioLang();
            setWakeMode(PlayControlUtil.isWakeOn());
            setSubtitleRenderByDemo(PlayControlUtil.isSubtitleRenderByDemo());
            wisePlayer.ready();
        }
    }

    public void start() {
        wisePlayer.start();
    }


    public int getCurrentTime() {
        if (wisePlayer != null) {
            return wisePlayer.getCurrentTime();
        } else {
            return 0;
        }
    }

    public int getDuration() {
        if (wisePlayer != null) {
            return wisePlayer.getDuration();
        } else {
            return 0;
        }
    }

    public void updateCurProgress(int progress) {
        if (wisePlayer != null) {
            wisePlayer.seek(progress);
        }
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        if (wisePlayer != null) {
            wisePlayer.setView(surfaceView);
        }
    }

    public void setTextureView(TextureView textureView) {
        if (wisePlayer != null) {
            wisePlayer.setView(textureView);
        }
    }

    public void suspend() {
        if (wisePlayer != null) {
            setBufferingStatus(false, false);
            wisePlayer.suspend();
        }
    }

    public void release() {
        if (wisePlayer != null) {
            wisePlayer.release();
            wisePlayer = null;
        }
        clearBitrateList();
    }

    private void clearBitrateList() {
        if (bitrateRangeList != null) {
            bitrateRangeList.clear();
            bitrateRangeList = null;
        }
        if (switchBitrateList != null) {
            switchBitrateList.clear();
            switchBitrateList = null;
        }
    }

    public void stop() {
        if (wisePlayer != null) {
            wisePlayer.stop();
        }
    }

    public void setPlayData(boolean isPlaying) {
        if (isPlaying) {
            wisePlayer.pause();
            setBufferingStatus(false, false);
        } else {
            wisePlayer.start();
            setBufferingStatus(true, false);
        }
    }

    public String getCurrentPlayName() {
        if (currentPlayData != null) {
            return StringUtil.getNotEmptyString(currentPlayData.getName());
        } else {
            return "";
        }
    }

    public void playResume(int play) {
        if (wisePlayer != null) {
            setBufferingStatus(true, false);
            wisePlayer.resume(play);
        }
    }

    public void setBufferingStatus(boolean status, boolean isUpdateLocal) {
        if (wisePlayer != null && (isUpdateLocal || PlayControlUtil.isLoadBuff())) {
            wisePlayer.setBufferingStatus(status);
            if (isUpdateLocal) {
                PlayControlUtil.setLoadBuff(status);
            }
        }
    }

    public void setPlaySpeed(String speedValue) {
        switch (speedValue) {
            case "1.25x":
                wisePlayer.setPlaySpeed(1.25f);
                break;
            case "1.5x":
                wisePlayer.setPlaySpeed(1.5f);
                break;
            case "1.75x":
                wisePlayer.setPlaySpeed(1.75f);
                break;
            case "2.0x":
                wisePlayer.setPlaySpeed(2.0f);
                break;
            case "0.5x":
                wisePlayer.setPlaySpeed(0.5f);
                break;
            case "0.75x":
                wisePlayer.setPlaySpeed(0.75f);
                break;
            default:
                wisePlayer.setPlaySpeed(1.0f);
                break;
        }
    }

    public int getBufferTime() {
        if (wisePlayer != null) {
            return wisePlayer.getBufferTime();
        } else {
            return 0;
        }
    }

    public long getBufferingSpeed() {
        if (wisePlayer != null) {
            return wisePlayer.getBufferingSpeed();
        } else {
            return 0;
        }
    }

    public List<String> getBitrateStringList() {

        if (switchBitrateList == null || switchBitrateList.size() == 0) {
            switchBitrateList = new ArrayList<>();
            bitrateRangeList = new ArrayList<>();
            if (wisePlayer != null) {
                VideoInfo videoInfo = wisePlayer.getVideoInfo();
                if (videoInfo != null && videoInfo.getStreamInfos() != null) {
                    Collections.sort(videoInfo.getStreamInfos(), new StreamInfoList());
                    for (StreamInfo streamInfo : videoInfo.getStreamInfos()) {
                        if (streamInfo != null) {
                            String bitrateValue = DataFormatUtil.getVideoQuality(context, streamInfo.getVideoHeight());
                            if (!TextUtils.isEmpty(bitrateValue) && !switchBitrateList.contains(bitrateValue)) {
                                switchBitrateList.add(bitrateValue);
                            }
                            addBitrateRangeList(streamInfo);
                        }
                    }
                }
            }
        }
        return switchBitrateList;
    }


    public void setBitrateRange(int minBitrate, int maxBitrate) {
        if (wisePlayer != null) {
            wisePlayer.setBitrateRange(minBitrate, maxBitrate);
        }
    }

    private void addBitrateRangeList(StreamInfo streamInfo) {
        BitrateInfo bitrateInfo = new BitrateInfo();
        bitrateInfo.setCurrentBitrate(streamInfo.getBitrate());
        bitrateInfo.setMaxBitrate(streamInfo.getBitrate());
        bitrateInfo.setVideoHeight(streamInfo.getVideoHeight());
        if (bitrateRangeList.size() == 0) {
            bitrateInfo.setMinBitrate(0);
            bitrateRangeList.add(bitrateInfo);
        } else {
            BitrateInfo lastBitrateInfo = bitrateRangeList.get(bitrateRangeList.size() - 1);
            bitrateInfo.setMinBitrate(lastBitrateInfo.getCurrentBitrate());
            if (isContainsVideoHeight(streamInfo.getVideoHeight())) {
                bitrateRangeList.set(bitrateRangeList.size() - 1, bitrateInfo);
            } else {
                bitrateRangeList.add(bitrateInfo);
            }
        }
    }


    private boolean isContainsVideoHeight(int videoHeight) {
        for (BitrateInfo bitrateInfo : bitrateRangeList) {
            if (bitrateInfo.getVideoHeight() == videoHeight) {
                return true;
            }
        }
        return false;
    }


    public List<BitrateInfo> getBitrateRangeList() {
        return bitrateRangeList;
    }


    public List<String> getSwitchBitrateList() {
        return switchBitrateList;
    }


    public int getCurrentBitrateIndex() {
        StreamInfo streamInfo = wisePlayer.getCurrentStreamInfo();
        if (bitrateRangeList != null) {
            for (int i = 0; i < bitrateRangeList.size(); i++) {
                BitrateInfo bitrateInfo = bitrateRangeList.get(i);
                if (streamInfo.getBitrate() >= (bitrateInfo.getMinBitrate() + Constants.BITRATE_WITHIN_RANGE)
                        && streamInfo.getBitrate() <= (bitrateInfo.getMaxBitrate() + Constants.BITRATE_WITHIN_RANGE)) {
                    return i;
                }
            }
        }
        return 0;
    }

    public void switchBitrateSmooth(int currentBitrate) {
        if (wisePlayer != null) {
            AppLog.logD(TAG, "switch bitrate smooth : currentBitrate " + currentBitrate);
            wisePlayer.switchBitrateSmooth(currentBitrate);
        }
    }

    public void switchBitrateDesignated(int currentBitrate) {
        if (wisePlayer != null) {
            AppLog.logD(TAG, "switch bitrate designated : currentBitrate " + currentBitrate);
            wisePlayer.switchBitrateDesignated(currentBitrate);
        }
    }


    public int getCurrentBitrate() {
        if (wisePlayer != null) {
            StreamInfo streamInfo = wisePlayer.getCurrentStreamInfo();
            if (streamInfo != null) {
                return streamInfo.getBitrate();
            } else {
                AppLog.logD(TAG, "get current bitrate info is empty!");
            }
        }
        return 0;
    }

    public int getCurrentVideoHeight() {
        if (wisePlayer != null) {
            StreamInfo streamInfo = wisePlayer.getCurrentStreamInfo();
            if (streamInfo != null) {
                return streamInfo.getVideoHeight();
            } else {
                AppLog.logD(TAG, "get current bitrate info is empty!");
            }
        }
        return 0;
    }


    public void setBandwidthSwitchMode(int mod, boolean updateLocate) {
        if (wisePlayer != null) {
            wisePlayer.setBandwidthSwitchMode(mod);
        }
        if (updateLocate) {
            PlayControlUtil.setBandwidthSwitchMode(mod);
        }
    }


    public float getPlaySpeed() {
        if (wisePlayer != null) {
            return wisePlayer.getPlaySpeed();
        }
        return 1f;
    }

    public void closeLogo() {
        if (wisePlayer != null) {
            wisePlayer.closeLogo();
        }
    }

    public void setPlayMode(int playMode, boolean updateLocate) {
        if (wisePlayer != null) {
            wisePlayer.setPlayMode(playMode);
        }
        if (updateLocate) {
            PlayControlUtil.setPlayMode(playMode);
        }
    }

    public int getPlayMode() {
        if (wisePlayer != null) {
            return wisePlayer.getPlayMode();
        } else {
            return 1;
        }
    }

    public void setCycleMode(boolean isCycleMode) {
        if (wisePlayer != null) {
            wisePlayer.setCycleMode(isCycleMode ? CycleMode.MODE_CYCLE : CycleMode.MODE_NORMAL);
        }
    }

    public boolean isCycleMode() {
        if (wisePlayer != null) {
            return wisePlayer.getCycleMode() == CycleMode.MODE_CYCLE;
        } else {
            return false;
        }
    }

    public void setMute(boolean status) {
        if (wisePlayer != null) {
            wisePlayer.setMute(status);
        }
        PlayControlUtil.setIsMute(status);
    }

    public void setVolume(float volume) {
        if (wisePlayer != null) {
            AppLog.logD(TAG, "current set volume is " + volume);
            wisePlayer.setVolume(volume);
        }
    }

    public void setVideoType(int videoType, boolean updateLocate) {
        if (wisePlayer != null) {
            wisePlayer.setVideoType(videoType);
        }
        if (updateLocate) {
            PlayControlUtil.setVideoType(videoType);
        }
    }

    public void setSurfaceChange() {
        if (wisePlayer != null) {
            wisePlayer.setSurfaceChange();
        }
    }

    public void setInitBitrateEnable() {
        if (PlayControlUtil.isInitBitrateEnable() && wisePlayer != null) {
            InitBitrateParam initBitrateParam = new InitBitrateParam();
            initBitrateParam.setBitrate(PlayControlUtil.getInitBitrate());
            initBitrateParam.setHeight(PlayControlUtil.getInitHeight());
            initBitrateParam.setWidth(PlayControlUtil.getInitWidth());
            initBitrateParam.setType(PlayControlUtil.getInitType());
            wisePlayer.setInitBitrate(initBitrateParam);
        }
    }

    public void setBitrateRange() {
        if (PlayControlUtil.isSetBitrateRangeEnable() && wisePlayer != null) {
            wisePlayer.setBitrateRange(PlayControlUtil.getMinBitrate(), PlayControlUtil.getMaxBitrate());
            PlayControlUtil.clearBitrateRange();
        }
    }


    public void setInitBufferTimeStrategy() {
        if (wisePlayer != null) {
            AppLog.logD("playControl", "begin set InitBufferTimeStrategy");
            wisePlayer.setInitBufferTimeStrategy(PlayControlUtil.getInitBufferTimeStrategy());
            PlayControlUtil.setInitBufferTimeStrategy(null);
        }
    }

    public void clearPlayProgress() {
        if (currentPlayData != null) {
            AppLog.logD("playControl", "clear current progress " + currentPlayData.getUrl());
            PlayControlUtil.clearPlayData(currentPlayData.getUrl());
        }
    }

    public void savePlayProgress() {
        if (currentPlayData != null && wisePlayer != null) {
            AppLog.logD("playControl", "save Play Progress");
            PlayControlUtil.savePlayData(currentPlayData.getUrl(), wisePlayer.getCurrentTime());
        }
    }

    public void setBookmark() {
        if (currentPlayData != null) {
            int bookmark = PlayControlUtil.getPlayData(currentPlayData.getUrl());
            AppLog.logD("playControl", "save Play Progress");
            if (wisePlayer != null && bookmark != 0) {
                wisePlayer.setBookmark(bookmark);
            }
        }
    }

    public List<PlayEntity> getPlayList() {
        if (playEntityList == null || playEntityList.size() == 0) {
            playEntityList = new ArrayList<>();
            playEntityList.addAll(DataFormatUtil.getPlayList(context));
        }
        return playEntityList;
    }

    public void refresh() {
        if (wisePlayer != null && currentPlayData != null) {
            wisePlayer.refreshPlayUrl(currentPlayData.getUrl());
        }
    }


    public PlayEntity getPlayFromPosition(int position) {
        if (playEntityList != null && playEntityList.size() > position) {
            return playEntityList.get(position);
        }
        return null;
    }

    public void reset() {
        if (wisePlayer != null) {
            wisePlayer.reset();
        }
        clearBitrateList();
    }

    public void setCloseLogo() {
        if (wisePlayer != null) {
            if (PlayControlUtil.isCloseLogo()) {
                if (!PlayControlUtil.isTakeEffectOfAll()) {
                    PlayControlUtil.setCloseLogo(false);
                }
                wisePlayer.closeLogo();
            }
        }
    }

    public void onPause() {
        if (currentPlayData != null && wisePlayer != null) {
            PlayControlUtil.savePlayData(currentPlayData.getUrl(), wisePlayer.getCurrentTime());
            suspend();
        }
    }

    public boolean isHttpVideo() {
        return isHttpVideo;
    }

    private void setHttpVideo(boolean httpVideo) {
        isHttpVideo = httpVideo;
    }

    public void switchSubtitle(int trackId) {
        AppLog.logD("playControl", "save Play Progress");
        wisePlayer.selectSubtitleTrack(trackId);
    }

    public void closeSubtitle() {
        AppLog.logD("playControl", "save Play Progress");
        wisePlayer.deselectSubtitleTrack();
    }

    public void setSubtitlePresetLanguage() {
        if (PlayControlUtil.isSubtitlePresetLanguageEnable() && wisePlayer != null) {
            wisePlayer.presetSubtitleLanguage(PlayControlUtil.getSubtitlePresetLanguage());
            PlayControlUtil.clearSubtitlePresetLanguage();
        }
    }

    static class StreamInfoList implements Comparator<StreamInfo>, Serializable {

        private static final long serialVersionUID = -7763024398518367069L;

        @Override
        public int compare(StreamInfo streamInfo, StreamInfo lastStreamInfo) {
            if (streamInfo.getBitrate() >= lastStreamInfo.getBitrate()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    public AudioTrackInfo[] getAudioTracks() {
        AudioTrackInfo[] audioTrack = null;
        if (wisePlayer != null) {
            audioTrack = wisePlayer.getAudioTracks();
            if (audioTrack == null || audioTrack.length == 0) {
                AppLog.logD("playControl", "save Play Progress");
                return null;
            }
            for (AudioTrackInfo audioTrackInfo : audioTrack) {
                AppLog.logD("playControl", "save Play Progress");
            }
        }
        return audioTrack;
    }

    public void getSelectedAudioTrack() {
        AudioTrackInfo audioTrack = null;
        if (wisePlayer != null) {
            AudioTrackInfo obj = wisePlayer.getSelectedAudioTrack();
            if (obj == null) {
                AppLog.logD("playControl", "save Play Progress");
                return;
            }
            audioTrack = obj;
            AppLog.logD("playControl", "save Play Progress");
        }
    }

    public void switchAudioTrack(String audioTrackname) {
        AppLog.logD("playControl", "save Play Progress");
        if (wisePlayer != null) {
            AudioTrackInfo[] audioTrack = null;
            audioTrack = wisePlayer.getAudioTracks();
            if (audioTrack == null) {
                return;
            }

            for (AudioTrackInfo audioTrackInfo : audioTrack) {
                if (audioTrackInfo.getDesc().equals(audioTrackname)) {
                    AppLog.logD("playControl", "save Play Progress");
                    wisePlayer.selectAudioTrack(audioTrackInfo.getId());
                    break;
                }
            }
        }
    }

    public int getAudioLangIndex() {
        if (wisePlayer != null) {
            return wisePlayer.getSelectedAudioTrack().getId();
        } else {
            return 0;
        }
    }

    public void setPreferAudioLang() {
        if (wisePlayer != null) {
            AppLog.logD("playControl", "save Play Progress");
            wisePlayer.presetAudioLanguage(PlayControlUtil.getPreferAudio());
        }
    }

    public void setProxyInfo() {
        if (wisePlayer != null) {
            AppLog.logD("playControl", "save Play Progress");
            wisePlayer.setProxy(PlayControlUtil.getProxyInfo());
        }
    }

    public void setDownloadLink() {
        if (wisePlayer != null) {
            AppLog.logD("playControl", "save Play Progress");
            wisePlayer.setProperties(PlayerConstants.Properties.SINGLE_LINK_DOWNLOAD, PlayControlUtil.isDownloadLinkSingle());
        }
    }

    public void setWakeMode(boolean isWakeOn) {
        if (isWakeOn) {
            wisePlayer.setWakeMode(context, PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE);
        } else {
            wisePlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        }
    }

    public void setSubtitleRenderByDemo(boolean isRenderByDemo) {
        PlayControlUtil.setSubtitleRenderByDemo(isRenderByDemo);
        if (isRenderByDemo) {
            wisePlayer.setSubtitleUpdateListener(onWisePlayerListener);
        } else {
            wisePlayer.setSubtitleUpdateListener(null);
        }
    }
}
