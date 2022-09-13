package com.mdp_grp33_androidmodule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class GridAdapter extends RecyclerView.Adapter<GridView> {

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
    public GridView onCreateViewHolder(ViewGroup parent, int viewType) {
        Obstacle obs = (Obstacle) mInflater.inflate(R.layout.grid_obstacle, parent, false);
        obs.getLayoutParams().width = this.itemWidth;
        obs.getLayoutParams().height = this.itemHeight;

        GridView viewHolder = new GridView(obs);
        return viewHolder;
    }

    // Binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(GridView holder, int position) {
        int x = position / GridManager.GetInstance().colCount;
        int y = position % GridManager.GetInstance().rowCount;
        holder.grid = new Vec2D(x, y);
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