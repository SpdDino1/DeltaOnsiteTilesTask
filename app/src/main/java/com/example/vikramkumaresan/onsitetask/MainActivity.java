package com.example.vikramkumaresan.onsitetask;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    static int n; //The grid size n x n
    int width;
    int height;

    boolean isInRedrawnState = false;

    Button undo;
    Button start;
    Button reviewPattern;
    boolean canCheckWin=false;
    boolean uiLock=false;

    ArrayList<Integer>rowId;    //STARTS FROM 1!! [1,2,3.....]
    ArrayList<Integer>buttonId;

    ArrayList<Integer>litButtons;

    ArrayList<Integer>movesPerformed;

    ArrayList<Integer>solutionPath;    //Carries the computer's process to get solution

    ArrayList<Integer>solutionTiles;//Carries the tiles lit in solution grid

    //Row 1 = [1000,1001,1002.......]
    //Row 2 =[2000,2001,2002.......]

    LinearLayout parentLayout;  //Add horizontal rows to this

    public static Intent go;

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        parentLayout = (LinearLayout)findViewById(R.id.fatherLayout);
        rowId=new ArrayList<>();
        buttonId=new ArrayList<>();
        litButtons=new ArrayList<>();
        movesPerformed=new ArrayList<>();
        solutionPath=new ArrayList<>();
        solutionTiles=new ArrayList<>();
        start = (Button)findViewById(R.id.start);
        ctx=this;

        undo=(Button)findViewById(R.id.undoButton);
        reviewPattern=(Button)findViewById(R.id.pattern);

        int statusBarHeight = (int) Math.ceil(25 * this.getResources().getDisplayMetrics().density);    //Status Bar Height
        //Log.d("TAG","Status Bar Height = "+statusBarHeight);

        //Getting Screen dimensions For button resizing
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        width = display.widthPixels;    //Device Width
        height = display.heightPixels-statusBarHeight;  //App Height
        Log.d("TAG","Width = "+width);
        //Log.d("TAG","Height = "+height);

        go=new Intent(this,GridSizeInput.class);
        startActivityForResult(go,102);

    }

    private void createNewRows(int n){

        int buttonCount=0;

        for(int i =1;i<=n;i++){
            Log.d("TAG","Pass");
            LinearLayout layout = new LinearLayout(this);
            layout.setId(i);
            //Width,Height
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

            layout.setOrientation(android.widget.LinearLayout.HORIZONTAL);

            //Button Population
            for(int j=1;j<=n;j++){
                Button temp = new Button(this);
                temp.setMinimumWidth(0);
                temp.setMinimumHeight(0);
                Log.d("TAG",""+(i*1000)+buttonCount);
                temp.setId((i*1000)+buttonCount);
                buttonCount++;
                buttonId.add(temp.getId());

                int buttonHeight = height/n;
                int buttonWidth = (width/n)-20;

                if(buttonHeight<=buttonWidth){
                    buttonWidth=buttonHeight;
                }
                else {
                    buttonHeight=buttonWidth;
                }

                temp.setHeight(buttonHeight);
                temp.setWidth(buttonWidth);
                temp.setText("");
                temp.setBackgroundColor(Color.parseColor("#ffffff"));
                temp.setOnClickListener(lightUpTiles(temp));

                LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(buttonWidth,buttonHeight);
                params.setMargins(10,10,10,10);
                temp.setLayoutParams(params);

                layout.addView(temp);
            }
            //....................

            parentLayout.addView(layout);
            rowId.add(layout.getId());
            buttonCount=0;
        }

    }

    private View.OnClickListener lightUpTiles(final Button button) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uiLock) {
                    int id = button.getId();
                    movesPerformed.add(id);
                    undo.setEnabled(true);
                    Log.d("TAG", "Moves Added = " + id);

                    int rightId = id + 1;
                    int leftId = id - 1;
                    int topId = id - 1000;
                    int bottomId = id + 1000;

                    if ((rightId % 1000) < n) {   //Not boundary
                        Button temp = (Button) findViewById(rightId);
                        if (litButtons.contains(rightId)) {   //Right already lit then dull
                            temp.setBackgroundColor(Color.parseColor("#ffffff"));
                            for (int i = 0; i < litButtons.size(); i++) {
                                if (litButtons.get(i) == rightId) {
                                    litButtons.remove(i);
                                    break;
                                }
                            }
                        } else {
                            temp.setBackgroundColor(Color.parseColor("#ccff00"));
                            litButtons.add(rightId);
                        }
                    }

                    if ((leftId % 1000) < n) {    //Not boundary
                        Button temp = (Button) findViewById(leftId);
                        if (litButtons.contains(leftId)) {   //Right already lit then dull
                            temp.setBackgroundColor(Color.parseColor("#ffffff"));
                            for (int i = 0; i < litButtons.size(); i++) {
                                if (litButtons.get(i) == leftId) {
                                    litButtons.remove(i);
                                    break;
                                }
                            }
                        } else {
                            temp.setBackgroundColor(Color.parseColor("#ccff00"));
                            litButtons.add(leftId);
                        }
                    }

                    if ((topId >= 1000)) {  //Not boundary
                        Button temp = (Button) findViewById(topId);
                        if (litButtons.contains(topId)) {   //Right already lit then dull
                            temp.setBackgroundColor(Color.parseColor("#ffffff"));
                            for (int i = 0; i < litButtons.size(); i++) {
                                if (litButtons.get(i) == topId) {
                                    litButtons.remove(i);
                                    break;
                                }
                            }
                        } else {
                            temp.setBackgroundColor(Color.parseColor("#ccff00"));
                            litButtons.add(topId);
                        }
                    }

                    if ((bottomId <= ((n * 1000)) + n - 1)) {//Not boundary
                        Button temp = (Button) findViewById(bottomId);
                        if (litButtons.contains(bottomId)) {   //Right already lit then dull
                            temp.setBackgroundColor(Color.parseColor("#ffffff"));
                            for (int i = 0; i < litButtons.size(); i++) {
                                if (litButtons.get(i) == bottomId) {
                                    litButtons.remove(i);
                                    break;
                                }
                            }
                        } else {
                            temp.setBackgroundColor(Color.parseColor("#ccff00"));
                            litButtons.add(bottomId);
                        }
                    }
                }
                if (canCheckWin) {    //Win condition check
                    Log.d("winnnercheck", "" + checkWin());
                    if (checkWin()) {
                        Intent intent = new Intent(ctx, WonTheGame.class);
                        startActivity(intent);
                    }
                }
            }


        };
    }

    public void undoMove(View view){
        Button temp = (Button)findViewById(movesPerformed.get(movesPerformed.size()-1));
        temp.callOnClick();

        movesPerformed.remove(movesPerformed.size()-1);
        int id = movesPerformed.get(movesPerformed.size()-1);
        movesPerformed.remove(movesPerformed.size()-1);
        Log.d("TAG","Moves Deleted = "+id);

        if (movesPerformed.size() == 0) {
            undo.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==102 &&resultCode==102){
            n=go.getIntExtra("grid",4);

            Random rand = new Random();

            int moves = rand.nextInt(10)+1;
            int row;
            int column;

            for(int i=0;i<=moves;i++){
                row = rand.nextInt(n)+1;
                column=rand.nextInt(n);
                solutionPath.add((row*1000)+column);
                Log.d("micro",""+(row*1000)+column);
            }
            showChallenge();
        }
    }

    private void showChallenge(){
        createNewRows(n);

        for(int i=0;i<solutionPath.size();i++){
            Button temp = (Button)findViewById(solutionPath.get(i));
            temp.callOnClick();
            undo.setEnabled(false);
        }
        start.setVisibility(View.VISIBLE);
        uiLock=true;
    }
    public void startGame(View view){
        uiLock=false;
        reviewPattern.setEnabled(true);
        for(int i:buttonId){    //Populates the solutionTiles
            Button temp = (Button)findViewById(i);
            ColorDrawable draw = (ColorDrawable)temp.getBackground();
            //Log.d("micro","Color = "+Integer.toHexString(draw.getColor()).substring(2));
            if(Integer.toHexString(draw.getColor()).substring(2).equals("ccff00")){
                solutionTiles.add(i);
            }
        }

        while(movesPerformed.size()!=0){
            Button temp = (Button)findViewById(movesPerformed.get(movesPerformed.size()-1));
            temp.callOnClick();
            movesPerformed.remove(movesPerformed.size()-1);
            movesPerformed.remove(movesPerformed.size()-1);
        }
        start.setVisibility(View.INVISIBLE);
        undo.setEnabled(false);
        litButtons.clear();
        canCheckWin=true;
    }

    private boolean checkWin(){
        for(int i:solutionTiles){
            if(litButtons.contains(i)){
                continue;
            }
            else {
                return false;
            }
        }
        if(solutionTiles.size()==litButtons.size())
            return true;
        else
            return false;
    }

    public void redraw(View view){
        if(!isInRedrawnState){  //Transition from game state to redraw state
            uiLock=true;
            canCheckWin=false;
            undo.setEnabled(false);
            for(int i:buttonId){
                if(solutionTiles.contains(i)){
                    Button temp = (Button)findViewById(i);
                    temp.setBackgroundColor(Color.parseColor("#ccff00"));
                }
                else {
                    Button temp = (Button)findViewById(i);
                    temp.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            }
            reviewPattern.setText("Continue Game");
            isInRedrawnState=true;
        }
        else{   //Transition from redrawn state to game state
            uiLock=false;
            canCheckWin=true;
            undo.setEnabled(true);
            for(int i:buttonId){
                if(litButtons.contains(i)){
                    Button temp = (Button)findViewById(i);
                    temp.setBackgroundColor(Color.parseColor("#ccff00"));
                }
                else {
                    Button temp = (Button)findViewById(i);
                    temp.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            }
            reviewPattern.setText("Review Pattern");
            isInRedrawnState=false;
        }
    }

}
