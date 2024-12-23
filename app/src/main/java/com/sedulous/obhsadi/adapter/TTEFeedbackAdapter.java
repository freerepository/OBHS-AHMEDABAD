package com.sedulous.obhsadi.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.sedulous.obhsadi.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TTEFeedbackAdapter extends RecyclerView.Adapter {

    private Context context;
    private JSONArray list;
    private RatingSelectionInterface ratingSelectionInterface;
    private boolean isEnglish=true;

    public TTEFeedbackAdapter(Context context, JSONArray list, RatingSelectionInterface ratingSelectionInterface, boolean isEnglish)
    {
        this.context=context;
        this.list=list;
        this.ratingSelectionInterface=ratingSelectionInterface;
        this.isEnglish=isEnglish;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_adapter_row_layout,null);
        return (new FeedbackHolder(view));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        try {
            FeedbackHolder mHolder = (FeedbackHolder) holder;
            final JSONObject jsonObject=list.getJSONObject(position);
            mHolder.tvIndex.setText((position + 1) + "");

            if (isEnglish)
                mHolder.tvIndexText.setText(jsonObject.getString("question"));
            else
                mHolder.tvIndexText.setText(jsonObject.getString("hindi_question"));

            mHolder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int id) {
                    if (id==R.id.cb_rating_5){
                        try {
                            ratingSelectionInterface.ratingSelection(jsonObject.getString("id"),5);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if (id==R.id.cb_rating_4){
                        try {
                            ratingSelectionInterface.ratingSelection(jsonObject.getString("id"),4);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if (id==R.id.cb_rating_3){
                        try {
                            ratingSelectionInterface.ratingSelection(jsonObject.getString("id"),3);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if (id==R.id.cb_rating_2){
                        try {
                            ratingSelectionInterface.ratingSelection(jsonObject.getString("id"),2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if (id==R.id.cb_rating_1){
                        try {
                            ratingSelectionInterface.ratingSelection(jsonObject.getString("id"),1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.length();
    }

    public class FeedbackHolder extends RecyclerView.ViewHolder
    {
        private TextView tvIndex,tvIndexText;
        private RadioGroup radioGroup;
        private RadioButton cb5,cb4,cb3,cb2,cb1;

        public FeedbackHolder(View itemView) {
            super(itemView);
            tvIndex=(TextView)itemView.findViewById(R.id.tv_index_number);
            tvIndexText=(TextView)itemView.findViewById(R.id.tv_index_text);
            radioGroup=(RadioGroup)itemView.findViewById(R.id.radioGroup);

            cb5=(RadioButton)itemView.findViewById(R.id.cb_rating_5);
            cb4=(RadioButton)itemView.findViewById(R.id.cb_rating_4);
            cb3=(RadioButton)itemView.findViewById(R.id.cb_rating_3);
            cb2=(RadioButton)itemView.findViewById(R.id.cb_rating_2);
            cb1=(RadioButton)itemView.findViewById(R.id.cb_rating_1);

        }
    }

    public interface RatingSelectionInterface{
        void ratingSelection(String id,float rating);
    }

}
