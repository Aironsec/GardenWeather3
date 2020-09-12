package com.example.gardenweather3;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

interface OnQueryTextChanged extends SearchView.OnQueryTextListener {
    default boolean onQueryTextSubmit(String s) {
        return false;
    }
}

public class CityListFragment extends Fragment {
    private ViewModelData modelData;
    private AdapterSearchCityList adapterSearchCityList;
    private AdapterCityList adapterCityList;
    private DataSourceTextPicTemp sourceData;
    private SearchView cityInput;
    private String newCityName;
    private String addCity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelData = ViewModelProviders.of(requireActivity()).get(ViewModelData.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_list, container, false);
        addCity = getResources().getString(R.string.add_city);
        sourceData = new DataSourceTextPicTemp(getResources());
        initRecycleCityList(sourceData.buildCityList(), view);
        return view;
    }

    @SuppressLint("CheckResult")
    public void subscribeWeather() {
        App.getInstance().getDb().getCityWithHistoryDao().loadCityWithHistory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CityWithHistory>>() {
                    @Override
                    public void accept(List<CityWithHistory> cityWithHistories) throws Exception {
                        if (cityWithHistories.size() < 1) return;

                        List<ItemTextPicTemp> temps = new ArrayList<>();

                        for (CityWithHistory item : cityWithHistories) {
                            String city = item.tableCity.cityName;
                            int pic = R.drawable.ic_sunny2;
                            String temp = Integer.toString(item.histories.get(0).temp);
                            ItemTextPicTemp addItem = new ItemTextPicTemp(city, pic, temp);
                            temps.add(addItem);
                        }

                        sourceData.setDataSource(temps);
                        adapterCityList.notifyDataSetChanged();
                    }
                });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecycleCityList(DataSourceTextPicTemp sourceData, View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_city);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        subscribeWeather();
        adapterCityList = new AdapterCityList(sourceData);
        recyclerView.setAdapter(adapterCityList);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator));
        recyclerView.addItemDecoration(itemDecoration);

        adapterCityList.SetOnItemClickListenerItem((view1, position) -> {
            TextView city = view1.findViewById(R.id.item_list_cites);

            backFragment();

            modelData.setCity(city.getText().toString());

            Snackbar.make(city, "Город изменён", Snackbar.LENGTH_LONG)
                    .setAction("Отменить",
                            v -> {
                                TempData td = TempData.getInstance();
                                modelData.setCity(td.getTempStr());
                            }).show();

        });
        adapterCityList.SetOnItemClickListenerFooter(((view1, position) -> showBottomSheet()));
    }

    private void backFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        assert fragmentManager != null;
        fragmentManager.popBackStack();
    }

    private void showBottomSheet() {
        LinearLayout llBottomSheet = getActivity().findViewById(R.id.dialog_bottom_sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        String[] cities = getResources().getStringArray(R.array.city);
        ArrayList<String> tempArray = new ArrayList<>(Arrays.asList(cities));
        cityInput = llBottomSheet.findViewById(R.id.inputCity);
        initRecycleSearchCityList(tempArray, bottomSheetBehavior);

        cityInput.setOnQueryTextListener((OnQueryTextChanged) s -> {
            adapterSearchCityList.getFilter().filter(s);
            newCityName = s;
            return false;
        });
    }

    private void initRecycleSearchCityList(ArrayList<String> sourceData, BottomSheetBehavior<View> bottomSheetBehavior) {
        RecyclerView recyclerView = getActivity().findViewById(R.id.find_city_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapterSearchCityList = new AdapterSearchCityList(sourceData);
        recyclerView.setAdapter(adapterSearchCityList);

        adapterSearchCityList.SetOnItemClickListener((view1, position) -> {
            TextView textView = view1.findViewById(R.id.city_name);
            String s = textView.getText().toString();
            if (s.equals(addCity)) {
                s = newCityName;
                adapterCityList.addItem(newCityName);
            }
            modelData.setCity(s);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            backFragment();
        });
    }

}