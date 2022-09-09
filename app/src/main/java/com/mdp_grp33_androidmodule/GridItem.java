package com.mdp_grp33_androidmodule;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

// Stores and recycles views as they are scrolled off screen
public class GridItem extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private static int globalId = 0;

    public TextView mytextView;
    public ImageView imageView;
    public View directionIndicator;
    private boolean containsObstacle = false;
    private char value = ' ';
    private int localId = 0;
    private int direction = -1;

    public GridItem(View itemView) {
        super(itemView);
        this.itemView.setOnLongClickListener(this);
        this.itemView.setOnClickListener(this);

        this.mytextView = (TextView) itemView.findViewById(R.id.info_text);
        this.imageView = (ImageView) itemView.findViewById(R.id.grid_image);
        this.directionIndicator = (View) itemView.findViewById(R.id.direction_indicator);

        this.mytextView.setText(" ");
        this.mytextView.setTextColor(Color.WHITE);
        this.directionIndicator.setVisibility(View.INVISIBLE);

        this.itemView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                // Handles each of the expected events.
                switch(event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Determines if this View can accept the dragged data.
                        if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            if (containsObstacle)
                                imageView.setColorFilter(Color.DKGRAY);
                            else
                                imageView.setColorFilter(Color.LTGRAY);

                            return true;// Returns true to indicate that the View can accept the dragged data.
                        }

                        // Returns false to indicate that, during the current drag and drop operation,
                        // this View will not receive events again until ACTION_DRAG_ENDED is sent.
                        return false;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        imageView.setColorFilter(Color.GREEN);
                        // Returns true; the value is ignored.
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        imageView.setColorFilter(Color.LTGRAY);
                        // Returns true; the value is ignored.
                        return true;
                    case DragEvent.ACTION_DROP:
                        // Gets the item containing the dragged data.
                        ClipData dragData = event.getClipData();
                        char obsVal = dragData.getItemAt(0).getText().charAt(0);
                        int obsDir = 0;// -1; <== should be by default
                        int obsId = -1;

                        if (dragData.getItemCount() > 1) {
                            obsDir = Integer.parseInt(dragData.getItemAt(1).getText().toString());
                        }
                        if (dragData.getItemCount() > 2) {
                            obsId = Integer.parseInt(dragData.getItemAt(2).getText().toString());
                        }

                        // Gets the text data from the item.
                        SetAsObstacle(obsId, obsVal, obsDir);

                        // Returns true. DragEvent.getResult() will return true.
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        if (containsObstacle)
                            imageView.setColorFilter(Color.BLACK);
                        else
                            imageView.clearColorFilter();

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

    private void SetAsObstacle(int id, char val, int dir){
        if (id < 0) {
            GridItem.globalId += 1;
            this.localId = globalId;
        }
        else {
            this.localId = id;
        }
        this.SetValue(val);
        this.SetDirection(dir);

        this.containsObstacle = true;

        this.mytextView.setVisibility(View.VISIBLE);
    }

    private void SetValue(char val){
        this.value = val;
        if (this.value == ' ') {
            this.mytextView.setTextSize(20);
            this.mytextView.setTypeface(Typeface.DEFAULT);
            this.mytextView.setText(Integer.toString(this.localId));
        }
        else {
            this.mytextView.setTypeface(Typeface.DEFAULT_BOLD);
            this.mytextView.setTextSize(40);
            this.mytextView.setText(Character.toString(val));
        }
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
        this.mytextView.setVisibility(View.INVISIBLE);
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
        ClipData.Item obsID = new ClipData.Item(Integer.toString(this.localId));
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

        ClipData dragData = new ClipData("", mimeTypes, obsValue);
        dragData.addItem(obsDir);
        dragData.addItem(obsID);

        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(this.itemView);

        view.startDrag(dragData, myShadow, null, 0);
        ClearObstacle();

        return true;
    }
}
