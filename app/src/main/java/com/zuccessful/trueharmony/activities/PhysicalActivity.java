package com.zuccessful.trueharmony.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.fragments.PdfRenderFragment;
import com.zuccessful.trueharmony.fragments.SarthiFrag;
import com.zuccessful.trueharmony.utilities.Utilities;

public class PhysicalActivity extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:


                    return true;
                case R.id.navigation_info:
                   /* PdfRenderFragment p= new PdfRenderFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("filename","physical.pdf");
                    bundle.putString("filename_hindi","physical_activity_hindi.pdf");

                    p.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_content, p).commit();
*/

                    Intent pIntent = new Intent(PhysicalActivity.this, PDFRenderActivity.class);
                    pIntent.putExtra("filename","physical.pdf");
                    pIntent.putExtra("filename_hindi","physical_activity_hindi.pdf");

                    startActivity(pIntent);
                    return true;
                case R.id.navigation_progress:
                    return true;
                case R.id.navigation_sarthi:
                    Bundle b1=new Bundle();
                    b1.putString("saarthi",getApplicationContext().getString(R.string.physical_saarthi));
                    SarthiFrag f = new SarthiFrag();
                    f.setArguments(b1);
                    fragmentTransaction.replace(R.id.fragment_content, f).commit();
                    return true;
            }
            return false;

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.getMenu().getItem(2).setVisible(false);
        navigation.getMenu().getItem(0).setVisible(false);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportActionBar().setTitle(getResources().getString(R.string.physical_health));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

       /* PdfRenderFragment p= new PdfRenderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("filename","physical.pdf");
        bundle.putString("filename_hindi","physical_activity_hindi.pdf");

        p.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_content, p).commit();*/
        Intent pIntent = new Intent(PhysicalActivity.this, PDFRenderActivity.class);
        pIntent.putExtra("filename","physical.pdf");
        pIntent.putExtra("filename_hindi","physical_activity_hindi.pdf");

        startActivity(pIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }}

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }

}
