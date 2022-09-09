package com.mdp_grp33_androidmodule;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    GridManager gridManager;
    Obstacle obstacle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetupBotControls();
        obstacle = new Obstacle((ImageView)findViewById(R.id.view_obstacle));

        gridManager = GridManager.GetInstance();
        gridManager.Init(10, 10, 1, this);

        RobotCar robotCar = (RobotCar) findViewById(R.id.robot_car);
        robotCar.Init(1);
    }

    protected void SetupBotControls(){
        Button upBtn = (Button)findViewById(R.id.btn_up);
        Button downBtn = (Button)findViewById(R.id.btn_down);
        Button leftBtn = (Button)findViewById(R.id.btn_left);
        Button rightBtn = (Button)findViewById(R.id.btn_right);

        Activity mainContext = this;

        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainContext, "UpBtn Pressed", Toast.LENGTH_SHORT).show();
            }
        });
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainContext, "downBtn Pressed", Toast.LENGTH_SHORT).show();
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainContext, "leftBtn Pressed", Toast.LENGTH_SHORT).show();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainContext, "rightBtn Pressed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}