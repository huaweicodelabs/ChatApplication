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

package com.huawei.chitchatapp.utils;


public interface Constants {

    String PHONE_NUMBER = "phoneNumber";
    String COUNTRY_CODE = "countryCode";
    String USER_NAME = "userName";
    String DB_ZONE_NAME = "ChitChatApp";
    String IS_CHAT_USER_PROFILE = "isChatUserProfile";

    String PROFILE_OR_CHAT = "ProfileOrChat";
    String TYPE_OF_DIALOG_FRAGMENT = "Type";

    int CONTACT_PERMISSION = 2002;
    int LOCATION_PERMISSION = 3003;
    int STORAGE_PERMISSION = 3004;
    int CAMERA_PERMISSION = 1001;

    String PUSH_TOKEN = "push_token";
    String ALREADY_LOGIN = "already";

    String PROFILE_URL = "chitchatapp/profile_pic/";
    String chatScreen = "chatRoom";
    String NAV_LOCATION = "nav_location";
    String NAV_CAMERA = "nav_camera";
    String NAV_GALLERY = "nav_gallery";
    String NAV_DOCUMENT = "nav_document";
    String NAV_VIDEO = "nav_video";

    String MAP_VIEW_BUNDLE_KEY = "xxxxxx";
    String MAP_VIEW_API_KEY = "xxxxxx";
    String SITE_MAP = "siteMAP";
    String CURRENT_LOCATION = "current_location";

    String STATUS_ONLINE = "online";
    String STATUS_OFFLINE = "offline";

    String MESSAGE_TYPE_MAP = "map";
    String MESSAGE_TYPE_IMAGE = "image";
    String MESSAGE_TYPE_TEXT = "text";
    String MESSAGE_TYPE_VIDEO = "video";
    String MESSAGE_TYPE_DOC = "doc";

    String LOCATION_TYPE_HOSPITAL = "hospital";
    String LOCATION_TYPE_PETROL_BUNK = "petrol bunk";
    String LOCATION_TYPE_ATM = "atm";
    String ROOM_ID = "roomId";

    String TOKEN_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";
    String CLIENT_SECRET = "xxxxxx";
    String CLIENT_ID = "xxxxxx";
    String PIC_PATH = "chitchatapp/profile_pic/";
    String PICTURE_DATA = "A Image has been recieved";
    String LOCATION_DATA = "A location has been recieved";

    String DATA_TYPE = "dataType";

    String VIDEO_PLAY_DATA = "VIDEO_PLAY_DATA";
    String MAP_DOMAIN = "mapapi.cloud.huawei.com";
    String STATIC_MAP = "/mapApi/v1/mapService/getStaticMap";

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
}
