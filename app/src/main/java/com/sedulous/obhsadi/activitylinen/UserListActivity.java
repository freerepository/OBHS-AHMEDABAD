package com.sedulous.obhsadi.activitylinen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.sedulous.obhsadi.ModelLinen.UserListModel;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.O;
import com.sedulous.obhsadi.service.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {
    public static String USER_LIST_API="http://obhsadi.projectrailway.in/api/atd/getallUsers";
    public static String GET_SEARCH_MYPRODUCTS = "http://obhsadi.projectrailway.in/api/atd/get_attendance_by_user";
    public static String DELETE_NOTIFICATION_API="http://obhsadi.projectrailway.in/atd/Linen/delUser";
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    ImageView v_back;
    TextView tv_empty_data;
    Button bt_view_register;
    AlertDialog dialog;
    View iv_search;
    EditText etSearch;
    ArrayList<UserListModel.UserItem> question_list=new ArrayList<>();
    UserListAdapter userListAdapter;
    UserListModel userListModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        bt_view_register=findViewById(R.id.bt_view_register);
        v_back=findViewById(R.id.v_back_btn);
        iv_search = findViewById(R.id.iv_search);
        etSearch=findViewById(R.id.et_search);
        v_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        bt_view_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserListActivity.this,RegistraionActivity.class);
                startActivity(intent);
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (userListAdapter != null && userListAdapter.linenList.size() > 0) {

                    ArrayList<UserListModel.UserItem> mUserList = new ArrayList();
                    for (int n = 0; n < userListModel.mUserList.size(); n++) {
                        if (!TextUtils.isEmpty(userListModel.mUserList.get(n).mEMPM_name) && userListModel.mUserList.get(n).mEMPM_name.toUpperCase().contains(s.toString().toUpperCase())) {
                            mUserList.add(userListModel.mUserList.get(n));

                        }
                    }
                    userListAdapter.linenList = mUserList;
                    userListAdapter.notifyDataSetChanged();
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
                if(!TextUtils.isEmpty(etSearch.getText().toString().trim())){
                    Toast.makeText(UserListActivity.this, "Search Name", Toast.LENGTH_SHORT).show();
                }

                getProduct(etSearch.getText().toString());
            }


        });
        tv_empty_data=findViewById(R.id.tv_empty_data);
        recyclerView = (RecyclerView) findViewById(R.id.rv_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userListAdapter = new UserListAdapter(new ArrayList<>());
        recyclerView.setAdapter(userListAdapter);
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if(O.checkNetwork(UserListActivity.this)) {
                getUserList();
            }else{
                srl.setRefreshing(false);
            }
        });
        getUserList();
    }

    //<---------------get product------------->//
    private void getProduct(String text) {
        if (TextUtils.isEmpty(text)) text = "";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("empl_id", text);
            jsonObject.put("depot_code",  PreferenceUtil.getDepot(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        StringRequest request = new StringRequest(Request.Method.POST, GET_SEARCH_MYPRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("pro_search", response);

                userListModel = new Gson().fromJson(response, UserListModel.class);
                userListAdapter.linenList = userListModel.mUserList;
                userListAdapter.notifyDataSetChanged();


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

    private void getUserList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_code", PreferenceUtil.getDepot(UserListActivity.this));
//            Toast.makeText(this, ""+PreferenceUtil.getDepot(UserListActivity.this), Toast.LENGTH_SHORT).show();
            srl.setRefreshing(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, USER_LIST_API, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("response_req", response.toString());
                        try {

                            userListModel = new Gson().fromJson(response.toString(), UserListModel.class);
                            if (userListModel.mUserList.size() > 0) {
                                userListAdapter.linenList=question_list=userListModel.mUserList;
                                recyclerView.setAdapter(userListAdapter);


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

    public class UserListAdapter extends RecyclerView.Adapter<UserListActivity.UserListAdapter.MyViewHolder> {
        private Object object;
        private ArrayList<UserListModel.UserItem> linenList;

        public UserListAdapter(Object object) {
            this.object = object;
            this.linenList = linenList;
        }

        @Override
        public UserListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.header_user_list, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull UserListAdapter.MyViewHolder holder, int pos) {
            holder.setIsRecyclable(false);
            int position=pos;
            int c = position + 1;
            holder.tv1.setText("" + c);
            holder.tv2.setText(linenList.get(position).mEMPM_userID);
            holder.tv3.setText(linenList.get(position).mEMPM_name);
            holder.tv4.setText(linenList.get(position).mEMPM_type);
//            holder.tv5.setText(linenList.get(position).depot_code);
            holder.tv6.setText(linenList.get(position).mEMPM_phone_no);

            if (linenList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tv_empty_data.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                tv_empty_data.setVisibility(View.GONE);
            }
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteList();
                }

                private void DeleteList() {
                    final Dialog dialog = new Dialog(UserListActivity.this, R.style.Dialog);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    //dialog.setCancelable(false);
                    dialog.setContentView(R.layout.diolog_delete);
                    final TextView tv_update = dialog.findViewById(R.id.tv_update);
                    tv_update.setText(linenList.get(position).mEMPM_name);


                    dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            if (TextUtils.isEmpty(et_date.getText().toString())) {
//                                Toast.makeText(LaundryReceived.this, "Select Date", Toast.LENGTH_SHORT).show();
//                            } else {
                            try {
                                OkDataDelete(dialog,linenList.get(position).id);
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
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
                private void OkDataDelete(Dialog dialog, String id) {
                    final JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("row_id", id);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final String requestBody = jsonObject.toString();
                    Log.e("reqbody", requestBody);
                    showLoading("Please wait...");

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            DELETE_NOTIFICATION_API, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideLoading();
                            try {
                                Log.e("response", response);

                                JSONObject jsonResponse = null;
                                try {
                                    jsonResponse = new JSONObject(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (jsonResponse != null && jsonResponse.has("message")) {
                                    String message = jsonResponse.getString("message");

                                    showConfirmationDialog(message);
                                } else {
                                    showConfirmationDialog(response);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideLoading();
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws com.android.volley.AuthFailureError {
                            try {
                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                return null;
                            }
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(UserListActivity.this);
                    requestQueue.add(stringRequest);
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
            TextView tv1,tv2,tv3, tv4,tv5,tv6;
            ImageView iv_delete;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv1=(TextView)itemView.findViewById(R.id.tv_index_number);
                tv2=(TextView)itemView.findViewById(R.id.tv_user_id);
                tv3=(TextView)itemView.findViewById(R.id.tv_name);
                tv4=(TextView)itemView.findViewById(R.id.tv_trainNo);
//                tv5=(TextView)itemView.findViewById(R.id.tv_station);
                tv6=(TextView)itemView.findViewById(R.id.tv_date);
                iv_delete=(ImageView) itemView.findViewById(R.id.iv_delete);
            }
        }
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
        iv_logo.setImageDrawable(getDrawable(R.mipmap.logo));
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
    public void showConfirmationDialog(String strMessage) {
        final Dialog dialog = new Dialog(UserListActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(strMessage);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(UserListActivity.this, UserListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }



}
