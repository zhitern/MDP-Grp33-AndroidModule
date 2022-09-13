package com.mdp_grp33_androidmodule;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ObstacleAdder extends FrameLayout implements View.OnLongClickListener {

    Obstacle obstacle = null;

    public ObstacleAdder(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setLongClickable(true);

        this.setOnLongClickListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        obstacle = (Obstacle)this.getChildAt(0);
        obstacle.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View view) {
        if (obstacle == null)
            return false;

        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(obstacle);

        view.startDrag(obstacle.GetClipData(true), myShadow, null, 0);

        return true;
    }
}