package com.theelitedevelopers.teamup.modules.main.chat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.StartNewChatLayoutBinding;
import com.theelitedevelopers.teamup.modules.data.models.UserDetails;
import com.theelitedevelopers.teamup.modules.main.chat.ChatActivity;

import java.util.ArrayList;

public class StartNewChatAdapter extends RecyclerView.Adapter<StartNewChatAdapter.StartNewChatViewHolder> {

    Context context;
    ArrayList<UserDetails> employees;

    public StartNewChatAdapter(Context context, ArrayList<UserDetails> employees){
        this.context = context;
        this.employees = employees;
    }

    @NonNull
    @Override
    public StartNewChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StartNewChatLayoutBinding binding = StartNewChatLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new StartNewChatViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StartNewChatViewHolder holder, int position) {

        holder.binding.inboxName.setText(employees.get(holder.getAdapterPosition()).getName());
        holder.binding.gender.setText(employees.get(holder.getAdapterPosition()).getRole());


            holder.binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra(Constants.RECEIVER_UID, employees.get(position).getUid());
                intent.putExtra(Constants.NAME, employees.get(position).getName());
                v.getContext().startActivity(intent);
            });
    }

    public void setList(ArrayList<UserDetails> employees){
        this.employees = employees;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public static class StartNewChatViewHolder extends RecyclerView.ViewHolder {
        StartNewChatLayoutBinding binding;
        public StartNewChatViewHolder(@NonNull StartNewChatLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
