package com.applaudostudios.app;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class HomeListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean isLargeScreen;
    private ApplaudoSQLiteController applaudoSQLiteController;
    private Stadium stadium;
    private int idStadium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        applaudoSQLiteController = new ApplaudoSQLiteController(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null)
        {
            isLargeScreen = true;
        }
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

        if(isInternetconnected(HomeListActivity.this) == true)
        {
            if (stadium != null)
            {
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
            else
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeListActivity.this);
                alertDialog.setTitle("Message");
                alertDialog.setMessage("You need to select a Team if you want to share the information");
                alertDialog.setNeutralButton("OK", null);
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeListActivity.this);
            alertDialog.setTitle("Message");
            alertDialog.setMessage("In order to continue, your device must be connected to the internet");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("OK", null);
            alertDialog.show();
        }
    }

    public void shareToFacebook()
    {
        if(isInternetconnected(HomeListActivity.this) == true)
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
                            shareDialog = new ShareDialog(HomeListActivity.this);
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle(stadium.getTeamName())
                                    .setContentDescription(scheduleGames)
                                    .setContentUrl(Uri.parse(stadium.getWebsite())).build();
                            shareDialog.show(linkContent);
                        }
                    }
                }

            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeListActivity.this);
                alertDialog.setTitle("Message");
                alertDialog.setMessage("You need to select a Team if you want to share via Facebook");
                alertDialog.setNeutralButton("OK", null);
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeListActivity.this);
            alertDialog.setTitle("Message");
            alertDialog.setMessage("In order to continue, your device must be connected to the internet");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("OK", null);
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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        try
        {
            List<Stadium> listStadium = null;
            boolean checkDatabaseInformation = checkDatabaseInformation();
            if(checkDatabaseInformation == true && recyclerView != null)
            {
                listStadium = applaudoSQLiteController.getAllStadium();
                SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(HomeListActivity.this, listStadium);
                recyclerView.setAdapter(simpleItemRecyclerViewAdapter);
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
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

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewRow> {

        //private final List<DummyContent.DummyItem> mValues;
        private final List<Stadium> listStadium;
        private Context context;
        /*public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
            listStadium = null;
        }*/

        public SimpleItemRecyclerViewAdapter(Context context, List<Stadium> list)
        {
            listStadium = list;
            this.context = context;
        }

        @Override
        public ViewRow onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_row_view, parent, false);
            return new ViewRow(view);
        }

        @Override
        public void onBindViewHolder(final ViewRow viewRow, int position) {
            Glide.with(context).load(listStadium.get(position).getImgLogo()).placeholder(R.mipmap.image_not_found).error(R.mipmap.image_not_found).into(viewRow.iconLogo);

            viewRow.teamName.setText(listStadium.get(position).getTeamName());
            viewRow.stadiumAddress.setText(listStadium.get(position).getAddress());
            viewRow.mainCotainer.setId(listStadium.get(position).getId());
            viewRow.mainCotainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout linearLayout = (LinearLayout)v;
                    int idRemoteStadium = 0;
                    idRemoteStadium = linearLayout.getId();
                    idStadium = getIdStadiumFromRemoteId(idRemoteStadium);
                    stadium = getStadiumById(idStadium);
                    if(isLargeScreen)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putInt("id_remote_stadium",  idRemoteStadium);
                        bundle.putBoolean("show_toolbar", false);
                        HomeDetailFragment fragment = new HomeDetailFragment();
                        fragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    }
                    else
                    {
                        Intent intent = new Intent(context, HomeDetailActivity.class);
                        intent.putExtra("id_remote_stadium", idRemoteStadium);
                        intent.putExtra("show_toolbar", true);
                        context.startActivity(intent);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listStadium.size();
        }

        public class ViewRow extends RecyclerView.ViewHolder
        {

            public final LinearLayout mainCotainer;
            public final ImageView iconLogo;
            public final TextView teamName;
            public final TextView stadiumAddress;

            public ViewRow(View view)
            {
                super(view);
                mainCotainer = (LinearLayout)view;
                iconLogo = (ImageView) mainCotainer.findViewById(R.id.imgLogo);
                teamName = (TextView) mainCotainer.findViewById(R.id.lblTeamName);
                stadiumAddress = (TextView) mainCotainer.findViewById(R.id.lblStadiumAddress);

            }

            public ViewRow(View view, LinearLayout mainCotainer, ImageView iconLogo, TextView teamName, TextView stadiumAddress)
            {
                super(view);
                this.mainCotainer = mainCotainer;
                this.iconLogo = iconLogo;
                this.teamName = teamName;
                this.stadiumAddress = stadiumAddress;
            }


        }
    }
}
