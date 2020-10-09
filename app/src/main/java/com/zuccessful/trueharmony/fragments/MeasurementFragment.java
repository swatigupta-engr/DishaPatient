package com.zuccessful.trueharmony.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.wajahatkarim3.longimagecamera.LongImageCameraActivity;
import com.wajahatkarim3.longimagecamera.PreviewLongImageActivity;
import com.zuccessful.trueharmony.DatabaseRoom;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.RoomEntity.BasicMeasurementsEntity;
import com.zuccessful.trueharmony.RoomEntity.BloodMeasurementsEntity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.LabInvestigation;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.CustomImageCamera;
import com.zuccessful.trueharmony.utilities.Utilities;

import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.zuccessful.trueharmony.utilities.CustomImageCamera.REPORT_NAME;


public class MeasurementFragment extends Fragment {

    private static final String TAG = MeasurementFragment.class.getSimpleName();
    private EditText heightEditText, weightEditText, otherEditText, lipidValueEt, bloodSugarEt, tshEt, waistEt, bloodPressureEt;
    private ProgressBar loadStateProgressBar;
    private LinearLayout testView;
    private TextView testUnitView, bmiTv, lipidMsgTv, bloodSugarMsgTv, tshMsgTv, bpMsgTv,bmimsgTv;
    private Spinner testSpinner;
    private Button submitCommon, submitOther, submitBloodTestButton, addReportsButton;
    private SakshamApp app;
    private Patient patient;
    private FirebaseFirestore db;
    private SimpleDateFormat sdf;
    private ArrayList<LabInvestigation> labInvestigations;
    private ArrayAdapter<String> spinnerAdapter;
    private Context mContext;
    DecimalFormat decimalFormat = new DecimalFormat(".##");
    long startTime;
    long endTime;
    String bp_l="";
    String bp_h="";

     TextView error_alert;
    private Map<String, String> reportMap;

    private static final double NORMAL_BMI_LOWER = 18.5;
    private static final double NORMAL_BMI_UPPER = 25;



    private static final int NORMAL_BP_LOW_LOWER = 120;
    private static final int NORMAL_BP_LOW_UPPER = 80;


    private static final int NORMAL_BP_UP_LOWER =60;
    private static final int NORMAL_BP_UP_UPPER = 80;

    private static final int BP_LOWER_CUTOFF = 140;
    private static final int BP_UPPER_CUTOFF = 90;

    private static final int BP_NORMAL_SYSTOLIC_LOW=90;
    private static final int BP_NORMAL_SYSTOLIC_HIGH=139;

    private static final int BP_NORMAL_DIASTOLIC_LOW=60;
    private static final int BP_NORMAL_DIASTOLIC_HIGH=89;


    private static final int BP_HIGH_SYSTOLIC=140;
    private static final int BP_HIGH_DIASTOLIC=90;

    private static final int BP_LOW_SYSTOLIC=89;
    private static final int BP_LOW_DIASTOLIC=59;

    private static final int NORMAL_LIPID_LOWER = 0;
    private static final int NORMAL_LIPID_UPPER = 240;

    private static final int NORMAL_DIABETES_LOWER = 32;
    private static final int NORMAL_DIABETES_UPPER = 32;

    private static final double NORMAL_TSH_LOWER = 0.5;
    private static final double NORMAL_TSH_UPPER = 5;

     private static final int NORMAL_BLOOD_SUGAR_UPPER = 200;


    private static final int NORMAL_CHOLESTROL_BORDER_LOW = 200;
    private static final int NORMAL_CHOLESTROL_BORDER_HIGH = 239;

    private static final int NORMAL_CHOLESTROL_HIGH = 240;
    private LinearLayout phyActLinearLayout;


