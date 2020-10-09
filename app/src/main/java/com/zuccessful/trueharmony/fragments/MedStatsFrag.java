package com.zuccessful.trueharmony.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.zuccessful.trueharmony.DatabaseRoom;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.RoomEntity.MedicineProgressEntity;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//import com.zuccessful.trueharmony.activities./s;

public class MedStatsFrag extends Fragment  {
    private BarChart chart;
    private ScatterChart scatterChart;
    private Spinner medSpinner, monthSpinner;
    String currentMonthInt;
    HashMap<Integer,HashMap<String,ArrayList<Integer>>> medProgress=new HashMap<>();
    HashMap<String, HashMap<String,Integer>> current_month;
    String selectedMed;
    HashMap<String,HashMap<String,ArrayList<Integer>>> map=new HashMap<>();
    List<MedicineProgressEntity> mList;
    HashMap<String,HashMap<String,HashMap<String,Integer>>> mapp=new HashMap<>();
    boolean flag=false;

    public MedStatsFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {


        View view = inflater.inflate(R.layout.fragment_med_stats, container, false);
        medSpinner = view.findViewById(R.id.med_selector);
        monthSpinner= view.findViewById(R.id.month_selector_med);
        chart= view.findViewById(R.id.stacked_bar_med);
        scatterChart= view.findViewById(R.id.scatter_chart);

        String[] arraySpinner = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item, arraySpinner);
        monthSpinner.setAdapter(adapter);
        monthSpinner.setSelection(0);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                currentMonthInt=getMonthInt(selected);
                makeBarChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayList<Medication> medications = Utilities.getListOfMedication();
        if(medications==null)
        {
            medications=new ArrayList<>();
        }
        String[] arraySpinner2 = new String[medications.size()];
        for(int i=0;i<medications.size();i++)
        {
            arraySpinner2[i]= medications.get(i).getName();
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item, arraySpinner2);
        medSpinner.setAdapter(adapter2);
        medSpinner.setSelection(0);
        if(arraySpinner2.length>1) {
            selectedMed = arraySpinner2[0];
        }
        medSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMed = adapterView.getItemAtPosition(i).toString();
                makeScatterChart(selectedMed);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseRoom database = DatabaseRoom.getInstance(getContext());
                mList = database.medicineProgressRecords().getMedicineProgressRecords();
                Log.d("bargraph", " size of med progress arraylist : " + mList.size());
                flag=true;
            }
        });
        while(!flag)
        {
            Log.d("bargraph","---");
        }


        for(MedicineProgressEntity me:mList)
        {
            String d= me.getDate();
            String medName= me.getMedicineName();
            Boolean status= me.getTaken();
            String month= d.split("-")[0];

            if(mapp.containsKey(month)) //entry for month exists
            {
                HashMap<String,HashMap<String,Integer>> temp= mapp.get(month);
                if(temp.containsKey(medName)) //entry for med exists in a given month
                {
                    HashMap<String,Integer> vals=temp.get(medName);
                    if(status==true)
                    {
                        vals.put("taken",vals.get("taken")+1);
                    }
                    else
                    {
                        vals.put("missed",vals.get("missed")+1);
                    }
                    temp.put(medName,vals);
                }
                else //no entry for med
                {
                    HashMap<String,Integer> vals=new HashMap<>();
                    if(status==true)
                    {
                        vals.put("taken",1);
                        vals.put("missed",0);
                    }
                    else
                    {
                        vals.put("missed",1);
                        vals.put("taken",0);
                    }
                    temp.put(medName,vals);
                }
                mapp.put(month,temp);
            }
            else  //entry for month doesnot exist
            {
                HashMap<String,HashMap<String,Integer>> temp=new HashMap<>();
                HashMap<String,Integer> vals=new HashMap<>();

                if(status==true)
                {
                    vals.put("taken",1);
                    vals.put("missed",0);
                }
                else
                {
                    vals.put("missed",1);
                    vals.put("taken",0);
                }
                temp.put(medName,vals);
                mapp.put(month,temp);
            }
        }

        Log.d("bargraph", "final mapp"+mapp);

       map= Utilities.getMedProgress();
