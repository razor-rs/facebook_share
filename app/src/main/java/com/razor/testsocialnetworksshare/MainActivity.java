package com.razor.testsocialnetworksshare;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.Card;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;

import java.io.File;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "N3rpYtTAr1xG46iYX8MJNJuVP";
    private static final String TWITTER_SECRET = "hBZgCNruL01N63vwW6Hfq1aCezPzBXB7cFB1Nzp2jzTGf78BYi";


    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private List<String> permissionNeeds;
    private MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button shareBtn = (Button) findViewById(R.id.share_face);
        Button shareTwitter = (Button) findViewById(R.id.share_twitter);
        final EditText content = (EditText) findViewById(R.id.share_content);


        // Facebook setup
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setApplicationId(getString(R.string.facebook_id));

        callbackManager = CallbackManager.Factory.create();
        permissionNeeds = Arrays.asList("publish_actions");
        activity = this;

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!content.getText().toString().isEmpty()) {
                    loginManager = LoginManager.getInstance();
                    loginManager.logInWithPublishPermissions(activity, permissionNeeds);
                    loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            sharePhotoToFacebook(content.getText().toString());
                            Toast.makeText(activity, "You successfully share on facebook", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            Log.d("test", "CANCEL");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Toast.makeText(activity, "An error has occured", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        // Twitter setup
        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new TweetComposer());

        shareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!content.getText().toString().isEmpty()) {
                    TweetComposer.Builder builder = new TweetComposer.Builder(activity)
                            .text(content.getText().toString());
                    builder.show();
                }
            }
        });


    }


    private void sharePhotoToFacebook(String text){
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption(text)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }
}
