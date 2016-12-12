package com.five.conquest.Chat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.five.conquest.MainActivity;
import com.five.conquest.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Terry Antony on 12/5/2016.
 */

public class SocialActivity extends AppCompatActivity {

    private ListView listView;
    private EditText editText;
    private ArrayList<Message> chatMessages;
    private ImageView chatView;
    private ChatList chatList;
    private Drawable background;

    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if(v==editText)
                {
                    sendMessage(editText.getText().toString());
                }

                editText.setText("");

                return true;
            }
            return false;

        }
    };

    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v==chatView)
            {
                sendMessage(editText.getText().toString());
            }

            editText.setText("");

        }
    };

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (editText.getText().toString().equals("")) {

            } else {
                chatView.setImageResource(R.drawable.ic_chat_send);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length()==0){
                chatView.setImageResource(R.drawable.ic_chat_send);
            }else{
                chatView.setImageResource(R.drawable.ic_chat_send_active);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        chatMessages = new ArrayList<>();
        listView = (ListView) findViewById(R.id.chat_list_view);
        editText = (EditText) findViewById(R.id.chat_edit_text1);
        chatView = (ImageView) findViewById(R.id.enter_chat1);
        background = getResources().getDrawable(R.drawable.knight);

        chatList = new ChatList(chatMessages, this);
        listView.setAdapter(chatList);
        editText.setOnKeyListener(keyListener);
        chatView.setOnClickListener(clickListener);
        editText.addTextChangedListener(watcher);
        background.setAlpha(20);

        Bundle b = getIntent().getExtras();

        if(b != null) {
            Toast toast2 = Toast.makeText(getApplicationContext(), "Hello toast!", Toast.LENGTH_SHORT);
            toast2.show();
            sendMessage(b.getString("loc"));
        }


    }

    private void sendMessage(final String msg)
    {
        if(msg.trim().length()==0)
            return;

        final Message message = new Message();
        message.setMessageText(msg);
        message.setMessageTime(new Date().getTime());
        chatMessages.add(message);

        if(chatList !=null)
            chatList.notifyDataSetChanged();

        // Mark message as delivered after one second

        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

        exec.schedule(new Runnable(){
            @Override
            public void run(){

                final Message message = new Message();
                message.setMessageText("What's Up");
                message.setUserType(false);
                message.setMessageTime(new Date().getTime());
                chatMessages.add(message);

                SocialActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        chatList.notifyDataSetChanged();
                    }
                });


            }
        }, 1, TimeUnit.SECONDS);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
