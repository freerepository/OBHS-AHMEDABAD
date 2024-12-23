package com.sedulous.obhsadi.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.adapter.InspectorReviewAdapter;
import com.sedulous.obhsadi.service.NetworkStatusClass;
import com.sedulous.obhsadi.service.PreferenceUtil;
import com.sedulous.obhsadi.service.Util;
import com.sedulous.obhsadi.service.WebServicesURLClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InspectorReviewActivity extends AppCompatActivity implements InspectorReviewAdapter.InspectorReviewInterface{

    private EditText etTrainNumber;
    private TextView tvGeneral,tvConsumables,tvWorkerUniform,tvEhkUniform;
    private ImageView ivTab1,ivTab2,ivTab3,ivTab4;
    private RecyclerView recyclerView;
    private Button btnNextSubmit;

    private ProgressBar progressBar;
    InspectorReviewAdapter adapter;
    private JSONArray arrayGeneral,arrayConsumables,arrayWorkersUniform,arrayEHKUniform;
    private int selectedTab=1;
    private Map<String,String> map=new HashMap<>();
    private JSONArray jsonArray;
    private boolean doubleBackToExitPressedOnce=false;
    private TextView tvAssignedJanitors, tvAvailableJanitors,tvTotalPenalityAmount;
    public float totalOldAmount;
    public static int totalAssignedJanitor,totalAbsentJanitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspector_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arrayGeneral=new JSONArray();
        etTrainNumber=findViewById(R.id.et_train_number);
        tvAssignedJanitors =findViewById(R.id.tv_assigned_janitors);
        tvAvailableJanitors =findViewById(R.id.tv_available_janitors);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        tvGeneral=(TextView)findViewById(R.id.tv_general);
        tvConsumables=(TextView)findViewById(R.id.tv_consumables);
        tvWorkerUniform=(TextView)findViewById(R.id.tv_workers_uniform);
        tvEhkUniform=(TextView)findViewById(R.id.tv_ehk_uniform);

        ivTab1=(ImageView)findViewById(R.id.iv_tab_1);
        ivTab2=(ImageView)findViewById(R.id.iv_tab_2);
        ivTab3=(ImageView)findViewById(R.id.iv_tab_3);
        ivTab4=(ImageView)findViewById(R.id.iv_tab_4);

        tvGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalTabSelect();
            }
        });

        tvConsumables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consumablesTabSelect();
            }
        });

        tvWorkerUniform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { workersUniformTabSelect();
            }
        });

        tvEhkUniform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ehkUniformTabSelect();
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager manager=new LinearLayoutManager(InspectorReviewActivity.this);
        recyclerView.setLayoutManager(manager);

        tvTotalPenalityAmount=(TextView)findViewById(R.id.tv_total_penality_amount);
        btnNextSubmit=(Button)findViewById(R.id.btn_next_submit);
        btnNextSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strTrainNumber=etTrainNumber.getText().toString().trim();
                btnNextSubmit.setBackgroundColor(getResources().getColor(R.color.colorOrange));

                if (selectedTab==1){
                    consumablesTabSelect();
                    btnNextSubmit.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
                }else if (selectedTab==2){
                    workersUniformTabSelect();
                    btnNextSubmit.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
                }else if (selectedTab==3){
                    ehkUniformTabSelect();
                    btnNextSubmit.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
                }else if (selectedTab==4) {
                    jsonArray=new JSONArray();
                    for (Map.Entry<String,String> mmap: map.entrySet()) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            String srValue=mmap.getValue();
                            String aa=srValue.substring(mmap.getValue().indexOf(",")+1);
                            String qtyIssued=Util.before(aa,",");
                            String aaa=aa.substring(aa.indexOf(",")+1);
                            String shortfall=Util.before(aaa,",");
                           // String itemPerJanitor=Util.after(aaa,",");

                            jsonObject.put("item_id", mmap.getKey());
                            jsonObject.put("category_id", Util.before(mmap.getValue(),","));
                            jsonObject.put("quantity_issued_by_organization", qtyIssued);
                            jsonObject.put("shortfall", shortfall);
                            jsonObject.put("quantity", "");
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (jsonArray.length()!=0 && !strTrainNumber.isEmpty()) {
                        progressBar.setVisibility(View.VISIBLE);
                        new submitInspectorFeedbackService().execute(PreferenceUtil.getUserIdSelected(InspectorReviewActivity.this),strTrainNumber, totalAbsentJanitor+"");
                    }else if (!strTrainNumber.isEmpty())
                        Toast.makeText(InspectorReviewActivity.this, "Please select at least 1 item to submit feedabck.", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(InspectorReviewActivity.this, "Please enter train number...", Toast.LENGTH_LONG).show();
                }
            }
        });

        etTrainNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length()==5)
                {
                    if (NetworkStatusClass.isNetworkStatusAvialable(InspectorReviewActivity.this))
                    {
                        new GetPenaltyListService().execute(editable.toString().trim());
                    }else
                        Toast.makeText(InspectorReviewActivity.this, R.string.internet_connection_text, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void generalTabSelect()
    {
        tvGeneral.setTextColor(getResources().getColor(R.color.colorBlueDark));
        tvConsumables.setTextColor(getResources().getColor(android.R.color.white));
        tvWorkerUniform.setTextColor(getResources().getColor(android.R.color.white));
        tvEhkUniform.setTextColor(getResources().getColor(android.R.color.white));

        tvGeneral.setBackgroundColor(getResources().getColor(android.R.color.white));
        tvConsumables.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
        tvWorkerUniform.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
        tvEhkUniform.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));

        ivTab1.setVisibility(View.VISIBLE);
        ivTab2.setVisibility(View.GONE);
        ivTab3.setVisibility(View.GONE);
        ivTab4.setVisibility(View.GONE);

        adapter=new InspectorReviewAdapter(InspectorReviewActivity.this,arrayGeneral, map,InspectorReviewActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        btnNextSubmit.setText("NEXT");
        selectedTab=1;
    }

    public void consumablesTabSelect()
    {
        tvGeneral.setTextColor(getResources().getColor(android.R.color.white));
        tvConsumables.setTextColor(getResources().getColor(R.color.colorBlueDark));
        tvWorkerUniform.setTextColor(getResources().getColor(android.R.color.white));
        tvEhkUniform.setTextColor(getResources().getColor(android.R.color.white));

        tvGeneral.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
        tvConsumables.setBackgroundColor(getResources().getColor(android.R.color.white));
        tvWorkerUniform.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
        tvEhkUniform.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));

        ivTab1.setVisibility(View.GONE);
        ivTab2.setVisibility(View.VISIBLE);
        ivTab3.setVisibility(View.GONE);
        ivTab4.setVisibility(View.GONE);

        adapter=new InspectorReviewAdapter(InspectorReviewActivity.this,arrayConsumables,map,InspectorReviewActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        btnNextSubmit.setText("NEXT");
        selectedTab=2;
    }

    public void workersUniformTabSelect()
    {
        tvGeneral.setTextColor(getResources().getColor(android.R.color.white));
        tvConsumables.setTextColor(getResources().getColor(android.R.color.white));
        tvWorkerUniform.setTextColor(getResources().getColor(R.color.colorBlueDark));
        tvEhkUniform.setTextColor(getResources().getColor(android.R.color.white));

        tvGeneral.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
        tvConsumables.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
        tvWorkerUniform.setBackgroundColor(getResources().getColor(android.R.color.white));
        tvEhkUniform.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));

        ivTab1.setVisibility(View.GONE);
        ivTab2.setVisibility(View.GONE);
        ivTab3.setVisibility(View.VISIBLE);
        ivTab4.setVisibility(View.GONE);

        adapter=new InspectorReviewAdapter(InspectorReviewActivity.this,arrayWorkersUniform,map,InspectorReviewActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        btnNextSubmit.setText("NEXT");
        selectedTab=3;
    }

    public void ehkUniformTabSelect()
    {
        tvGeneral.setTextColor(getResources().getColor(android.R.color.white));
        tvConsumables.setTextColor(getResources().getColor(android.R.color.white));
        tvWorkerUniform.setTextColor(getResources().getColor(android.R.color.white));
        tvEhkUniform.setTextColor(getResources().getColor(R.color.colorBlueDark));

        tvGeneral.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
        tvConsumables.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
        tvWorkerUniform.setBackgroundColor(getResources().getColor(R.color.colorBlueDark));
        tvEhkUniform.setBackgroundColor(getResources().getColor(android.R.color.white));

        ivTab1.setVisibility(View.GONE);
        ivTab2.setVisibility(View.GONE);
        ivTab3.setVisibility(View.GONE);
        ivTab4.setVisibility(View.VISIBLE);

        adapter=new InspectorReviewAdapter(InspectorReviewActivity.this,arrayEHKUniform,map,InspectorReviewActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        btnNextSubmit.setText("SUBMIT");
        selectedTab=4;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_logout)
        {
            Intent intent=new Intent(InspectorReviewActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemSelect(String itemId, String catId, String qtyIssued,String shortfall,String penalityAmount,String qtyPerJanitor) {
        map.put(itemId,catId+","+qtyIssued+","+shortfall+","+penalityAmount+","+qtyPerJanitor);

        float amount=0;
        for (Map.Entry<String,String> mmap: map.entrySet()) {
            try {
                float nshortfall=0, npenalty=0;
                String srValue=mmap.getValue();
                String []data=srValue.split(",");
                if(TextUtils.isEmpty(data[2])) nshortfall=0;
                else nshortfall=Float.parseFloat(data[2]);
                if(TextUtils.isEmpty(data[3])) npenalty=0;
                else npenalty=Float.parseFloat(data[3]);
                amount=amount+(nshortfall*npenalty);
                Log.e("mmaps","aaaa "+data[2]+"   "+data[3]);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        tvTotalPenalityAmount.setText(getResources().getString(R.string.ruppee_text)+amount+"");
    }

    @Override
    public void itemUnSelect(String itemId) {
        map.remove(itemId);
        float amount=0;
        for (Map.Entry<String,String> mmap: map.entrySet()) {
            try {
                float nshortfall=0, npenalty=0;
                String srValue=mmap.getValue();
                String []data=srValue.split(",");
                if(TextUtils.isEmpty(data[2])) nshortfall=0;
                else nshortfall=Float.parseFloat(data[2]);
                if(TextUtils.isEmpty(data[3])) npenalty=0;
                else npenalty=Float.parseFloat(data[3]);

                amount=amount+(nshortfall*npenalty);
                Log.e("mmaps","aaaa "+data[2]+"   "+data[3]);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        tvTotalPenalityAmount.setText(getResources().getString(R.string.ruppee_text)+amount+"");
    }


    public class GetPenaltyListService extends AsyncTask<String,String,JSONObject>
    {

        @Override
        protected JSONObject doInBackground(String... strings) {
//            List<NameValuePair> params=new ArrayList<>();
//            params.add(new BasicNameValuePair("train_no",strings[0]));
            JSONObject jObject=new JSONObject();
            try {
                jObject.put("train_no",strings[0]);
                jObject.put("depot_code", PreferenceUtil.getDepot(InspectorReviewActivity.this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            WebServicesURLClass webServicesURLClass=new WebServicesURLClass();
            JSONObject jsonObject=webServicesURLClass.getPenaltyListMethod(jObject);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                progressBar.setVisibility(View.GONE);
                Log.e("PenaltyListRes:", jsonObject.toString()+"");
                totalAssignedJanitor=0;
                totalAssignedJanitor=Integer.parseInt(jsonObject.getJSONObject("data").getString("janitor_present").trim());
                tvAssignedJanitors.setText(jsonObject.getJSONObject("data").getString("janitor_required"));
                tvAvailableJanitors.setText(jsonObject.getJSONObject("data").getString("janitor_present"));

                arrayGeneral = jsonObject.getJSONObject("data").getJSONArray("General");
                arrayConsumables = jsonObject.getJSONObject("data").getJSONArray("Consumables");
                arrayWorkersUniform = jsonObject.getJSONObject("data").getJSONArray("Workers Uniform");
                arrayEHKUniform = jsonObject.getJSONObject("data").getJSONArray("EHK Uniform");

                totalAbsentJanitor=Integer.parseInt(jsonObject.getJSONObject("data").getString("janitor_required"))-Integer.parseInt(jsonObject.getJSONObject("data").getString("janitor_present"));

                adapter=new InspectorReviewAdapter(InspectorReviewActivity.this,arrayGeneral,map, InspectorReviewActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                float generalAmount=0,consumableAmount=0,uniformAmount=0,ehkAmount=0;
                for (int i=0;i<arrayGeneral.length();i++)
                {
                    if (!arrayGeneral.getJSONObject(i).getString("shortfall").trim().isEmpty()&&!arrayGeneral.getJSONObject(i).getString("shortfall").trim().equalsIgnoreCase("0")) {
                        float shortFall=Float.parseFloat(arrayGeneral.getJSONObject(i).getString("shortfall").trim());
                        float penalty=Float.parseFloat(arrayGeneral.getJSONObject(i).getString("penalty").trim());
                        float qtyPerItem=Float.parseFloat(arrayGeneral.getJSONObject(i).getString("required_quantity_of_item_per_janitor").trim());

                        if (shortFall <= qtyPerItem) {
                            generalAmount = generalAmount + penalty;
                        } else {
                            float a=(int)(shortFall/qtyPerItem);
                            float b=shortFall%qtyPerItem;
                            if (b==0)
                                generalAmount = generalAmount + a*penalty;
                            else
                                generalAmount = generalAmount +a*penalty+penalty;
                        }
                    }
                }
                for (int i=0;i<arrayConsumables.length();i++)
                {
                    if (!arrayConsumables.getJSONObject(i).getString("shortfall").trim().isEmpty()&&!arrayConsumables.getJSONObject(i).getString("shortfall").trim().equalsIgnoreCase("0")) {
                        float shortFall=Float.parseFloat(arrayConsumables.getJSONObject(i).getString("shortfall").trim());
                        float penalty=Float.parseFloat(arrayConsumables.getJSONObject(i).getString("penalty").trim());
                        float qtyPerItem=Float.parseFloat(arrayConsumables.getJSONObject(i).getString("required_quantity_of_item_per_janitor").trim());

                        if (shortFall <= qtyPerItem) {
                            consumableAmount = consumableAmount + penalty;
                        } else {
                            float a=(int)(shortFall/qtyPerItem);
                            float b=shortFall%qtyPerItem;
                            if (b==0)
                                consumableAmount = consumableAmount + a*penalty;
                            else
                                consumableAmount = consumableAmount +a*penalty+penalty;
                        }
                    }
                }
                for (int i=0;i<arrayWorkersUniform.length();i++)
                {
                    if (!arrayWorkersUniform.getJSONObject(i).getString("shortfall").trim().isEmpty()&&!arrayWorkersUniform.getJSONObject(i).getString("shortfall").trim().equalsIgnoreCase("0")) {
                        float shortFall=Float.parseFloat(arrayWorkersUniform.getJSONObject(i).getString("shortfall").trim());
                        float penalty=Float.parseFloat(arrayWorkersUniform.getJSONObject(i).getString("penalty").trim());
                        float qtyPerItem=Float.parseFloat(arrayWorkersUniform.getJSONObject(i).getString("required_quantity_of_item_per_janitor").trim());

                        if (shortFall <= qtyPerItem) {
                            uniformAmount = uniformAmount + penalty;
                        } else {
                            float a=(int)(shortFall/qtyPerItem);
                            float b=shortFall%qtyPerItem;
                            if (b==0)
                                uniformAmount = uniformAmount + a*penalty;
                            else
                                uniformAmount = uniformAmount +a*penalty+penalty;
                        }
                    }
                }
                for (int i=0;i<arrayEHKUniform.length();i++)
                {
                    if (!arrayEHKUniform.getJSONObject(i).getString("shortfall").trim().isEmpty()&&!arrayEHKUniform.getJSONObject(i).getString("shortfall").trim().equalsIgnoreCase("0")) {
                        float shortFall=Float.parseFloat(arrayEHKUniform.getJSONObject(i).getString("shortfall").trim());
                        float penalty=Float.parseFloat(arrayEHKUniform.getJSONObject(i).getString("penalty").trim());
                        float qtyPerItem=Float.parseFloat(arrayEHKUniform.getJSONObject(i).getString("required_quantity_of_item_per_janitor").trim());

                        if (shortFall <= qtyPerItem) {
                            ehkAmount = ehkAmount + penalty;
                        } else {
                            float a=(int)(shortFall/qtyPerItem);
                            float b=shortFall%qtyPerItem;
                            if (b==0)
                                ehkAmount = ehkAmount + a*penalty;
                            else
                                ehkAmount = ehkAmount +a*penalty+penalty;
                        }
                    }
                }
                totalOldAmount =generalAmount+consumableAmount+uniformAmount+ehkAmount;

                tvTotalPenalityAmount.setText(getResources().getString(R.string.ruppee_text)+ totalOldAmount +"");

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class submitInspectorFeedbackService extends AsyncTask<String,String,JSONObject>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject jObject=new JSONObject();
            try {
                jObject.put("user_id",strings[0]);
                jObject.put("train_number",strings[1]);
                jObject.put("absent_janitor",strings[2]);
                jObject.put("item_list",jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            WebServicesURLClass webServicesURLClass=new WebServicesURLClass();
            JSONObject jsonObject=webServicesURLClass.submitInspectorFeedbackMethod(jObject);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                progressBar.setVisibility(View.GONE);
                Log.e("submitInsFeedRes:", jsonObject.toString()+"");
                if (jsonObject.getInt("status")==1) {
                    Intent intent = new Intent(InspectorReviewActivity.this, InspectorReviewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                Toast.makeText(InspectorReviewActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