    public MeasurementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_measurement, container, false);
        startTime = System.currentTimeMillis();
        mContext = getContext();
        labInvestigations = new ArrayList<>();
        app = SakshamApp.getInstance();
        patient = app.getAppUser(null);
        db = app.getFirebaseDatabaseInstance();
        heightEditText = view.findViewById(R.id.height_value);
        bmiTv = view.findViewById(R.id.bmi_tv);
        bpMsgTv = view.findViewById(R.id.bp_msg_tv);
        weightEditText = view.findViewById(R.id.weight_value);
        otherEditText = view.findViewById(R.id.test_value);

        bloodSugarEt = view.findViewById(R.id.blood_sugar_value);
        tshEt = view.findViewById(R.id.tsh_value);
        lipidValueEt = view.findViewById(R.id.lipid_value);

        waistEt = view.findViewById(R.id.waist_value);
        bloodPressureEt = view.findViewById(R.id.bp_value);

        loadStateProgressBar = view.findViewById(R.id.other_test_load_state);
        testView = view.findViewById(R.id.test_view);
        testSpinner = view.findViewById(R.id.test_type);

        testUnitView = view.findViewById(R.id.test_unit);
        submitCommon = view.findViewById(R.id.submit_common_values);
        submitOther = view.findViewById(R.id.submit_other_value);
        submitBloodTestButton = view.findViewById(R.id.button_submit_blood_test);
        addReportsButton = view.findViewById(R.id.add_reports_button);

        lipidMsgTv = view.findViewById(R.id.lipid_msg_tv);
        tshMsgTv = view.findViewById(R.id.tsh_msg_tv);
        bloodSugarMsgTv = view.findViewById(R.id.blood_sugar_msg_tv);

        phyActLinearLayout = view.findViewById(R.id.phy_act_linear_layout);

        bmimsgTv=view.findViewById(R.id.bmi_msg_tv);
        error_alert= view.findViewById(R.id.alert);
        reportMap = loadMap();
        if(reportMap==null){
            reportMap = new HashMap<>();
        }


        final String height = Utilities.getDataFromSharedpref(getContext(), Constants.KEY_HEIGHT);
        final String weight = Utilities.getDataFromSharedpref(getContext(), Constants.KEY_WEIGHT);
        final String waist = Utilities.getDataFromSharedpref(getContext(), Constants.KEY_WAIST);
        String bp = Utilities.getDataFromSharedpref(getContext(), Constants.KEY_BP);
        String cholesterol = Utilities.getDataFromSharedpref(getContext(), Constants.KEY_CHOLESTEROL);
        final String tsh = Utilities.getDataFromSharedpref(getContext(), Constants.KEY_TSH);
        final String sugar = Utilities.getDataFromSharedpref(getContext(), Constants.KEY_SUGAR);

        if (height != null) heightEditText.setText(height);
        if (weight != null) weightEditText.setText(weight);
        if (waist != null) waistEt.setText(waist);
        if (bp != null) bloodPressureEt.setText(bp);
        if (cholesterol != null) lipidValueEt.setText(cholesterol);
        if (tsh != null) tshEt.setText(tsh);
        if (sugar != null) bloodSugarEt.setText(sugar);
               Double bmi = getbmi(weight, height);
        if (bmi != null) {
            bmiTv.setVisibility(View.VISIBLE);
            bmimsgTv.setVisibility(View.VISIBLE);
            bmiTv.setText(getResources().getString(R.string.bmi) +" : " + decimalFormat.format(bmi));
            if(bmi<NORMAL_BMI_LOWER){
                // Toast.makeText(app, "Your BMI is lower than normal.", Toast.LENGTH_SHORT).show();
                bmimsgTv.setText(getResources().getString(R.string.bmi_low));
                bmimsgTv.setTextColor(Color.RED);
            }else if(bmi>NORMAL_BMI_UPPER){
                //Toast.makeText(app, "Your BMI is higher than normal.", Toast.LENGTH_SHORT).show();
                bmimsgTv.setText(getResources().getString(R.string.bmi_high));
                bmimsgTv.setTextColor(Color.RED);


            }
            else {
                //    Toast.makeText(app, "Your BMI is normal.", Toast.LENGTH_SHORT).show();
                bmimsgTv.setText(getResources().getString(R.string.bmi_normal));

            }
        }

        sdf = Utilities.getSimpleDateFormat();
        final String t_date=sdf.format(new Date());

        submitCommon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Clicked Common Button");
                final String heightStr = heightEditText.getText().toString();
                final String weightStr = weightEditText.getText().toString();
                final String waistStr = waistEt.getText().toString();
                final String bloodPressuretStr = bloodPressureEt.getText().toString();

               String bp_split[]= bloodPressuretStr.split("\\/");

               Log.v("BP split:",bp_split.length+"---"+bp_split);

                  if(heightStr.length()>0&& weightStr.length()>0&&waistStr.length()>0&&bloodPressuretStr.length()>0 && bp_split.length==2) {

                    Double bmi = getbmi(weightStr, heightStr);
                    if (bmi != null) {
                        bmiTv.setVisibility(View.VISIBLE);
                        bmimsgTv.setVisibility(View.VISIBLE);
                        bmiTv.setText(getResources().getString(R.string.bmi) + " : " + decimalFormat.format(bmi));
                        if (bmi < NORMAL_BMI_LOWER) {
                            // Toast.makeText(app, "Your BMI is lower than normal.", Toast.LENGTH_SHORT).show();
                            bmimsgTv.setText(getResources().getString(R.string.bmi_low));
                            bmimsgTv.setTextColor(Color.RED);
                        } else if (bmi > NORMAL_BMI_UPPER) {
                            //Toast.makeText(app, "Your BMI is higher than normal.", Toast.LENGTH_SHORT).show();
                            bmimsgTv.setText(getResources().getString(R.string.bmi_high));
                            bmimsgTv.setTextColor(Color.RED);


                        } else {
                            //    Toast.makeText(app, "Your BMI is normal.", Toast.LENGTH_SHORT).show();
                            bmimsgTv.setText(getResources().getString(R.string.bmi_normal));

                        }
                    }
                    error_alert.setText("");


                try
                {
                    final double height = Double.parseDouble(heightStr);
                    final double weight = Double.parseDouble(weightStr);

                    if(height>0.0){
                        Utilities.saveDataInSharedpref(getContext(), Constants.KEY_HEIGHT, String.valueOf(height));
                        ArrayList<Double> record=new ArrayList<>();
                        record.add(weight);
                   //     Utilities.saveMeasurements(Constants.KEY_WEIGHT_lIST,t_date,record);
                    }
                    if(weight>0.0)
                    {  Utilities.saveDataInSharedpref(getContext(), Constants.KEY_WEIGHT,String.valueOf(weight));}
//                    Map<String, Object> today_date = new HashMap<>();
//                    SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
//                    today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
//                    db.collection("patient_records_dates/"+app.getPatientID()+"/dates").document("dates").set(today_date);

                    Double newBmi = getbmi(weightStr, heightStr);
                    if (newBmi != null)
                    { bmiTv.setVisibility(View.VISIBLE);
                      bmiTv.setText(getResources().getString(R.string.bmi) +" : " + decimalFormat.format(newBmi));
                    }
                } catch (Exception e) { e.printStackTrace(); }


                try
                { String bloodPressure = bloodPressuretStr;
                  String []bp = bloodPressure.split("\\/");
                  bp_l=bp[0];
                  bp_h=bp[1];
                  double bloodPressure_low = Double.parseDouble(bp[0]);
                  double bloodPressure_high = Double.parseDouble(bp[1]);
                  Log.d("saumya",bloodPressure_low+" "+bloodPressure_high);
                  if (bloodPressure_low > 0.0)
                  {
                      Utilities.saveDataInSharedpref(getContext(), Constants.KEY_BP, bloodPressure);
/*
                      if ((bloodPressure_low < NORMAL_BP_UP_UPPER)||(bloodPressure_high < NORMAL_BP_UP_LOWER))
                      {
                            bpMsgTv.setVisibility(View.VISIBLE);
                            bpMsgTv.setText(getResources().getString(R.string.bp_low));
                            bpMsgTv.setTextColor(Color.RED);
                      }
                      else if ((bloodPressure_low > BP_LOWER_CUTOFF)||(bloodPressure_high > BP_UPPER_CUTOFF))
                      {
                            bpMsgTv.setVisibility(View.VISIBLE);
                            bpMsgTv.setText(getResources().getString(R.string.bp_high));
                            bpMsgTv.setTextColor(Color.RED);
                      }
                      else  if ((bloodPressure_low>=NORMAL_BP_LOW_UPPER &&bloodPressure_low<= NORMAL_BP_LOW_LOWER)&& bloodPressure_high>=NORMAL_BP_UP_LOWER&&(bloodPressure_high <= NORMAL_BP_LOW_UPPER)) {
                            bpMsgTv.setVisibility(View.VISIBLE);
                            bpMsgTv.setText(getResources().getString(R.string.bp_normal));
                          bpMsgTv.setTextColor(Color.parseColor("#065535"));
                        }*/


                      if ((bloodPressure_low >=BP_HIGH_SYSTOLIC)||(bloodPressure_high >= BP_HIGH_DIASTOLIC))
                      {
                          bpMsgTv.setVisibility(View.VISIBLE);
                          bpMsgTv.setText(getResources().getString(R.string.bp_high));
                          bpMsgTv.setTextColor(Color.RED);
                      }

                     else if ((bloodPressure_low <=BP_LOW_SYSTOLIC)||(bloodPressure_high < BP_LOW_DIASTOLIC))
                      {
                          bpMsgTv.setVisibility(View.VISIBLE);
                          bpMsgTv.setText(getResources().getString(R.string.bp_low));
                          bpMsgTv.setTextColor(Color.RED);
                      }

                      else  if ((bloodPressure_low>=BP_NORMAL_SYSTOLIC_LOW &&bloodPressure_low<= BP_NORMAL_SYSTOLIC_HIGH)&& bloodPressure_high>=BP_NORMAL_DIASTOLIC_LOW&&(bloodPressure_high <= BP_NORMAL_DIASTOLIC_HIGH)) {
                          bpMsgTv.setVisibility(View.VISIBLE);
                          bpMsgTv.setText(getResources().getString(R.string.bp_normal));
                          bpMsgTv.setTextColor(Color.parseColor("#065535"));
                      }
                        // PUT INTO LOG
                        ArrayList<Double> record=new ArrayList<>();
                        record.add(bloodPressure_low);
                        record.add(bloodPressure_high);
                    //    Utilities.saveMeasurements(Constants.KEY_BP_lIST,t_date,record);

                    }
                } catch (Exception e) { e.printStackTrace();}

                try {
                    double waist = Double.parseDouble(waistStr);
                    if (waist > 0.0) {
                        Utilities.saveDataInSharedpref(getContext(), Constants.KEY_WAIST, String.valueOf(waist));
                } }catch (Exception e) { e.printStackTrace();}
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                final String t_date=sdf.format(new Date());
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseRoom database = DatabaseRoom.getInstance(getContext());
                        Log.d("Logg", " " + t_date + " " + heightStr + " " + weightStr+" "+waistStr+" "+bp_l+" "+bp_h);
                        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                        String t_date = sdf.format(new Date());

                        int chk=0;
                        if(weightStr!=null&&heightStr!=null&& waistStr!=null&& bp_h!=null&&bp_l!=null){
                            chk=1;
                        }

                        if(chk==1){
                        BasicMeasurementsEntity be= new BasicMeasurementsEntity(t_date,heightStr,weightStr,waistStr,bp_l,bp_h);
                        database.BasicMeasurementsRecords().addBasicMeasurementsRecord(be);
                        List<BasicMeasurementsEntity> ret = database.BasicMeasurementsRecords().getBasicMeasurementsRecords();
                        Log.d("Logg", " size of retrieved arraylist : " + ret.size());

                        }


                     }
                });}
                else{
                    if(bp_split.length<2 && heightStr.length()>0&& weightStr.length()>0&&waistStr.length()>0&&bloodPressuretStr.length()>0 ){
                        error_alert.setTextColor(Color.RED);
                        error_alert.setText("Incorrect BP!");
                    }
                    else{
                    error_alert.setTextColor(Color.RED);
                    error_alert.setText("Incomplete data.Kindly enter all fields and then submit!");}

                }
            }
        });


        submitBloodTestButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { final String lipidStr = lipidValueEt.getText().toString();
              final String tshStr = tshEt.getText().toString();
              final String bloodSugarStr = bloodSugarEt.getText().toString();

              if(lipidStr.length()>0&& tshStr.length()>0&&bloodSugarStr.length()>0) {
                  try {
                      double lipid = Double.parseDouble(lipidStr);
                      if (lipid > 0.0) {
                          Utilities.saveDataInSharedpref(getContext(), Constants.KEY_CHOLESTEROL, String.valueOf(lipid));
                          if (lipid < NORMAL_LIPID_LOWER) {
                              lipidMsgTv.setVisibility(View.VISIBLE);
                              lipidMsgTv.setText(getResources().getString(R.string.lipid_low));
                              lipidMsgTv.setTextColor(Color.RED);
                          } else if (lipid > NORMAL_LIPID_UPPER) {
                              lipidMsgTv.setVisibility(View.VISIBLE);
                              lipidMsgTv.setText(getResources().getString(R.string.lipid_high));
                              lipidMsgTv.setTextColor(Color.RED);
                          } else if (lipid > NORMAL_CHOLESTROL_BORDER_LOW && lipid < NORMAL_CHOLESTROL_BORDER_HIGH) {
                              lipidMsgTv.setVisibility(View.VISIBLE);
                              lipidMsgTv.setText(getResources().getString(R.string.lipid_high_border));
                              lipidMsgTv.setTextColor(Color.RED);
                          } else if (lipid < NORMAL_CHOLESTROL_BORDER_LOW) {
                              lipidMsgTv.setVisibility(View.VISIBLE);
                              lipidMsgTv.setText(getResources().getString(R.string.lipid_normal));
                              lipidMsgTv.setTextColor(Color.parseColor("#065535"));
                          }
                          // PUT INTO LOG
                          ArrayList<Double> record = new ArrayList<>();
                          record.add(lipid);
                          //  Utilities.saveMeasurements(Constants.KEY_CHOLESTEROL_LIST, t_date, record);
                      }

                  } catch (Exception e) {
                      e.printStackTrace();
                  }
                  error_alert.setText("");


                    try
                    {
                        double tsh = Double.parseDouble(tshStr);
                        if(tsh>0.0) {
                            Utilities.saveDataInSharedpref(getContext(), Constants.KEY_TSH, String.valueOf(tsh));
                            if (tsh < NORMAL_TSH_LOWER) {
                                tshMsgTv.setVisibility(View.VISIBLE);
                                tshMsgTv.setText(getResources().getString(R.string.tsh_low));
                                tshMsgTv.setTextColor(Color.RED);
                            } else if (tsh > NORMAL_TSH_UPPER) {
                                tshMsgTv.setVisibility(View.VISIBLE);
                                tshMsgTv.setText(getResources().getString(R.string.tsh_high));
                                tshMsgTv.setTextColor(Color.RED);
                            } else {
                                tshMsgTv.setVisibility(View.VISIBLE);
                                tshMsgTv.setText(getResources().getString(R.string.tsh_normal));
                                tshMsgTv.setTextColor(Color.parseColor("#065535"));
                            }
                            // PUT INTO LOG
                            ArrayList<Double> record = new ArrayList<>();
                            record.add(tsh);
                           // Utilities.saveMeasurements(Constants.KEY_TSH_LIST, t_date, record);
                        }

                    } catch (Exception e) {e.printStackTrace(); }

                    try {
                        double bloodSugar = Double.parseDouble(bloodSugarStr);
                        if(bloodSugar>0.0) {
                            Utilities.saveDataInSharedpref(getContext(), Constants.KEY_SUGAR, String.valueOf(bloodSugar));
                            if (bloodSugar <= NORMAL_BLOOD_SUGAR_UPPER && bloodSugar>0) {
                                bloodSugarMsgTv.setVisibility(View.VISIBLE);
                                bloodSugarMsgTv.setText(getResources().getString(R.string.sugar_normal));
                                 bloodSugarMsgTv.setTextColor(Color.parseColor("#065535"));
                            } else if (bloodSugar > NORMAL_BLOOD_SUGAR_UPPER) {
                                bloodSugarMsgTv.setVisibility(View.VISIBLE);
                                bloodSugarMsgTv.setText(getResources().getString(R.string.sugar_high));
                                bloodSugarMsgTv.setTextColor(Color.RED);
                            } else {
                                bloodSugarMsgTv.setVisibility(View.VISIBLE);
                                bloodSugarMsgTv.setText(getResources().getString(R.string.sugar_low));
                                bloodSugarMsgTv.setTextColor(Color.RED);

                            }
                            // PUT INTO LOG
                            ArrayList<Double> record = new ArrayList<>();
                            record.add(bloodSugar);
                         //   Utilities.saveMeasurements(Constants.KEY_SUGAR_LIST, t_date, record);
                        }


                    } catch (Exception e) { e.printStackTrace(); }
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseRoom database = DatabaseRoom.getInstance(getContext());
                        Log.d("Logg", " " + t_date + " " + lipidStr + " " + tshStr+" "+bloodSugarStr);
                        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                        String t_date = sdf.format(new Date());
                        int chk=0;
                        if(tshStr!=null &&bloodSugarStr!=null&& lipidStr!=null){
                            chk=1;
                        }

                        if(chk==1) {
                            BloodMeasurementsEntity be = new BloodMeasurementsEntity(t_date, lipidStr, tshStr, bloodSugarStr);
                            database.bloodMeasurementsRecords().addBloodMeasurementsRecord(be);
                            List<BloodMeasurementsEntity> ret = database.bloodMeasurementsRecords().getBloodMeasurementsRecords();
                           // Log.d("Logg", " size of retrieved arraylist : dsate" + ret.get(0).getDate() + "\n tsh " + ret.get(0).getTsh() + "\n cholstrol" + ret.get(0).getCholesterol() + "\n sugar" + ret.get(0).getSugar());
                         }




                    }
                });}
              else{
                  error_alert.setTextColor(Color.RED);
                  error_alert.setText("Incomplete data.Kindly enter all fields and then submit!");
              }
            }
        });


