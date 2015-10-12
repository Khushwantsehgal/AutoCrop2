package whitewalker.autocrop2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adobe.creativesdk.foundation.auth.AdobeAuthException;
import com.adobe.creativesdk.foundation.auth.AdobeAuthSessionHelper;
import com.adobe.creativesdk.foundation.auth.AdobeAuthSessionLauncher;
import com.adobe.creativesdk.foundation.auth.AdobeUXAuthManager;
import com.adobe.creativesdk.foundation.internal.storage.AdobePhotoAssetsDataSource;
import com.adobe.creativesdk.foundation.storage.AdobePhotoAsset;
import com.adobe.creativesdk.foundation.storage.AdobePhotoException;
import com.adobe.creativesdk.foundation.storage.AdobeSelectionPhotoAsset;
import com.adobe.creativesdk.foundation.storage.AdobeUXAssetBrowser;
import com.adobe.creativesdk.foundation.storage.IAdobeGenericRequestCallback;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private AdobeAuthSessionHelper mAuthSessionHelper;
    private final AdobeUXAuthManager mUXAuthManager = AdobeUXAuthManager.getSharedAuthManager();
    private static int DEFAULT_SIGN_IN_REQUEST_CODE =  2002;
    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView mImageNameTextView, mImageSizeTextView, mCreationDateTextView, mLastEditedOn;

    private ImageView mActivePhotoImageView;

    private AdobeAuthSessionHelper.IAdobeAuthStatusCallback mStatusCallback = new AdobeAuthSessionHelper.IAdobeAuthStatusCallback() {
        @Override
        public void call(AdobeAuthSessionHelper.AdobeAuthStatus adobeAuthStatus, AdobeAuthException e) {
            if (AdobeAuthSessionHelper.AdobeAuthStatus.AdobeAuthLoggedIn == adobeAuthStatus) {
                // if the user is logged in
                showAuthenticatedUI();
            } else {
                // if the user is not logged in
                showUnauthenticatedUI();
            }

        }
    };
    public void showAuthenticatedUI() {
        setContentView(R.layout.activity_main);
        final Button mAssetBrowserButton = (Button)findViewById(R.id.assetBrowserButton);

        mImageNameTextView = (TextView)findViewById(R.id.imageNameTextView);
        mImageSizeTextView = (TextView)findViewById(R.id.imageSizeTextView);
        mCreationDateTextView = (TextView) findViewById(R.id.imageCreationDateTextView);
        mLastEditedOn = (TextView) findViewById(R.id.imageModificationDateTextView);
        mActivePhotoImageView = (ImageView)findViewById(R.id.activePhotoImageView);

        View.OnClickListener mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdobeUXAssetBrowser mBrowser = AdobeUXAssetBrowser.getSharedInstance();
                mBrowser.popupFileBrowser(MainActivity.this, DEFAULT_SIGN_IN_REQUEST_CODE);

            }
        };
        mAssetBrowserButton.setOnClickListener(mListener);
    }

    public void showUnauthenticatedUI() {
        mUXAuthManager.login(new AdobeAuthSessionLauncher.Builder().withActivity(this).withRequestCode(DEFAULT_SIGN_IN_REQUEST_CODE).build());

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthSessionHelper = new AdobeAuthSessionHelper(mStatusCallback);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuthSessionHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuthSessionHelper.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuthSessionHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuthSessionHelper.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuthSessionHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("Debug","onActivityResult started");
        Log.d("Debug","requestCode" + requestCode);
        Log.d("Debug","resultCode" + resultCode);
        Log.d("Debug","resultCode" + RESULT_OK );

        if (data != null) {
            Bundle extras = data.getExtras();

            super.onActivityResult(requestCode, resultCode, data);
            mAuthSessionHelper.onActivityResult(requestCode, resultCode, data);

            if (requestCode == DEFAULT_SIGN_IN_REQUEST_CODE && resultCode == RESULT_OK && extras.get("ADOBE_ASSETBROWSER_PHOTOASSET_SELECTION_LIST") != null) {

                final AdobePhotoAsset mPhotoAsset = getPhotoAsset(data);

                Log.i(TAG, "Result success! " + mPhotoAsset.getName());
                updateUI(mPhotoAsset);



            }
        }
    }

    public void updateUI(final AdobePhotoAsset mPhotoAsset) {

        final String mPhotoName = mPhotoAsset.getName();
        final Long mPhotoSize = mPhotoAsset.getSize();
        final Date mPhotoCreationDate = mPhotoAsset.getCreationDate();
        final Date mLastModifiedOn = mPhotoAsset.getModificationDate();


        Thread thread = new Thread(){
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            mImageNameTextView.setText(mPhotoName);
                            mImageSizeTextView.setText(mPhotoSize.toString());
                            mCreationDateTextView.setText(mPhotoCreationDate.toString());
                            mLastEditedOn.setText(mLastModifiedOn.toString());
                            Log.d("Debug", "inrun");

                        }catch (final Exception e){
                            Log.e(TAG, "Exception is runnable: " + e);
                        }
                    }
                });
            }
        };

        thread.start();

        final IAdobeGenericRequestCallback<byte[], AdobePhotoException> mMasterDataDownloadCallback = new IAdobeGenericRequestCallback<byte[], AdobePhotoException>() {
            @Override
            public void onCancellation() {

            }

            @Override
            public void onCompletion(byte[] bytes) {

                InputStream inputStream = new ByteArrayInputStream(bytes);
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                mActivePhotoImageView.setImageBitmap(image);
 
            }

            @Override
            public void onError(AdobePhotoException e) {

                Log.e(TAG, "Exception: " + e);

                Context context = getApplicationContext();
                CharSequence text = "Download error due to: " + e.getDescription();
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }

            @Override
            public void onProgress(double v) {

            }
        };

        AdobePhotoAssetsDataSource.getRenditionForAsset(mPhotoAsset, mMasterDataDownloadCallback);

    }


    public AdobePhotoAsset getPhotoAsset(Intent data) {

        AdobeUXAssetBrowser.ResultProvider mAssetBrowserResult = new AdobeUXAssetBrowser.ResultProvider(data);
        ArrayList mListOfSelectedAssetFiles = mAssetBrowserResult.getSelectionAssetArray();
        AdobeSelectionPhotoAsset mAsset = (AdobeSelectionPhotoAsset)mListOfSelectedAssetFiles.get(0);
        return mAsset.getSelectedItem();
    };




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mUXAuthManager.logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
