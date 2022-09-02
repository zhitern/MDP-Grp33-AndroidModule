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

    public GridManager(int rowCount, int colCount, int spacing, Activity activity){
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

        float ratio = (float)colCount / rowCount;
        int width = recyclerView.getLayoutParams().width;
        int height = recyclerView.getLayoutParams().height;
        if (ratio > 1.0f) {
            recyclerView.getLayoutParams().height = (int)(((float)height / ratio) + 0.5f);
        }
        else if (ratio < 1.0f) {
            recyclerView.getLayoutParams().width = (int)(((float)width * ratio) + 0.5f);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(activity, colCount));
        adapter = new GridAdapter(activity, data, colCount, rowCount, recyclerView.getLayoutParams().width, recyclerView.getLayoutParams().height, spacing);
        // adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(colCount, spacing, true, 0));
    }
}
