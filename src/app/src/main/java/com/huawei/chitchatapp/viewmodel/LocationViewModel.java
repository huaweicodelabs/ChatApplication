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

package com.huawei.chitchatapp.viewmodel;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.HwLocationType;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.util.ArrayList;

public class LocationViewModel extends ViewModel {

    private static final String TAG = LocationViewModel.class.getSimpleName();
    public MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Site>> arrayListMutableLiveData = new MutableLiveData<>();


    public void getCurrentLocation(Context context) {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(context);
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            AppLog.logD(TAG,
                    "Lat long--->Fushed" + location.getLongitude()
                            + "," + location.getLatitude() + "," + location.getAccuracy());
            if (location != null) {
                locationMutableLiveData.postValue(location);
            }
        }).addOnFailureListener(e -> {
            AppLog.logE(TAG, "error" + e.getMessage());
        });

    }

    public void getNearbyData(double latitude, double longitude, SearchService searchService, String locationType) {
        NearbySearchRequest request = new NearbySearchRequest();
        Coordinate location = new Coordinate(latitude, longitude);
        request.setLocation(location);
        request.setQuery(locationType);
        request.setRadius(5);
        request.setHwPoiType(HwLocationType.ADDRESS);
        request.setLanguage("en");
        request.setPageIndex(1);
        request.setPageSize(10);
        request.setStrictBounds(false);
        SearchResultListener<NearbySearchResponse> resultListener = new SearchResultListener<NearbySearchResponse>() {
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                arrayListMutableLiveData.postValue(new ArrayList<>(results.getSites()));
            }

            @Override
            public void onSearchError(SearchStatus status) {
                AppLog.logE("TAG", "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
            }
        };
        searchService.nearbySearch(request, resultListener);

    }

}
