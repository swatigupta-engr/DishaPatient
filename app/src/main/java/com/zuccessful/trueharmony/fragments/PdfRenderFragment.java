package com.zuccessful.trueharmony.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfRenderFragment extends Fragment {
    private ImageView imageViewPdf;
    private FloatingActionButton prePageButton;
    private FloatingActionButton nextPageButton;

    private String filename,filename_hindi ;
    private int pageIndex;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;

    private ImageView mButtonZoomin;
    private ImageView mButtonZoomout;
    private Button mButtonNext;
    private float currentZoomLevel = 12;
     private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";

    public PdfRenderFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
             filename= bundle.getString("filename");
             filename_hindi=bundle.getString("filename_hindi");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_pdf_render, container, false);
        imageViewPdf= v.findViewById(R.id.pdf_image);
        prePageButton=v.findViewById(R.id.button_pre_doc);
        nextPageButton=v.findViewById(R.id.button_next_doc);
        pageIndex = 0;
        mButtonZoomin = v.findViewById(R.id.zoomin);
        mButtonZoomout = v.findViewById(R.id.zoomout);

        mButtonZoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to the next page
                ++currentZoomLevel;
                showPage(currentPage.getIndex());
            }
        });
        mButtonZoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                --currentZoomLevel;
                showPage(currentPage.getIndex());
            }
        });
        prePageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                currentZoomLevel = 12;
                showPage(currentPage.getIndex() - 1);
            }
        });

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                currentZoomLevel = 12;

                showPage(currentPage.getIndex() + 1);
            }
        });
        return v;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != currentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, currentPage.getIndex());
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStart() {
        super.onStart();
        try {
            openRenderer(getContext());
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

        File file =null;


        String langPrefType  = Utilities.getDataFromSharedpref( getActivity().getApplicationContext(), Constants.KEY_LANGUAGE_PREF);
        if(langPrefType!=null) {
            int lang = Integer.parseInt(langPrefType);
            Log.v("Lang",langPrefType+" "+lang);

            if(lang==1) {
                //language is hindi
                file = new File(context.getCacheDir(), filename_hindi);
                filename=filename_hindi;
            }else{
                //language is english
                try{
                file = new File(context.getCacheDir(), filename);}catch (Exception e){}
                //filename=filename;
            }

        }else{
//            language is english by default
            try{
            file = new File(context.getCacheDir(), filename);}
            catch (Exception e){}
            //filename=filename;

        }
        // In this sample, we read a PDF from the assets directory.
     //   File file = new File(context.getCacheDir(), filename);
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
                currentPage.close();
                currentPage = null;
            }
            catch (Exception e){}
        }
        try{
        pdfRenderer.close();
        parcelFileDescriptor.close();}
        catch (Exception e){}
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != currentPage) {
            try{
            currentPage.close();
            currentPage=null;}
            catch (Exception e){}
        }
        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
//        Bitmap bitmap = Bitmap.createBitmap(
//                getResources().getDisplayMetrics().densityDpi * currentPage.getWidth() / 72,
//                getResources().getDisplayMetrics().densityDpi * currentPage.getHeight() / 72,
//                Bitmap.Config.ARGB_8888
//        );
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
        if (currentZoomLevel == 2) {
            mButtonZoomout.setActivated(false);
        } else {
            mButtonZoomout.setActivated(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getPageCount() {
        return pdfRenderer.getPageCount();
    }

}
