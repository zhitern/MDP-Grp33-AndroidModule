package com.mdp_grp33_androidmodule;

import android.content.Context;
import android.util.AttributeSet;

public class RobotCar extends androidx.appcompat.widget.AppCompatImageView {
    private Vec2D pos;
    private int orientation;

    public void Init(int gridCount){
        this.getLayoutParams().width = gridCount * GridManager.GetInstance().gridLength + (gridCount - 1) * GridManager.GetInstance().spacing;
        this.getLayoutParams().height = gridCount * GridManager.GetInstance().gridLength + (gridCount - 1) * GridManager.GetInstance().spacing;
    }

    public RobotCar(Context context) {
        super(context);
    }

    public RobotCar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RobotCar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
