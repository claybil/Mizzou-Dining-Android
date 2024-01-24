package com.example.myapplication.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.Timer;

import com.example.myapplication.Location;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    MainActivity mainActivity;
    ListView listView;

    void initListView() {
        ArrayAdapter<String> aa = new ArrayAdapter<>(mainActivity,
                R.layout.list_item_textview,
                mainActivity.strAllLocations);
        listView.setAdapter(aa);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mainActivity = (MainActivity) requireActivity();
        listView = binding.allLocationsList;

        initListView();

        binding.filterOpenCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ArrayAdapter<String> aa = new ArrayAdapter<>(mainActivity,
                        R.layout.list_item_textview,
                        mainActivity.strOpenLocations);
                listView.setAdapter(aa);
            }
            else {
                initListView();
            }
        });

//        SearchViewModel searchViewModel =
//                new ViewModelProvider(this).get(SearchViewModel.class);
//        searchViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}