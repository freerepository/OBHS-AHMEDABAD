package com.sedulous.obhsadi.activitylinen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sedulous.obhsadi.ModelLinen.AttendanceListModel;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.O;
import com.sedulous.obhsadi.service.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EmpInListActvity extends AppCompatActivity {
    public static String Attendance_Out="http://obhsadi.projectrailway.in/api/atd/get_current_attendancedata";
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    ImageView v_back;
    TextView tv_empty_data;
    ArrayList<AttendanceListModel.UserItem> question_list=new ArrayList<>();
    EmpInListAdapter attendanceAdapter;
    AttendanceListModel attendanceListModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empin_list_actvity);


        v_back=findViewById(R.id.v_close);
        v_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tv_empty_data=findViewById(R.id.tv_empty_data);
        recyclerView = (RecyclerView) findViewById(R.id.rv_attendance_out);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendanceAdapter = new EmpInListAdapter(new ArrayList<>());

        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if(O.checkNetwork(EmpInListActvity.this)) {
                getAttendanceOutData();
            }else{
                srl.setRefreshing(false);
            }
        });
        getAttendanceOutData();
    }


    private void getAttendanceOutData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RAPD_PunchDateTime", O.getcurrentDateAndTime());
            jsonObject.put("depot_code", PreferenceUtil.getDepot(this));
            jsonObject.put("login_id", PreferenceUtil.getUserIdSelected(this));

            srl.setRefreshing(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Attendance_Out, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("response_req", response.toString());
                        try {

                            attendanceListModel = new Gson().fromJson(response.toString(), AttendanceListModel.class);
                            if (attendanceListModel.mUserItemList.size() > 0) {
                                attendanceAdapter.linenList=question_list=attendanceListModel.mUserItemList;
                                recyclerView.setAdapter(attendanceAdapter);


                            } else {

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }

    public class EmpInListAdapter extends RecyclerView.Adapter<EmpInListAdapter.MyViewHolder> {
        private Object object;
        private ArrayList<AttendanceListModel.UserItem> linenList;

        public EmpInListAdapter(Object object) {
            this.object = object;
            this.linenList = linenList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.header_employee_list, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            int c = position + 1;
            holder.tv1.setText("" + c);
            holder.tv2.setText(linenList.get(position).RAPD_BioID);
            holder.tv3.setText(linenList.get(position).EMPM_Name);
            holder.tv4.setText(linenList.get(position).RAPD_Field4);
            holder.tv5.setText(linenList.get(position).mCoach);
            holder.tv6.setText(linenList.get(position).RAPD_PunchDateTime);
            holder.tv7.setText(linenList.get(position).address);

            if (linenList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tv_empty_data.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                tv_empty_data.setVisibility(View.GONE);
            }

        }
        @Override
        public int getItemCount() {

            if (linenList != null)
                return linenList.size();
            else
                return 0;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv1,tv2,tv3, tv4,tv5,tv6,tv7;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv1=(TextView)itemView.findViewById(R.id.tv_index_number);
                tv2=(TextView)itemView.findViewById(R.id.tv_user_id);
                tv3=(TextView)itemView.findViewById(R.id.tv_name);
                tv4=(TextView)itemView.findViewById(R.id.tv_trainNo);
                tv5=(TextView)itemView.findViewById(R.id.tv_coach);
                tv6=(TextView)itemView.findViewById(R.id.tv_date);
                tv7=(TextView)itemView.findViewById(R.id.tv_address);
            }
        }
    }

}

