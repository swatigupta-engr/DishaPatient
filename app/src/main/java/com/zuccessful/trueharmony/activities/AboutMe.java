package com.zuccessful.trueharmony.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class AboutMe extends AppCompatActivity {

     ImageView imageViewPdf;
      FloatingActionButton prePageButton;
      FloatingActionButton nextPageButton;
      int api;

    private static final String FILENAME = "userbooklet_patient.pdf";
    private static final String FILENAME_HINDI = "user_booklet_hindi_patient.pdf";
   String filename;

    private int pageIndex;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pdf_render);
         pageIndex = 0;
        api=Integer.valueOf(Build.VERSION.SDK);
         prePageButton= findViewById(R.id.button_pre_doc);
         nextPageButton=findViewById(R.id.button_next_doc);
         imageViewPdf=findViewById(R.id.pdf_image);

         prePageButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 if(api>Build.VERSION_CODES.LOLLIPOP)
                 showPage(currentPage.getIndex() - 1);
             }
         });

         nextPageButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(api>Build.VERSION_CODES.LOLLIPOP)

                     showPage(currentPage.getIndex() + 1);

             }
         });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        try {
            openRenderer(getApplicationContext());
            showPage(pageIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStop() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        File file ;

        String langPrefType  = Utilities.getDataFromSharedpref( getApplicationContext(), Constants.KEY_LANGUAGE_PREF);
        if(langPrefType!=null) {
            int lang = Integer.parseInt(langPrefType);
            Log.v("Lang",langPrefType+" "+lang);

            if(lang==1) {
                //language is hindi
                file = new File(context.getCacheDir(), FILENAME_HINDI);
                filename=FILENAME_HINDI;
             }else{
                //language is english
                file = new File(context.getCacheDir(), FILENAME);
                filename=FILENAME;
             }

        }else{
//            language is english by default
            file = new File(context.getCacheDir(), FILENAME);
            filename=FILENAME;

        }

        Log.v("filename",filename);

        if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            InputStream asset = context.getAssets().open(filename);
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        // This is the PdfRenderer we use to render the PDF.
        if (parcelFileDescriptor != null) {
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeRenderer() throws IOException {
        if (null != currentPage) {
            try{
                currentPage.close();}
            catch (Exception e){}
        }
        pdfRenderer.close();
        parcelFileDescriptor.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != currentPage) {
            try {
                currentPage.close();
            }
            catch (Exception e){}
        }        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // We are ready to show the Bitmap to user.
        imageViewPdf.setImageBitmap(bitmap);
        updateUi();
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateUi() {
        int index = currentPage.getIndex();
        int pageCount = pdfRenderer.getPageCount();
        prePageButton.setEnabled(0 != index);
        nextPageButton.setEnabled(index + 1 < pageCount);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getPageCount() {
        return pdfRenderer.getPageCount();
    }

}