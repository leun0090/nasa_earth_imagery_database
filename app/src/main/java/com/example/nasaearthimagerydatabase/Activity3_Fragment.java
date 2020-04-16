package com.example.nasaearthimagerydatabase;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class Activity3_Fragment extends Fragment {
    private Bundle dataFromActivity;
    private long id;
    private AppCompatActivity parentActivity;
    DbNasaEarthImagery dbHelper;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromActivity = getArguments();
        dbHelper = new DbNasaEarthImagery(this.getContext());

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_activity3_, container, false);
        TextView title = (TextView) result.findViewById(R.id.edit_title);
        TextView descr = (TextView) result.findViewById(R.id.edit_description);
        ImageView view = result.findViewById(R.id.full_image);
        ArrayList<MapElement> le=dbHelper.getListElements();
        int pos=dataFromActivity.getInt(Activity3.ITEM_POSITION);
        title.setText(le.get(pos).getTitle());
        descr.setText(le.get(pos).getDescription());
        if (le.get(pos).getImage() != null)
            le.get(pos).bitMapToImageView(view, le.get(pos).getImage());
        return result;
    }
}
