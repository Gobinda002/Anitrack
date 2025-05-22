package com.example.anitrack;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OngoingFragment extends Fragment {

    private LinearLayout ongoingList;
    private DatabaseHelper dbHelper;
    private FloatingActionButton fabAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ongoingList = view.findViewById(R.id.OngoingList);
        fabAdd = view.findViewById(R.id.fabAdd);
        dbHelper = new DatabaseHelper(getContext());

        loadOngoingAnimes();

        fabAdd.setOnClickListener(v -> showAddDialog());

        return view;
    }

    private void loadOngoingAnimes() {
        ongoingList.removeAllViews();
        int count = dbHelper.getOngoingAnimeCount();
        for (int i = 0; i < count; i++) {
            String animeName = dbHelper.getOngoingAnimeAt(i);

            TextView textView = new TextView(getContext());
            textView.setText((i + 1) + ". " + animeName);
            textView.setTextSize(18);
            textView.setPadding(10, 10, 10, 10);

            // Single click shows option to edit or mark as completed
            textView.setOnClickListener(v -> showOptionDialog(textView, animeName));

            ongoingList.addView(textView);
        }
    }

    private void showOptionDialog(TextView textView, String animeName) {
        String[] options = {"Edit Name", "Mark as Completed"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Action");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showEditDialog(textView, animeName);
            } else if (which == 1) {
                markAsCompleted(textView, animeName);
            }
        });
        builder.show();
    }

    private void markAsCompleted(TextView textView, String animeName) {
        if (dbHelper.addCompletedAnime(animeName)) {
            if (dbHelper.deleteOngoingAnime(animeName)) {

                // Remove view from layout
                ongoingList.removeView(textView);
                refreshListNumbers();

                // Optional: update the completed fragment only if it's visible and method exists
                if (getActivity() instanceof HomeActivity) {
                    try {
                        ((HomeActivity) getActivity()).addAnimeToCompleted(animeName);
                    } catch (Exception e) {
                        e.printStackTrace(); // Catch and log any exception
                    }
                }

                Toast.makeText(getContext(), animeName + " marked as completed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to remove from ongoing list", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Failed to add to completed list", Toast.LENGTH_SHORT).show();
        }
    }


    private void refreshListNumbers() {
        int childCount = ongoingList.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView tv = (TextView) ongoingList.getChildAt(i);
            String text = tv.getText().toString();
            String[] parts = text.split("\\. ", 2);
            if (parts.length == 2) {
                tv.setText((i + 1) + ". " + parts[1]);
            }
        }
    }

    private void showEditDialog(TextView textView, String oldName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Anime Name");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(oldName);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.completedAnimeExists(newName) || dbHelper.ongoingAnimeExists(newName)) {
                Toast.makeText(getContext(), "Anime already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.updateOngoingAnime(oldName, newName)) {
                textView.setText(textView.getText().toString().replace(oldName, newName));
                Toast.makeText(getContext(), "Anime updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update anime", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Ongoing Anime");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.ongoingAnimeExists(name)) {
                Toast.makeText(getContext(), "Anime already in ongoing list", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.addOngoingAnime(name)) {
                loadOngoingAnimes();
                Toast.makeText(getContext(), "Anime added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add anime", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
