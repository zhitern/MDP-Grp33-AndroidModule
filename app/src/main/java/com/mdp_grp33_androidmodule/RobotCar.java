package com.mdp_grp33_androidmodule;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

public class RobotCar extends androidx.appcompat.widget.AppCompatImageView implements View.OnClickListener, View.OnLongClickListener {
    private Vec2D pos;
    private Vec2D origin; // defaul 0,0 is top left
    private Vec2D boundaryMin;
    private Vec2D boundaryMax;
    private int direction;

    public void Init(int gridCount){
        this.getLayoutParams().width = gridCount * GridManager.GetInstance().gridLength + (gridCount - 1) * GridManager.GetInstance().spacing;
        this.getLayoutParams().height = gridCount * GridManager.GetInstance().gridLength + (gridCount - 1) * GridManager.GetInstance().spacing;

        this.direction = 0;
        this.setRotation(this.direction * 90);

        this.setOnClickListener(this);
//        this.setOnDragListener(this);
//        this.setOnLongClickListener(this);
    }

    public RobotCar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    float x, y;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            x = event.getX();
//            y = event.getY();
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            x = event.getRawX() - 68;
            y = event.getRawY();

//            this.setX(x);
//            this.setY(y);

            float x2, y2;
            x2 = event.getRawX();
            y2 = event.getRawY();
            Vec2D point = GridManager.GetInstance().GetGridPos(new Vec2D((int)x2, (int)y2));

            if (point != null) {
                this.setX(point.x);
                this.setY(point.y);
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View view) {
        //Rotates direction
        this.direction = (direction + 1) % 4;
        this.setRotation(this.direction * 90);
    }
    @Override
    public boolean onLongClick(View view) {

        return true;
    }
}
