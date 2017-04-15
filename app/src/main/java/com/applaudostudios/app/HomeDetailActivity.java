package com.applaudostudios.app;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applaudostudios.database.ApplaudoSQLiteController;
import com.applaudostudios.models.Stadium;
import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeDetailActivity extends AppCompatActivity implements OnMapReadyCallback, UniversalVideoView.VideoViewCallback {
    private SupportMapFragment supportMapFragment;
    private GoogleMap gMaps;
    private Intent intent;
    private ApplaudoSQLiteController applaudoSQLiteController;
    private int idStadium;
    private int idRemoteStadium;
    private Stadium stadium;
    private View mVideoLayout;
    private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;
    private ImageView teamLogo;
    private TextView textTeam;
    private TextView textDescription;
    private boolean isFullscreen;
    private LinearLayout linearLayout;
    private LinearLayout linearLayoutTeamInformationContainer;
    private LinearLayout linearLayoutMapContainer;
    private int mSeekPosition;
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        isFullscreen = fullscreen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linearLayoutTeamInformationContainer = (LinearLayout)findViewById(R.id.lnl_team_information_container);
        linearLayoutMapContainer = (LinearLayout) findViewById(R.id.lnl_map_container);

        teamLogo = (ImageView) findViewById(R.id.imgTeamLogo);
        textTeam = (TextView) findViewById(R.id.lblDetailTeamName);
        textDescription = (TextView) findViewById(R.id.lblDetailDescription);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_container);
        supportMapFragment.getMapAsync(this);
        applaudoSQLiteController = new ApplaudoSQLiteController(HomeDetailActivity.this);
        intent = getIntent();
        idRemoteStadium = intent.getIntExtra("id_remote_stadium", 0);
        this.idStadium = getIdStadiumFromRemoteId(idRemoteStadium);
        this.stadium = getStadiumById(this.idStadium);
        initializateVideoContainer(this.stadium.getVideoUrl());
        initializateDetailTeam();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_share:
                shareOnOtherSocialMedia();
                return true;
            case R.id.menu_item_facebook:
                shareToFacebook();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareOnOtherSocialMedia() {

        if(isInternetconnected(HomeDetailActivity.this) == true)
        {
            if (stadium != null) {
                String sharingDate = "";
                try {
                    sharingDate += stadium.getWebsite() + " \n";
                    for (int a = 0; a < stadium.getScheduleGames().size(); a++) {
                        sharingDate += stadium.getScheduleGames().get(a).getStadium() + " ";

                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                        String scheduleGameDate = stadium.getScheduleGames().get(0).getDate();
                        Date date = simpleDateFormat1.parse(scheduleGameDate);

                        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
                        String scheduleGameDateFormat = simpleDateFormat2.format(date);

                        if (a == (stadium.getScheduleGames().size() - 1)) {
                            sharingDate += scheduleGameDateFormat;
                        } else {
                            sharingDate += scheduleGameDateFormat + " - ";
                        }
                    }
                } catch (Exception er) {

                }

                if (sharingDate != null) {
                    if (sharingDate.length() > 0) {
                        List<Intent> shareIntentsLists = new ArrayList<Intent>();
                        Intent chooserIntent = new Intent();
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, sharingDate);
                        List<ResolveInfo> resInfos = getPackageManager().queryIntentActivities(shareIntent, 0);
                        if (!resInfos.isEmpty()) {
                            for (ResolveInfo resInfo : resInfos) {
                                String packageName = resInfo.activityInfo.packageName;
                                if (!packageName.toLowerCase().contains("facebook")) {
                                    Intent intent2 = new Intent();
                                    intent2.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                                    intent2.setAction(Intent.ACTION_SEND);
                                    intent2.setType("text/plain");
                                    intent2.setPackage(packageName);
                                    intent2.putExtra(Intent.EXTRA_TEXT, sharingDate);
                                    shareIntentsLists.add(intent2);
                                }
                            }
                            if (!shareIntentsLists.isEmpty()) {
                                chooserIntent = Intent.createChooser(shareIntentsLists.remove(0), "Choose app to share");
                                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntentsLists.toArray(new Parcelable[]{}));
                                startActivity(chooserIntent);
                            } else {
                                Log.e("Error", "No Apps can perform your task");
                            }

                        }
                    }
                }
            }
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeDetailActivity.this);
            alertDialog.setTitle("Message");
            alertDialog.setMessage("In order to continue, your device must be connected to the internet");
            alertDialog.setNeutralButton("OK", null);
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    public void shareToFacebook()
    {
        if(isInternetconnected(HomeDetailActivity.this) == true)
        {
            if (stadium != null) {
                String sharingDate = "";
                if (stadium.getId() > 0) {
                    String scheduleGames = "";
                    for (int a = 0; a < stadium.getScheduleGames().size(); a++) {
                        scheduleGames += stadium.getScheduleGames().get(a).getStadium() + " " + stadium.getScheduleGames().get(a).getDate() + " \n";
                    }

                    try {
                        for (int a = 0; a < stadium.getScheduleGames().size(); a++) {
                            sharingDate += stadium.getScheduleGames().get(a).getStadium() + " ";

                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                            String scheduleGameDate = stadium.getScheduleGames().get(0).getDate();
                            Date date = simpleDateFormat1.parse(scheduleGameDate);

                            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
                            String scheduleGameDateFormat = simpleDateFormat2.format(date);

                            if (a == (stadium.getScheduleGames().size() - 1)) {
                                sharingDate += scheduleGameDateFormat;
                            } else {
                                sharingDate += scheduleGameDateFormat + " - ";
                            }
                        }
                    } catch (Exception er) {

                    }

                    if (sharingDate != null) {
                        if (sharingDate.length() > 0) {
                            ShareDialog shareDialog;
                            shareDialog = new ShareDialog(HomeDetailActivity.this);
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle(stadium.getTeamName())
                                    .setContentDescription(scheduleGames)
                                    .setContentUrl(Uri.parse(stadium.getWebsite())).build();
                            shareDialog.show(linkContent);
                        }
                    }
                }
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeDetailActivity.this);
                alertDialog.setTitle("Message");
                alertDialog.setMessage("You need to select a Team if you want to share via Facebook");
                alertDialog.setNeutralButton("OK", null);
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeDetailActivity.this);
            alertDialog.setTitle("Message");
            alertDialog.setMessage("In order to continue, your device must be connected to the internet");
            alertDialog.setNeutralButton("OK", null);
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    public static boolean isInternetconnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            Network[] activeNetworks = cm.getAllNetworks();
            for (Network n: activeNetworks) {
                NetworkInfo nInfo = cm.getNetworkInfo(n);
                if(nInfo.isConnected())
                    return true;
            }

        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }

        return false;

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(this.stadium != null)
        {
            if(this.stadium.getId() > 0)
            {
                gMaps = googleMap;
                gMaps.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                //googleMap.setMyLocationEnabled(true);
                LatLng location = new LatLng(this.stadium.getLatitude(), this.stadium.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.title("" + this.stadium.getAddress());
                gMaps.addMarker(markerOptions);

                CameraPosition.Builder cameraBuilder = CameraPosition.builder();
                cameraBuilder.target(location);
                cameraBuilder.zoom(16);

                CameraPosition cameraPosition = cameraBuilder.build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                gMaps.moveCamera(cameraUpdate);
            }
        }
    }

    public Stadium getStadiumById(int id)
    {
        Stadium stadium = null;
        try
        {
            if(id > 0)
            {
                stadium = applaudoSQLiteController.getStadiumById(id);
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
            stadium = null;
        }
        return stadium;
    }

    public int getIdStadiumFromRemoteId(int id)
    {
        int idStadiumLocal = 0;
        try
        {
            if(id > 0)
            {
                idStadiumLocal = applaudoSQLiteController.getStadiumIdByRemoteId(id);
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
            idStadiumLocal = 0;
        }
        return idStadiumLocal;
    }

    public void initializateDetailTeam()
    {
        try
        {
            String text = null;
            if(teamLogo != null)
            {
                Glide.with(HomeDetailActivity.this).load(stadium.getImgLogo()).placeholder(R.mipmap.image_not_found).error(R.mipmap.image_not_found).into(teamLogo);
            }

            if(textTeam != null)
            {
                text = "";
                text = stadium.getTeamName();
                textTeam.setText(text);
            }

            if(textDescription != null)
            {
                text = "";
                text = stadium.getDescription();
                textDescription.setText(text);
            }

        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }

    public void initializateVideoContainer(String videoUrl)
    {
        try
        {
            if(videoUrl != null)
            {
                if(videoUrl.length() > 0)
                {
                    mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
                    mVideoLayout = findViewById(R.id.video_layout);
                    mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
                    mVideoView.setMediaController(mMediaController);
                    mVideoView.setVideoPath("" + videoUrl);

                    mVideoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
                        @Override
                        public void onScaleChange(boolean isFullscreen) {
                            //this.isFullscreen = isFullscreen;
                            setFullscreen(isFullscreen);
                            if (isFullscreen()) {

                                ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
                                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                                mVideoLayout.setLayoutParams(layoutParams);
                                linearLayoutTeamInformationContainer.setVisibility(View.GONE);
                                linearLayoutMapContainer.setVisibility(View.GONE);
                                //GONE the unconcerned views to leave room for video and controller

                            } else {
                                ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
                                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                                mVideoLayout.setLayoutParams(layoutParams);
                                linearLayoutTeamInformationContainer.setVisibility(View.VISIBLE);
                                linearLayoutMapContainer.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onPause(MediaPlayer mediaPlayer) {
                            Log.d("Pause", "onPause UniversalVideoView callback");
                        }

                        @Override
                        public void onStart(MediaPlayer mediaPlayer) {

                            Log.d("Start", "onStart UniversalVideoView callback");
                        }

                        @Override
                        public void onBufferingStart(MediaPlayer mediaPlayer) {
                            Log.d("BufferingStart", "onBufferingStart UniversalVideoView callback");
                        }

                        @Override
                        public void onBufferingEnd(MediaPlayer mediaPlayer) {
                            Log.d("BufferingEnd", "onBufferingEnd UniversalVideoView callback");
                        }

                    });
                }
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {

    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        super.onPause();

        if (mVideoView != null && mVideoView.isPlaying()) {
            mSeekPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
        }
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        if (mSeekPosition > 0) {
            mVideoView.seekTo(mSeekPosition);
        }
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {

    }
}
