package com.example.vikramkumaresan.onsitetask;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GridSizeInput extends AppCompatActivity {
    EditText edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_size_input);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setFinishOnTouchOutside(false);

        edit = (EditText)findViewById(R.id.editText);
        Button button = (Button)findViewById(R.id.button);

    }

    public void submit(View view){
        String row =edit.getText().toString();
        if(row.contains("-")){
            Toast.makeText(this,"Pls Enter a Poitive Integer",Toast.LENGTH_LONG).show();
        }
        else {
            try {
                int test = Integer.parseInt(row);
                MainActivity.go.putExtra("grid",test);
                setResult(102);
                finish();
            }catch (Exception e){
                Toast.makeText(this,"Pls Enter a Poitive Integer",Toast.LENGTH_LONG).show();
            }
        }

    }
}
