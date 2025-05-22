package com.example.anitrack;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CompletedFragment extends Fragment {

    private LinearLayout completedList;
    private FloatingActionButton fabAdd;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_completed, container, false);

        completedList = view.findViewById(R.id.CompletedLists);
        fabAdd = view.findViewById(R.id.fabAddCompleteds);

        dbHelper = new DatabaseHelper(getContext());

        loadAnimeList();

        fabAdd.setOnClickListener(v -> showAddDialog());

        return view;
    }

    // Show dialog to add new anime
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Anime");

        // Create EditText
        final EditText input = new EditText(getContext());
        input.setHint("Enter anime name");

        // Create a LinearLayout to wrap the EditText and apply margin
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);

        int marginInDp = 16; // margin in dp
        float scale = getResources().getDisplayMetrics().density;
        int marginInPx = (int) (marginInDp * scale + 0.5f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(marginInPx, 0, 0, 0); // left margin only

        input.setLayoutParams(params);
        container.addView(input);

        builder.setView(container);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.completedAnimeExists(name)) {
                Toast.makeText(getContext(), "Anime already in completed list", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.addCompletedAnime(name)) {
                addAnimeToView(name);
                refreshListNumbers();
                Toast.makeText(getContext(), "Anime added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add anime", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Add anime to the LinearLayout list view
    private void addAnimeToView(String name) {
        if (name == null || name.isEmpty()) return;

        String capitalized = name.substring(0, 1).toUpperCase() + name.substring(1);

        TextView tv = new TextView(getContext());
        tv.setText((completedList.getChildCount() + 1) + ". " + capitalized);
        tv.setPadding(0, 0, 0, 16);
        tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 18);

        // On click, show options (edit/delete)
        tv.setOnClickListener(v -> showOptionsDialog(tv, name));

        completedList.addView(tv);
    }

    // Show options dialog on name click
    private void showOptionsDialog(TextView textView, String animeName) {
        String[] options = {"Edit Name", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showEditDialog(textView, animeName);
            } else if (which == 1) {
                showDeleteConfirmation(textView, animeName);
            }
        });
        builder.show();
    }

    // Edit dialog
    private void showEditDialog(TextView textView, String oldName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Anime Name");
        final EditText input = new EditText(getContext());
        input.setText(oldName);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (TextUtils.isEmpty(newName)) {
                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.completedAnimeExists(newName)) {
                Toast.makeText(getContext(), "Anime already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.updateCompletedAnime(oldName, newName)) {
                textView.setText(""); // Clear old text
                textView.setText((completedList.indexOfChild(textView) + 1) + ". " +
                        newName.substring(0, 1).toUpperCase() + newName.substring(1));
                Toast.makeText(getContext(), "Anime updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Confirm deletion
    private void showDeleteConfirmation(TextView textView, String animeName) {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(getContext());
        confirmDialog.setTitle("Delete Anime");
        confirmDialog.setMessage("Are you sure you want to delete this anime?");
        confirmDialog.setPositiveButton("Yes", (dialog, which) -> {
            if (dbHelper.deleteCompletedAnime(animeName)) {
                completedList.removeView(textView);
                refreshListNumbers();
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });

        confirmDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        confirmDialog.show();
    }

    // Refresh numbering in the list after changes
    private void refreshListNumbers() {
        int count = completedList.getChildCount();
        for (int i = 0; i < count; i++) {
            TextView tv = (TextView) completedList.getChildAt(i);
            String[] parts = tv.getText().toString().split("\\. ", 2);
            if (parts.length == 2) {
                tv.setText((i + 1) + ". " + parts[1]);
            }
        }
    }

    // Load anime from database and display
    private void loadAnimeList() {
        ArrayList<String> savedAnime = dbHelper.getAllCompletedAnime();
        if (savedAnime != null) {
            for (String animeName : savedAnime) {
                addAnimeToView(animeName);
            }
        }
    }

    // Optional: called externally to add anime programmatically
    public void addAnimeExternally(String name) {
        if (!dbHelper.completedAnimeExists(name)) {
            if (dbHelper.addCompletedAnime(name)) {
                addAnimeToView(name);
                refreshListNumbers();
            }
        }
    }
}
