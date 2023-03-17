package com.theelitedevelopers.teamup.modules.main.task;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theelitedevelopers.teamup.databinding.DropDownRvItemBinding;
import com.theelitedevelopers.teamup.modules.data.models.UserDetails;

import java.util.ArrayList;

/**
 * @author Victor
 * @created 11/03/2022 - 12:55 AM
 * @project TeamUp
 */
public class BottomSheetDialogRvTsAdapter extends RecyclerView.Adapter<BottomSheetDialogRvTsAdapter.BottomSheetDialogViewHolder> {
    private ArrayList<String> status;
    private ItemClicked listener;

    public BottomSheetDialogRvTsAdapter(ArrayList<String> status) {
        this.status = status;
    }

    @NonNull
    @Override
    public BottomSheetDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DropDownRvItemBinding binding = DropDownRvItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new BottomSheetDialogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetDialogViewHolder holder, int position) {
        holder.bind(status.get(position));
    }

    @Override
    public int getItemCount() {
        return status.size();
    }

    public class BottomSheetDialogViewHolder extends RecyclerView.ViewHolder {
        private DropDownRvItemBinding binding;

        public BottomSheetDialogViewHolder(DropDownRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String status) {
            binding.textView.setText(status);

            binding.getRoot().setOnClickListener(v -> listener.itemClicked(status));
        }
    }

    public void setOnItemCLickListener(ItemClicked listener) {
        this.listener = listener;
    }

    public interface ItemClicked {
        void itemClicked(String status);
    }
}
