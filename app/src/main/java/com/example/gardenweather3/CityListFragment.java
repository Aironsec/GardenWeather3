package com.example.gardenweather3;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

interface AfterTextChangedWatcher extends TextWatcher {
    default void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    default void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
}

public class CityListFragment extends Fragment {
    private ViewModelData modelData;
    private Pattern checkCity = Pattern.compile("[a-zA-Z]*");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelData = ViewModelProviders.of(requireActivity()).get(ViewModelData.class);
    }

    private void initRecycleCityList(DataSourceTextPicTemp sourceData, View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_city);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        AdapterCityList adapter = new AdapterCityList(sourceData);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator));
        recyclerView.addItemDecoration(itemDecoration);

        FragmentManager fragmentManager = getFragmentManager();
        adapter.SetOnItemClickListener((view1, position) -> {
            assert fragmentManager != null;
            fragmentManager.popBackStack();
            TextView textView = view1.findViewById(R.id.textView_city);

            modelData.setCity(textView.getText().toString());

            Snackbar.make(textView, "Город изменён", Snackbar.LENGTH_LONG)
                    .setAction("Отменить",
                            v -> {
                                TempData td = TempData.getInstance();
                                modelData.setCity(td.getTempStr());
                            }).show();

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_list, container, false);

        DataSourceTextPicTemp sourceData = new DataSourceTextPicTemp(getResources());
        initRecycleCityList(sourceData.buildCityList(), view);

        TextInputEditText textInput = view.findViewById(R.id.inputCity);
        textInput.addTextChangedListener(
                (AfterTextChangedWatcher) editable -> CityListFragment.this.validate(textInput, checkCity));

        return view;
    }

    private void validate(TextView tv, Pattern check) {
        String value = tv.getText().toString();
        if (check.matcher(value).matches()) {
            hideError(tv);
        } else {
            showError(tv);
        }
    }

    private void showError(TextView view) {
        view.setError("Только латинские буквы!!");
    }

    private void hideError(TextView view) {
        view.setError(null);
    }

}