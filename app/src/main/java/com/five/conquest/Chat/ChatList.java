package com.five.conquest.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.five.conquest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Terry Antony on 12/5/2016.
 */

public class ChatList extends BaseAdapter {

    private ArrayList<Message> chat;
    private Context context;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    public ChatList(ArrayList<Message> chat, Context context) {
        this.chat = chat;
        this.context = context;

    }

    @Override
    public int getCount() {
        return chat.size();
    }

    @Override
    public Object getItem(int position) {
        return chat.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        Message message = chat.get(position);
        MessageView mView;
        if (convertView == null) {
            if(message.getUserType()) {
                v = LayoutInflater.from(context).inflate(R.layout.user_me, null, false);
            } else {
                v = LayoutInflater.from(context).inflate(R.layout.user_other, null, false);
            }

            mView = new MessageView();

            mView.messageTextView = (TextView) v.findViewById(R.id.message_text);
            mView.timeTextView = (TextView) v.findViewById(R.id.time_text);

            v.setTag(mView);
        } else {
            v = convertView;
            mView = (MessageView) v.getTag();

        }

        mView.messageTextView.setText(message.getMessageText());
        mView.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMessageTime()));
        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class MessageView {
        public TextView messageTextView;
        public TextView timeTextView;
    }
}
