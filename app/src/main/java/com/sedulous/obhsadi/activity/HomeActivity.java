package com.sedulous.obhsadi.activity;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.sedulous.obhsadi.Model.TaskCatModel;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.O;
import com.sedulous.obhsadi.service.PreferenceUtil;
import com.sedulous.obhsadi.service.VolleySingleton;
import com.sedulous.obhsadi.service.WebServicesURLClass;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    TaskTypeAdapter adapter;
    ArrayList<TaskCatModel.Task> taskTypeArrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView=findViewById(R.id.rv);
        srl=findViewById(R.id.srl);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new TaskTypeAdapter(this,taskTypeArrayList);
        recyclerView.setAdapter(adapter);
        if (O.checkNetwork(HomeActivity.this)){
            getTaskType();
        }else {

        }
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (O.checkNetwork(HomeActivity.this)){
                    getTaskType();
                }else {
                    srl.setRefreshing(false);
                }
            }
        });

        findViewById(R.id.v_log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog(HomeActivity.this);
            }
        });

    }

    public void getTaskType(){
        srl.setRefreshing(true);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, WebServicesURLClass.GET_TASK_CAT,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    srl.setRefreshing(false);
                    Log.e("GetTaskTypeRes", "" + response);
                    if ( !response.toString().isEmpty()) {
                        taskTypeArrayList.clear();
                        TaskCatModel responseClass=(new Gson()).fromJson(response.toString(), TaskCatModel.class);
                        if (responseClass.mStatus==1) {
                            taskTypeArrayList.addAll(responseClass.mTasks);
                            if (taskTypeArrayList.size() > 0) {
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(HomeActivity.this, "Please try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);
            }
        });
        VolleySingleton.getInstance(HomeActivity.this).addToRequestQueue(stringRequest);
    }

    public class TaskTypeAdapter extends RecyclerView.Adapter<TaskTypeAdapter.TaskTypeHolder> {

        private Context context;
        private ArrayList<TaskCatModel.Task> typeArrayList=new ArrayList<>();

        public TaskTypeAdapter(Context context,ArrayList<TaskCatModel.Task> typeArrayList){
            this.context=context;
            this.typeArrayList=typeArrayList;
        }

        @NonNull
        @Override
        public TaskTypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_type,null);
            return (new TaskTypeHolder(view));
        }

        @Override
        public void onBindViewHolder(TaskTypeHolder holder, final int position) {
            holder.setIsRecyclable(false);
            TaskCatModel.Task taskType=typeArrayList.get(position);
            holder.tvTitle.setText(taskType.mTask);
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //amardeep
                    if (!checkAndRequestPermissions()) {
                        checkAndRequestPermissions();
                    }else {
                        if (taskType.mId.equalsIgnoreCase("3")) {
                            //feedback
                            Intent intent = new Intent(HomeActivity.this, LoginUserSelectionActivity.class);
                            intent.putExtra("user_type", "Passenger");
                            startActivity(intent);
                        } else if (taskType.mId.equalsIgnoreCase("1")) {
                            //Group picture
                            Intent intent = new Intent(HomeActivity.this, GroupPicActivity.class);
                            intent.putExtra("user_type", "Passenger");
                            intent.putExtra("task_id", "2");
                            startActivity(intent);
                        } else if (taskType.mId.equalsIgnoreCase("2")) {
                            //Pre Cleaning Picture
                            Intent intent = new Intent(HomeActivity.this, CleaningPicActivity.class);
                            intent.putExtra("user_type", "Passenger");
                            intent.putExtra("task_id", "3");
                            startActivity(intent);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return typeArrayList.size();
        }

        public class TaskTypeHolder extends RecyclerView.ViewHolder{

            private TextView tvTitle;
            public TaskTypeHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle=itemView.findViewById(R.id.tv_title);
            }
        }
    }

    public void showLogoutAlertDialog(final Context c) {
        AlertDialog.Builder builder=new AlertDialog.Builder(c);
        builder.setTitle("Logout Confirm");
        builder.setMessage("Do you want to logout ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                PreferenceUtil.clearPref(c);
                Intent intent = new Intent(c, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                c.startActivity(intent);
                finishAffinity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    //amardeep
    private boolean checkAndRequestPermissions() {
        if (SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager() ) {
            int loc = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int loc2 = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            int networkstate = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE);
            int storage = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int storage_write = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int camera = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (loc2 != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (loc != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (networkstate != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (storage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (storage_write != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 111);
                return false;
            }
        } else if (SDK_INT >= Build.VERSION_CODES.R) {

            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
            return false;
        } else {
            int loc = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int loc2 = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            int networkstate = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE);
            int storage = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int storage_write = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int camera = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (loc2 != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (loc != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (networkstate != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (storage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (storage_write != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 111);
                return false;
            }
        }

        return true;
    }

}
