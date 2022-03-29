# Chat Application

# Huawei Mobile Services
Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.

## Table of Contents
* [Introduction](#introduction)
* [What you will Create](#what-you-will-create)
* [What You Will Learn](#what-you-will-learn)
* [Hardware Requirements](#hardware-requirements)
* [Software Requirements](#software-requirements)
* [License](#license)

## Introduction :
Chat app will give you an insight about the Serverless components from Huawei Ecosystem such as Auth Service, Cloud Storage, Cloud DB, and Cloud Functions. Apart from that you will also get an idea about Location Kit, Map Kit, and Site Kit that can be used to the share the location, which is an important feature, on the app. The registration process involves the sign-in with the phone number and the OTP, and the save user profile on the database.
## What You Will Create

In this code lab, you will create a Chat Application project and use the APIs of HUAWEI Cloud DB, Cloud Storage, Site , Map, Location. We are going to create a end to end chat application for two people to communicate with each other, upload image and share location.

*  Store the Media files in Cloud DB & Cloud Storage using Cloud-Core kits.
*  Using Map ,Location kit & Site kit to share the map as media file.
*  Using Push Service to notify users.

## What You Will Learn

In this code lab, you will learn how to:
*  Map Kit
*  Location Kit
*  Site Kit
*  Cloud DB
*  Cloud Storage
*  Auth Service
*  Push Kit

## What You Will Need

### Hardware Requirements

*  A computer (desktop or laptop) that runs the Windows 10 operating system
*  Huawei phone with HMS Core (APK) 5.0.0.300 or later installed
```
**Note:** Please prepare the preceding hardware environment and relevant devices in advance.
```
### Software Requirements

*  [Android Studio 3.X](https://developer.android.com/studio)
*  JDK 1.8 and later
*  SDK Platform 23 and later
*  Gradle 4.6 and later


## Prepare Initial configuration

Use the below link to do initial configuration for the application development
https://developer.huawei.com/consumer/en/codelab/HMSPreparation/index.html#3

## Enable HUAWEI Service(s) in AGC console

Enable the API permission for below kits from Project Settings, Manage APIs and enable the API permission.
*  Map Kit
*  Location Kit
*  Site Kit
*  Cloud DB
*  Cloud Storage
*  Auth Service
*  Push Kit

```
**Note:** Some API’s will be enabled by default. If not enable it manually.
```

## Integrating HMS SDK
For official Documentation and more services, please refer below documentation from developer website

*  [Map Kit](https://developer.huawei.com/consumer/en/hms/huawei-MapKit/)
*  [Location Kit](https://developer.huawei.com/consumer/en/hms/huawei-locationkit/)
*  [Site Kit](https://developer.huawei.com/consumer/en/hms/huawei-sitekit/)
*  [Cloud DB](https://developer.huawei.com/consumer/en/agconnect/cloud-base/)
*  [Auth Service](https://developer.huawei.com/consumer/en/agconnect/auth-service/)
* [Cloud Storage](https://developer.huawei.com/consumer/en/agconnect/cloud-storage/)
*  [Push Kit](https://developer.huawei.com/consumer/en/hms/huawei-pushkit/)


## License
HMS Guide sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).