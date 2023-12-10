package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.evanditaWiratamaPutraJBusER.jbus_android.databinding.ActivityBusScheduleBinding;
import com.evanditaWiratamaPutraJBusER.jbus_android.databinding.ActivityMainBinding;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Account;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.BaseResponse;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Bus;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Schedule;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.BaseApiService;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.UtilsApi;

import org.w3c.dom.Text;

import java.util.*;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusScheduleActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private Handler handler;
    private int currentImageIndex;
    private int[] gradientImages = {R.drawable.gradient_bg9, R.drawable.gradient_bg9_2, R.drawable.gradient_bg9_4, R.drawable.gradient_bg9_3};
    private static final int FADE_DURATION = 3000;
    private Animation slideInLeft;
    private Animation slideInRight;
    private Animation slideOutLeft;
    private Animation slideOutRight;
    private Animation fadeIn, fadeOut;
    private Animation rotate;

    private List<Schedule> scheduleList = new ArrayList<>();
    private ListView scheduleListView;
    private TextView busName;
    private Button addSchedule;
    private Bus bus;
    private String selectedTime;
    private String selectedDate;
    public static Schedule scheduleSelected;


    private BaseApiService mApiService;
    public static Context ctx;
    private ScheduleBusArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_schedule);
        getSupportActionBar().hide();

        currentImageIndex = 0;
        linearLayout = findViewById(R.id.bus_schedule_page);
        handler = new Handler();
        animateBackground();

        slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);

        mApiService = UtilsApi.getApiService();
        ctx = this;

        scheduleListView = findViewById(R.id.bus_schedule_listView);
        busName = findViewById(R.id.bus_schedule_bus_name);
        addSchedule = findViewById(R.id.bus_schedule_add_schedule);


        scheduleList = ManageBusActivity.busSelected.schedules;
        adapter = new ScheduleBusArrayAdapter((Context) this, scheduleList);
        scheduleListView.setAdapter(adapter);
        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ctx, RenterPaymentActivity.class);
                scheduleSelected = scheduleList.get(i);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        busName.setText(ManageBusActivity.busSelected.name + " Schedule");

        addSchedule.setOnClickListener(v -> {
            openDatePickerDialog();
        });

    }

    private void animateBackground() {
        Drawable currentDrawable = getResources().getDrawable(gradientImages[currentImageIndex]);
        Drawable nextDrawable = getResources().getDrawable(gradientImages[(currentImageIndex + 1) % gradientImages.length]);

        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{currentDrawable, nextDrawable});
        //transitionDrawable.setCrossFadeEnabled(true);
        transitionDrawable.startTransition(FADE_DURATION);

        linearLayout.setBackground(transitionDrawable);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentImageIndex = (currentImageIndex + 1) % gradientImages.length;
                animateBackground();
            }
        }, FADE_DURATION);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void openTimePickerDialog() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        // Handle the selected time (e.g., display it)
                        selectedTime = selectedHour + ":" + selectedMinute + ":00";
                        handleAddSchedule();
                        // You can use the selected time as needed
                        // For example, display it in a TextView or perform some other action
                        // For now, let's just print it to the console


                    }
                },
                hourOfDay,
                minute,
                true // 24-hour format
        );

        // Show the TimePickerDialog
        timePickerDialog.show();
    }

    public void openDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        // Handle the selected date
                        selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        openTimePickerDialog();

                    }
                },
                year,
                month,
                dayOfMonth
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }



    protected void handleAddSchedule() {
        // handling empty field
        int idS = ManageBusActivity.busSelected.id;
        String timeS = selectedDate + " " + selectedTime;

        mApiService.addSchedule(idS, timeS).enqueue(new Callback<BaseResponse<Bus>>() {
            @Override
            public void onResponse(Call<BaseResponse<Bus>> call, Response<BaseResponse<Bus>> response) {
                // handle the potential 4xx & 5xx error
                if (!response.isSuccessful()) {
                    Toast.makeText(ctx, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<Bus> res = response.body();

                // if success finish this activity (back to login activity)
                if(res.success){
                    int idx = res.payload.schedules.size();
                    ManageBusActivity.busSelected.schedules.add(res.payload.schedules.get(idx - 1));

                    scheduleList = ManageBusActivity.busSelected.schedules;
                    scheduleListView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Bus>> call, Throwable t) {
                //Log.e("NetworkError", "Error: " + t.getMessage(), t);
                Toast.makeText(ctx, "Problem with the server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }




}

