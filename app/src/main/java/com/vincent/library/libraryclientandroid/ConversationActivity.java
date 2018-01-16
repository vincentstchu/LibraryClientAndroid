package com.vincent.library.libraryclientandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationActivity extends AppCompatActivity {
    String[] message;
    TextView msgText;
    ImageView img;
    Button okBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        msgText = (TextView) findViewById(R.id.conversationText);
        img = (ImageView) findViewById(R.id.converImage);
        Intent intent = getIntent();
        if(intent!=null) {
            message = intent.getStringArrayExtra("message");
            msgText.setText(message[0]);
            if(message[1].equals("0"))
                img.setImageResource(R.mipmap.ic_fail);
        }
        okBtn = (Button) findViewById(R.id.okButton);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(ConversationActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }
        });
    }
}
