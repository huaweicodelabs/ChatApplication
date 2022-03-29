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

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.agconnect.cloud.storage.core.UploadTask;
import com.huawei.chitchatapp.utils.OnApiError;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;

import java.io.File;

public class ChitChatStorageViewModel extends ViewModel {

    private static final String TAG = "ChitChatStorageViewModel";

    private MutableLiveData<Uri> uploadFileLiveData;

    private OnApiError onApiError;

    public LiveData<Uri> uploadFileLiveData() {
        if (uploadFileLiveData == null) {
            uploadFileLiveData = new MutableLiveData<>();
        }
        return uploadFileLiveData;
    }

    public void uploadFile(StorageReference reference, String fileName, File filePath, OnApiError onApiError) {
        this.onApiError = onApiError;
        UploadTask task = reference.putFile(filePath);
        task.addOnSuccessListener(uploadSuccessListener)
                .addOnFailureListener(uploadFailureListener);

    }

    OnSuccessListener<UploadTask.UploadResult> uploadSuccessListener = new OnSuccessListener<UploadTask.UploadResult>() {
        @Override
        public void onSuccess(UploadTask.UploadResult uploadResult) {
            uploadResult.getStorage().getDownloadUrl().addOnSuccessListener(downloadLink)
                    .addOnFailureListener(downloadUriFailureListener);
        }
    };

    OnSuccessListener<Uri> downloadLink = new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            uploadFileLiveData.postValue(uri);
        }
    };

    OnFailureListener uploadFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(Exception e) {
            if (onApiError != null) {
                onApiError.onError("Error in uploading file to server", e);
            }
        }
    };

    OnFailureListener downloadUriFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(Exception e) {
            onApiError.onError("Failed in getting uri", e);
        }
    };


}
