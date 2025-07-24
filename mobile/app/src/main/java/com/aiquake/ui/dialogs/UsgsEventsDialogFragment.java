package com.aiquake.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aiquake.R;
import com.aiquake.adapters.EventAdapter;
import com.aiquake.models.EarthquakeEvent;

import java.util.List;

public class UsgsEventsDialogFragment extends DialogFragment {
    private static final String ARG_EARTHQUAKES = "earthquakes";
    private List<EarthquakeEvent> earthquakes;
    private EventAdapter adapter;

    public static UsgsEventsDialogFragment newInstance(List<EarthquakeEvent> earthquakes) {
        UsgsEventsDialogFragment fragment = new UsgsEventsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EARTHQUAKES, (java.io.Serializable) earthquakes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_QuakeApp);
        if (getArguments() != null) {
            earthquakes = (List<EarthquakeEvent>) getArguments().getSerializable(ARG_EARTHQUAKES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_usgs_events, container, false);
        
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new EventAdapter(getContext(), earthquakes);
        recyclerView.setAdapter(adapter);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // You can add more customization to the dialog here if needed
        // For example, setting a specific width or height
        if (getDialog() != null && getDialog().getWindow() != null) {
             getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
} 