package com.example.isha.myapp;


import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.example.isha.myapp.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class TessOCR {
    private TessBaseAPI mTess;

    public TessOCR() {
        // TODO Auto-generated constructor stub

        mTess = new TessBaseAPI();
       // AssetManager assetManager=
        try {
          String datapath = Environment.getExternalStorageDirectory() + "/DemoOCR/";
          String language = "eng";
          // AssetManager assetManager = getAssets();
        File dir = new File(datapath + "/tessdata/");

//          File dir = new File(datapath + "/mnt/sdcard/tesseract/"); //21-12-16
          if (!dir.exists())
//              dir.mkdirs();                                   //21-12-16
              dir.mkdir();
          mTess.init(datapath, language);
      }catch(Exception e)
      { Log.v("Main","!!!!Address exception "+e);   }

    }

    public String getOCRResult(Bitmap bitmap) {

        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();

        return result;
}

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }

}
