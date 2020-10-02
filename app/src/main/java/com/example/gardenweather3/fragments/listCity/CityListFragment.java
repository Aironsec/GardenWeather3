package com.example.gardenweather3.fragments.listCity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardenweather3.activity.App;
import com.example.gardenweather3.DataSourceTextPicTemp;
import com.example.gardenweather3.ItemTextPicTemp;
import com.example.gardenweather3.R;
import com.example.gardenweather3.ViewModelData;
import com.example.gardenweather3.db.TableCity;
import com.example.gardenweather3.helperReciclerView.SimpleItemTouchHelperCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class CityListFragment extends Fragment {
    private ViewModelData modelData;
    private AdapterCityList adapterCityList;
    private DataSourceTextPicTemp sourceData;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private Context context;
    private final String CELCIA = "\u00B0";

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelData = ViewModelProviders.of(requireActivity()).get(ViewModelData.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_list, container, false);
        sourceData = new DataSourceTextPicTemp(getResources());
        initRecycleCityList(sourceData.buildCityList(), view);
        return view;
    }


    @SuppressLint("CheckResult")
    public void subscribeToTheListOfCitiesWeather() {
        App.getInstance().getDb().getCurrentHoursDay().loadOnlyAllCity()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TableCity>>() {
                    @Override
                    public void accept(List<TableCity> tableCities) throws Exception {
                        if (tableCities.size() < 1) return;

                        List<ItemTextPicTemp> temps = new ArrayList<>();
                        long time = new Date().getTime();
                        TableCity updateCity = null;
                        for (TableCity item : tableCities) {

                            if (time - item.timeUpdate > App.getInstance().getTimeSetting()) {
                                updateCity = item;
                            }

                            String city = item.cityName;
                            int pic = App.getInstance().loadPic(item.icon);
                            String temp = item.tempCurrent + CELCIA;
                            ItemTextPicTemp addItem = new ItemTextPicTemp(city, pic, temp);
                            temps.add(addItem);
                        }

                        sourceData.setDataSourceObject(tableCities);
                        sourceData.setDataSource(temps);
                        adapterCityList.notifyDataSetChanged();

                        App.getInstance().updateWeather(updateCity);
                    }
                });
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecycleCityList(DataSourceTextPicTemp sourceData, @NonNull View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_city);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        subscribeToTheListOfCitiesWeather();
        adapterCityList = new AdapterCityList(sourceData);
        recyclerView.setAdapter(adapterCityList);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapterCityList);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator));
        recyclerView.addItemDecoration(itemDecoration);

        adapterCityList.SetOnItemClickListenerItem((view1, position) -> {
            TextView city = view1.findViewById(R.id.item_list_cites);
            modelData.setCityName(city.getText().toString());

        });

        adapterCityList.SetOnItemClickListenerFooter((view1, position) -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setTypeFilter(TypeFilter.CITIES)
                    .build(context);

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) return;
                Place place = Autocomplete.getPlaceFromIntent(data);
                modelData.setCityName(place.getName());
                App.getInstance().requestRetrofitModelOneCallApi(Objects.requireNonNull(place.getLatLng()).latitude,
                        place.getLatLng().longitude);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                assert data != null;
                Status status = Autocomplete.getStatusFromIntent(data);
                assert status.getStatusMessage() != null;
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                return;
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}