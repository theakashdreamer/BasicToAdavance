package com.skysoftsolution.basictoadavance.webRTC.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skysoftsolution.basictoadavance.R;
import com.skysoftsolution.basictoadavance.webRTC.entity.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_received, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        holder.sender_layout.setVisibility(chatMessages.get(position).isSentByMe() ? View.VISIBLE : View.GONE);
        holder.receiver_layout .setVisibility(chatMessages.get(position).isSentByMe() ? View.GONE : View.VISIBLE);
        holder.sender_message.setText(chatMessage.getMessage());
        holder.receiver_message.setText(chatMessage.getMessage());
        holder.receiver_time.setText(chatMessage.getTimestamp());
        holder.sender_time.setText(chatMessage.getTimestamp());
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).isSentByMe() ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView sender_message,receiver_message,receiver_time,sender_time;
        LinearLayout sender_layout,receiver_layout;
        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            sender_message = itemView.findViewById(R.id.sender_message);
            receiver_message = itemView.findViewById(R.id.receiver_message);
            receiver_time = itemView.findViewById(R.id.receiver_time);
            sender_time = itemView.findViewById(R.id.sender_time);

            sender_layout = itemView.findViewById(R.id.sender_layout);
            receiver_layout = itemView.findViewById(R.id.receiver_layout);
        }
    }
}
