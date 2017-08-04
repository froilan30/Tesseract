package com.example.nisarg.tesseract;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    //Bitmap image;
    private TessBaseAPI mTess;
    String datapath = "";
    private ImageView imageView;
    private Bitmap image;
    private static int SELECT_PICTURE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView) findViewById(R.id.imageView);
        //init image
        //image = BitmapFactory.decodeResource(getResources(), R.drawable.test_image);
        Picasso.with(MainActivity.this).load(R.drawable.ocr).fit().into(imageView);

      //  image= Bitmap.createBitmap(imageView.getWidth(),imageView.getHeight(),Bitmap.Config.ARGB_8888);//i is imageview whch u want to convert in bitmap
        //Canvas canvas = new Canvas(image);

       // imageView.draw(canvas);
        //initialize Tesseract API
        String language = "eng";
        datapath = getFilesDir()+ "/tesseract/";
        mTess = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));

        mTess.init(datapath, language);
    }

    public void processImage(View view){
        String OCRresult = null;

        imageView.buildDrawingCache();
        image = imageView.getDrawingCache();
      //  image = BitmapFactory.decodeResource(getResources(), R.drawable.test_image);
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setText(OCRresult);
        image.eraseColor(Color.TRANSPARENT);

    }

    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }


            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getter(View view) {
        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setText("");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        TextView get=(TextView) findViewById(R.id.getimg);
        get.setVisibility(View.INVISIBLE);
        RelativeLayout r=(RelativeLayout) findViewById(R.id.imgContainer);
        r.setVisibility(View.INVISIBLE);
        RelativeLayout c=(RelativeLayout) findViewById(R.id.OCRButtonContainer);
        c.setVisibility(View.VISIBLE);
        TextView process=(TextView) findViewById(R.id.OCRbutton);
        process.setVisibility(View.VISIBLE);



    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageURI = data.getData();

                Picasso.with(MainActivity.this).load(selectedImageURI).noPlaceholder().centerCrop().fit()
                        .into((ImageView) findViewById(R.id.imageView));
            }

        }
    }
}