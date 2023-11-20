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

import com.evanditaWiratamaPutraJBusER.jbus_android.model.Bus;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private Handler handler;
    private int currentImageIndex;
    private int[] gradientImages = { R.drawable.gradient_bg6, R.drawable.gradient_bg7, R.drawable.gradient_bg7_3, R.drawable.gradient_bg6_3, R.drawable.gradient_bg7_3, R.drawable.gradient_bg7};
    private static final int FADE_DURATION = 5000;
    private ListView bus;
    final private int account_menu_id = R.id.account_button;

    private Button[] btns;
    private int currentPage = 0;
    private int pageSize = 20; // kalian dapat bereksperimen dengan field ini
    private int listSize;
    private int noOfPages;
    private List<Bus> listBus = new ArrayList<>();
    private Button prevButton = null;
    private Button nextButton = null;
    private ListView busListView = null;
    private HorizontalScrollView pageScroll = null;

    private Animation slideInLeft;
    private Animation slideInRight;
    private Animation slideOutLeft;
    private Animation slideOutRight;
    private Animation fadeIn;
    public BusArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentImageIndex = 0;
        linearLayout = findViewById(R.id.main_page);
        handler = new Handler();
        animateBackground();


        slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        bus = findViewById(R.id.listView);

        // hubungkan komponen dengan ID nya
        prevButton = findViewById(R.id.prev_page);
        nextButton = findViewById(R.id.next_page);
        pageScroll = findViewById(R.id.page_number_scroll);
        busListView = findViewById(R.id.listView);
        // membuat sample list
        listBus = Bus.sampleBusList(500);
        listSize = listBus.size();
        // construct the footer
        paginationFooter();
        goToPage(currentPage);
        // listener untuk button prev dan button
        prevButton.setOnClickListener(v -> {
            currentPage = currentPage != 0? currentPage-1 : 0;
            goToPage(currentPage);
        });
        nextButton.setOnClickListener(v -> {
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
            Intent intent = new Intent(this, AboutMeActivity.class);
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
                currentPage = j;
                goToPage(j);
            });
        }
    }

    private void goToPage(int index) {
        for (int i = 0; i< noOfPages; i++) {
            if (i == index) {
                btns[index].setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));
                btns[i].setTextColor(getResources().getColor(android.R.color.black));
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

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bus.startAnimation(fadeIn);
                bus.setAdapter(adapter);
            }
        }, 700);


        adapter = new BusArrayAdapter((Context) this, paginatedList);

        int firstVisiblePosition = bus.getFirstVisiblePosition();
        int lastVisiblePosition = bus.getLastVisiblePosition();

        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            View listItem = bus.getChildAt(i - firstVisiblePosition);

            if (listItem != null) {
                if (i % 2 == 0) {
                    listItem.startAnimation(slideOutLeft);
                }
                else {
                    listItem.startAnimation(slideOutRight);
                }
            }
        }

    }


}