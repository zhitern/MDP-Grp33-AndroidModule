package com.mdp_grp33_androidmodule;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

// Stores and recycles views as they are scrolled off screen
public class GridView extends RecyclerView.ViewHolder implements View.OnDragListener, View.OnLongClickListener {

    public Vec2D grid;
    private boolean showingObstacle = false;
    Obstacle obstacle = null;
    ImageView background = null;

    public GridView(Obstacle obs) {
        super(obs);

        this.obstacle = obs;
        background = obstacle.imgDisplay;

        HideObstacle();

        this.itemView.setOnDragListener(this);
        this.itemView.setOnLongClickListener(this);
    }

    protected void HideObstacle() {
        this.showingObstacle = false;
        this.obstacle.dirDisplay.setVisibility(View.GONE);
        this.obstacle.valDisplay.setVisibility(View.GONE);
        this.obstacle.imgDisplay.setColorFilter(Color.LTGRAY);
        this.obstacle.setClickable(false);
        this.obstacle.setLongClickable(false);
    }
    protected void ShowObstacle() {
        this.showingObstacle = true;
        this.obstacle.dirDisplay.setVisibility(View.VISIBLE);
        this.obstacle.valDisplay.setVisibility(View.VISIBLE);
        this.obstacle.imgDisplay.setColorFilter(Color.BLACK);
        this.obstacle.setClickable(true);
        this.obstacle.setLongClickable(true);
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        // Handles each of the expected events.
        switch(dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (showingObstacle)
                    return false;
                // Determines if this View can accept the dragged data.
                if (dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    background.setColorFilter(Color.DKGRAY);

                    return true;// Returns true to indicate that the View can accept the dragged data.
                }

                // Returns false to indicate that, during the current drag and drop operation,
                // this View will not receive events again until ACTION_DRAG_ENDED is sent.
                return false;
            case DragEvent.ACTION_DRAG_ENTERED:
                if (showingObstacle)
                    return true;
                background.setColorFilter(Color.GREEN);
                // Returns true; the value is ignored.
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                if (showingObstacle)
                    return true;
                background.setColorFilter(Color.DKGRAY);
                // Returns true; the value is ignored.
                return true;
            case DragEvent.ACTION_DROP:
                if (showingObstacle)
                    return false;
                // Gets the item containing the dragged data.
                ClipData dragData = dragEvent.getClipData();
                if (this.obstacle.LoadClipData(dragData)) {
                    ShowObstacle();
                    Log.i("THIS POSSY ISSU", Integer.toString(this.grid.x) + ", " + Integer.toString(this.grid.y));
                }

                // Returns true. DragEvent.getResult() will return true.
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                if (!showingObstacle)
                    background.setColorFilter(Color.LTGRAY);

                // Returns true; the value is ignored.
                return true;

            // An unknown action type was received.
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        if (!showingObstacle)
            return false;

        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);

        view.startDrag(obstacle.GetClipData(false), myShadow, null, 0);

        HideObstacle();

        return true;
    }
}
