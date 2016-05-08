package com.itschool.itprogect.sudoku_master;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        image=(ImageView)findViewById(R.id.imageView2);
        Uri uri=MainActivity.uri;
        image.setImageURI(uri);
        bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        Transformation transformation=new Transformation();
        if (bitmap.getWidth()>displaymetrics.widthPixels/2 && bitmap.getHeight()>displaymetrics.heightPixels/2){
            bitmap=transformation.getResizedBitmap(bitmap, displaymetrics.widthPixels/2, displaymetrics.heightPixels/2);
        }
        bitmap=transformation.transform(bitmap);
        image.setImageBitmap(bitmap);
        if (bitmap.getHeight()!=bitmap.getWidth()){
            Toast.makeText(this, getString(R.string.gridError), Toast.LENGTH_LONG).show();
            image.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    public void rotationPlus(View view){
        Transformation transformation=new Transformation();
        bitmap=transformation.rotate(bitmap, 90);
        image.setImageBitmap(bitmap);
    }

    public void rotationMinus(View view){
        Transformation transformation=new Transformation();
        bitmap=transformation.rotate(bitmap, -90);
        image.setImageBitmap(bitmap);
    }

    public void flipVertical(View view){
        Transformation transformation=new Transformation();
        bitmap=transformation.flip(bitmap, 1, -1);
        image.setImageBitmap(bitmap);
    }

    public void flipHorizontal(View view){
        Transformation transformation=new Transformation();
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
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                DetectDigit digit = new DetectDigit(x, y, i, j);
                digit.execute();
                x += bitmap.getWidth() / 9;
            }
            x = 0;
            y += bitmap.getHeight() / 9;
        }
        setResult(RESULT_OK, null);
        finish();
    }

    class DetectDigit extends AsyncTask<Void, Void, String> {
        private int x;
        private int y;
        private int i;
        private int j;

        public DetectDigit(int x, int y, int i, int j){
            this.x=x;
            this.y=y;
            this.i=i;
            this.j=j;
        }

        @Override
        protected String doInBackground(Void... params) {
            Bitmap b=Bitmap.createBitmap(bitmap, x+5, y+5, bitmap.getWidth()/9-5, bitmap.getHeight()/9-5, null, false);
            TessOCR tess=new TessOCR(SettingsActivity.this);
            String digit=tess.translate(b);
            return digit;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            MainActivity.field[i][j].setText(result);
            if (i==8 && j==8){
                MainActivity.show();
            }
        }

    }
}
