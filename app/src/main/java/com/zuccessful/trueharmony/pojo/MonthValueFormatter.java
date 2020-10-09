package com.zuccessful.trueharmony.pojo;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

public class MonthValueFormatter implements IAxisValueFormatter
{
        ArrayList<String> list;
        public MonthValueFormatter(ArrayList<String>l)
        {
            list=l;
        }


    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        Log.d("saumya","---"+value+" "+axis.mAxisRange);
        return list.get((int)(value));
    }
}


