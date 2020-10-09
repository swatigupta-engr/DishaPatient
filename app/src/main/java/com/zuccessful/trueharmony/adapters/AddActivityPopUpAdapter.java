package com.zuccessful.trueharmony.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddActivityPopUpAdapter  extends RecyclerView.Adapter<AddActivityPopUpAdapter.ViewHolder>
{
    ArrayList<String>list;
    private LayoutInflater mInflater;
    Context c;
    String type;
    ArrayList<String> phyActList, basicActList;
    public AddActivityPopUpAdapter(Context context, ArrayList<String> list,String type)
    {
        this.list=list;
        mInflater=LayoutInflater.from(context);
        c=context;
        this.type=type;
        phyActList = Utilities.getListFromSharedPref(Constants.KEY_PHY_ACT_LIST);
        basicActList = Utilities.getListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST);
        if (phyActList == null) {
            phyActList = new ArrayList<>();
        }

        if (basicActList == null) {
            basicActList = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public AddActivityPopUpAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = mInflater.inflate(R.layout.popup_items, viewGroup, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddActivityPopUpAdapter.ViewHolder viewHolder, int i)
    {
        String name = list.get(i);
        viewHolder.activityName.setText(name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView activityName;
        ViewHolder(View itemView)
        {
            super(itemView);
            activityName= itemView.findViewById(R.id.popup);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
              activityName=  view.findViewById(R.id.popup);
              String name=activityName.getText().toString();
              Log.d("saumya",name+"");
              if (name.equalsIgnoreCase("other"))
              { addNewAddPhysicalActDialoge(type);
                  Toast.makeText(c, R.string.activity_added, Toast.LENGTH_SHORT).show();}
              else if (type == "physical" && phyActList.contains(name))
              { Toast.makeText(c, R.string.activity_already_added, Toast.LENGTH_SHORT).show(); }
              else if (type == "basic" && basicActList.contains(name))
              { Toast.makeText(c, R.string.activity_already_added, Toast.LENGTH_SHORT).show(); }
              else
                  {
                      Toast.makeText(c, R.string.activity_added, Toast.LENGTH_SHORT).show();
                      savePhysicalActData(name, type);
                  }
        }

        private void addNewAddPhysicalActDialoge(final String type) {
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle(R.string.enter_activity_name);
            final EditText input = new EditText(c);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    savePhysicalActData(m_Text, type);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }

        private void savePhysicalActData(String activity, String type) {
            ArrayList<String> actList = new ArrayList<>();
            if (type == "physical") {
                actList = phyActList;
            } else if (type == "basic") {
                actList = basicActList;
            }
            if (actList != null) {
                actList.add(activity);
            } else {
                actList = new ArrayList<>();
                actList.add(activity);
            }
            if (type == "physical") {
                Utilities.saveListToSharedPref(actList, Constants.KEY_PHY_ACT_LIST);
                Map<String, Object> data = new HashMap<>();
                data.put("name", activity);

            } else if (type == "basic") {
                Utilities.saveListToSharedPref(actList, Constants.KEY_LEISURE_ACT_LIST);
                Map<String, Object> data = new HashMap<>();
                data.put("name", activity);
            }
            Log.d("saumya", "Updated lists: " + Utilities.getListFromSharedPref(Constants.KEY_PHY_ACT_LIST) + "\n" + Utilities.getListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST));
        }

    }
}
