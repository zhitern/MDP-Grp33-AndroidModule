package com.mdp_grp33_androidmodule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    GridManager gridManager;
    DraggableImage obstacle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetupBotControls();
        obstacle = new DraggableImage((ImageView)findViewById(R.id.view_obstacle));

        gridManager = new GridManager(10, 10, this);
    }

    protected void SetupBotControls(){
        Button upBtn = (Button)findViewById(R.id.btn_up);
        Button downBtn = (Button)findViewById(R.id.btn_down);
        Button leftBtn = (Button)findViewById(R.id.btn_left);
        Button rightBtn = (Button)findViewById(R.id.btn_right);
    }

//    protected void SetupMapObstacle(){
//        obstacleView = (ImageView) findViewById(R.id.view_obstacle);
//        if (obstacleView == null)
//            return;
//
//        obstacleView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
//    }

//    float x, y;
//    float dx, dy;
//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//
//        if (event.getAction() == MotionEvent.ACTION_DOWN){
//            x = event.getX();
//            y = event.getY();
//        }
//        else if (event.getAction() == MotionEvent.ACTION_MOVE){
//            dx = event.getX() - x;
//            dy = event.getY() - y;
//
//            obstacleView.setX(obstacleView.getX() + dx);
//            obstacleView.setY(obstacleView.getY() + dy);
//
//            x = event.getX();
//            y = event.getY();
//        }
//
//        return super.onTouchEvent(event);
//    }
}