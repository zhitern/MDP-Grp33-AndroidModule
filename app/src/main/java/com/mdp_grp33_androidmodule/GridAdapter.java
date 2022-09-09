package com.mdp_grp33_androidmodule;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.text.Layout;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class GridAdapter extends RecyclerView.Adapter<GridItem> {

    private List<Character> mData = null;
    private LayoutInflater mInflater;
    private int itemWidth;
    private int itemHeight;

    // Data is passed into the constructor
    public GridAdapter(Context context, List<Character> data, int colCount, int rowCount, int width, int height, int spacing) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.itemWidth = (width - (spacing * (colCount + 1))) /colCount;
        this.itemHeight = (height - (spacing * (rowCount + 1))) / rowCount;

        GridManager.GetInstance().gridLength = itemWidth;
        GridManager.GetInstance().spacing = spacing;
    }

    // Inflates the cell layout from xml when needed
    @Override
    public GridItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.grid_item, parent, false);
        view.getLayoutParams().width = this.itemWidth;
        view.getLayoutParams().height = this.itemHeight;

        GridItem viewHolder = new GridItem(view);
        return viewHolder;
    }

    // Binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(GridItem holder, int position) {
//        char val = mData.get(position);
//        holder.textView.setText(String.valueOf(val));
    }

    // Total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // Convenience method for getting data at click position
    public char getItem(int id) {
        return mData.get(id);
    }
}