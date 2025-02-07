package com.example.n099madassignment2;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskSelectedListener {
    private FirebaseHelper firebaseHelper;
    private TaskAdapter taskAdapter;
    private EditText editTextDescription;
    private Spinner spinnerPriority;
    private String selectedTaskId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseHelper = new FirebaseHelper();
        taskAdapter = new TaskAdapter(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        editTextDescription = findViewById(R.id.editText_description);
        spinnerPriority = findViewById(R.id.spinner_priority);

        firebaseHelper.getTasks().observe(this, tasks -> {
            if (tasks != null) {
                taskAdapter.setTasks(tasks);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task taskToDelete = taskAdapter.getTaskAtPosition(position);
                if (taskToDelete != null) {
                    firebaseHelper.deleteTask(taskToDelete.getId());
                    taskAdapter.removeTaskAtPosition(position);
                    Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.button_add).setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();
            if (!description.isEmpty()) {
                firebaseHelper.addTask(description, priority);
                Toast.makeText(MainActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(MainActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_update).setOnClickListener(v -> {
            String newDescription = editTextDescription.getText().toString().trim();
            String newPriority = spinnerPriority.getSelectedItem().toString();

            if (!newDescription.isEmpty()) {
                boolean taskFound = false;
                for (Task task : taskAdapter.getTasks()) {
                    if (task.getDescription().equals(newDescription)) {
                        taskFound = true;
                        firebaseHelper.updateTask(task.getId(), newDescription, newPriority, isSuccessful -> {
                            if (isSuccessful) {
                                Toast.makeText(MainActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
                                clearFields();
                            } else {
                                Toast.makeText(MainActivity.this, "Error: Task not found", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }

                if (!taskFound && selectedTaskId != null) {
                    firebaseHelper.updateTask(selectedTaskId, newDescription, newPriority, isSuccessful -> {
                        if (isSuccessful) {
                            Toast.makeText(MainActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(MainActivity.this, "Error: Task not found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if (!taskFound && selectedTaskId == null) {
                    Toast.makeText(MainActivity.this, "No matching task found, please select a task or enter a matching description", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_delete).setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();
            if (!description.isEmpty()) {
                firebaseHelper.deleteTaskByDescriptionAndPriority(description, priority);
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(MainActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onTaskSelected(Task task) {
        editTextDescription.setText(task.getDescription());
        spinnerPriority.setSelection(getPriorityPosition(task.getPriority()));
        selectedTaskId = task.getId();
    }

    private int getPriorityPosition(String priority) {
        switch (priority) {
            case "High": return 0;
            case "Medium": return 1;
            case "Low": return 2;
            default: return 0;
        }
    }

    private void clearFields() {
        editTextDescription.setText("");
        spinnerPriority.setSelection(0);
        selectedTaskId = null;
    }
}
