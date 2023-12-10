package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evanditaWiratamaPutraJBusER.jbus_android.databinding.ActivityMainBinding;
import com.evanditaWiratamaPutraJBusER.jbus_android.databinding.ActivityManageBusBinding;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Account;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.BaseResponse;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Bus;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.BaseApiService;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.UtilsApi;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageBusActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private Handler handler;
    private int currentImageIndex;
    private int[] gradientImages = {R.drawable.gradient_bg9, R.drawable.gradient_bg9_2, R.drawable.gradient_bg9_4, R.drawable.gradient_bg9_3};
    private static final int FADE_DURATION = 3000;
    private Button[] btns;
    private int currentPage = 0;
    private int prevPage = 0;
    private int pageSize = 5; // kalian dapat bereksperimen dengan field ini
    private int listSize;
    private int noOfPages;
    private List<Bus> listBus = new ArrayList<>();
    private Button prevButton = null;
    private Button nextButton = null;
    private ListView busListView = null;
    private HorizontalScrollView pageScroll = null;
    private BaseApiService mApiService;
    public static Context ctx;
    private ManageBusArrayAdapter adapter;
    final private int addbus_menu_id = R.id.addbus_button;
    public static Bus busSelected;

    private Animation slideInLeft;
    private Animation slideInRight;
    private Animation slideOutLeft;
    private Animation slideOutRight;
    private Animation fadeIn, fadeOut;
    private Animation rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bus);

        currentImageIndex = 0;
        linearLayout = findViewById(R.id.managebus_page);
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

        // hubungkan komponen dengan ID nya
        prevButton = findViewById(R.id.managebus_prev_page);
        nextButton = findViewById(R.id.managebus_next_page);
        pageScroll = findViewById(R.id.managebus_page_number_scroll);
        busListView = findViewById(R.id.managebus_listView);

        // membuat sample list
        handleGetMyBus();

        prevButton.startAnimation(slideInLeft);
        nextButton.startAnimation(slideInRight);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                    listSize = listBus.size();
                    // construct the footer
                    paginationFooter();
                    goToPage(currentPage);
            }
        }, 500);

        // listener untuk button prev dan button
        prevButton.setOnClickListener(v -> {
            prevPage = currentPage;
            currentPage = currentPage != 0? currentPage-1 : 0;
            goToPage(currentPage);
        });
        nextButton.setOnClickListener(v -> {
            prevPage = currentPage;
            currentPage = currentPage != noOfPages -1? currentPage+1 : currentPage;
            goToPage(currentPage);
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

    private void paginationFooter() {
        int val = listSize % pageSize;
        val = val == 0 ? 0:1;
        noOfPages = listSize / pageSize + val;
        LinearLayout ll = findViewById(R.id.managebus_btn_layout);
        ll.removeAllViews();
        btns = new Button[noOfPages];
        if (noOfPages <= 6) {
            ((FrameLayout.LayoutParams) ll.getLayoutParams()).gravity = Gravity.CENTER;
        }
        for (int i = 0; i < noOfPages; i++) {
            btns[i]=new Button(this);
            btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
            btns[i].setText(""+(i+1));
            // ganti dengan warna yang kalian mau
            btns[i].setTextColor(getResources().getColor(R.color.white));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100,
                    100);
            btns[i].startAnimation(fadeIn);
            ll.addView(btns[i], lp);

            final int j = i;

            btns[j].setOnClickListener(v -> {
                prevPage = currentPage;
                currentPage = j;
                goToPage(j);
            });

        }
    }

    private void goToPage(int index) {
        for (int i = 0; i< noOfPages; i++) {
            if (i == index) {
                btns[index].setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));
                btns[i].setTextColor(getResources().getColor(android.R.color.white));
                btns[i].startAnimation(rotate);
                scrollToItem(btns[index]);
                viewPaginatedList(listBus, currentPage);
            } else {
                btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
                btns[i].setTextColor(getResources().getColor(android.R.color.white));
            }
        }
    }

    private void scrollToItem(Button item) {
        int scrollX = item.getLeft() - (pageScroll.getWidth() - item.getWidth()) / 2;
        pageScroll.smoothScrollTo(scrollX, 0);
    }

    private void viewPaginatedList(List<Bus> listBus, int page) {
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, listBus.size());
        List<Bus> paginatedList = listBus.subList(startIndex, endIndex);
        // Tampilkan paginatedList ke listview
        // seperti yang sudah kalian lakukan sebelumnya,
        // menggunakan array adapter.
        ;

        if (currentPage > prevPage) {
            busListView.startAnimation(slideOutLeft);
        }
        else if (currentPage < prevPage) {
            busListView.startAnimation(slideOutRight);
        }
        else {
            busListView.startAnimation(fadeOut);
        }

        adapter = new ManageBusArrayAdapter((Context) this, paginatedList);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentPage > prevPage) {
                    busListView.startAnimation(slideInRight);
                }
                else if (currentPage < prevPage) {
                    busListView.startAnimation(slideInLeft);
                }
                else {
                    busListView.startAnimation(fadeIn);
                }
                busListView.setAdapter(adapter);
                busListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        prevPage = currentPage;
                        Intent intent = new Intent(ctx, BusScheduleActivity.class);
                        busSelected = listBus.get((currentPage) * pageSize + i);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    }
                });

            }
        }, 1000);

    }

    protected void handleGetMyBus() {
        // handling empty field
        int idS = LoginActivity.loggedAccount.id;

        mApiService.getMyBus(idS).enqueue(new Callback<BaseResponse<List<Bus>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Bus>>> call, Response<BaseResponse<List<Bus>>> response) {
                // handle the potential 4xx & 5xx error
                if (!response.isSuccessful()) {
                    Toast.makeText(ctx, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<List<Bus>> res = response.body();

                // if success finish this activity (back to login activity)
                if(res.success){
                    List <Bus> tmp = res.payload;
                    listBus.clear();
                    for (Bus i : tmp) {
                        listBus.add(i);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Bus>>> call, Throwable t) {
                //Log.e("NetworkError", "Error: " + t.getMessage(), t);
                Toast.makeText(ctx, "Problem with the server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addbus_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == addbus_menu_id) {
            //animationOut();
            Intent intent = new Intent(this, AddBusActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleGetMyBus();
        prevButton.startAnimation(slideInLeft);
        nextButton.startAnimation(slideInRight);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                listSize = listBus.size();
                // construct the footer
                paginationFooter();
                goToPage(currentPage);
            }
        }, 500);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}