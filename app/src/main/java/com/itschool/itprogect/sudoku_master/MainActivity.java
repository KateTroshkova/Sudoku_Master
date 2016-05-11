package com.itschool.itprogect.sudoku_master;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST=0;
    private final int PICTURE_REQUEST = 1;
    private final int SETTINGS_REQUEST=2;

    public static EditText[][] field;
    private static TextView text;
    private int[][] sudoku;
    public static ProgressBar progress;
    private static int size=9;
    public static Uri uri=Uri.parse(Environment.getExternalStorageDirectory()+"test.jpg");

    private BaseLoaderCallback callback=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, callback);
        }
        else{
            callback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress=(ProgressBar)findViewById(R.id.progressBar);
        progress.setVisibility(View.INVISIBLE);
        progress.setMax(81);
        progress.setProgress(0);
        progress.setProgressDrawable(getResources().getDrawable(R.drawable.progresstyle));
        text=(TextView)findViewById(R.id.textView2);
        text.setVisibility(View.INVISIBLE);
        field=new EditText[size][size];
        sudoku=new int[size][size];
        init();
    }

    private void init(){
        RelativeLayout back=(RelativeLayout)findViewById(R.id.back);
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int x=10;
        int y=0;
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                field[i][j]=new EditText(this);
                field[i][j].setTextSize(displaymetrics.widthPixels/25-10);
                field[i][j].setWidth(displaymetrics.widthPixels/9-10);
                field[i][j].setHeight(displaymetrics.widthPixels/9-10);
                field[i][j].setX(x);
                field[i][j].setY(y);
                field[i][j].setInputType(InputType.TYPE_CLASS_NUMBER);
                x+=displaymetrics.widthPixels/9-10;
                back.addView(field[i][j]);
            }
            x=10;
            y+=displaymetrics.widthPixels/9-10;
        }
    }

    public static void hide(){
        progress.setVisibility(View.VISIBLE);
        text.setVisibility(View.VISIBLE);
        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                field[i][j].setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void show(){
        progress.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                field[i][j].setVisibility(View.VISIBLE);
            }
        }
    }

    public void click(View view){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    public void cameraClick(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                "test.jpg");
        uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, PICTURE_REQUEST);
    }

    public void solve(View view){
        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                if (field[i][j].getText().toString().length()<1 || field[i][j].getText().toString().equals("0")){
                    sudoku[i][j]=0;
                    field[i][j].setTextColor(getResources().getColor(R.color.colorText));
                }
                else {
                    sudoku[i][j] = Integer.parseInt(field[i][j].getText().toString());
                    field[i][j].setTextColor(getResources().getColor(R.color.colorAccent));
                }

            }
        }
        Solve solve=new Solve(sudoku, this);
    }

    public void clean(View view){
        progress.setProgress(0);
       for(int i=0; i<size; i++){
           for (int j=0; j<size; j++){
               field[i][j].setText("");
               field[i][j].setTextColor(getResources().getColor(R.color.colorText));
           }
       }
    }

    @Override
    protected void onActivityResult(int code, int result, Intent data) {
        if (result==RESULT_OK) {
            if (code == PICTURE_REQUEST) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_REQUEST);
            }
            if (code == GALLERY_REQUEST) {
                uri = data.getData();
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_REQUEST);
            }
            if (code == SETTINGS_REQUEST) {
                hide();
            }
        }
    }

}