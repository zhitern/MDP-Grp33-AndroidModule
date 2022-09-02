package com.mdp_grp33_androidmodule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

        gridManager = new GridManager(10, 10, 5, this);
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