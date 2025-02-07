package com.example.n099madassignment2;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private OnTaskSelectedListener listener;

    public interface OnTaskSelectedListener {
        void onTaskSelected(Task task);
    }

    public TaskAdapter(OnTaskSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
        holder.itemView.setOnClickListener(v -> listener.onTaskSelected(task));
    }

    @Override
    public int getItemCount() { return tasks.size(); }

    public void setTasks(List<Task> tasks) { this.tasks = tasks; notifyDataSetChanged(); }

    public Task getTaskAtPosition(int position) { return tasks.get(position); }

    public void removeTaskAtPosition(int position) { tasks.remove(position); notifyItemRemoved(position); }

    public List<Task> getTasks() { return tasks; }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView description, priority;

        public TaskViewHolder(View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.task_description);
            priority = itemView.findViewById(R.id.task_priority);
        }

        public void bind(Task task) {
            description.setText(task.getDescription());
            priority.setText(task.getPriority());
        }
    }
}
