package com.sedulous.obhsadi.activitylinen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.sedulous.obhsadi.ModelLinen.CatPenaltyModel;
import com.sedulous.obhsadi.ModelLinen.QanswerData;
import com.sedulous.obhsadi.ModelLinen.UserDataModel;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.O;
import com.sedulous.obhsadi.service.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;

public class PenaltyLinenActivity extends AppCompatActivity {
    private final static String SubmitPenaltyData = "http://obhsadi.projectrailway.in/api/Linen/save_penalties";
    private final static String questionAPI ="http://obhsadi.projectrailway.in/api/Linen/penalty_questions";
    private final static String CatogriesAPI = "http://obhsadi.projectrailway.in/api/Linen/penalty_categories";
    ImageView iv_backView;
    PenalityAdapter adapter;
    Button submit;
    TextView tv_total_penality_amount;
    RecyclerView recyclerView;
    public JSONArray questionArray;
    String requestBody;
    String shift,date;
    String message;
    TabLayout tabLayout;
    CatPenaltyModel catPenaltyModel=null;
    public HashMap<String, QanswerData> qmap=new HashMap<>();
    UserDataModel userDataModel=null;
    SwipeRefreshLayout srl;
    ProgressDialog mProgressDialog;
    int shortfall_focus_position=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penalty_linen);
        try {
            userDataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), (Type) UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_total_penality_amount=findViewById(R.id.tv_total_penality_amount);

        tabLayout=findViewById(R.id.tl);
        srl=findViewById(R.id.srl);
        recyclerView=findViewById(R.id.rv);
        submit=findViewById(R.id.btn_next_submit);

        iv_backView=findViewById(R.id.v_back);
        iv_backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        submit.setText("Next");
        adapter = new PenalityAdapter(PenaltyLinenActivity.this, questionArray);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tabLayout.getTabCount()>0){
                    if (tabLayout.getSelectedTabPosition()<tabLayout.getTabCount()-1) {
                        tabLayout.getTabAt(tabLayout.getSelectedTabPosition()+1).select();
                    }
                    else{
                        if (qmap.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Please give atleast one penalty", Toast.LENGTH_LONG).show();
                        } else {
                            showLoading("Uploading...");
                            submit.setEnabled(false);
                            submit.setBackgroundResource(R.drawable.button_orange_bg);
                            JSONArray jsonArray = new JSONArray();
                            boolean isAllOk=true;

                            for (QanswerData qanswerData : qmap.values()) {
//                                if(Float.parseFloat(qanswerData.rate)>=Float.parseFloat(qanswerData.shortfall)) {
//                                    isAllOk = false;
//                                    Toast.makeText(getApplicationContext(), "Penalty should be greater than rate!", Toast.LENGTH_LONG).show();
//                                    break;
//                                }
                                JSONObject jsonObject = new JSONObject();
                                try {

                                    jsonObject.put("quest_id", qanswerData.quest_id);
                                    jsonObject.put("cat_id", qanswerData.cat_id);
                                    jsonObject.put("qty", qanswerData.quantity);
                                    jsonObject.put("amount", qanswerData.rate);
                                    jsonObject.put("total_amount", qanswerData.total_penalty_amount);
                                    jsonObject.put("unit", qanswerData.unit);
                                    jsonArray.put(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("supervisor_id", PreferenceUtil.getUserIdSelected(PenaltyLinenActivity.this));
                                jsonObject.put("date", date);
                                jsonObject.put("Penalty_Data", jsonArray);
                                requestBody = jsonObject.toString();
                            } catch (Exception e) {

                            }

                            Log.v("requestBody", requestBody);

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, SubmitPenaltyData,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            hideLoading();

                                            JSONObject jsonObject= null;
                                            try {
                                                jsonObject = new JSONObject(response);
                                                message=jsonObject.getString("message");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            qmap.clear();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(PenaltyLinenActivity.this);
                                            //  builder.setTitle("Message")
                                            builder.setMessage(response)
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int a) {
                                                            Intent i = new Intent(PenaltyLinenActivity.this, SupervisorActivity.class);
                                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(i);
                                                        }
                                                    });

                                            AlertDialog dialog = builder.create();
                                            dialog.show();

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    hideLoading();
                                    submit.setEnabled(true);
                                    submit.setBackgroundResource(R.drawable.button_orange_bg);
                                    Toast.makeText(PenaltyLinenActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();

                                }
                            }) {
                                @Override
                                public String getBodyContentType() {
                                    return String.format("application/json; charset=utf-8");
                                }

                                @Override

                                public byte[] getBody() throws AuthFailureError {
                                    try {
                                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                                    } catch (UnsupportedEncodingException uee) {
                                        return null;
                                    }
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(PenaltyLinenActivity.this);
                            requestQueue.add(stringRequest);
                        }
                    }}
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        getCat();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                callTab(catPenaltyModel.mCatItems.get(tab.getPosition()).mCatId);
                if(tab.getPosition()<catPenaltyModel.mCatItems.size()-1){
                    submit.setText("Next");
                }else{
                    submit.setText("Submit");
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(catPenaltyModel==null){
                    getCat();
                }else if(adapter==null || adapter.list==null || adapter.list.length()==0){
                    callTab(catPenaltyModel.mCatItems.get(tabLayout.getSelectedTabPosition()).mCatId);
                }else{
                    srl.setRefreshing(false);
                }
            }
        });
        Log.e("ResponceTab1","in create");
    }
    public void getCat(){
        Log.e("ResponceTab2","inget nTab");
        srl.setRefreshing(true);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, CatogriesAPI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("ResponceTab3",response.toString());
                        try {
                            catPenaltyModel = new Gson().fromJson(response.toString(), CatPenaltyModel.class);
                            if (catPenaltyModel.mCatItems.size() > 0) {
                                tabLayout.removeAllTabs();
                                for(int a=0; a<catPenaltyModel.mCatItems.size(); a++){

                                    String cat_name=catPenaltyModel.mCatItems.get(a).mCat_title;
                                    TabLayout.Tab tab=tabLayout.newTab();
                                    String name=cat_name;
                                    if(cat_name.contains(" ")){
                                        // name=cat_name.substring(0,cat_name.indexOf(" "));
                                    }
                                    tab.setText(name);
                                    tabLayout.addTab(tab);
                                }
                                callTab(catPenaltyModel.mCatItems.get(0).mCatId);
                            } else {
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                srl.setRefreshing(false);
                Log.e("Error Responce",error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);
    }

    private void callTab(String cat_id) {
        srl.setRefreshing(true);
        JSONObject object =new JSONObject();
        try {
            object.put("cat_id",cat_id);

        }catch (JSONException e){}
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, questionAPI, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("q_response",""+response);
                        try {
                            questionArray = response.getJSONArray("GetPenaltyQuestions");
                            adapter.list=questionArray;
                            adapter.notifyDataSetChanged();

                        }catch (Exception e){}

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                srl.setRefreshing(false);
            }
        });
        RequestQueue requestQueue1 = Volley.newRequestQueue(this);
        requestQueue1.add(objectRequest);
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Exit ? All data & progress will be lost!")
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        qmap.clear();
                        PenaltyLinenActivity.super.onBackPressed();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        qmap.clear();
        super.onDestroy();
    }

    public class PenalityAdapter extends RecyclerView.Adapter<PenalityAdapter.PenViewHolder> {
        private Context context;
        private JSONArray list;
        public PenalityAdapter(Context context, JSONArray list) {
            this.context = context;
            this.list = list;
        }
        @NonNull
        @Override
        public PenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_penalty,parent,false);
            PenViewHolder vh=new PenViewHolder(view);
            return vh;
        }
        @Override
        public void onBindViewHolder(@NonNull final PenViewHolder holder, int pos) {
            final int position = pos;
            holder.setIsRecyclable(false);

            try {
                final JSONObject jsonObject = list.getJSONObject(position);
                holder.tv_index.setText((position+1)+"");
                holder.tv_ques.setText(jsonObject.getString("question_name"));
                holder.tv_qty.setText(jsonObject.getString("qty"));
                holder.tv_unit.setText(jsonObject.getString("Unit"));
                holder.tv_rate.setText(jsonObject.getString("penalty_amount"));

                for (QanswerData qanswerData: qmap.values())
                {
                    if (qanswerData.quest_id.equalsIgnoreCase(jsonObject.getString("id")))
                    {
                        try {
                            holder.tv_amount.setText(qanswerData.total_penalty_amount);
                            holder.et_shrtfall.setText(qanswerData.shortfall);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                holder.et_shrtfall.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void afterTextChanged(Editable editable) {
                        shortfall_focus_position=position;
                        try {
                            //  holder.et_shrtfall.requestFocus(holder.et_shrtfall.getText().length());
                            if (!holder.et_shrtfall.getText().toString().trim().isEmpty()) {
                                String cat_ID ="", ques_id = "", rate="", quantity = "",shortfall = "", total_pamount = "", unit="";
                                ques_id = jsonObject.getString("id");
                                rate = jsonObject.getString("penalty_amount");
                                cat_ID = jsonObject.getString("cat_id");
                                quantity = jsonObject.getString("qty");
                                shortfall = holder.et_shrtfall.getText().toString().trim();
                                Float s = Float.parseFloat(shortfall);
                                total_pamount = String.format("%.2f",s);
                                unit = jsonObject.getString("Unit");

                                itemselect(new QanswerData().setQuestId(ques_id).setCatId(cat_ID).setQuantity(quantity).
                                        setShortfall(shortfall).setRate(rate).setTotalPAmount(total_pamount).setUnit(unit));

                            } else {
                                Log.e("akm ","item unselect called");
                                itemUnSelect(jsonObject.getString("id"));
                                holder.tv_amount.setText("");
                            }
                        }catch (Exception e){ }
                    }
                });
            }catch (Exception e){ }

            if(position==shortfall_focus_position){
                holder.et_shrtfall.requestFocus();
            }
            float sum=0;
            for (QanswerData answer : qmap.values()) {
                try {
                    sum = sum + Float.parseFloat(answer.total_penalty_amount);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            Log.e("size ", String.valueOf(qmap.values().size()));
            tv_total_penality_amount.setText(getResources()
                    .getString(R.string.ruppee_text)+sum+"");

        }
        @Override
        public int getItemCount() {
            if (list!=null)
                return list.length();
            else
                return 0;
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class PenViewHolder extends RecyclerView.ViewHolder{
            TextView tv_index, tv_ques, tv_qty, tv_unit, tv_rate, tv_amount;
            EditText et_shrtfall;
            public PenViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = itemView.findViewById(R.id.tv_index_number);
                tv_ques = (TextView) itemView.findViewById(R.id.tv_qus);
                tv_qty = itemView.findViewById(R.id.tv_qty);
                tv_unit = itemView.findViewById(R.id.unit);
                tv_rate = itemView.findViewById(R.id.tv_rates);
                tv_amount = itemView.findViewById(R.id.ammount);
                et_shrtfall = itemView.findViewById(R.id.et_shortfall);
            }
        }
    }

    public void itemselect(QanswerData qanswerData){

        qmap.put(qanswerData.quest_id, qanswerData);
        adapter.notifyDataSetChanged();
        Log.e("akm select", "qdata "+
                "\nquestid "+qanswerData.quest_id+" "+
                "\ntv_rate "+qanswerData.rate+" "+
                "\ntv_unit "+qanswerData.unit+" "+
                "\nshortfall "+qanswerData.shortfall+" "+
                "\ntv_qty "+qanswerData.quantity+" "+
                "\ncatid "+qanswerData.cat_id+" "+
                "\ntotalamount "+qanswerData.total_penalty_amount+" ");
    }

    public void itemUnSelect(String itemId) {
        qmap.remove(itemId);
        adapter.notifyDataSetChanged();
    }
    protected void showLoading(@NonNull String message0) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message0);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}