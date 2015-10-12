package whitewalker.autocrop2;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;
import com.aviary.android.feather.sdk.IAviaryClientCredentials;

/**
 * Created by khsingh on 10/6/15.
 */
public class MainApplication extends Application implements IAdobeAuthClientCredentials, IAviaryClientCredentials {

    private static final String CREATIVE_SDK_SAMPLE_CLIENT_ID ="4e385d5a72294502bac345cac1c72213";
    private static final String CREATIVE_SDK_SAMPLE_CLIENT_SECRET ="039c188f-9777-4356-98e4-8f8bf6d0fa5c";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_SAMPLE_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_SAMPLE_CLIENT_SECRET;
    }

    @Override
    public String getBillingKey() {
        return null;
    }
}
