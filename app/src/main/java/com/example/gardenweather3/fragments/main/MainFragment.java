package com.example.gardenweather3.fragments.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardenweather3.activity.App;
import com.example.gardenweather3.DataSourceTextPicTemp;
import com.example.gardenweather3.ItemTextPicTemp;
import com.example.gardenweather3.R;
import com.example.gardenweather3.ViewModelData;
import com.example.gardenweather3.db.RelationCityHoursDaily;
import com.example.gardenweather3.db.TableHours;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


public class MainFragment extends Fragment {
    private DataSourceTextPicTemp sourceData;
    private AdapterLineClock adapterLineClock;
    private App allFunc;
    private final String CELCIA = "\u00B0";
    private CalendarView calendarView;
    private int indexTime = 0;
    private ViewModelData modelData;
    private static final int RC_SIGN_IN = 40404;
    private static final String TAG = "GoogleAuth";
    private GoogleSignInClient googleSignInClient;
    private com.google.android.gms.common.SignInButton buttonSignIn;
    private TextView token;
    private String serverClientId = "652916761979-se5tqldpeoasjb0vrh9ijc28l7mb4chl.apps.googleusercontent.com";
    private MaterialButton buttonSingOut;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allFunc = App.getInstance();
        modelData = ViewModelProviders.of(requireActivity()).get(ViewModelData.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        sourceData = new DataSourceTextPicTemp(getResources());
        initRecycleLineHours(sourceData.buildLineByClock(), view);
        subscribeLoadWeatherOneCity();
        calendarView = view.findViewById(R.id.calendarWeather);
        long nowTime = new Date().getTime();
        calendarView.setMinDate(nowTime);
        calendarView.setMaxDate(nowTime + 7 * 1000 * 60 * 60 * 24);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                indexTime = (int) (calendar.getTimeInMillis() - nowTime + 1000 * 60 * 60 * 24) / 1000 / 60 / 60 / 24;
                subscribeLoadWeatherOneCity();
            }
        });

        initSignInParameters();
        initGui(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        enableSign();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity());
        if (account != null) {
            disableSign();
            token.setText(account.getEmail());
        }
    }

    private void initSignInParameters() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(serverClientId)
                .requestServerAuthCode(serverClientId, false)
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void initGui(View view) {
        token = view.findViewById(R.id.user_token);
        buttonSignIn = view.findViewById(R.id.sign_in_button);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        buttonSingOut = view.findViewById(R.id.sing_out_button);
        buttonSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Выход из учётной записи в приложении
    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        token.setText("email");
                        enableSign();
                    }
                });
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            disableSign();
            token.setText(account.getEmail());
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void enableSign(){
        buttonSignIn.setEnabled(true);
        buttonSingOut.setEnabled(false);
    }

    private void disableSign(){
        buttonSignIn.setEnabled(false);
        buttonSingOut.setEnabled(true);
    }


    @SuppressLint("CheckResult")
    public void subscribeLoadWeatherOneCity() {
        allFunc.getDb().getCurrentHoursDay().oneCityHoursDaily(allFunc.getCurrentCityName())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RelationCityHoursDaily>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void accept(List<RelationCityHoursDaily> relationCityHoursDailies) throws Exception {
                        if (relationCityHoursDailies.size() < 1) return;

                        List<ItemTextPicTemp> temps = new ArrayList<>();

                        for (int i = 3; i < relationCityHoursDailies.get(0).hours.size(); i++) {
                            TableHours item = relationCityHoursDailies.get(0).hours.get(i);
                            Date date = new Date((long) item.dt * 1000);
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
                            String hour = dateFormat.format(date);

                            int pic = allFunc.loadPic(item.icon);

                            String temp = item.tempHour + CELCIA;
                            ItemTextPicTemp addItem = new ItemTextPicTemp(hour, pic, temp);
                            temps.add(addItem);
                        }

                        sourceData.setDataSource(temps);
                        adapterLineClock.notifyDataSetChanged();

                        modelData.setCityData(relationCityHoursDailies.get(0).dailies.get(indexTime));
                    }
                });
    }

    private void initRecycleLineHours(DataSourceTextPicTemp sourceData, @NonNull View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        adapterLineClock = new AdapterLineClock(sourceData);
        recyclerView.setAdapter(adapterLineClock);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }
}