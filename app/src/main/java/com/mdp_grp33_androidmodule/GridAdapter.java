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

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridItem> {

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

    // Stores and recycles views as they are scrolled off screen
    public class GridItem extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView mytextView;
        public ImageView imageView;
        public View directionIndicator;
        private boolean containsObstacle = false;
        private char value = ' ';
        private int direction = -1;

        private void SetAsObstacle(){
            this.containsObstacle = true;
        }
        private void SetAsObstacle(char val, int dir){
            this.SetValue(val);
            this.SetDirection(dir);
            this.containsObstacle = true;
        }

        private void SetValue(char val){
            this.value = val;
            this.mytextView.setText(Character.toString(val));
        }

        private void SetDirection(int dir){
            this.direction = dir;
            if (dir < 0) {
                this.directionIndicator.setVisibility(View.INVISIBLE);
            }
            else {
                this.directionIndicator.setVisibility(View.VISIBLE);
                directionIndicator.setRotation(direction * 90);
            }
        }

        private void ClearObstacle() {
            this.SetValue(' ');
            this.SetDirection(-1);
            this.containsObstacle = false;
        }

        public GridItem(View itemView) {
            super(itemView);
            this.itemView.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);

            this.mytextView = (TextView) itemView.findViewById(R.id.info_text);
            this.imageView = (ImageView) itemView.findViewById(R.id.grid_image);
            this.directionIndicator = (View) itemView.findViewById(R.id.direction_indicator);

            this.mytextView.setText(" ");
            this.directionIndicator.setVisibility(View.INVISIBLE);

            this.itemView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    // Handles each of the expected events.
                    switch(event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:

                            // Determines if this View can accept the dragged data.
                            if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                                imageView.setColorFilter(Color.LTGRAY);
                                v.invalidate();// Invalidate the view to force a redraw in the new tint.

                                return true;// Returns true to indicate that the View can accept the dragged data.
                            }

                            // Returns false to indicate that, during the current drag and drop operation,
                            // this View will not receive events again until ACTION_DRAG_ENDED is sent.
                            return false;
                        case DragEvent.ACTION_DRAG_ENTERED:

                            imageView.setColorFilter(Color.GREEN);
                            v.invalidate();

                            // Returns true; the value is ignored.
                            return true;
                        case DragEvent.ACTION_DRAG_EXITED:

                            imageView.setColorFilter(Color.LTGRAY);
                            v.invalidate();

                            // Returns true; the value is ignored.
                            return true;
                        case DragEvent.ACTION_DROP:

                            // Gets the item containing the dragged data.
                            ClipData dragData = event.getClipData();
                            char obsVal = dragData.getItemAt(0).getText().charAt(0);
                            int obsDir = 0;// -1; <== should be by default

                            if (dragData.getItemCount() > 1) {
                                obsDir = Integer.parseInt(dragData.getItemAt(1).getText().toString());
                            }

                            // Gets the text data from the item.
                            SetAsObstacle(obsVal, obsDir);

                            imageView.clearColorFilter();
                            v.invalidate();

                            // Returns true. DragEvent.getResult() will return true.
                            return true;

                        case DragEvent.ACTION_DRAG_ENDED:

                            imageView.clearColorFilter();
                            v.invalidate();

                            // Returns true; the value is ignored.
                            return true;

                        // An unknown action type was received.
                        default:
                            break;
                    }
                    return false;
                }
            });
        }

        @Override
        public void onClick(View view) {
            //Rotates direction
            if (direction >= 0) {
                this.SetDirection((direction + 1) % 4);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (!containsObstacle)
                return false;

            ClipData.Item obsValue = new ClipData.Item(Character.toString(this.value));
            ClipData.Item obsDir = new ClipData.Item(Integer.toString(this.direction));
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

            ClipData dragData = new ClipData("", mimeTypes, obsValue);
            dragData.addItem(obsDir);

            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(this.itemView);

            view.startDrag(dragData, myShadow, null, 0);
            ClearObstacle();

            return true;
        }
    }
}