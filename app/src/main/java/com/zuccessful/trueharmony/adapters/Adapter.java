package com.zuccessful.trueharmony.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.zuccessful.trueharmony.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Adapter extends SimpleAdapter {

 private Context mcontext;
 public Adapter(Context context, List<? extends Map<String, ?>> data,
   int resource, String[] from, int[] to) {
  super(context, data, resource, from, to);
  // TODO Auto-generated constructor stub
  mcontext = context;
  
 }
  @SuppressWarnings("unchecked")
  @Override
     public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater object = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         if (convertView == null) 
         {
                 convertView = object.inflate(R.layout.content,
                        null);
            }

            HashMap<String, String> data = (HashMap<String, String>) getItem(position);
            
            
            ((TextView) convertView.findViewById(R.id.tv_content)).setText((String) data.get("key"));
 
            return convertView;
 }
 

 
}