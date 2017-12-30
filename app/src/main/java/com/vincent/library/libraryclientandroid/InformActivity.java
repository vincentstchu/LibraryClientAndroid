package com.vincent.library.libraryclientandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class InformActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTextView;
    private Button mButton;
    private File currentImageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform);
        mImageView = (ImageView) findViewById(R.id.informImgView);
        mTextView = (EditText) findViewById(R.id.informMsgText);
        mButton = (Button) findViewById(R.id.msgSendButton);

        /*File dir = new File(Environment.getExternalStorageDirectory(),"pictures");
        if(dir.exists()) {
            dir.mkdirs();
        }
        currentImageFile = new File(dir,System.currentTimeMillis()+".jpg");
        if(!currentImageFile.exists()) {
            try {
                currentImageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        */
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0x01);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0x01) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            mImageView.setImageBitmap(bitmap);
        }

    }
}
