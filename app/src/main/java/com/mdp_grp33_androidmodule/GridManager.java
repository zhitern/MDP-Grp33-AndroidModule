package com.mdp_grp33_androidmodule;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GridManager {

    GridAdapter adapter;
    RecyclerView recyclerView;

    public GridManager(int rowCount, int colCount, Activity activity){
        if (activity == null) {
            return;
        }

        // data to populate the RecyclerView with
        List<Character> data = new ArrayList<Character>();
        for (int i = 0; i < colCount * rowCount; ++i)
        {
            data.add('0');
        }

        // set up the RecyclerView
        recyclerView = (RecyclerView) activity.findViewById(R.id.gridContainer);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, colCount));
        adapter = new GridAdapter(activity, data, colCount, rowCount, recyclerView.getLayoutParams().width, recyclerView.getLayoutParams().height);
        // adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }
}
