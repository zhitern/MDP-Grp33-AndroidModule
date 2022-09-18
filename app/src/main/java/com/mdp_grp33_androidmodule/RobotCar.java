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

public class RobotCar extends androidx.appcompat.widget.AppCompatImageView {
    private static RobotCar instance = null;

    private Vec2D pos;
    private int gridCount;
    private Vec2D origin; // default (0,0) is top left
    private int direction;

    public static RobotCar GetInstance() {
        return instance;
    }

    public Vec2D GetGrid(){
        return pos;
    }
    public int GetDirection() {
        return direction;
    }

    public void SetGrid(int x, int y) {
        this.pos.x = x;
        this.pos.y = GridManager.GetInstance().rowCount - gridCount - y;
        Vec2D point = GridManager.GetInstance().GridToWorld(this.pos);

        this.setX(point.x);
        this.setY(point.y);
    }
    public void SetDirection(int dir) {
        this.direction = dir;
        this.setRotation(this.direction * 90);
    }

    public void Init(int gridCount, Vec2D origin){
        this.instance = this;

        this.pos = new Vec2D();

        this.gridCount = gridCount;
        this.getLayoutParams().width = gridCount * GridManager.GetInstance().gridLength + (gridCount - 1) * GridManager.GetInstance().spacing;
        this.getLayoutParams().height = gridCount * GridManager.GetInstance().gridLength + (gridCount - 1) * GridManager.GetInstance().spacing;

        this.origin = origin;

        //this.setClickable(true);
        this.SetDirection(0);
        this.SetGrid(0,0);
    }

    public RobotCar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    float x, y;
    boolean moved;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            moved = false;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            moved = true;

            x = event.getRawX();
            y = event.getRawY();

            Vec2D grid = GridManager.GetInstance().WorldToGrid(new Vec2D((int)x, (int)y));

            if (grid != null) {
                grid.x -= origin.x;
                grid.y -= origin.y;

                if (CheckBoundary(grid)) {
                    this.pos = grid;
                    Vec2D point = GridManager.GetInstance().GridToWorld(grid);

                    this.setX(point.x);
                    this.setY(point.y);
                }
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            // On Click
            if (!moved) {
                this.SetDirection((direction + 1) % 4);
            }
            moved = false;
        }

        return super.onTouchEvent(event);
    }

    private boolean CheckBoundary(Vec2D point) {

        if (point.x < 0 || point.x + gridCount > GridManager.GetInstance().colCount)
            return false;
        if (point.y < 0 || point.y + gridCount > GridManager.GetInstance().rowCount)
            return false;

        return true;
    }
}
