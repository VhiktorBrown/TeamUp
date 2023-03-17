package com.theelitedevelopers.teamup.modules.main.task.adapters;

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
import com.theelitedevelopers.teamup.databinding.TaskLayoutBinding;
import com.theelitedevelopers.teamup.modules.data.models.Chat;
import com.theelitedevelopers.teamup.modules.data.models.Task;
import com.theelitedevelopers.teamup.modules.main.chat.ChatActivity;
import com.theelitedevelopers.teamup.modules.main.task.EditTaskActivity;

import java.util.ArrayList;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    Context context;
    ArrayList<Task> taskArrayList;

    public TaskListAdapter(Context context, ArrayList<Task> taskArrayList){
        this.context = context;
        this.taskArrayList = taskArrayList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaskLayoutBinding binding = TaskLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new TaskViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        holder.binding.taskTitle.setText(taskArrayList.get(position).getTitle());
        holder.binding.taskDescription.setText(taskArrayList.get(position).getDescription());

        switch (taskArrayList.get(position).getTaskStatus()){
            case 0:
                holder.binding.progressStatus.setText(Constants.TODO);
                holder.binding.progressStatus.setBackground(context.getResources().getDrawable(R.drawable.to_do_bg));
                break;
            case 1:
                holder.binding.progressStatus.setText(Constants.IN_PROGRESS);
                holder.binding.progressStatus.setBackground(context.getResources().getDrawable(R.drawable.in_progress_bg));
                break;
            case 2:
                holder.binding.progressStatus.setText(Constants.COMPLETED);
                holder.binding.progressStatus.setBackground(context.getResources().getDrawable(R.drawable.completed_bg));
                break;
        }

        holder.binding.taskEndDate.setText(
                AppUtils.Companion.getTimeInDaysOrWeeks(
                        AppUtils.Companion.fromTimeStampToString(
                                taskArrayList.get(position).getDeadLine().getSeconds()
                        )));

        holder.binding.getRoot().setOnClickListener(v -> {
            if(SharedPref.getInstance(context.getApplicationContext()).getBoolean(Constants.ADMIN)){
                v.getContext().startActivity(new Intent(v.getContext(), EditTaskActivity.class)
                        .putExtra(Constants.TASK_DETAILS, taskArrayList.get(position)));
            }else {
                if(taskArrayList.get(position).getUid().equals(SharedPref.getInstance(context.getApplicationContext()).getString(Constants.UID))){
                    v.getContext().startActivity(new Intent(v.getContext(), EditTaskActivity.class)
                            .putExtra(Constants.TASK_DETAILS, taskArrayList.get(position)));
                }else {
                    AppUtils.Companion.showToastMessage(v.getContext(), "You cannot edit this task because it's not yours.");
                }
            }
        });
    }

    public void setList(ArrayList<Task> taskArrayList){
        this.taskArrayList = taskArrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TaskLayoutBinding binding;
        public TaskViewHolder(@NonNull TaskLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
