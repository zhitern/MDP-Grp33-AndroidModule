package com.mdp_grp33_androidmodule;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class Obstacle extends FrameLayout implements View.OnClickListener {
    private static int globalId = 0;
    private static String label = "obs";

    public View dirDisplay;
    public ImageView imgDisplay;
    public TextView valDisplay;

    public char value = ' ';
    public int localId = 0;
    public int direction = 0;

    public Obstacle(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOnClickListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        dirDisplay = (View) this.getChildAt(2);
        imgDisplay = (ImageView) this.getChildAt(0);
        valDisplay = (TextView) this.getChildAt(1);

        valDisplay.setTextSize(20);
        dirDisplay.setRotation(direction * 90);
    }

    private void SetDirection(int dir){
        this.direction = dir;
        dirDisplay.setRotation(direction * 90);
    }
    private void SetValue(char val){
        this.value = val;
        if (this.value == ' ') {// value not found yet, display ID
            this.valDisplay.setTypeface(Typeface.DEFAULT);
            this.valDisplay.setTextColor(Color.WHITE);
            this.valDisplay.setText(Integer.toString(this.localId));
        }
        else {
            this.valDisplay.setTypeface(Typeface.DEFAULT_BOLD);
            this.valDisplay.setTextColor(Color.GREEN);
            this.valDisplay.setText(Character.toString(val));
        }
    }

    public ClipData GetClipData(boolean isNewlyAdded) {
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

        ClipData.Item dir = new ClipData.Item(Integer.toString(this.direction));
        ClipData.Item id = new ClipData.Item(Integer.toString(this.localId));
        ClipData.Item val = new ClipData.Item(Character.toString(this.value));
        ClipData.Item newFlag = new ClipData.Item(Boolean.toString(isNewlyAdded));

        ClipData dragData = new ClipData(Obstacle.label, mimeTypes, dir);
        dragData.addItem(id);
        dragData.addItem(val);
        dragData.addItem(newFlag);

        return dragData;
    }
    public boolean LoadClipData(ClipData data) {
        if (data.getDescription().getLabel().toString().compareTo(Obstacle.label) != 0)// if not equals
            return false;

        this.direction = Integer.parseInt(data.getItemAt(0).getText().toString());
        this.localId = Integer.parseInt(data.getItemAt(1).getText().toString());
        this.value = data.getItemAt(2).getText().toString().charAt(0);
        boolean isNewlyAdded = Boolean.parseBoolean(data.getItemAt(3).getText().toString());

        if (isNewlyAdded) {
            this.localId = Obstacle.globalId;
            Obstacle.globalId += 1;
        }

        SetDirection(this.direction);
        SetValue(this.value);

        return true;
    }

    @Override
    public void onClick(View view) {
        //Rotates direction
        this.SetDirection((direction + 1) % 4);
    }
}