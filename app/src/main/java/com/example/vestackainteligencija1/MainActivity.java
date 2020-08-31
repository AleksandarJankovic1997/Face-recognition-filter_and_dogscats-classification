package com.example.vestackainteligencija1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends FragmentActivity {
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FaceFilterFragment fragment;
    private MyCustomModelMLKITFragment myCustomModelMLKITFragment=new MyCustomModelMLKITFragment();


    private int trenutniTab=0;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout=findViewById(R.id.tablayout_id);
        fragment=new FaceFilterFragment();
        fm=getSupportFragmentManager();
        ft=fm.beginTransaction();
        ft.add(R.id.activityFrameLayout,fragment).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(trenutniTab!=tab.getPosition()){
                    if(tab.getPosition()==1){
                        ft=fm.beginTransaction();
                        ft.replace(R.id.activityFrameLayout,myCustomModelMLKITFragment);
                        trenutniTab=1;
                    }
                    else if(tab.getPosition()==0) {
                        ft=fm.beginTransaction();
                        ft.replace(R.id.activityFrameLayout,fragment);
                        trenutniTab=0;
                    }
                    ft.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (trenutniTab == 0) {
            fragment.onActivityResult(requestCode, resultCode, data);

        } else if (trenutniTab == 1) {
            myCustomModelMLKITFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
