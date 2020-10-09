package com.zuccessful.trueharmony.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.adapters.AddActivityPopUpAdapter;

import java.util.ArrayList;

public class AddActivityPopUpFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    ArrayList<String> b_list, p_list;
    RecyclerView b_acts,p_acts;
    LinearLayoutManager linearLayoutManager1, linearLayoutManager2;

    public AddActivityPopUpFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linearLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager2= new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_add_activity_pop_up, container, false);
       b_list=getArguments().getStringArrayList("basic");
       p_list=getArguments().getStringArrayList("physical");
       b_acts=v.findViewById(R.id.basic_activities);
       p_acts=v.findViewById(R.id.physical_activities);
       b_acts.setLayoutManager(linearLayoutManager1);
       p_acts.setLayoutManager(linearLayoutManager2);
       AddActivityPopUpAdapter adapter1= new AddActivityPopUpAdapter(getContext(),b_list,"basic");
       b_acts.setAdapter(adapter1);
        AddActivityPopUpAdapter adapter2= new AddActivityPopUpAdapter(getContext(),p_list,"physical");
        p_acts.setAdapter(adapter2);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else { throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener"); }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
