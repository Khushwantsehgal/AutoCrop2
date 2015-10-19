package com.aviary.android.sample;

/**
 * Created by khsingh on 10/15/15.
 */
import android.support.multidex.MultiDexApplication;

import com.aviary.android.feather.sdk.IAviaryClientCredentials;

public class MyApplication extends MultiDexApplication implements IAviaryClientCredentials {

    private static final String CREATIVE_SDK_SAMPLE_CLIENT_ID ="4e385d5a72294502bac345cac1c72213";
    private static final String CREATIVE_SDK_SAMPLE_CLIENT_SECRET ="039c188f-9777-4356-98e4-8f8bf6d0fa5c";
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public String getBillingKey() {
        return "";
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_SAMPLE_CLIENT_ID;
    }


    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_SAMPLE_CLIENT_SECRET;
    }
}
