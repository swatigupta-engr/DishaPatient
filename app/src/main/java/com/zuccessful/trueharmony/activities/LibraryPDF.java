package com.zuccessful.trueharmony.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.fragments.PdfRenderFragment;

public class LibraryPDF extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_pdf);
        Intent i=getIntent();
        String pdfName,pdfName_hindi;
        if(i!=null)
        {
           // pdfName=i.getStringExtra("filename");
            pdfName_hindi=i.getStringExtra("filename_hindi");
            FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            PdfRenderFragment p= new PdfRenderFragment();
            Bundle bundle = new Bundle();
         //   bundle.putString("filename",pdfName);
            bundle.putString("filename_hindi",pdfName_hindi);

            p.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_content, p).commit();
        }

    }
}