//        Log.d("MAPPP", map.toString());

        if(map==null)
       {
           map=new HashMap<>();
       }
        makeScatterChart(selectedMed );
        return view;
    }
    private void makeScatterChart(String medName)
    {
        final ArrayList<String> dates= new ArrayList<>();
        //final ArrayList<Date> dateslist=new ArrayList<>();
         scatterChart.getDescription().setEnabled(false);

        scatterChart.setDrawGridBackground(true);
        scatterChart.setTouchEnabled(false);
     //   chart.setMaxHighlightDistance(50f);

        // enable scaling and dragging
        scatterChart.setDragEnabled(true);
        scatterChart.setScaleEnabled(false);

       // scatterChart.setMaxVisibleValueCount(200);
        scatterChart.setPinchZoom(true);

        Legend l = scatterChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setYOffset(5f);


        ArrayList<Entry> values1 = new ArrayList<>();
        ArrayList<Entry> values2 = new ArrayList<>();

        final ArrayList<String> time=new ArrayList<>();
         ArrayList<Medication> meds= Utilities.getListOfMedication();
        if(meds==null)
        {
            meds=new ArrayList<>();
        }
        ArrayList<String> reminders=null;
        Log.d("scatterGraph","med name is "+medName);
        for(Medication m:meds)
        {
            if(m.getName().equals(medName))
            {

                reminders=m.getReminders();

            }
        }
         for(int i=0;i<mList.size();i++){
Log.d("list",mList.get(i).getMedicineName()+"______"+mList.get(i).getDate()+"_____________"+mList.get(i).getTime()+"_________"+mList.get(i).getTaken());
if(mList.get(i).getMedicineName().equalsIgnoreCase(medName))
            time.add(mList.get(i).getTime());

        }

 Log.v("TIMEEE",time+"");
  //   time=reminders;
        Log.d("scatterGrpah"," map is "+map);

        int d=0;
        for(Map.Entry m1:map.entrySet())
        {
            HashMap<String,ArrayList<Integer>> t=(HashMap)m1.getValue();

            Log.d("Hashmap:",t.get(medName)+"");
            if(t.containsKey(medName) )
            {
                dates.add((String)m1.getKey());

//                dateslist.add((Date)m1.getKey());
                ArrayList<Integer> list= t.get(medName);

                Log.d("Arraylist::",list.size()+"");
                for(int i=0;i<list.size();i++)
                {

                    Log.d("Arraylist::",list.get(i).toString()+"");

                    if(list.get(i)==1)
                    {
                        Log.d("scatterGraph"," d is "+d);
                        values1.add(new Entry(d,i));
                    }
                    else
                    {
                        Log.d("scatterGraph"," d is "+d);
                        values2.add(new Entry(d, i));
                    }

                }
                d=d+1;
            }

        }

        Log.d("scatterGraph"," TAKEN : "+values1+"\nMISSED"+values2);

/*        dates.clear();
        dates.add("06-14-20");
        dates.add("06-19-20");
        dates.add("05-12-20");
        dates.add("05-11-20");
       */
        Log.d("scatterGraph"," dates : "+dates);
        ArrayList<String>dates_sorted=null;
        try{
            dates_sorted=sortDates(dates);
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d("sorted",dates_sorted+"");

        XAxis xaxis = scatterChart.getXAxis();
        xaxis.setDrawGridLines(false);
       // xaxis.setAxisMinimum(-1f);
        xaxis.setGranularity(1f);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xaxis.setValueFormatter(new IndexAxisValueFormatter(dates_sorted));
        YAxis yaxis = scatterChart.getAxisLeft();
        scatterChart.getAxisRight().setEnabled(false);

        yaxis.setGranularity(1f);
        yaxis.setDrawGridLines(true);
        yaxis.setDrawZeroLine(true);
          Log.d("scatterGraph"," time: "+time);
        Log.d("scatterGraph"," values1:taken "+values1);
        Log.d("scatterGraph"," values2:missed "+values2);


        Log.v("Without duplicate count",removeDuplicates(time)+"______________"+
                removeDuplicates(time).size()+"");
 /*
if(count==1 && time.size()>1)
      //  yaxis.setLabelCount(count,true);

else yaxis.setLabelCount(time.size(),false);*/

     //  yaxis.removeAllLimitLines();


        yaxis.setValueFormatter(new IndexAxisValueFormatter(removeDuplicates(time)));

         ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        // create a dataset and give it a type
        if(values1.size()>0) {
            ScatterDataSet set1 = new ScatterDataSet(values1, "TAKEN");
            set1.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
            set1.setColor(ColorTemplate.COLORFUL_COLORS[3]);
            set1.setScatterShapeHoleColor(ColorTemplate.COLORFUL_COLORS[3]);
            set1.setScatterShapeHoleRadius(3f);
            set1.setScatterShapeSize(8f);
           dataSets.add(set1);
        }
        if(values2.size()>0)
        {
            ScatterDataSet set2 = new ScatterDataSet(values2, "MISSED");
            set2.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
            set2.setScatterShapeHoleRadius(3f);
            set2.setColor(ColorTemplate.COLORFUL_COLORS[0]);
            set2.setScatterShapeHoleColor(ColorTemplate.COLORFUL_COLORS[0]);
            set2.setScatterShapeSize(8f);
            dataSets.add(set2);
        }

        Log.v("data sets",dataSets.toString());
        ScatterData data = new ScatterData(dataSets);
        data.setDrawValues(false);



        scatterChart.setData(data);
        scatterChart.invalidate();

    }/*

    private void makeBarChart() {
        float groupSpace = 0.2f;
        float barSpace = 0.02f;
        float barWidth = 0.15f;
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
       // chart.setTouchEnabled(true);
      //  chart.setDragEnabled(true);
      //  chart.setScaleEnabled(true);
     //   chart.setPinchZoom(true);
        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
      //  xAxis.setCenterAxisLabels(true);
        xAxis.setTextSize(12);
        xAxis.setAxisMinimum(0f);

        xAxis.setLabelRotationAngle(-90);
        xAxis.setCenterAxisLabels(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(40f);
        //leftAxis.setDrawZeroLine(true);


        chart.getAxisRight().setEnabled(false);
        ArrayList<String> uniqueMeds = new ArrayList<>();
        // get unique acts in current month
//        if (medProgress.containsKey(currentMonthInt) == true) {
//            current_month = medProgress.get(12);
//            for (Map.Entry m : current_month.entrySet()) {
//                uniqueMeds.add((String) m.getKey());
//            }
//        }
        Log.d("bargraph","---->"+currentMonthInt);
        if (mapp.containsKey(currentMonthInt) == true) {
            Log.d("bargraph","---->"+currentMonthInt+" found");
            current_month = mapp.get(currentMonthInt);
            for (Map.Entry m : current_month.entrySet()) {
                uniqueMeds.add((String) m.getKey());
            }
        }

        Log.d("barGraph", " unique meds:  " + uniqueMeds.size());

        ArrayList<BarEntry> taken = new ArrayList<>();
        ArrayList<BarEntry> missed = new ArrayList<>();
        for (int i = 0; i < uniqueMeds.size(); i++)
        {
            taken.add(new BarEntry(i, current_month.get(uniqueMeds.get(i)).get("taken")));
//            if(current_month.get(uniqueMeds.get(i)).size()>1) {
            missed.add(new BarEntry(i, current_month.get(uniqueMeds.get(i)).get("missed")));
//            }
//            else
//            {
//                missed.add(new BarEntry(i,0));
//            }
        }

        Log.d("barGraph"," taken: "+taken+"\nmissed: "+missed);


        BarDataSet set1, set2;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart.getData().getDataSetByIndex(1);

            set1.setValues(taken);
            set2.setValues(missed);
            chart.setFitBars(true);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(taken, "Taken");
            int startColor3 = ContextCompat.getColor(getContext(), android.R.color.holo_red_dark);
            int startColor4 = ContextCompat.getColor(getContext(), android.R.color.holo_green_dark);
            set1.setColor(startColor4);
            set2 = new BarDataSet(missed, "Missed");
            set2.setColor(startColor3);
            BarData data = new BarData(set1, set2);
            chart.setFitBars(true);
            chart.setData(data);
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(uniqueMeds));

    //    xAxis.setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace));
        chart.getBarData().setBarWidth(barWidth);
        chart.groupBars(0, groupSpace, barSpace);
        chart.invalidate();


    }*/

    private void makeBarChart() {
        float groupSpace = 0.2f;
        float barSpace = 0.02f;
        float barWidth = 0.15f;
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        //chart.setTouchEnabled(true);
        // chart.setDragEnabled(true);
        //  chart.setScaleEnabled(true);
        // chart.setPinchZoom(true);
        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        //xAxis.setAvoidFirstLastClipping(true);
        //  xAxis.setCenterAxisLabels(true);
        xAxis.setTextSize(12);
        xAxis.setAxisMinimum(0f);


        xAxis.setLabelRotationAngle(-90);
        xAxis.setCenterAxisLabels(true);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawZeroLine(true);
        //leftAxis.setSpaceTop(20f);
        //  leftAxis.setDrawZeroLine(true);


        chart.getAxisRight().setEnabled(false);
        ArrayList<String> uniqueMeds = new ArrayList<>();
        // get unique acts in current month
//        if (medProgress.containsKey(currentMonthInt) == true) {
//            current_month = medProgress.get(12);
//            for (Map.Entry m : current_month.entrySet()) {
//                uniqueMeds.add((String) m.getKey());
//            }
//        }
        Log.d("bargraph","---->"+currentMonthInt);
        if (mapp.containsKey(currentMonthInt) == true) {
            Log.d("bargraph","---->"+currentMonthInt+" found");
            current_month = mapp.get(currentMonthInt);
            for (Map.Entry m : current_month.entrySet()) {
                uniqueMeds.add((String) m.getKey());
            }
        }

        Log.d("barGraph", " unique meds:  " + uniqueMeds.size());

        ArrayList<BarEntry> taken = new ArrayList<>();
        ArrayList<BarEntry> missed = new ArrayList<>();
        for (int i = 0; i < uniqueMeds.size(); i++)
        {
            taken.add(new BarEntry(i, current_month.get(uniqueMeds.get(i)).get("taken")));
//            if(current_month.get(uniqueMeds.get(i)).size()>1) {
            missed.add(new BarEntry(i, current_month.get(uniqueMeds.get(i)).get("missed")));
//            }
//            else
//            {
//                missed.add(new BarEntry(i,0));
//            }
        }

        Log.d("barGraph"," taken: "+taken+"\nmissed: "+missed);


        BarDataSet set1, set2;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart.getData().getDataSetByIndex(1);

            set1.setValues(taken);
            set2.setValues(missed);
            chart.setFitBars(true);

            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(taken, "Taken");
            int startColor3 = ContextCompat.getColor(getContext(), android.R.color.holo_red_dark);
            int startColor4 = ContextCompat.getColor(getContext(), android.R.color.holo_green_dark);
            set1.setColor(startColor4);
            set2 = new BarDataSet(missed, "Missed");
            set2.setColor(startColor3);
            BarData data = new BarData(set1, set2);
            data.setDrawValues(false);

            chart.setFitBars(true);

            chart.setData(data);
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(uniqueMeds));

        //    xAxis.setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace));
        //chart.getBarData().setBarWidth(barWidth);
        // chart.groupBars(0, groupSpace, barSpace);
        chart.invalidate();


    }
    public String getMonth(int m)
    {
        switch(m)
        {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "Aug";
            case 9:
                return "Sept";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
        }
        return "";
    }

    public String getMonthInt(String m)
    {
        switch(m)
        {
            case "January":
                return "01";
            case "February":
                return "02";
            case "March":
                return "03";
            case "April":
                return "04";
            case "May":
                return "05";
            case "June":
                return "06";
            case "July":
                return "07";
            case "August":
                return "08";
            case "September":
                return "09";
            case "October":
                return "10";
            case "November":
                return "11";
            case "December":
                return "12";
        }
        return "";
    }


    private ArrayList<String> sortDates(ArrayList<String> dates) throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("MM-dd-yy");

        Map <Date, String> dateFormatMap = new TreeMap<>();
        for (String date: dates)
            dateFormatMap.put(f.parse(date), date);
        return new ArrayList<>(dateFormatMap.values());
    }
    // Function to remove duplicates from an ArrayList
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }
/*    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            // Refresh your fragment here
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(MedStatsFrag.this).attach(MedStatsFrag.this).commit();
        }
    }*/
}
