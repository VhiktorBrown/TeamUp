package com.theelitedevelopers.teamup.modules.main.group_chat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.theelitedevelopers.teamup.R;
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.ChatLayoutBinding;
import com.theelitedevelopers.teamup.databinding.GroupChatLayoutBinding;
import com.theelitedevelopers.teamup.modules.data.models.Chat;
import com.theelitedevelopers.teamup.modules.main.chat.ChatActivity;
import com.theelitedevelopers.teamup.modules.main.group_chat.GroupChatActivity;

import java.util.ArrayList;

public class GroupChatListAdapter extends RecyclerView.Adapter<GroupChatListAdapter.ChatViewHolder> {

    Context context;
    ArrayList<Chat> chatArrayList;

    public GroupChatListAdapter(Context context, ArrayList<Chat> chatArrayList){
        this.context = context;
        this.chatArrayList = chatArrayList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GroupChatLayoutBinding binding = GroupChatLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ChatViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        holder.binding.inboxGroupName.setText(chatArrayList.get(holder.getAdapterPosition()).getTitle());

        //set the Last message sent.
        if(!chatArrayList.get(holder.getAdapterPosition()).getLastMessage().equals("") &&
        !chatArrayList.get(holder.getAdapterPosition()).getMessagedLast().equals("")){
            if(chatArrayList.get(holder.getAdapterPosition()).getUid().equals(
                    SharedPref.getInstance(context.getApplicationContext()).getString(Constants.UID)
            )){
                holder.binding.lastMessage.setText("You: "+
                        chatArrayList.get(holder.getAdapterPosition()).getLastMessage());
            }else {
                holder.binding.lastMessage.setText(chatArrayList.get(holder.getAdapterPosition()).getMessagedLast()+": "+
                        chatArrayList.get(holder.getAdapterPosition()).getLastMessage());
            }
        }else {
            holder.binding.lastMessage.setText("");
        }

        if(chatArrayList.get(position).getDate() != null){
            holder.binding.date.setText(AppUtils.Companion.getInboxDate(
                    AppUtils.Companion.fromTimeStampToString(chatArrayList.get(position).getDate().getSeconds())));
        }else {
            holder.binding.date.setText("");
        }

            Picasso.get()
                    .load(chatArrayList.get(position).getImage())
                    .placeholder(R.drawable.group)
                    .into(holder.binding.inboxImage);

            holder.binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), GroupChatActivity.class);
                intent.putExtra(Constants.GROUP_CHAT_ID, chatArrayList.get(position).getId());
                intent.putExtra(Constants.GROUP_CHAT_NAME, chatArrayList.get(position).getTitle());
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
        GroupChatLayoutBinding binding;
        public ChatViewHolder(@NonNull GroupChatLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
