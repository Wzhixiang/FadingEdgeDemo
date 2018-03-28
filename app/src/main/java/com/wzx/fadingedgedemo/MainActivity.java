package com.wzx.fadingedgedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.wzx.fadingedgedemo.widget.FadingEdgeRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FadingEdgeRecyclerView mRv;
    private StringAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRv = findViewById(R.id.fadingEdgeRV);

        mRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("名字很长的" + i);
        }
        mAdapter = new StringAdapter(list);

        mRv.setAdapter(mAdapter);
    }
}
