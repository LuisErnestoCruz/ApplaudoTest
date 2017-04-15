package com.applaudostudios.network;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import com.applaudostudios.app.HomeActivity;
import com.applaudostudios.app.HomeListActivity;
import com.applaudostudios.app.SplashActivity;
import com.applaudostudios.database.ApplaudoSQLiteController;
import com.applaudostudios.models.Stadium;

import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tarles on 08/04/2017.
 */

public class RetrofitCall {

    private Call<List<Stadium>> call;
    private ApplaudoApiService applaudoApiService;
    private Context context;
    private List<Stadium> listStadium;
    private ApplaudoSQLiteController applaudoSQLiteController;
    private Intent intent;
    private SplashActivity splashActivity;

    public RetrofitCall(Context context)
    {
        this.context = context;
        this.applaudoSQLiteController = new ApplaudoSQLiteController(context);
    }

    public void execute()
    {
        if(isInternetconnected(context))
        {
            this.applaudoApiService = RetrofitClient.getApiService();
            call = applaudoApiService.getJSON();
            call.enqueue(getenqueue());
        }
        else if(isInternetconnected(context) == false)
        {
            if(countTotalStadiumDatabaseInformation() == 0)
            {
                this.splashActivity = (SplashActivity)context;

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Message");
                alertDialog.setMessage("In order to continue, your device must be connected to the internet");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try
                        {
                            if(splashActivity != null)
                            {
                                splashActivity.finish();
                            }
                        }
                        catch (Exception er)
                        {
                            er.printStackTrace();
                        }
                    }
                });
                alertDialog.show();
                applaudoSQLiteController.closeDatabase();

            }
            else if(countTotalStadiumDatabaseInformation() > 0)
            {
                applaudoSQLiteController.closeDatabase();
                //intent = new Intent(context, HomeActivity.class);
                intent = new Intent(context, HomeListActivity.class);
                context.startActivity(intent);
                this.splashActivity = (SplashActivity)context;
                this.splashActivity.finish();
            }
        }
    }


    public Callback<List<Stadium>> getenqueue()
    {
        Callback<List<Stadium>> callback = new Callback<List<Stadium>>() {
            @Override
            public void onResponse(Call<List<Stadium>> call, Response<List<Stadium>> response) {
                if(response.isSuccessful())
                {
                    listStadium = response.body();
                    if(listStadium != null)
                    {
                        if(listStadium.size() > 0)
                        {
                            if(countTotalStadiumDatabaseInformation() == 0)
                            {
                                insertDatabaseInformation(listStadium);
                                //intent = new Intent(context, HomeActivity.class);
                                intent = new Intent(context, HomeListActivity.class);
                                context.startActivity(intent);
                                splashActivity = (SplashActivity)context;
                                splashActivity.finish();
                            }
                            else if(countTotalStadiumDatabaseInformation() > 0)
                            {
                                deleteAllDatabaseInformation();
                                insertDatabaseInformation(listStadium);
                                //intent = new Intent(context, HomeActivity.class);
                                intent = new Intent(context, HomeListActivity.class);
                                context.startActivity(intent);
                                splashActivity = (SplashActivity)context;
                                splashActivity.finish();
                            }
                            applaudoSQLiteController.closeDatabase();
                        }
                    }
                }
                else if(response.isSuccessful() == false)
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Message");
                    alertDialog.setMessage("Failed to query information");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("OK", null);
                }
            }

            @Override
            public void onFailure(Call<List<Stadium>> call, Throwable t) {
                if(countTotalStadiumDatabaseInformation() == 0)
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Message");
                    alertDialog.setMessage("Failed to query information");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("OK", null);
                    alertDialog.show();
                }
                else if(countTotalStadiumDatabaseInformation() > 0)
                {
                    intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    splashActivity = (SplashActivity)context;
                    splashActivity.finish();
                }
            }
        };

        return callback;
    }

    public void insertDatabaseInformation(List<Stadium> listStadium)
    {
        try
        {
            if(listStadium != null)
            {
                if(listStadium.size() > 0)
                {
                    for(int a = 0; a < listStadium.size(); a++)
                    {
                        applaudoSQLiteController.insertStadium(listStadium.get(a));
                    }

                    //
                }
            }
        }
        catch (Exception er)
        {
            Log.e("Error Insert DB", er.getMessage());
        }
    }

    public int countTotalStadiumDatabaseInformation()
    {
        int count = 0;
        try
        {
            count = applaudoSQLiteController.countTotalStadium();
        }
        catch (Exception er)
        {
            Log.e("Error Count Stadium", er.getMessage());
            count  = 0;
        }
        return count;
    }

    public void deleteAllDatabaseInformation()
    {
        try
        {
            applaudoSQLiteController.deleteAllScheduleGame();
            applaudoSQLiteController.deleteAllStadium();
        }
        catch (Exception er)
        {
            Log.e("Error Delete Database", er.getMessage());
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
}
