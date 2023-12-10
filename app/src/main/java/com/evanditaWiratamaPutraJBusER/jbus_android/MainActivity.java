package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.evanditaWiratamaPutraJBusER.jbus_android.model.BaseResponse;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Bus;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.BaseApiService;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private Handler handler;
    private int currentImageIndex;
    private int[] gradientImages = { R.drawable.gradient_bg6, R.drawable.gradient_bg7, R.drawable.gradient_bg7_3, R.drawable.gradient_bg6_3, R.drawable.gradient_bg7_3, R.drawable.gradient_bg7};
    private static final int FADE_DURATION = 5000;
    final private int account_menu_id = R.id.account_button;
    final private int payment_menu_id = R.id.balance_button;

    private Button[] btns;
    private int currentPage = 0;
    private int prevPage = 0;
    private int pageSize = 5; // kalian dapat bereksperimen dengan field ini
    private int listSize;
    private int noOfPages;
    public static List<Bus> listBus = new ArrayList<>();
    private Button prevButton = null;
    private Button nextButton = null;
    private ListView busListView = null;
    private HorizontalScrollView pageScroll = null;

    private Animation slideInLeft;
    private Animation slideInRight;
    private Animation slideOutLeft;
    private Animation slideOutRight;
    private Animation fadeIn, fadeOut;
    private Animation rotate;
    private BaseApiService mApiService;
    public static Context ctx;
    public BusArrayAdapter adapter;
    public static Bus busSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentImageIndex = 0;
        linearLayout = findViewById(R.id.main_page);
        handler = new Handler();
        animateBackground();

        mApiService = UtilsApi.getApiService();
        ctx = this;

        slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        // hubungkan komponen dengan ID nya
        prevButton = findViewById(R.id.prev_page);
        nextButton = findViewById(R.id.next_page);
        pageScroll = findViewById(R.id.page_number_scroll);
        busListView = findViewById(R.id.listView);

        prevButton.startAnimation(slideInLeft);
        nextButton.startAnimation(slideInRight);

        // membuat sample list
        handleGetAllBus();

        prevButton.startAnimation(slideInLeft);
        nextButton.startAnimation(slideInRight);

        // construct the footer
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

    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == account_menu_id) {
            //animationOut();
            prevPage = currentPage;
            Intent intent = new Intent(this, AboutMeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        else if (item.getItemId() == payment_menu_id) {
            //animationOut();
            prevPage = currentPage;
            Intent intent = new Intent(this, BuyerPaymentActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
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
        LinearLayout ll = findViewById(R.id.btn_layout);
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

        adapter = new BusArrayAdapter((Context) this, paginatedList);


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
                        Intent intent = new Intent(ctx, BusDetailActivity.class);
                        busSelected = listBus.get((currentPage) * pageSize + i);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    }
                });
            }
        }, 1000);



    }

    protected void handleGetAllBus() {

        mApiService.getAllBus().enqueue(new Callback<BaseResponse<List<Bus>>>() {
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
    protected void onResume() {
        super.onResume();
        handleGetAllBus();
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

}