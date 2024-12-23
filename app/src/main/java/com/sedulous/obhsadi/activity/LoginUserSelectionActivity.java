package com.sedulous.obhsadi.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sedulous.obhsadi.R;

public class LoginUserSelectionActivity extends AppCompatActivity {

    private Button btnInspector,btnPassenger,btnTTE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_user_selection);
        btnInspector=(Button)findViewById(R.id.btn_inspector);
        btnPassenger=(Button)findViewById(R.id.btn_passenger);
        btnTTE=(Button)findViewById(R.id.btn_tte);

        btnInspector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnInspector.setBackgroundResource(R.drawable.button_orange_bg);
                btnPassenger.setBackgroundResource(R.drawable.button_blue_bg);
                btnTTE.setBackgroundResource(R.drawable.button_blue_bg);
                Intent intent=new Intent(LoginUserSelectionActivity.this,InspectorReviewActivity.class);
                intent.putExtra("user_type","Inspector");
                startActivity(intent);
            }
        });

        btnPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnInspector.setBackgroundResource(R.drawable.button_blue_bg);
                btnPassenger.setBackgroundResource(R.drawable.button_orange_bg);
                btnTTE.setBackgroundResource(R.drawable.button_blue_bg);
                Intent intent=new Intent(LoginUserSelectionActivity.this,PassengerDetailsActivity.class);
                intent.putExtra("user_type","Passenger");
                startActivity(intent);
            }
        });

        btnTTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnInspector.setBackgroundResource(R.drawable.button_blue_bg);
                btnPassenger.setBackgroundResource(R.drawable.button_blue_bg);
                btnTTE.setBackgroundResource(R.drawable.button_orange_bg);
                Intent intent=new Intent(LoginUserSelectionActivity.this,TteDetailActivity.class);
                intent.putExtra("user_type","TTE");
                startActivity(intent);
            }
        });
    }


}
