package com.sedulous.obhsadi.activitylinen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sedulous.obhsadi.ModelLinen.LinenQuestionData;
import com.sedulous.obhsadi.ModelLinen.QanswerData;
import com.sedulous.obhsadi.ModelLinen.SaveAttendance;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.O;
import com.sedulous.obhsadi.service.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ReceviceLostLinenActivity extends AppCompatActivity {
    public static String Train_number = "http://obhsadi.projectrailway.in/api/Linen/getTrain";
    public static String coach_list= "http://obhsadi.projectrailway.in/api/Linen/coachList";
    public static String LINEN_DATA= "http://obhsadi.projectrailway.in/api/Linen/getlilenData";
    private RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    Button btn_next;
    ImageView v_close;
    public JSONArray questionArray;
    EditText et_JourneyDate;
    TextView tv_iDcardNo,tv_NameACCN ,tv_total_penality_amount;
    ArrayList<String> trainNoList = new ArrayList<>();
    ArrayList<String> coachNameList = new ArrayList<>();
    public HashMap<String, QanswerData> qmaps=new HashMap<>();
    ArrayAdapter<String> trainNoAdapter,coachNameAdapter;
    LinenAdapter linenAdapter;
    public String selectedTrain="", selectedCoachName="",user_id = "", deport_code = "", designation = "";

    LinenQuestionData linenQuestionData=null;
    Spinner spTrainNo,spCoachName;
    SaveAttendance saveAttendance;
    AlertDialog dialog;
    int shortfall_focus_position=0;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recevice_lost_linen);
        user_id=getIntent().getStringExtra("user_id");
        designation=getIntent().getStringExtra("designation");
        deport_code = getIntent().getStringExtra("deport_code");
        linenQuestionData=(LinenQuestionData)getIntent().getSerializableExtra("qdata");

        et_JourneyDate = findViewById(R.id.et_journey_date);
        spCoachName = findViewById(R.id.sp_coach_list);
        spTrainNo = findViewById(R.id.sp_train_list);
        tv_iDcardNo = findViewById(R.id.tv_id_cardno);
        tv_NameACCN = findViewById(R.id.tv_name_ACCN);
        srl = findViewById(R.id.srl);
        btn_next = findViewById(R.id.btn_next_submit);
        btn_next.setText("Next");
        v_close = findViewById(R.id.v_back);
        tv_total_penality_amount = findViewById(R.id.tv_total_penality_amount);

        recyclerView = (RecyclerView) findViewById(R.id.questions);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(ReceviceLostLinenActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(linenAdapter);
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (linenAdapter == null || linenAdapter.linenList.length()==0) {
                    if (O.checkNetwork(ReceviceLostLinenActivity.this)) {
                        getLinenData();

                    }
                } else {
                    srl.setRefreshing(false);
                }
            }
        });

        v_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Registeration.this.onBackPressed();
                android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(ReceviceLostLinenActivity.this);

                alertbox.setTitle("Exit ? All data & progress will be lost!");
                alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // finish used for destroyed activity
                        finish();
                    }
                });

                alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Nothing will be happened when clicked on no button
                        // of Dialog
                    }
                });
                alertbox.show();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean isAllChecked = true;
                if (linenAdapter == null || linenAdapter.linenList.length() == 0) {
                    isAllChecked = false;
                } else {
                    for (int n = 0; n <= linenAdapter.linenList.length(); n++) {
                        try {
                            String key = linenAdapter.linenList.getJSONObject(n).getString("id");
//                            if (!map.containsKey(key)){
//                                isAllChecked=false;
//                                Toast.makeText(getApplicationContext(), "Please give all rating",
//                                        Toast.LENGTH_LONG).show();
//                                break;
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (spTrainNo.getSelectedItemPosition() == 0) {
                    Toast.makeText(ReceviceLostLinenActivity.this, "Select Train", Toast.LENGTH_LONG).show();
                } else if (et_JourneyDate.getText().toString().isEmpty()) {
                    Toast.makeText(ReceviceLostLinenActivity.this, "Select Journey Date", Toast.LENGTH_SHORT).show();
                } else if (spCoachName.getSelectedItemPosition() == 0) {
                    Toast.makeText(ReceviceLostLinenActivity.this, "Select Coach", Toast.LENGTH_SHORT).show();

                } else if (isAllChecked) {
                    Intent i = new Intent(ReceviceLostLinenActivity.this, TakeCaSingatureActivity.class);
                    i.putExtra("qdata", qmaps);
                    i.putExtra("train_no",selectedTrain);
                    i.putExtra("coach",selectedCoachName);
                    i.putExtra("emp_id",tv_iDcardNo.getText().toString());
                    i.putExtra("emp_name",tv_NameACCN.getText().toString());
                    i.putExtra("jdate", et_JourneyDate.getText().toString());

                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Please give all rating",
                            Toast.LENGTH_LONG).show();
                }
            }

        });
        GetTrain();
        trainNoList.add(0, "Select Train.");
        trainNoAdapter = new ArrayAdapter<String>(ReceviceLostLinenActivity.this, android.R.layout.simple_spinner_dropdown_item, trainNoList);
        spTrainNo.setAdapter(trainNoAdapter);
        spTrainNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedTrain = "";

                } else {
                    selectedTrain = trainNoList.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final DatePickerDialog.OnDateSetListener journeyDate1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd-MM-yyyy", Locale.US);
                String dt = "" + dayOfMonth;
                if (dt.length() == 1) dt = "0" + dt;
                String mnth = "" + (monthOfYear + 1);
                if (mnth.length() == 1) mnth = "0" + mnth;
                et_JourneyDate.setText(year + "-" + mnth + "-" + dt);
            }
        };
        et_JourneyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // O.selectDate(ReceviceLostLinenActivity.this, O.MAX_NOW, et_JourneyDate);
                DatePickerDialog dpd = new DatePickerDialog(ReceviceLostLinenActivity.this, journeyDate1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMaxDate(new Date().getTime());
                dpd.show();


            }
        });
        et_JourneyDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(et_JourneyDate.getText().toString())) ;
                GetCoach();
            }
        });

        coachNameList.add(0, "Select Coach");
        coachNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, coachNameList);
        coachNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spCoachName.setAdapter(coachNameAdapter);
        spCoachName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int myPosition, long myID) {
                if (myPosition == 0) {
                    selectedCoachName = "";

                } else {
                    selectedCoachName = coachNameList.get(myPosition);
                    getLinenData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getLinenData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("train_no", selectedTrain);
            jsonObject.put("station_code", PreferenceUtil.getDepot(this));
            jsonObject.put("journey_date", et_JourneyDate.getText().toString());
            jsonObject.put("coach", selectedCoachName);

            srl.setRefreshing(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, LINEN_DATA, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        srl.setRefreshing(false);
                        Log.e("response_req", response.toString());
                        try {

                            questionArray = response.getJSONArray("lineItemsList");
                            linenAdapter = new LinenAdapter(questionArray,getApplicationContext(), ReceviceLostLinenActivity.this);
                            linenAdapter.linenList=questionArray;
                            recyclerView.setAdapter(linenAdapter);
                            linenAdapter.notifyDataSetChanged();
                            linenQuestionData = new Gson().fromJson(response.toString(), LinenQuestionData.class);
                            tv_iDcardNo.setText(linenQuestionData.mItemList.get(0).mIdcard_no);
                            tv_NameACCN.setText(linenQuestionData.mItemList.get(0).mAttendent_name);


                        }catch (Exception e){}


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }
    public class LinenAdapter extends RecyclerView.Adapter<LinenAdapter.MyViewHolder> {
        private Context context;
        private JSONArray linenList;
        private  ReceviceLostLinenActivity rating;


        public LinenAdapter(JSONArray linenList, Context context, ReceviceLostLinenActivity receviceLostLinenActivity) {
            this.context = context;
            this.linenList = linenList;
            this.rating=receviceLostLinenActivity;
        }


        @NonNull
        @Override
        public LinenAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_recevice_lost_linen, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            holder.setIsRecyclable(false);
            final int position=pos;
            try {
                final JSONObject jsonObject = linenList.getJSONObject(position);
                holder.tv_index.setText((position+1)+"");
                holder.tv_items.setText(jsonObject.getString("item_name"));
                holder.tv_total_given.setText(jsonObject.getString("item_total"));


                for (QanswerData qanswerData: qmaps.values())
                {
                    if (qanswerData.quest_id.equalsIgnoreCase(jsonObject.getString("id")))
                    {
                        try {
                            holder.tv_amount.setText(qanswerData.total_penalty_amount);
                            holder.et_total_return.setText(qanswerData.total_return);
                            holder.tv_shortfall.setText(qanswerData.shortfall);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                holder.et_total_return.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void afterTextChanged(Editable editable) {
                        Log.e("data1",editable.toString());
                        shortfall_focus_position=position;
                        try {
                            //holder.et_total_return.requestFocus(holder.et_total_return.getText().length());
                            if (!holder.et_total_return.getText().toString().trim().isEmpty()) {
                                String cat_ID ="", ques_id = "", rate="", quantity = "", total_pamount = "", unit="";
                                int shortfall=0;
                                ques_id = jsonObject.getString("id");
                                rate = jsonObject.getString("amount");
                                shortfall = Integer.parseInt(jsonObject.getString("item_total"))-Integer.parseInt(holder.et_total_return.getText().toString().trim());

                                total_pamount = String.format("%.2f",Float.parseFloat(rate)* shortfall);
                                Log.e("data","ques_id  "+ques_id+"  rate  "+rate+"  shortfall  "+shortfall+"  total_pamount  "+total_pamount);
                                itemselect(new QanswerData().setQuestId(ques_id).setCatId(cat_ID).setQuantity(quantity).setTotal_given(jsonObject.getString("item_total")).
                                        setShortfall(""+shortfall).setRate(rate).setTotalPAmount(total_pamount).setUnit(unit).setTotal_return(holder.et_total_return.getText().toString().trim()));

                            } else {
                                Log.e("akm ","item unselect called");
                                itemUnSelect(jsonObject.getString("id"));
                                holder.tv_amount.setText("");
                            }
                            notifyItemChanged(position,jsonObject);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }catch (Exception e){ }

            if(position==shortfall_focus_position){
                holder.et_total_return.requestFocus();
            }
            float sum=0;
            for (QanswerData answer : qmaps.values()) {
                try {
                    sum = sum + Float.parseFloat(answer.total_penalty_amount);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            Log.e("size ", String.valueOf(qmaps.values().size()));
            tv_total_penality_amount.setText(getResources()
                    .getString(R.string.ruppee_text)+sum+"");

        }
        @Override
        public int getItemCount() {

            if (linenList != null)
                return linenList.length();
            else
                return 0;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_index,tv_items,tv_total_given,tv_amount,tv_shortfall;
            EditText et_total_return;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index=itemView.findViewById(R.id.tv_index_number);
                tv_items=itemView.findViewById(R.id.tv_items);
                tv_total_given=itemView.findViewById(R.id.tv_total_given);
                et_total_return=itemView.findViewById(R.id.et_total_return);
                tv_shortfall=itemView.findViewById(R.id.tv_shortfall);
                tv_amount=itemView.findViewById(R.id.tv_amount);
            }
        }
    }
    public void itemselect(QanswerData qanswerData){

        qmaps.put(qanswerData.quest_id, qanswerData);
        linenAdapter.notifyDataSetChanged();
        Log.e("akm select", "qdata "+
                "\nquestid "+qanswerData.quest_id+" "+
                "\ntv_rate "+qanswerData.rate+" "+
                "\ntv_unit "+qanswerData.unit+" "+
                "\nshortfall "+qanswerData.shortfall+" "+
                "\ntv_qty "+qanswerData.quantity+" "+
                "\ntotal_given "+qanswerData.total_given+" "+
                "\ntotal_return "+qanswerData.total_return+" "+
                "\ncatid "+qanswerData.cat_id+" "+
                "\ntotalamount "+qanswerData.total_penalty_amount+" ");
    }

    public void itemUnSelect(String itemId) {
        qmaps.remove(itemId);
        linenAdapter.notifyDataSetChanged();
    }
    private void GetTrain() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("station_code", PreferenceUtil.getDepot(this));
            showLoading("Loading question...");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Train_number, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("TrainList");
                            trainNoList.clear();
                            trainNoList.add(0,"Select Train");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                trainNoList.add(obj.getString("train_no"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spTrainNo.setAdapter(new ArrayAdapter<String>(ReceviceLostLinenActivity.this, android.R.layout.simple_spinner_dropdown_item, trainNoList));
                        spTrainNo.setSelected(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }

    private void GetCoach() {

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("train_no",selectedTrain);
            jsonObject.put("station_code", PreferenceUtil.getDepot(this));
            jsonObject.put("journey_date", et_JourneyDate.getText().toString());
            showLoading("Loading question...");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, coach_list, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("coachList");
                            if (array.length() > 0) {
                                coachNameList.clear();
                                coachNameList.add(0,"Select Coach");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    coachNameList.add(obj.getString("coach"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spCoachName.setAdapter(new ArrayAdapter<String>(ReceviceLostLinenActivity.this, android.R.layout.simple_dropdown_item_1line, coachNameList));
                        spCoachName.setSelected(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }
    protected void showLoading(@NonNull String message0) {
        LinearLayout ll = new LinearLayout(this);
        ll.setPadding(16, 16, 16, 16);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setGravity(Gravity.CENTER);
        ll.setLayoutParams(llParam);

        TextView tv = new TextView(this);
        tv.setText(message0);
        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(llParam);
        tv.setPadding(8, 8, 8, 8);
        ll.addView(tv);

        RelativeLayout rl = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rl.setLayoutParams(rlParam);

        ImageView iv = new ImageView(this);
        iv.setImageDrawable(getDrawable(R.drawable.progress));
        rlParam = new RelativeLayout.LayoutParams(100, 100);
        rlParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        rlParam.addRule(RelativeLayout.BELOW, tv.getId());
        iv.setLayoutParams(rlParam);
        rl.addView(iv);
        iv.animate().setInterpolator(new DecelerateInterpolator()).rotation(-3600).setDuration(20000).start();

        ImageView iv_logo = new ImageView(this);
        iv_logo.setImageDrawable(getDrawable(R.drawable.logo));
        iv_logo.setPadding(20, 20, 20, 20);
        rlParam = new RelativeLayout.LayoutParams(100, 100);
        rlParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        rlParam.addRule(RelativeLayout.BELOW, tv.getId());
        iv_logo.setLayoutParams(rlParam);
        rl.addView(iv_logo);
        iv_logo.animate().setInterpolator(new DecelerateInterpolator()).rotation(3600).setDuration(20000).start();

        ll.addView(rl);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(ll);
        dialog = builder.create();
        dialog.show();
    }

    protected void hideLoading() {
        dialog.dismiss();
    }
}



