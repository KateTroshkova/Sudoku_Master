package com.itschool.itprogect.sudoku_master;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TessOCR {
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/Tesseract/";
	public static final String lang = "eng";
	
	public TessOCR(Context context){
		if (!new File(DATA_PATH+"tessdata/").exists()){
			new File(DATA_PATH+"tessdata/").mkdirs();
		}
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {
				AssetManager assetManager = context.getAssets();
				InputStream in = assetManager.open("tessdata/eng.traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/eng.traineddata");
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				
			} catch (IOException e) {
			}
		}
	}
	
	public String translate(Bitmap bitmap){
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setPageSegMode(10);

		String whiteList = "/123456789";
		baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whiteList);
		baseApi.setImage(bitmap);
		
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();
		return recognizedText;
	}

}
