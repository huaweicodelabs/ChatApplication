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

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.chitchatapp.R;
import com.huawei.chitchatapp.adapter.NearByLocationAdapter;
import com.huawei.chitchatapp.utils.AppLog;
import com.huawei.chitchatapp.utils.Constants;
import com.huawei.chitchatapp.utils.Util;
import com.huawei.chitchatapp.viewmodel.LocationViewModel;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Site;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, NearByLocationAdapter.NearbyLocationListener {

    private static final String TAG = LocationActivity.class.getSimpleName();
    private LocationViewModel locationViewModel;
    private SearchService searchService;
    private HuaweiMap hMap;
    private MapView mMapView;
    private RecyclerView mRvNearByLocation;
    private TextView currentLocationAccuracy;
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        try {
            searchService = SearchServiceFactory.create(this, URLEncoder.encode(Constants.MAP_VIEW_API_KEY, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            AppLog.logE(TAG, "encode apikey error");
        }
        locationViewModel = new ViewModelProvider(LocationActivity.this).get(LocationViewModel.class);

        initView();
        stMapView(savedInstanceState);
    }

    private void initView() {
        currentLocationAccuracy = findViewById(R.id.currnet_location_accurecy);
        mRvNearByLocation = findViewById(R.id.rvNearByLocation);
        mMapView = findViewById(R.id.mapView);
        ImageView backButton = findViewById(R.id.imageLocation);
        Button btnPetrolBunk = findViewById(R.id.btnPetrolBunk);
        Button btnAtm = findViewById(R.id.btnAtm);
        Button btnHospital = findViewById(R.id.btnHospital);

        backButton.setOnClickListener(view -> super.onBackPressed());

        btnHospital.setOnClickListener(view -> {
            clearMap();
            locationViewModel.getNearbyData(location.getLatitude(), location.getLongitude(), searchService, Constants.LOCATION_TYPE_HOSPITAL);
        });

        btnAtm.setOnClickListener(view -> {
            clearMap();
            locationViewModel.getNearbyData(location.getLatitude(), location.getLongitude(), searchService, Constants.LOCATION_TYPE_ATM);
        });

        btnPetrolBunk.setOnClickListener(view -> {
            clearMap();
            locationViewModel.getNearbyData(location.getLatitude(), location.getLongitude(), searchService, Constants.LOCATION_TYPE_PETROL_BUNK);
        });
    }

    private void clearMap() {
        if (hMap != null) {
            hMap.clear();
        }
    }

    private void stMapView(Bundle savedInstanceState) {

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAP_VIEW_BUNDLE_KEY);
        }

        MapsInitializer.setApiKey(Constants.MAP_VIEW_API_KEY);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }


    private void recyclerView(ArrayList<Site> sites) {
        hMap.clear();
        mRvNearByLocation.setLayoutManager(new LinearLayoutManager(LocationActivity.this, LinearLayoutManager.VERTICAL, false));
        NearByLocationAdapter nearByLocationAdapter = new NearByLocationAdapter(LocationActivity.this, sites);
        nearByLocationAdapter.setNearbyLocationListener(this);
        mRvNearByLocation.setAdapter(nearByLocationAdapter);
        for (int i = 0; i < sites.size(); i++) {
            hMap.addMarker(new MarkerOptions().position(new LatLng(sites.get(i).getLocation().getLat(), sites.get(i).getLocation().getLng())));
        }
    }


    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;
        hMap.setMyLocationEnabled(true);
        hMap.getUiSettings().setMyLocationButtonEnabled(true);
        Util.showProgressBar(LocationActivity.this);
        locationViewModel.getCurrentLocation(LocationActivity.this);
        locationViewModel.locationMutableLiveData.observe(LocationActivity.this, location -> {
            if (location != null) {
                this.location = location;
                updateDetails(location);
            }
        });
        locationViewModel.arrayListMutableLiveData.observe(LocationActivity.this, this::recyclerView);
    }

    private void updateDetails(Location location) {
        float zoom = 14.0f;
        LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, zoom);
        hMap.animateCamera(cameraUpdate);
        currentLocationAccuracy.setText(String.format("Accurate to %s meters", location.getAccuracy()));

        locationViewModel.getNearbyData(location.getLatitude(), location.getLongitude(), searchService, Constants.LOCATION_TYPE_HOSPITAL);


        Util.stopProgressBar();

    }


    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void nearbyAddress(Site site) {
        Intent intent = new Intent();
        intent.putExtra(Constants.SITE_MAP, site);
        intent.putExtra(Constants.CURRENT_LOCATION, location);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}