package com.sedulous.obhsadi.activitylinen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sedulous.obhsadi.ModelLinen.LinenQuestionData;
import com.sedulous.obhsadi.ModelLinen.MainCategory;
import com.sedulous.obhsadi.ModelLinen.UserDataModel;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.activity.CleaningPicActivity;
import com.sedulous.obhsadi.activity.GroupPicActivity;
import com.sedulous.obhsadi.activity.LoginActivity;
import com.sedulous.obhsadi.service.O;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SupervisorActivity extends AppCompatActivity {
    public static String Main_Category = "http://obhsadi.projectrailway.in/api/atd/get_vcategory";
    ImageView close;
    TextView tvUserType;
    SwipeRefreshLayout srl;
    RecyclerView recyclerView;
    String  user_id="",deport_code="",designation="" ;
    UserDataModel userDataModel;
    LinenQuestionData getlilenData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor);
        user_id=getIntent().getStringExtra("user_id");
        designation=getIntent().getStringExtra("designation");
        deport_code = getIntent().getStringExtra("deport_code");

        if (getIntent() != null)
            getlilenData = (LinenQuestionData) getIntent().getSerializableExtra("data");

        try {
            userDataModel = new Gson().fromJson(O.getPreference(SupervisorActivity.this, "data"),(Type) UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvUserType=findViewById(R.id.tvUserType);
        tvUserType.setText("Admin");

        close = findViewById(R.id.iv_logout);
        recyclerView = findViewById(R.id.recyclerView);
        srl = findViewById(R.id.srl);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Main_Category();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();

            }

            private void  showLogoutAlertDialog() {
                final Dialog dialog = new Dialog(SupervisorActivity.this, R.style.Dialog);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog);
                TextView tv = dialog.findViewById(R.id.tv);
                tv.setText("Logout Confirm ?");
                dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        O.clearPref(SupervisorActivity.this);
                        Intent i = new Intent(SupervisorActivity.this, LoginActivity.class);
                        finishAffinity();
                        startActivity(i);
                    }
                });
                dialog.findViewById(R.id.v_negative).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


    }

    private void Main_Category() {
        JSONObject jsonObject = new JSONObject();

        final String requestBody=jsonObject.toString();

        StringRequest request=new StringRequest(Request.Method.GET, Main_Category, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("kumar", response);

                MainCategory getlist = new Gson().fromJson(response, MainCategory.class);
                recyclerView.setAdapter(new LinenAdapter(getlist.CatList));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley", error.toString());

            }
        }){
            class LinenAdapter {
            }

            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }};
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    public class LinenAdapter extends RecyclerView.Adapter<SupervisorActivity.ViewHolder> {
        private ArrayList<MainCategory.Item> listdata;

        public LinenAdapter(ArrayList<MainCategory.Item> listdata) {
            this.listdata = listdata;
        }

        @NonNull
        @Override
        public SupervisorActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_task_type_linen, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SupervisorActivity.ViewHolder holder, final int position) {

            MainCategory.Item taskType=listdata.get(position);
            holder.tv.setText(taskType.mCategory);
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(taskType.mID.equalsIgnoreCase("1")){
                        //AttendanceAllotment
                        Intent intent=new Intent(SupervisorActivity.this, AttendanceAllotment.class);
                        intent.putExtra("user_type","Category");
                        startActivity(intent);

                    }else if(taskType.mID.equalsIgnoreCase("3")){
                        //ReceviceLostLinenActivity
                        Intent intent=new Intent(SupervisorActivity.this, ReceviceLostLinenActivity.class);
                        intent.putExtra("user_type","Category");
                        intent.putExtra("task_id","3");
                        startActivity(intent);
                    }else if(taskType.mID.equalsIgnoreCase("4")){
                        //PenalityActivity
                        Intent intent=new Intent(SupervisorActivity.this, PenaltyLinenActivity.class);
                        intent.putExtra("user_type","Category");
                        intent.putExtra("task_id","4");
                        startActivity(intent);
                    }else if(taskType.mID.equalsIgnoreCase("5")){
                        //AttnedanceInActivity
                        Intent intent=new Intent(SupervisorActivity.this, AttendanceInActivity.class);
                        intent.putExtra("user_type","Category");
                        intent.putExtra("task_id","5");
                        startActivity(intent);
                    }else if(taskType.mID.equalsIgnoreCase("6")){
                        //AttendanceOutActivity
                        Intent intent=new Intent(SupervisorActivity.this, AttendanceListActivity.class);
                        intent.putExtra("user_type","Category");
                        intent.putExtra("task_id","6");
                        startActivity(intent);
                    }else if(taskType.mID.equalsIgnoreCase("7")){
                        //AttendanceOutActivity
                        Intent intent=new Intent(SupervisorActivity.this, UserListActivity.class);
                        intent.putExtra("user_type","Category");
                        intent.putExtra("task_id","7");
                        startActivity(intent);
                    }else if(taskType.mID.equalsIgnoreCase("8")){
                        //preCleaning
                        Intent intent=new Intent(SupervisorActivity.this, PreCleaning.class);
                        intent.putExtra("user_type","Category");
                        intent.putExtra("task_id","8");
                        startActivity(intent);

                    }
                }
            });

        }
        @Override
        public int getItemCount() {
            return listdata.size();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv=(TextView)itemView.findViewById(R.id.tv_view);
        }
    }
}