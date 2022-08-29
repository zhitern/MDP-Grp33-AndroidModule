package com.mdp_grp33_androidmodule;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
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

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private List<Character> mData = null;
    private LayoutInflater mInflater;
    private int itemWidth;
    private int itemHeight;

    // Data is passed into the constructor
    public GridAdapter(Context context, List<Character> data, int colCount, int rowCount, int width, int height) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.itemWidth = width/colCount;
        this.itemHeight = height/rowCount;
        Log.i("TAG", "width is " + width);
        Log.i("TAG", "height is " + height);
    }

    // Inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.grid_item, parent, false);
        view.getLayoutParams().width = this.itemWidth;
        view.getLayoutParams().height = this.itemHeight;

        view.findViewById(R.id.grid_image).setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                // Handles each of the expected events.
                switch(event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:

                        // Determines if this View can accept the dragged data.
                        if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                            // As an example of what your application might do, applies a blue color tint
                            // to the View to indicate that it can accept data.
                            ((ImageView)v).setColorFilter(Color.BLUE);

                            // Invalidate the view to force a redraw in the new tint.
                            v.invalidate();

                            // Returns true to indicate that the View can accept the dragged data.
                            return true;

                        }

                        // Returns false to indicate that, during the current drag and drop operation,
                        // this View will not receive events again until ACTION_DRAG_ENDED is sent.
                        return false;
                    case DragEvent.ACTION_DRAG_ENTERED:

                        // Applies a green tint to the View.
                        ((ImageView)v).setColorFilter(Color.GREEN);

                        // Invalidates the view to force a redraw in the new tint.
                        v.invalidate();

                        // Returns true; the value is ignored.
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:

                        // Resets the color tint to blue.
                        ((ImageView)v).setColorFilter(Color.BLUE);

                        // Invalidates the view to force a redraw in the new tint.
                        v.invalidate();

                        // Returns true; the value is ignored.
                        return true;
                    case DragEvent.ACTION_DROP:

                        // Gets the item containing the dragged data.
                        ClipData.Item item = event.getClipData().getItemAt(0);

                        // Gets the text data from the item.
                        CharSequence dragData = item.getText();

                        // Displays a message containing the dragged data.
                        // Toast.makeText(this, "Dragged data is " + dragData, Toast.LENGTH_LONG).show();

                        // Turns off any color tints.
                        ((ImageView)v).clearColorFilter();

                        // Invalidates the view to force a redraw.
                        v.invalidate();

                        // Returns true. DragEvent.getResult() will return true.
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:

                        // Turns off any color tinting.
                        ((ImageView)v).clearColorFilter();

                        // Invalidates the view to force a redraw.
                        v.invalidate();

                        // Does a getResult(), and displays what happened.
//                        if (event.getResult()) {
//                            Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG).show();
//                        }

                        // Returns true; the value is ignored.
                        return true;

                    // An unknown action type was received.
                    default:
                        Log.e("DragDrop Example","Unknown action type received by View.OnDragListener.");
                        break;
                }
                return false;
            }
        });

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        char val = mData.get(position);
        holder.myTextView.setText(String.valueOf(val));
    }

    // Total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // Stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView myTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            myTextView = (TextView) itemView.findViewById(R.id.info_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClick(view, getAdapterPosition());
        }
    }

    // Convenience method for getting data at click position
    public char getItem(int id) {
        return mData.get(id);
    }

    // Method that executes your code for the action received
    public void onItemClick(View view, int position) {
        Log.i("TAG", "You clicked number " + getItem(position) + ", which is at cell position " + position);
    }
}