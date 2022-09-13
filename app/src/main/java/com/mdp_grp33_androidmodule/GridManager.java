package com.mdp_grp33_androidmodule;

import android.app.Activity;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GridManager {
    private static GridManager instance = null;

    GridAdapter adapter;
    public RecyclerView recyclerView;
    public int gridLength = -1;
    public int spacing = 0;
    public int colCount;
    public int rowCount;
    public int width;
    public int height;

    public static GridManager GetInstance() {
        if (instance == null)
            instance = new GridManager();

        return instance;
    }
    private GridManager(){}

    public void Init(int rowCount, int colCount, int spacing, Activity activity){
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
        this.colCount = colCount;
        this.rowCount = rowCount;

        float ratio = (float)colCount / rowCount;
        width = recyclerView.getLayoutParams().width;
        height = recyclerView.getLayoutParams().height;

        if (ratio > 1.0f) {
            recyclerView.getLayoutParams().height = (int)(((float)height / ratio) + 0.5f);
        }
        else if (ratio < 1.0f) {
            recyclerView.getLayoutParams().width = (int)(((float)width * ratio) + 0.5f);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(activity, colCount));
        adapter = new GridAdapter(activity, data, colCount, rowCount, recyclerView.getLayoutParams().width, recyclerView.getLayoutParams().height, spacing);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(colCount, spacing, true, 0));
    }

    public Vec2D GetGridPos(Vec2D point) {
        int[] location = new int[2];
        this.recyclerView.getLocationOnScreen(location);

        Vec2D result = new Vec2D();

        this.width = this.recyclerView.getLayoutParams().width;
        this.height = this.recyclerView.getLayoutParams().height;

        //convert to grid local space
        point.x -= location[0];
        point.y -= location[1];
        //if out of bounds
        if (point.x < 0 || point.x > this.width || point.y < 0 || point.y > this.height) {
            return null;
        }

        int colPos = point.x / (gridLength + spacing);
        int rowPos = point.y / (gridLength + spacing);
        colPos = Math.min(colPos, colCount);
        rowPos = Math.min(rowPos, rowCount);

        result.x = (spacing * (colPos + 1)) + (gridLength * colPos);
        result.y = (spacing * (rowPos + 1)) + (gridLength * rowPos);

        return result;
    }

    public Vec2D WorldToGrid(Vec2D point) {
        return null;
    }
    public Vec2D GridToWorld(Vec2D point) {
        return null;
    }
}
