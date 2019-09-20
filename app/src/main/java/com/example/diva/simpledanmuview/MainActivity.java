package com.example.diva.simpledanmuview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.diva.simpledanmuview.danmu.FakeDanmuFactory;
import com.example.diva.simpledanmuview.danmu.SimpleDanmuView;


public class MainActivity extends AppCompatActivity {
    SimpleDanmuView simpleDanmuView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleDanmuView = findViewById(R.id.simple_danmu_view);


        findViewById(R.id.pause_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleDanmuView.pause();
            }
        });

        findViewById(R.id.resume_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleDanmuView.resume();
            }
        });
        findViewById(R.id.seek_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleDanmuView.seek(2500);
            }
        });

        findViewById(R.id.hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleDanmuView.hide();
            }
        });
        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleDanmuView.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FakeDanmuFactory.initDate(this);

//        ArrayList<DanmuItem > danmuItems = new ArrayList<>();
//        DanmuItem danmuItem = new DanmuItem("花有重开日,人无再少年，问渠哪得清如许，为有源头活水来。安皇天辽阔，斧高不及毛。",2600);
//            danmuItems.add(danmuItem);
//        simpleDanmuView.setDanmusAndPlay(danmuItems);
        simpleDanmuView.setDanmusAndPlay(FakeDanmuFactory.getComment(150));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleDanmuView.destroy();
    }
}
