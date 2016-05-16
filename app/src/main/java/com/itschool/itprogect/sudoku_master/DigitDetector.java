package com.itschool.itprogect.sudoku_master;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

class DigitDetector extends AsyncTask<Void, Void, String> {
    private int x;
    private int y;
    private int i;
    private int j;
    private Context context;
    private Bitmap bitmap;

    public DigitDetector(Context context, Bitmap bitmap, int x, int y, int i, int j){
        this.x=x;
        this.y=y;
        this.i=i;
        this.j=j;
        this.context=context;
        this.bitmap=bitmap;
    }

    @Override
    protected String doInBackground(Void... params) {
        Bitmap b=Bitmap.createBitmap(bitmap, x+3, y+3, bitmap.getWidth()/9-3, bitmap.getHeight()/9-3, null, false);
        Transformation t=new Transformation();
        if (!t.isEmpty(b)) {
            TessOCR tess=new TessOCR(context);
            String digit=tess.translate(b);
            return digit;
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        MainActivity.field[i][j].setText(result);
        MainActivity.progress.setProgress(MainActivity.progress.getProgress()+1);
        if (i==8 && j==8){
            MainActivity.show();
        }
    }

}
