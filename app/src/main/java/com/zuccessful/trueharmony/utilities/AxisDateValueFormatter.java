package com.zuccessful.trueharmony.utilities;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

public class AxisDateValueFormatter implements IAxisValueFormatter {

    private static final String TAG = AxisDateValueFormatter.class.getSimpleName();
    private ArrayList<String> dates;

    public AxisDateValueFormatter(ArrayList<String> dates) {
        this.dates = dates;
    }


    @Override
    public String getFormattedValue(float value, AxisBase axis)
    {
        return dates.get((int)value);
    }
}
