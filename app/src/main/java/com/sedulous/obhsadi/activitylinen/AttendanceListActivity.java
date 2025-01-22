package com.sedulous.obhsadi.activitylinen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sedulous.obhsadi.ModelLinen.AttendanceListModel;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.O;
import com.sedulous.obhsadi.service.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AttendanceListActivity extends AppCompatActivity {
    public static String Attendance_Out = "http://obhsadi.projectrailway.in/api/atd/get_current_attendancelist";
    public static String GET_SEARCH_MYPRODUCTS = "http://obhsadi.projectrailway.in/api/atd/get_attendance_by_user";

    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    ImageView v_back;
    View iv_search;
    EditText etSearch;
    ArrayList<AttendanceListModel.UserItem> question_list = new ArrayList<>();
    AttendanceAdapter attendanceAdapter;
    AttendanceListModel attendanceListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_list);
        v_back = findViewById(R.id.v_back_btn);

        v_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        iv_search = findViewById(R.id.iv_search);
        etSearch = findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (attendanceListModel != null && attendanceListModel.mUserItemList.size() > 0) {

                    ArrayList<AttendanceListModel.UserItem> mList = new ArrayList();
                    for (int n = 0; n < attendanceListModel.mUserItemList.size(); n++) {
                        if (!TextUtils.isEmpty(attendanceListModel.mUserItemList.get(n).EMPM_Name) && attendanceListModel.mUserItemList.get(n).EMPM_Name.toUpperCase().contains(s.toString().toUpperCase())) {
                            mList.add(attendanceListModel.mUserItemList.get(n));
                        }else if(!TextUtils.isEmpty(attendanceListModel.mUserItemList.get(n).RAPD_Field4) && attendanceListModel.mUserItemList.get(n).RAPD_Field4.toUpperCase().contains(s.toString().toUpperCase())){
                            mList.add(attendanceListModel.mUserItemList.get(n));
                        }

                    }
                    attendanceAdapter.linenList = mList;
                    attendanceAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String s=et_search.getText().toString();
//                O.closeKeyboard(BuyClothListProductItems.this, (getCurrentFocus()));
//                if (getcloth!=null && getcloth.clothList.size() > 0) {
//
//                    ArrayList<Getcloth.Item> mList=new ArrayList();
//                    for(int n=0; n<getcloth.clothList.size(); n++){
//                        if(!TextUtils.isEmpty(getcloth.clothList.get(n).mproduct_name) && getcloth.clothList.get(n).mproducts_name.toUpperCase().contains(s.toString().toUpperCase())){
//                            mList.add(getcloth.clothList.get(n));
//                        }
//                    }
//                    gridViewAdapter.list = mList;
//                    gridViewAdapter.notifyDataSetChanged();
//                }
                if (!TextUtils.isEmpty(etSearch.getText().toString().trim())) {
                    Toast.makeText(AttendanceListActivity.this, "Search Name", Toast.LENGTH_SHORT).show();
                }

                getProduct(etSearch.getText().toString());
            }


        });
        recyclerView = (RecyclerView) findViewById(R.id.rv_attendance_out);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendanceAdapter = new AttendanceAdapter(new ArrayList<>());
        recyclerView.setAdapter(attendanceAdapter);
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(AttendanceListActivity.this)) {
                getAttendanceOutData();
            } else {
                srl.setRefreshing(false);
            }
        });
        getAttendanceOutData();
    }

    //<---------------get product------------->//
    private void getProduct(String text) {
        if (TextUtils.isEmpty(text)) text = "";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("empl_id", text);
            jsonObject.put("depot_code", PreferenceUtil.getDepot(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        StringRequest request = new StringRequest(Request.Method.POST, GET_SEARCH_MYPRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("pro_search", response);

                attendanceListModel = new Gson().fromJson(response, AttendanceListModel.class);
                attendanceAdapter.linenList = attendanceListModel.mUserItemList;
                attendanceAdapter.notifyDataSetChanged();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("kumar", error.toString());

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
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getAttendanceOutData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RAPD_PunchDateTime", O.getcurrentDateAndTime());
            jsonObject.put("depot_code", PreferenceUtil.getDepot(this));
            jsonObject.put("login_id", PreferenceUtil.getUserIdSelected(this));

//            Toast.makeText(this, "RAPD_PunchDateTime "+O.getcurrentDateAndTime(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "depot_code "+PreferenceUtil.getDepot(this), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "login_id "+PreferenceUtil.getUserIdSelected(this), Toast.LENGTH_SHORT).show();

            srl.setRefreshing(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Attendance_Out, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("responseList", response.toString());
                        try {
                            attendanceListModel = new Gson().fromJson(response.toString(), AttendanceListModel.class);
                            if (attendanceListModel.mUserItemList.size() > 0) {
                                attendanceAdapter.linenList = question_list = attendanceListModel.mUserItemList;
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
                Toast.makeText(AttendanceListActivity.this, "Error: File not Show", Toast.LENGTH_SHORT).show();


            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }

    public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder> {
        private Object object;
        private ArrayList<AttendanceListModel.UserItem> linenList;

        public AttendanceAdapter(Object object) {
            this.object = object;
            this.linenList = linenList;
        }

        @Override
        public AttendanceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.header_attendance_out, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @SuppressLint("RecyclerView")
        @Override
        public void onBindViewHolder(@NonNull AttendanceAdapter.MyViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            int c = position + 1;
            holder.tv1.setText("" + c);
            holder.tv2.setText(linenList.get(position).RAPD_BioID);
            holder.tv3.setText(linenList.get(position).EMPM_Name);
            holder.tv4.setText(linenList.get(position).RAPD_Field4);
            holder.tv5.setText(linenList.get(position).mCoach);
            holder.tv6.setText(linenList.get(position).RAPD_PunchDateTime);
            holder.tv7.setText(linenList.get(position).address);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AttendanceListActivity.this, AttendanceOutActivity.class);
                    intent.putExtra("data", linenList.get(position).id);
                    intent.putExtra("punch_data_time", linenList.get(position).RAPD_PunchDateTime);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {

            if (linenList != null)
                return linenList.size();
            else
                return 0;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv1, tv2, tv3, tv4, tv5, tv6,tv7;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv1 = (TextView) itemView.findViewById(R.id.tv_index_number);
                tv2 = (TextView) itemView.findViewById(R.id.tv_user_id);
                tv3 = (TextView) itemView.findViewById(R.id.tv_name);
                tv4 = (TextView) itemView.findViewById(R.id.tv_trainNo);
                tv5 = (TextView) itemView.findViewById(R.id.tv_coach);
                tv6 = (TextView) itemView.findViewById(R.id.tv_date);
                tv7 = (TextView) itemView.findViewById(R.id.tv_address);
            }
        }
    }

}
