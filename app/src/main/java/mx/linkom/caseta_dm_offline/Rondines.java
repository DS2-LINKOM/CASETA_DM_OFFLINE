package mx.linkom.caseta_dm_offline;

import android.content.Intent;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import mx.linkom.caseta_dm_offline.Controller.PagerControlador;


public class Rondines  extends mx.linkom.caseta_dm_offline.Menu{

    TabLayout tablayout;
    TabItem tabRecibir,tabEstacionar,tabRecoger,tabEntrega;
    ViewPager viewPager;
    PagerControlador pagerAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rondines_lista);


        TabLayout tablayout = (TabLayout) findViewById(R.id.tablayout);
        TabItem tabs1 = (TabItem) findViewById(R.id.tabUbicacion);
        TabItem tabs2 = (TabItem) findViewById(R.id.tabQr);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        pagerAdapter=new PagerControlador(getSupportFragmentManager(),tablayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0){
                    pagerAdapter.notifyDataSetChanged();
                }
                if(tab.getPosition()==1){
                    pagerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
        finish();
    }


}
