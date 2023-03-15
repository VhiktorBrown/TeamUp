package com.theelitedevelopers.teamup.modules.main.chat.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.theelitedevelopers.teamup.R;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.ChatLayoutBinding;
import com.theelitedevelopers.teamup.modules.data.models.Chat;
import com.theelitedevelopers.teamup.modules.main.chat.ChatActivity;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    Context context;
    ArrayList<Chat> chatArrayList;

    public ChatListAdapter(Context context, ArrayList<Chat> chatArrayList){
        this.context = context;
        this.chatArrayList = chatArrayList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatLayoutBinding binding = ChatLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ChatViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        holder.binding.inboxName.setText(chatArrayList.get(holder.getAdapterPosition()).getName());

        //set the Last message sent.
        if(chatArrayList.get(holder.getAdapterPosition()).getLastMessage() != null){
            holder.binding.lastMessage.setText(chatArrayList.get(holder.getAdapterPosition()).getLastMessage());
        }else {
            holder.binding.lastMessage.setText("");
        }

        holder.binding.date.setText(AppUtils.Companion.getInboxDate(
                AppUtils.Companion.fromTimeStampToString(chatArrayList.get(position).getDate().getSeconds())));

            Picasso.get()
                    .load(chatArrayList.get(position).getImage())
                    .placeholder(R.drawable.academia_profile)
                    .into(holder.binding.inboxImage);

            holder.binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra(Constants.RECEIVER_UID, chatArrayList.get(position).getUid());
                intent.putExtra(Constants.NAME, chatArrayList.get(position).getName());
                v.getContext().startActivity(intent);
            });
    }

    public void setList(ArrayList<Chat> chatArrayList){
        this.chatArrayList = chatArrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        ChatLayoutBinding binding;
        public ChatViewHolder(@NonNull ChatLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
