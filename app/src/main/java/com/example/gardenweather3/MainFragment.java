package com.example.gardenweather3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initRecycleLineHours(DataSourceTextPicTemp sourceData, View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        AdapterLineClock adapter = new AdapterLineClock(sourceData);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        DataSourceTextPicTemp sourceData = new DataSourceTextPicTemp(getResources());
        initRecycleLineHours(sourceData.buildLineByClock(), view);

//        ImageView imageView = view.findViewById(R.id.imageView13);
//        TextView textView = view.findViewById(R.id.textView);
//
//        imageView.setOnClickListener((View) -> {
//            Uri uri = Uri.parse("https://yandex.ru/pogoda/obninsk");
//            Intent browser = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(browser);
//        });

//        textView.setOnClickListener((View) -> {
//            assert getFragmentManager() != null;
//            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//            fragmentTransaction.replace(R.id.frame_dynamic, new CityListFragment()).commit();
//        });

//        modelData.getCity().observe(this, textView::setText);

        return view;
    }
}