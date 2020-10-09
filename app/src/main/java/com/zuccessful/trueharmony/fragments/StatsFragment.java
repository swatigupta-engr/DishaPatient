package com.zuccessful.trueharmony.fragments;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.DatabaseRoom;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.RoomEntity.BasicMeasurementsEntity;
import com.zuccessful.trueharmony.RoomEntity.BloodMeasurementsEntity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {

    private static final String TAG = StatsFragment.class.getSimpleName();
    private LineChart mWeightChart, mBPChart, mSugarChart,mTSHchart,mLipidchart;
    private SakshamApp app;
    private FirebaseFirestore db;
    private Patient patient;
    long startTime;
    long endTime;
    String label;
    private List<BasicMeasurementsEntity> basic;
    private List<BloodMeasurementsEntity> blood;
    Boolean flag=false;
    public StatsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        mWeightChart = view.findViewById(R.id.weight_chart);
        mBPChart= view.findViewById(R.id.bp_chart);
        mLipidchart= view.findViewById(R.id.lipid_chart);
        mTSHchart= view.findViewById(R.id.tsh_chart);
        mSugarChart= view.findViewById(R.id.sugar_chart);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseRoom database = DatabaseRoom.getInstance(getContext());
                basic = database.BasicMeasurementsRecords().getBasicMeasurementsRecords();
                Log.d("saumya", " size of BASIC arraylist : " + basic.size());

                blood = database.bloodMeasurementsRecords().getBloodMeasurementsRecords();
                Log.d("saumya ", " size of Blood arraylist : " + blood.size());
                flag=true;
            }
        });

        while(!flag)
        {
            Log.d("saumya "," flag is false");
        }
     //   try {

            label=getResources().getString(R.string.label_bp);
            createBPChart( mBPChart,label);

            label=getResources().getString(R.string.label_cholestrol);

            createChart(Constants.KEY_CHOLESTEROL_LIST, mLipidchart,label);

            label=getResources().getString(R.string.label_tsh);

            createChart(Constants.KEY_TSH_LIST, mTSHchart,label);

            label=getResources().getString(R.string.label_sugar);

            createChart(Constants.KEY_SUGAR_LIST, mSugarChart,label);

            label=getResources().getString(R.string.label_weight);
            createChart(Constants.KEY_WEIGHT_lIST, mWeightChart,label);

        /*  } catch (Exception e) { Log.i("ChartException", "Error in creaintg chart : " + e); }*/



        return view;
    }

    private void createBPChart(final LineChart chart, final String label)
    {
        Log.d("saumya"," in BP create chart");
        final ArrayList<String> dates = new ArrayList<>();
        final ArrayList<Double> values1 = new ArrayList<>();
        final ArrayList<Double> values2 = new ArrayList<>();
      //  HashMap<String,ArrayList<Double>> measurements= Utilities.getMeasurements(Constants.KEY_BP_lIST);

        for (BasicMeasurementsEntity be : basic) {
            dates.add(be.getDate());
            try{
            if(be.getBp_high()!=null&& be.getBp_low()!=null)
            values1.add(Double.parseDouble(be.getBp_high()));
            values2.add(Double.parseDouble(be.getBp_low()));}
            catch (Exception e){}
        }

        Log.d("saumya"," value1: "+values1+"\nvalues2: "+values2+"\ndates: "+dates);



//        for(Map.Entry<String,ArrayList<Double>> entry : measurements.entrySet())
//        {
//            dates.add(entry.getKey());
//            values1.add(entry.getValue().get(0));
//            values2.add(entry.getValue().get(1));
//        }
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        chart.getAxisRight().setEnabled(false);
        chart.animateX(2500);

//        xAxis.setValueFormatter(new AxisDateValueFormatter(dates));
        xAxis.setGranularity(1.0f);
        ArrayList<Entry> data1 = new ArrayList<>();
        ArrayList<Entry> data2 = new ArrayList<>();
        for (int i = 0; i < values1.size(); i++) {
            data1.add(new Entry(i, values1.get(i).floatValue()));
            data2.add(new Entry(i, values2.get(i).floatValue()));
        }
        Log.d("chart","data1: "+data1+" data2: "+data2);
        LineDataSet set1;
        LineDataSet set2;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            Log.d("chart","in if");
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(data1);

            set2= (LineDataSet) chart.getData().getDataSetByIndex(0);
            set2.setValues(data2);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            Log.d("chart","in else");
            set1 = new LineDataSet(data1,getResources().getString(R.string.bp_high_label));
            set2= new LineDataSet(data2,getResources().getString(R.string.bp_low_label));
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.RED);
            set1.setCircleColor(Color.RED);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setFillColor(Color.RED);

            set2.setDrawIcons(false);
            set2.enableDashedLine(10f, 5f, 0f);
            set2.enableDashedHighlightLine(10f, 5f, 0f);
            set2.setColor(Color.CYAN);
            set2.setCircleColor(Color.CYAN);
            set2.setLineWidth(1f);
            set2.setCircleRadius(3f);
            set2.setDrawCircleHole(false);
            set2.setValueTextSize(9f);
            set2.setDrawFilled(true);
            set2.setFormLineWidth(1f);
            set2.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set2.setFormSize(15.f);
            set2.setFillColor(Color.CYAN);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            if(set1!=null && set2!=null)
            {
                if(data1.size()!=0&& data2.size()!=0) {
                    Log.d("saumya", "---------NOT NULL");
                    dataSets.add(set1); // add the datasets
                    dataSets.add(set2);
                    LineData datas = new LineData(dataSets);
                    chart.setData(datas);
                }
            }


        }
    }

    private void createChart(final String testName, final LineChart chart, final String label)
    {
        Log.d("saumya"," in create chart");
   //     HashMap<String,ArrayList<Double>> measurements= Utilities.getMeasurements(testName);
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Double> values = new ArrayList<>();
       /* try {
            if (label.equals(getResources().getString(R.string.label_weight))) {
                for (BasicMeasurementsEntity be : basic) {
                    dates.add(be.getDate());
                    values.add(Double.parseDouble(be.getWeight()));
                }

            }
            setChartData(chart, values, dates,label);

        }catch (Exception e){
            Log.v("exception in wight","e..");
        }*/

       try{
        if (label.equals(getResources().getString(R.string.label_weight))) {
            for (BasicMeasurementsEntity be : basic) {
                Log.v("data",be.toString());

                dates.add(be.getDate());
                if( be.getWeight()!=null)
                values.add(Double.parseDouble(be.getWeight()));
            }

        }}
       catch (Exception e){
           e.printStackTrace();
       }
        setChartData(chart, values, dates,label);
        try{
          if(label.equals(getResources().getString(R.string.label_cholestrol)))
        {
            for (BloodMeasurementsEntity be : blood)
            {
                dates.add(be.getDate());
                if(be.getCholesterol()!=null)
                values.add(Double.parseDouble(be.getCholesterol()));
                Log.v("excptn no:",be.getCholesterol());
            }
        }
            setChartData(chart, values, dates,label);
        }
        catch (Exception e){
            Log.v("exception in cholestrol","e..");

        }

        try {
            if (label.equals(getResources().getString(R.string.label_tsh))) {
                for (BloodMeasurementsEntity be : blood) {
                    dates.add(be.getDate());
                    if(be.getTsh()!=null)
                    values.add(Double.parseDouble(be.getTsh()));
                }
            }
            setChartData(chart, values, dates,label);

        }catch (Exception e){
            Log.v("exception in tsh","e..");


        }
        try {
            if (label.equals(getResources().getString(R.string.label_sugar))) {
                for (BloodMeasurementsEntity be : blood) {
                    dates.add(be.getDate());
                    if(be.getSugar()!=null)
                    values.add(Double.parseDouble(be.getSugar()));
                }
            }
            setChartData(chart, values, dates,label);

        }catch (Exception e){
            Log.v("exception insugar","e..");

        }
        Log.d("saumya"," values: "+values+"\ndates: "+dates);

//        for(Map.Entry<String,ArrayList<Double>> entry : measurements.entrySet())
//        {
//            dates.add(entry.getKey());
//            values.add(entry.getValue().get(0));
//        }



    }

    private void setChartData(LineChart chart, ArrayList<Double> dataDouble, ArrayList<String> dates, String label) {
        Log.d("saumya"," in set chartData");
        if (dataDouble == null || dataDouble.size() == 0)
        {
            return;
        }

        chart.setDrawGridBackground(false);

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // mHeightChart.setScaleXEnabled(true);
        // mHeightChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        // mHeightChart.setBackgroundColor(Color.GRAY);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        chart.getAxisRight().setEnabled(false);
        chart.animateX(2500);

      //  xAxis.setValueFormatter(new AxisDateValueFormatter(dates));
        xAxis.setGranularity(1.0f);
        ArrayList<Entry> data = new ArrayList<>();
        for (int i = 0; i < dataDouble.size(); i++) {
            Log.d("saumya", String.valueOf(i + " : " + dates.get(i)));
            String arr=dates.get(i).replaceAll("-","");
            Log.d("saumya", String.valueOf(i + " : " + dates.get(i))+" "+arr);
            data.add(new Entry(i, dataDouble.get(i).floatValue()));
        }
        LineDataSet set1;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(data);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(data, label);
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setFillColor(Color.RED);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets
            // create a data object with the datasets
            LineData datas = new LineData(dataSets);
            // set data
            chart.setData(datas);
        }
    }


}
