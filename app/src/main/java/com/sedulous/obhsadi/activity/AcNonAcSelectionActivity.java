package com.sedulous.obhsadi.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sedulous.obhsadi.R;

public class AcNonAcSelectionActivity extends AppCompatActivity {

    private Button btnAc,btnNonAc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ac_non_ac_selection);
        try {
            final String strPassengerName = getIntent().getStringExtra("strPassengerName");
            final String strTrainNumber = getIntent().getStringExtra("strTrainNumber");
            final String strCoachNumber = getIntent().getStringExtra("strCoachNumber");
            final String strSeatNumber = getIntent().getStringExtra("strSeatNumber");
            final String strPNRNumber = getIntent().getStringExtra("strPNRNumber");
            final String strIdentityNo = getIntent().getStringExtra("strIdentityNo");
            final String strMobile = getIntent().getStringExtra("strMobile");
            final String strSignatureFileName = getIntent().getStringExtra("strSignatureFileName");
            final String strSignatureFilePath = getIntent().getStringExtra("strSignatureFilePath");
            final String strIdentityFilePath = getIntent().getStringExtra("strIdentityFilePath");
            final String strFeedbackType = getIntent().getStringExtra("feedback_type");

            final String strTTEName = getIntent().getStringExtra("strTTEName");
            final String strTTEIdNumber = getIntent().getStringExtra("strTTEIdNumber");

            btnAc = (Button) findViewById(R.id.btn_ac);
            btnNonAc = (Button) findViewById(R.id.btn_non_ac);
            if(strCoachNumber.startsWith("S")||strCoachNumber.startsWith("s")){
                btnAc.setVisibility(View.GONE);
            }else{
                btnNonAc.setVisibility(View.GONE);
            }
            btnAc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnAc.setBackgroundResource(R.drawable.button_orange_bg);
                    btnNonAc.setBackgroundResource(R.drawable.button_blue_bg);
                    if (strFeedbackType.equalsIgnoreCase("passenger")) {
                        Intent intent = new Intent(AcNonAcSelectionActivity.this, PassengerFeedbackAcActivity.class);
                        intent.putExtra("inspection_type", "AC");
                        intent.putExtra("strPassengerName", strPassengerName);
                        intent.putExtra("strTrainNumber", strTrainNumber);
                        intent.putExtra("strCoachNumber", strCoachNumber);
                        intent.putExtra("strSeatNumber", strSeatNumber);
                        intent.putExtra("strPNRNumber", strPNRNumber);
                        intent.putExtra("strIdentityNo", strIdentityNo);
                        intent.putExtra("strMobile", strMobile);
                        intent.putExtra("strSignatureFileName", strSignatureFileName);
                        intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                        intent.putExtra("strIdentityFilePath", strIdentityFilePath);
                        startActivity(intent);
                    } else if (strFeedbackType.equalsIgnoreCase("tte")) {
                        Intent intent = new Intent(AcNonAcSelectionActivity.this, TteFeedbackActivity.class);
                        intent.putExtra("inspection_type", "AC");
                        intent.putExtra("strTrainNumber", strTrainNumber);
                        intent.putExtra("strTTEName", strTTEName);
                        intent.putExtra("strTTEIdNumber", strTTEIdNumber);
                        intent.putExtra("strMobile", strMobile);
                        intent.putExtra("strSignatureFileName", strSignatureFileName);
                        intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                        startActivity(intent);
                    }
                }
            });

            btnNonAc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnAc.setBackgroundResource(R.drawable.button_blue_bg);
                    btnNonAc.setBackgroundResource(R.drawable.button_orange_bg);
                    if (strFeedbackType.equalsIgnoreCase("passenger")) {
                        Intent intent = new Intent(AcNonAcSelectionActivity.this, PassengerFeedbackNonAcActivity.class);
                        intent.putExtra("inspection_type", "Non AC");
                        intent.putExtra("strPassengerName", strPassengerName);
                        intent.putExtra("strTrainNumber", strTrainNumber);
                        intent.putExtra("strCoachNumber", strCoachNumber);
                        intent.putExtra("strSeatNumber", strSeatNumber);
                        intent.putExtra("strPNRNumber", strPNRNumber);
                        intent.putExtra("strIdentityNo", strIdentityNo);
                        intent.putExtra("strMobile", strMobile);
                        intent.putExtra("strSignatureFileName", strSignatureFileName);
                        intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                        intent.putExtra("strIdentityFilePath", strIdentityFilePath);
                        startActivity(intent);
                    } else if (strFeedbackType.equalsIgnoreCase("tte")) {
                        Intent intent = new Intent(AcNonAcSelectionActivity.this, TteFeedbackActivity.class);
                        intent.putExtra("inspection_type", "AC");
                        intent.putExtra("strTrainNumber", strTrainNumber);
                        intent.putExtra("strTTEName", strTTEName);
                        intent.putExtra("strTTEIdNumber", strTTEIdNumber);
                        intent.putExtra("strMobile", strMobile);
                        intent.putExtra("strSignatureFileName", strSignatureFileName);
                        intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                        startActivity(intent);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
