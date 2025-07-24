package com.aiquake.ui.activities.safety;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.aiquake.R;
import java.util.ArrayList;
import java.util.List;

public class FirstAidActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirstAidAdapter adapter;
    private TextInputEditText searchEditText;
    private List<FirstAidItem> firstAidItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_aid);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("First Aid Guide");

        // Initialize views
        recyclerView = findViewById(R.id.first_aid_recycler_view);
        searchEditText = findViewById(R.id.search_edit_text);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firstAidItems = getFirstAidItems();
        adapter = new FirstAidAdapter(firstAidItems);
        recyclerView.setAdapter(adapter);

        // Setup search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private List<FirstAidItem> getFirstAidItems() {
        List<FirstAidItem> items = new ArrayList<>();
        
        // Add first aid items
        items.add(new FirstAidItem(
            "Bleeding Control",
            "Apply direct pressure to the wound using a clean cloth or bandage. Elevate the injured area if possible. If bleeding is severe, apply a tourniquet above the wound.",
            "1. Clean the wound with soap and water if possible\n" +
            "2. Apply direct pressure for at least 10 minutes\n" +
            "3. If bleeding continues, apply more bandages\n" +
            "4. Seek medical help if bleeding is severe",
            "https://www.youtube.com/watch?v=NxO5LvgqZe0&pp=ygUkQmxlZWRpbmcgQ29udHJvbCBtaW5pIHZpZGVvIHR1dG9yaWFs0gcJCY0JAYcqIYzv"
        ));

        items.add(new FirstAidItem(
            "Broken Bones",
            "Immobilize the injured area. Don't try to realign the bone. Apply ice to reduce swelling and pain.",
            "1. Keep the injured area still\n" +
            "2. Apply ice wrapped in cloth\n" +
            "3. Create a splint if possible\n" +
            "4. Seek medical attention immediately",
            "https://www.youtube.com/watch?v=2v8vlXgGXwE&pp=ygUqQnJva2VuIEJvbmVzIGZpcnN0IGFpZCBtaW5pIHZpZGVvIHR1dG9yaWFs"
        ));

        items.add(new FirstAidItem(
            "Head Injuries",
            "Keep the person still and monitor their breathing. Look for signs of concussion.",
            "1. Check for responsiveness\n" +
            "2. Monitor breathing\n" +
            "3. Look for clear fluid from ears/nose\n" +
            "4. Seek immediate medical help",
            "https://www.youtube.com/watch?v=7CgtIgSyAiU"
        ));

        items.add(new FirstAidItem(
            "Crush Injuries",
            "Don't remove heavy objects. Stabilize the area and seek immediate medical help.",
            "1. Don't remove crushing object\n" +
            "2. Keep the person warm\n" +
            "3. Monitor breathing\n" +
            "4. Call emergency services",
            "https://www.youtube.com/watch?v=8fWaZ0XWYE8&pp=ygUsQ3J1c2ggSW5qdXJpZXMgZmlyc3QgYWlkIG1pbmkgdmlkZW8gdHV0b3JpYWw%3D"
        ));

        items.add(new FirstAidItem(
            "Shock Management",
            "Keep the person warm and comfortable. Elevate legs if possible. Monitor breathing.",
            "1. Lay person on their back\n" +
            "2. Elevate legs 12 inches\n" +
            "3. Keep person warm\n" +
            "4. Monitor vital signs",
            "https://www.youtube.com/watch?v=8H98BgRzpOM"
        ));

        return items;
    }

    private void filterItems(String query) {
        List<FirstAidItem> filteredList = new ArrayList<>();
        for (FirstAidItem item : firstAidItems) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateItems(filteredList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 