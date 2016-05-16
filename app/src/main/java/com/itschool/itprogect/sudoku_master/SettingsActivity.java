package com.itschool.itprogect.sudoku_master;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private ImageView image;
    private Uri uri;
    private Transformation transformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        try {
            init();

            if (bitmap.getWidth() > displaymetrics.widthPixels / 2 && bitmap.getHeight() > displaymetrics.heightPixels / 2) {
                bitmap = transformation.getResizedBitmap(bitmap, displaymetrics.widthPixels / 2, displaymetrics.heightPixels / 2);
            }

            bitmap = transformation.transform(bitmap);

            image.setImageBitmap(bitmap);

            if (bitmap.getHeight() != bitmap.getWidth()) {
                printError();
            }
            else{
                createGrid();
            }
        }
        catch(Exception e){
            printError();
        }
    }

    private void init(){
        transformation = new Transformation();
        uri = MainActivity.uri;
        image = (ImageView) findViewById(R.id.imageView2);
        image.setImageURI(uri);
        bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
    }

    private void createGrid(){
        FrameLayout layout=(FrameLayout)findViewById(R.id.frame);
        Grid grid=new Grid(this, Grid.STYLE.STYLE_WHITE, bitmap.getWidth()/3, bitmap.getWidth()/3);
        layout.addView(grid);
    }

    private void printError(){
        Toast.makeText(this, getString(R.string.gridError), Toast.LENGTH_LONG).show();
        image.setImageResource(android.R.drawable.ic_menu_gallery);
        bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
    }

    public void rotationPlus(View view){
        bitmap=transformation.rotate(bitmap, 90);
        image.setImageBitmap(bitmap);
    }

    public void rotationMinus(View view){
        bitmap=transformation.rotate(bitmap, -90);
        image.setImageBitmap(bitmap);
    }

    public void flipVertical(View view){
        bitmap=transformation.flip(bitmap, 1, -1);
        image.setImageBitmap(bitmap);
    }

    public void flipHorizontal(View view){
        bitmap=transformation.flip(bitmap, -1, 1);
        image.setImageBitmap(bitmap);
    }

    public void back(View view){
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void translate(View view){
        int x = 0;
        int y = 0;
        int size=9;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                DigitDetector digit = new DigitDetector(this, bitmap, x, y, i, j);
                digit.execute();
                x += bitmap.getWidth() / size;
            }
            x = 0;
            y += bitmap.getHeight() / size;
        }
        setResult(RESULT_OK, null);
        finish();
    }
}
