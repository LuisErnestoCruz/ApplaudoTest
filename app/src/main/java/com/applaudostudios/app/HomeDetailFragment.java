package com.applaudostudios.app;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.applaudostudios.database.ApplaudoSQLiteController;
import com.applaudostudios.models.Stadium;
import com.bumptech.glide.Glide;
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

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.applaudostudios.app.HomeListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class HomeDetailFragment extends Fragment implements OnMapReadyCallback, UniversalVideoView.VideoViewCallback{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    /**
     * The dummy content this fragment is presenting.
     */
    private SupportMapFragment supportMapFragment;
    private Stadium stadium;
    private GoogleMap gMaps;
    private int idRemoteStadium;
    private int idStadium;
    private ApplaudoSQLiteController applaudoSQLiteController;
    private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;
    private View mVideoLayout;
    private boolean isFullscreen;
    private ImageView teamLogo;
    private TextView textTeam;
    private TextView textDescription;
    private Bundle bundle;
    private boolean showToolbar;
    private Toolbar toolbar;

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        isFullscreen = fullscreen;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle != null)
        {
            idRemoteStadium = bundle.getInt("id_remote_stadium");
            this.applaudoSQLiteController = new ApplaudoSQLiteController(getContext());
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
                Glide.with(HomeDetailFragment.this).load(stadium.getImgLogo()).placeholder(R.mipmap.image_not_found).error(R.mipmap.image_not_found).into(teamLogo);
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

    public void initializateVideoContainer(View view, String videoUrl)
    {
        try
        {
            if(videoUrl != null)
            {
                if(videoUrl.length() > 0)
                {
                    mVideoView = (UniversalVideoView) view.findViewById(R.id.videoView);
                    mVideoLayout = view.findViewById(R.id.video_layout);
                    mMediaController = (UniversalMediaController) view.findViewById(R.id.media_controller);

                    mVideoView.setMediaController(mMediaController);
                    mVideoView.setVideoPath("" + videoUrl);


                    mVideoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
                        @Override
                        public void onScaleChange(boolean isFullscreen) {
                            Log.d("onScaleChange", "onScaleChange UniversalVideoView callback");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home_detail, container, false);

        if (idRemoteStadium > 0) {
            supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
            supportMapFragment.getMapAsync(HomeDetailFragment.this);

            teamLogo = (ImageView) rootView.findViewById(R.id.imgTeamLogo);
            textTeam = (TextView) rootView.findViewById(R.id.lblDetailTeamName);
            textDescription = (TextView) rootView.findViewById(R.id.lblDetailDescription);


            this.idStadium = getIdStadiumFromRemoteId(idRemoteStadium);
            this.stadium = getStadiumById(this.idStadium);
            initializateVideoContainer(rootView, this.stadium.getVideoUrl());
            initializateDetailTeam();
        }

        if(showToolbar == false)
        {
            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
        }
        return rootView;
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

    @Override
    public void onScaleChange(boolean isFullscreen) {

    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {

    }
}
