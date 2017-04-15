package com.applaudostudios.app;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.applaudostudios.adapters.StadiumAdapter;
import com.applaudostudios.database.ApplaudoSQLiteController;
import com.applaudostudios.models.Stadium;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ApplaudoSQLiteController applaudoSQLiteController;
    private StadiumAdapter stadiumAdapter;
    private ListView listViewStadium;
    private ShareActionProvider share;
    private Intent intent;
    private Stadium stadium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listViewStadium = (ListView) findViewById(R.id.listStadium);

        applaudoSQLiteController = new ApplaudoSQLiteController(this);
        getDatabaseInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

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

        if(stadium != null)
        {
            String sharingDate = "";
            try
            {
                sharingDate += stadium.getWebsite() + " \n";
                for(int a = 0; a < stadium.getScheduleGames().size(); a++)
                {
                    sharingDate += stadium.getScheduleGames().get(a).getStadium() + " ";

                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                    String scheduleGameDate = stadium.getScheduleGames().get(0).getDate();
                    Date date = simpleDateFormat1.parse(scheduleGameDate);

                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
                    String scheduleGameDateFormat = simpleDateFormat2.format(date);

                    if(a == (stadium.getScheduleGames().size() - 1))
                    {
                        sharingDate += scheduleGameDateFormat;
                    }
                    else
                    {
                        sharingDate += scheduleGameDate + " - ";
                    }
                }
            }
            catch (Exception er)
            {

            }

            if(sharingDate != null)
            {
                if(sharingDate.length() > 0)
                {
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

    public void shareToFacebook()
    {
        if(stadium != null)
        {
            String sharingDate = "";
            if(stadium.getId() > 0)
            {
                String scheduleGames = "";
                for(int a = 0; a < stadium.getScheduleGames().size(); a++)
                {
                    scheduleGames += stadium.getScheduleGames().get(a).getStadium() + " " + stadium.getScheduleGames().get(a).getDate() + " \n";
                }

                try
                {
                    for(int a = 0; a < stadium.getScheduleGames().size(); a++)
                    {
                        sharingDate += stadium.getScheduleGames().get(a).getStadium() + " ";

                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                        String scheduleGameDate = stadium.getScheduleGames().get(0).getDate();
                        Date date = simpleDateFormat1.parse(scheduleGameDate);

                        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
                        String scheduleGameDateFormat = simpleDateFormat2.format(date);

                        if(a == (stadium.getScheduleGames().size() - 1))
                        {
                            sharingDate += scheduleGameDateFormat;
                        }
                        else
                        {
                            sharingDate += scheduleGameDate + " - ";
                        }
                    }
                }
                catch (Exception er)
                {

                }

                if(sharingDate != null)
                {
                    if(sharingDate.length() > 0)
                    {
                        ShareDialog shareDialog;
                        shareDialog = new ShareDialog(HomeActivity.this);
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(stadium.getTeamName())
                                .setContentDescription(scheduleGames)
                                .setContentUrl(Uri.parse(stadium.getWebsite())).build();
                        shareDialog.show(linkContent);
                    }
                }
            }
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
            alertDialog.setTitle("Message");
            alertDialog.setMessage("You need to select a Team if you want to share via Facebook");
            alertDialog.setNeutralButton("OK", null);
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    public boolean checkDatabaseInformation()
    {
        boolean check = false;
        try
        {
            int count = 0;
            if(applaudoSQLiteController != null)
            {
                count = applaudoSQLiteController.countTotalStadium();
                if(count > 0)
                {
                    check = true;
                }
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
        return check;
    }

    public void getDatabaseInformation()
    {
        try
        {
            List<Stadium> listStadium = null;
            boolean checkDatabaseInformation = checkDatabaseInformation();
            if(checkDatabaseInformation == true)
            {
                listStadium = applaudoSQLiteController.getAllStadium();
                stadiumAdapter = new StadiumAdapter(HomeActivity.this, listStadium);
                listViewStadium.setAdapter(stadiumAdapter);
                listViewStadium.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        LinearLayout linearLayout = (LinearLayout)view;
                        int idRemoteStadium = 0;
                        int idStadium = 0;
                        idRemoteStadium = linearLayout.getId();
                        idStadium = applaudoSQLiteController.getStadiumIdByRemoteId(idRemoteStadium);
                        stadium = applaudoSQLiteController.getStadiumById(idStadium);

                        Intent intent = new Intent(HomeActivity.this, HomeDetailActivity.class);
                        intent.putExtra("id_remote_stadium", idRemoteStadium);
                        startActivity(intent);
                    }
                });
                //applaudoSQLiteController.closeDatabase();
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }
}