//        db.collection("lab_investigations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//
//                    // TODO: Make a custom adapter to simplify this process
//                    final ArrayList<String> testNames = new ArrayList<>();
//                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
//                        LabInvestigation l = snapshot.toObject(LabInvestigation.class);
//                        l.setId(snapshot.getId());
//                        testNames.add(l.getName());
//                        labInvestigations.add(l);
//                    }
//
//                    spinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, testNames);
//                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    testSpinner.setAdapter(spinnerAdapter);
//
//                    loadStateProgressBar.setVisibility(View.GONE);
//                    testView.setVisibility(View.VISIBLE);
//                }
//            }
//        });

//        testSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                testUnitView.setText(labInvestigations.get(i).getUnit());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        submitOther.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int i = testSpinner.getSelectedItemPosition();
//                String valueStr = otherEditText.getText().toString();
//                if (valueStr.equals("")) {
////                    Toast.makeText(mContext, "Enter some value before submitting!!!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                try {
//                    Double value = Double.valueOf(valueStr);
//                    if (value >= 0.0) {
//                        Map<String, Double> data = new HashMap<>();
//                        data.put(labInvestigations.get(i).getId(), value);
//                        db.collection("records/" + patient.getId() + "/patient").document(sdf.format(new Date()))
//                                .set(data, SetOptions.merge())
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
////                                        Toast.makeText(mContext, "Successfully Uploaded Test.", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                        Map<String, Object> today_date = new HashMap<>();
//                        SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
//                        today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
//                        db.collection("patient_records_dates/"+app.getPatientID()+"/dates").document("dates").set(today_date);
//                    } else {
//                        Toast.makeText(mContext, getResources().getString(R.string.valid_value), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(mContext, getResources().getString(R.string.valid_value), Toast.LENGTH_SHORT).show();
//                }
//                otherEditText.setText("");
//            }
//        });


        addReportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSelectReportTypeDialog(getResources().getStringArray(R.array.report_types));
            }
        });

        for (Map.Entry<String,String> entry : reportMap.entrySet()) {
            addtoPhysicalActivityLayout(entry.getKey());
        }

        return view;
    }

    private void createSelectReportTypeDialog(final String[] phyActChoices) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.pick_report));

        builder.setItems(phyActChoices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              System.out.println("Choice  : "+ phyActChoices[which]+"Others");
                String others = phyActChoices[which];

                if (which == 3) {

//                  Toast.makeText(getContext(), "Choice  : "+ phyActChoices[which], Toast.LENGTH_SHORT).show();

                   createOthersDialog();
                }
                    else{

                   // Toast.makeText(getContext(), "Choice  : " + phyActChoices[which], Toast.LENGTH_SHORT).show();
                    String currentTime = getCurrentTimeStamp();
                    //CustomImageCamera.launch(getActivity(),phyActChoices[which]+"_"+currentTime);


                    addtoPhysicalActivityLayout(phyActChoices[which]+"_"+currentTime);

                    Intent ii = new Intent(getActivity(), CustomImageCamera.class);
                    ii.putExtra(REPORT_NAME,phyActChoices[which]+"_"+currentTime);
                    getActivity().startActivityForResult(ii, 1234);


                }

            }
        });

        builder.show();
    }

    private void createOthersDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.others_spec));

        final EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);


        builder.setPositiveButton(getResources().getString(R.string.submit_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String testrep = input.getText().toString();
                        String currentTime = getCurrentTimeStamp();
                        CustomImageCamera.launch(getActivity(),testrep+"_"+currentTime);
                        addtoPhysicalActivityLayout(testrep+"_"+currentTime);
                    }
                });

        builder.show();
    }
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    private void addtoPhysicalActivityLayout(final String activity) {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View view = inflater.inflate(R.layout.activity_user_profile, null, false);

        final View inflatedLayout= inflater.inflate(R.layout.textview_with_edit, (ViewGroup) view, false);
        ((TextView)inflatedLayout.findViewById(R.id.physical_activity_tv_edit)).setText(activity);
        Log.d("MeasurementFragment", "addtoPhysicalActivityLayout: " + activity);
        ( inflatedLayout.findViewById(R.id.physical_activity_tv_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(reportMap.get(((TextView) inflatedLayout.findViewById(R.id.physical_activity_tv_edit)).getText().toString()))) {
                   /* Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setType("image/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/

                    Intent i=new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    Log.v("path",imageFileName);
                    i.setDataAndType(Uri.fromFile(new File(imageFileName)), "image/*");
                    startActivity(i);

                } else {
                    Log.d("MeasurementFragment", "onClick: Delete clicked");
                    Intent ii = new Intent(getActivity(), PreviewLongImageActivity.class);
                    ii.putExtra("imageName",
                            reportMap.get(((TextView) inflatedLayout.findViewById(R.id.physical_activity_tv_edit)).getText().toString()));
                    saveMap(reportMap);
                    startActivity(ii);
                }
            }
        });
     /*   //gb: commented out the code for delete icon in textview_with_delete.xml, userprofileactivity.java, measurementfragment.java
        (inflatedLayout.findViewById(R.id.physical_activity_tv_edit)).setVisibility(View.VISIBLE);
        (inflatedLayout.findViewById(R.id.physical_activity_tv_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    reportMap.remove(((TextView) inflatedLayout.findViewById(R.id.physical_activity_tv_edit)).getText().toString());
                   // saveMap(reportMap);
                    //phyActLinearLayout.removeView(inflatedLayout);

                if (TextUtils.isEmpty(reportMap.get(((TextView) inflatedLayout.findViewById(R.id.physical_activity_tv_edit)).getText().toString()))) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setType("image/*");
                    Log.v("path",imageFileName);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Log.d("MeasurementFragment", "onClick: Delete clicked");
                    Intent ii = new Intent(getActivity(), PreviewLongImageActivity.class);
                    ii.putExtra("imageName",
                            reportMap.get(((TextView) inflatedLayout.findViewById(R.id.physical_activity_tv_edit)).getText().toString()));
                    saveMap(reportMap);
                    startActivity(ii);
                }
            }
        });*/

//        (inflatedLayout.findViewById(R.id.edit_physical_activity_button)).setVisibility(View.VISIBLE);
//            (inflatedLayout.findViewById(R.id.edit_physical_activity_button)).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                        //open DailyRoutingActivity and pass this activity as a parameter to edit it
//                }
//            });

        phyActLinearLayout.addView(inflatedLayout);

    }

    private Double getbmi(String weight, String height) {
        if (height == null || weight == null) return null;
        try {
            return (Double.parseDouble(weight) / Math.pow(Double.parseDouble(height), 2)) * 10000;
        } catch (Exception e) {
            return null;
        }
    }

    String imageFileName;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == LongImageCameraActivity.LONG_IMAGE_RESULT_CODE && data != null) {
            imageFileName = data.getStringExtra(LongImageCameraActivity.IMAGE_PATH_KEY);
            reportMap.put(data.getStringExtra(REPORT_NAME),imageFileName);
            Log.e(TAG, "onActivityResult: " + imageFileName);
        }
    }

    private void saveMap(Map<String,String> inputMap){
        SharedPreferences pSharedPref = getContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("My_map").commit();
            editor.putString("My_map", jsonString);
            editor.commit();
        }
    }

    private Map<String,String> loadMap(){
        Map<String,String> outputMap = new HashMap<String,String>();
        SharedPreferences pSharedPref = getContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("My_map", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    String value = (String) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }
    private interface  FirestroreCallBack{
        void onCallBack(Long time);
    }
    protected void fetchObject(final FirestroreCallBack firestroreCallBack){



//        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
//
//        final DocumentReference documentReference;
//        documentReference = db.collection("time_spent/")
//                .document(app.getAppUser(null)
//                        .getId())
//                .collection(sdf.format(new Date())).document("Physical Health Monitor Fragment");
//
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot documentSnapshot=task.getResult();
//                    Activity_Usage activity_usage = documentSnapshot.toObject(Activity_Usage.class);
//                    if(activity_usage==null){
//                        long time = 0;
//                        firestroreCallBack.onCallBack(time);
//                        return;
//                    }
//                    Log.d(TAG, "oldtime on database : " + Long.toString(activity_usage.getTime()));
//                    firestroreCallBack.onCallBack(activity_usage.getTime());
//
//                }else{
//                    Log.d(TAG,"Error: Can't get Activity_Usage",task.getException());
//                }
//
//            }
//
//        });

    }
    @Override
    public void onStop()
    {
        endTime = System.currentTimeMillis();
        final long timeSpend = endTime - startTime;
        //update time
//        fetchObject(new FirestroreCallBack() {
//            @Override
//            public void onCallBack(Long totaltime) {
//                totaltime += timeSpend;
//                //change format of time
//                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totaltime),
//                        TimeUnit.MILLISECONDS.toMinutes(totaltime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totaltime)),
//                        TimeUnit.MILLISECONDS.toSeconds(totaltime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totaltime)));
//
//
//                //create object
//                Activity_Usage au = new Activity_Usage(hms,totaltime,"Physical Health Monitor Fragment");
//                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
//
//                //add to database
//                try {db.collection("time_spent/")
//                        .document(app.getAppUser(null)
//                                .getId())
//                        .collection(sdf.format(new Date()))
//                        .document("Physical Health Monitor Fragment")
//                        .set(au);
//                    Map<String, Object> today_date = new HashMap<>();
//                    SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
//                    today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
//                    db.collection("time_dates/"+app.getPatientID()+"/dates").document("dates").set(today_date, SetOptions.merge());
//                    Map<String, Object> module_name = new HashMap<>();
//                    module_name.put(au.getActivity_name(), au.getActivity_name());
//                    db.collection("time_dates/"+app.getPatientID()+"/dates").document("module").set(module_name,SetOptions.merge());
//                } catch (Exception e) { Log.d(TAG ,"ERROR : can't get object", e); }
//
//            }
//        });





        super.onStop();
    }


}
