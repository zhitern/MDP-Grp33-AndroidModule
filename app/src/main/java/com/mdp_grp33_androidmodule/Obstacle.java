package com.mdp_grp33_androidmodule;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

public class Obstacle {

    Obstacle(ImageView img) {
        if (img == null)
            return;//delete?

        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item(" ");
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                //ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
                ClipData dragData = new ClipData("", mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(img);

                v.startDrag(dragData, myShadow, null, 0);

                return true;
            }
        });

        img.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                // Handles each of the expected events.
                switch(event.getAction()) {

                    case DragEvent.ACTION_DRAG_STARTED:
                        // this View will not receive events again until ACTION_DRAG_ENDED is sent.
                        return false;

                    case DragEvent.ACTION_DRAG_ENTERED:

                        // Applies a green tint to the View.
                        ((ImageView)v).setColorFilter(Color.GREEN);

                        // Invalidates the view to force a redraw in the new tint.
                        v.invalidate();

                        // Returns true; the value is ignored.
                        return true;

                    case DragEvent.ACTION_DRAG_LOCATION:

                        // Ignore the event.
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
    }
}