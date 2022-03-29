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

package com.huawei.chitchatapp.videoplayer.utils;

public class Constants {

    public static final String VIDEO_PLAY_DATA = "video_play_data";

    public static final long DELAY_MILLIS_500 = 500;

    public static final long DELAY_MILLIS_3000 = 3000;

    public static final long DELAY_MILLIS_1000 = 1000;

    public static final float HEIGHT_DP = 300;

    public static final int PLAYING_WHAT = 1;

    public static final int UPDATE_PLAY_STATE = 4;

    public static final int PLAY_ERROR_FINISH = 5;

    public static final int UPDATE_SWITCH_BITRATE_SUCCESS = 6;

    public static final int PLAYER_SWITCH_STOP_REQUEST_STREAM = 7;

    public static final int MSG_SETTING = 8;

    public static final int PLAYER_SWITCH_PLAY_SPEED = 9;

    public static final int PLAYER_SWITCH_BITRATE = 10;

    public static final int PLAYER_SWITCH_AUTO_DESIGNATED = 11;

    public static final int PLAYER_SWITCH_BANDWIDTH_MODE = 12;

    public static final int PLAYER_SWITCH_PLAY_MODE = 13;

    public static final int PLAYER_SWITCH_LOOP_PLAY_MODE = 14;

    public static final int PLAYER_SWITCH_VIDEO_MUTE_MODE = 15;

    public static final int PLAYER_SWITCH_SUBTITLE = 16;

    public static final int PLAYER_GET_AUDIO_TRACKS = 17;

    public static final int PLAYER_SWITCH_AUDIO_TRACK = 18;


    public static final int PLAYER_SET_WAKE_MODE = 19;

    public static final int PLAYER_SWITCH_VIDEO_MODE = 0;


    public static final int PLAYER_SWITCH_VIDEO_VIEW = 1;

    public static final int PLAYER_SWITCH_VIDEO_MUTE = 3;

    public static final int PLAYER_SWITCH_VIDEO_PLAY = 4;

    public static final int PLAYER_SWITCH_BANDWIDTH = 5;

    public static final int PLAYER_SWITCH_INIT_BANDWIDTH = 6;

    public static final int PLAYER_SWITCH_CLOSE_LOGO = 7;

    public static final int PLAYER_SWITCH_CLOSE_LOGO_EFFECT = 8;

    public static final int PLAYER_SET_PREFER_LANG = 10;

    public static final int DIALOG_INDEX_ONE = 0;

    public static final int DIALOG_INDEX_TWO = 1;

    public static final int VIDEO_TYPE_LIVE = 1;

    public static final int VIDEO_TYPE_ON_DEMAND = 0;

    public static final int DISPLAY_HEIGHT_SMOOTH = 270;

    public static final int DISPLAY_HEIGHT_SD = 480;

    public static final int DISPLAY_HEIGHT_HD = 720;

    public static final int DISPLAY_HEIGHT_BLUE_RAY = 1080;

    public static final int BITRATE_WITHIN_RANGE = 100;

    public static final int DOWNLOAD_LINK_NUM = 11;

    public static final int SET_WAKE_MODE = 12;

    public static final int SET_SUBTITLE_RENDER_MODE = 13;

    public static final int LOG_FILE_SIZE = 1024;

    public static final int LOG_FILE_NUM = 20;

    public static final int LEVEL_DEBUG = 0;

    public static final int LEVEL_INFO = 1;

    public static final int LEVEL_WARN = 2;

    public static final int LEVEL_ERROR = 3;

    public static final int LEVEL_CLOSE = 10;

    public static class UrlType {

        public static final int URL = 0;

        public static final int URL_MULTIPLE = 1;

        public static final int URL_JSON = 2;

        public static final int URL_JSON_FORMAT = 3;
    }
}
