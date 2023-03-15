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
public class BottomSheetDialogRvAdapter extends RecyclerView.Adapter<BottomSheetDialogRvAdapter.BottomSheetDialogViewHolder> {
    private ArrayList<UserDetails> employees;
    private ItemClicked listener;

    public BottomSheetDialogRvAdapter(ArrayList<UserDetails> employees) {
        this.employees = employees;
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
        holder.bind(employees.get(position));
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class BottomSheetDialogViewHolder extends RecyclerView.ViewHolder {
        private DropDownRvItemBinding binding;

        public BottomSheetDialogViewHolder(DropDownRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(UserDetails employee) {
            binding.textView.setText(employee.getName());

            binding.getRoot().setOnClickListener(v -> listener.itemClicked(employee));
        }
    }

    public void setOnItemCLickListener(ItemClicked listener) {
        this.listener = listener;
    }

    public interface ItemClicked {
        void itemClicked(UserDetails employee);
    }
}
