package me.pntutorial.pnrtcblog.adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import me.pntutorial.pnrtcblog.R;
import me.pntutorial.pnrtcblog.adt.ChatMessage;


/**
 * The chatting adapter used in the blog.
 */
public class ChatAdapter extends ArrayAdapter<ChatMessage> {
    private static final long MESSAGE_TIME = 3000;
    private LayoutInflater inflater;
    private List<ChatMessage> values;

    public ChatAdapter(Context context, List<ChatMessage> values) {
        super(context, R.layout.chat_message_row_layout, android.R.id.text1, values);
        this.inflater = LayoutInflater.from(context);
        this.values=values;
    }

    class ViewHolder {
        TextView sender;
        TextView message;
        TextView timeStamp;
        ChatMessage chatMsg;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ChatMessage chatMsg;
        if(position >= values.size()){ chatMsg = new ChatMessage("","",0); } // Catch Edge Case
        else { chatMsg = this.values.get(position); }
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chat_message_row_layout, parent, false);
            holder.sender = (TextView) convertView.findViewById(R.id.chat_user);
            holder.message = (TextView) convertView.findViewById(R.id.chat_message);
            holder.timeStamp = (TextView) convertView.findViewById(R.id.chat_timestamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.sender.setText(chatMsg.getSender() + ": ");
        holder.message.setText(chatMsg.getMessage());
        holder.timeStamp.setText(formatTimeStamp(chatMsg.getTimeStamp()));
        holder.chatMsg=chatMsg;
        setRemoveTimeout(chatMsg);
        return convertView;
    }

    @Override
    public int getCount() {
        return this.values.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position){
        if (position >= values.size()){ return -1; }
        return values.get(position).hashCode();
    }

    public void removeMsg(int loc){
        this.values.remove(loc);
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage chatMsg){
        this.values.add(chatMsg);
        notifyDataSetChanged();
    }

    private void setRemoveTimeout(final ChatMessage message){
        Log.i("AdapterFade", "Caling Fade3");
        long elapsed = System.currentTimeMillis() - message.getTimeStamp();
        if (elapsed >= MESSAGE_TIME){
            if (values.contains(message))
                values.remove(message);
            notifyDataSetChanged();
            return;
        }
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (values.contains(message)){
                    values.remove(message);
                }
                notifyDataSetChanged();
            }
        }, MESSAGE_TIME - elapsed);
    }


    /**
     * Format the long System.currentTimeMillis() to a better looking timestamp. Uses a calendar
     *   object to format with the user's current time zone.
     * @param timeStamp
     * @return
     */
    public static String formatTimeStamp(long timeStamp){
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm.ss a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return formatter.format(calendar.getTime());
    }

}