package com.applaudostudios.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applaudostudios.app.R;
import com.applaudostudios.models.Stadium;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.List;

/**
 * Created by Tarles on 01/04/2017.
 */

public class StadiumAdapter extends ArrayAdapter<Stadium>{

    public List<Stadium> stadiums;
    public Context context;
    private LayoutInflater inflater;

    public StadiumAdapter(Context context, List<Stadium> stadiums)
    {
        super(context, 0, stadiums);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.stadiums = stadiums;
    }


    @Override
    public Stadium getItem(int position) {
        return stadiums.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewRow row;
        if(convertView == null)
        {
            View view = inflater.inflate(R.layout.list_row_view, parent, false);
            row = ViewRow.create((LinearLayout)view);
            view.setTag(row);;
        }
        else
        {
            row = (ViewRow) convertView.getTag();
        }

        Stadium stadium = new Stadium();
        stadium = getItem(position);

        row.teamName.setText(stadium.getTeamName());
        row.stadiumAddress.setText(stadium.getAddress());

        Glide.with(context).load(stadium.getImgLogo()).placeholder(R.mipmap.image_not_found).error(R.mipmap.image_not_found).into(row.iconLogo);

        row.mainCotainer.setId(stadium.getId());

        return row.mainCotainer;
        //return super.getView(position, convertView, parent);
    }

    public static class ViewRow
    {

        public final LinearLayout mainCotainer;
        public final ImageView iconLogo;
        public final TextView teamName;
        public final TextView stadiumAddress;

        public ViewRow(LinearLayout mainCotainer, ImageView iconLogo, TextView teamName, TextView stadiumAddress)
        {
            this.mainCotainer = mainCotainer;
            this.iconLogo = iconLogo;
            this.teamName = teamName;
            this.stadiumAddress = stadiumAddress;
        }

        public static ViewRow create(LinearLayout container)
        {
            ImageView image = (ImageView) container.findViewById(R.id.LnlRowContainer).findViewById(R.id.imgLogo);
            TextView teamName = (TextView) container.findViewById(R.id.LnlRowContainer).findViewById(R.id.LnlRowContainerInfomation).findViewById(R.id.lblTeamName);
            TextView stadiumAddress = (TextView) container.findViewById(R.id.LnlRowContainer).findViewById(R.id.LnlRowContainerInfomation).findViewById(R.id.lblStadiumAddress);
            ViewRow row = new ViewRow(container, image, teamName, stadiumAddress);
            return row;
        }
    }
}
