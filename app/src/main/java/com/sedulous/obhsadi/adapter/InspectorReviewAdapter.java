package com.sedulous.obhsadi.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.activity.InspectorReviewActivity;
import com.sedulous.obhsadi.service.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;


public class InspectorReviewAdapter extends RecyclerView.Adapter {

    private Context context;
    private JSONArray list;
    private InspectorReviewInterface reviewInterface;
    private Map<String,String> map=new HashMap<>();

    public InspectorReviewAdapter(Context context, JSONArray list, Map<String,String> receivedMap, InspectorReviewInterface reviewInterface)
    {
        this.context=context;
        this.list=list;
        this.map=receivedMap;
        this.reviewInterface=reviewInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.inspecter_review_row_layout,null);
        FeedbackHolder holder=new FeedbackHolder(view);
        view.setTag(holder);
        return (holder);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int pos) {
        try {
            final int position=pos;
            final FeedbackHolder mHolder = (FeedbackHolder) holder;
            mHolder.setIsRecyclable(false);
            mHolder.tvIndex.setText((position + 1) + "");
            mHolder.tvIndexText.setText(list.getJSONObject(position).getString("penalty_items"));
            mHolder.tvQtyPerJanitor.setText(list.getJSONObject(position).getString("required_quantity_of_item_per_janitor"));
            mHolder.etQtyIssuedAtOrigin.setText(list.getJSONObject(position).getString("quantity_issued_by_organization"));
            mHolder.etShortFall.setText(list.getJSONObject(position).getString("shortfall"));
            mHolder.checkBox.setText( list.getJSONObject(position).getString("penalty"));
            mHolder.checkBox.setTag(position);

            if (!list.getJSONObject(position).getString("shortfall").isEmpty()) {
                mHolder.checkBox.setEnabled(false);
                mHolder.checkBox.setChecked(true);
                mHolder.etQtyIssuedAtOrigin.setEnabled(false);
                mHolder.etShortFall.setEnabled(false);
            }

            for (Map.Entry mEntry: map.entrySet())
            {
                if (mEntry.getKey().equals(list.getJSONObject(position).getString("id")))
                {
                    mHolder.checkBox.setChecked(true);
                    try {
                        String srValue=mEntry.getValue().toString();
                        String aa=srValue.substring(mEntry.getValue().toString().indexOf(",")+1);
                        String aaa=aa.substring(aa.indexOf(",")+1);
                        String aaaa=aaa.substring(aaa.indexOf(",")+1);
                        String qtyIssued= Util.before(aa,",");
                        String shrtfall=Util.before(aaa,",");
                        String penaltyAmt=Util.before(aaaa,",");
                        String qtyPerJanitr=Util.after(aaaa,",");

                        mHolder.etQtyIssuedAtOrigin.setText(qtyIssued);
                        mHolder.etShortFall.setText(shrtfall);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            mHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        if (!mHolder.etShortFall.getText().toString().trim().isEmpty()) {
                            try {
                                reviewInterface.itemSelect(list.getJSONObject(position).getString("id"), list.getJSONObject(position).getString("category_id"), mHolder.etQtyIssuedAtOrigin.getText().toString().trim(), mHolder.etShortFall.getText().toString().trim(),list.getJSONObject(position).getString("penalty").trim(),list.getJSONObject(position).getString("required_quantity_of_item_per_janitor").trim());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else
                            mHolder.checkBox.setChecked(false);
                    }else {
                        try {
                            reviewInterface.itemUnSelect(list.getJSONObject(position).getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            mHolder.etQtyIssuedAtOrigin.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        if (!editable.toString().isEmpty()) {
                            if (list.getJSONObject(position).getString("manual").equalsIgnoreCase("YES")){
                                mHolder.etShortFall.setEnabled(true);
                            }else {
                                float maxValue = InspectorReviewActivity.totalAssignedJanitor * Float.parseFloat(list.getJSONObject(position).getString("required_quantity_of_item_per_janitor").trim());
                                if (Float.parseFloat(editable.toString()) > maxValue) {
                                    mHolder.etQtyIssuedAtOrigin.setText(maxValue + "");
                                    mHolder.etShortFall.setText("0");
                                } else {
                                    float shortFallDiff = maxValue - Float.parseFloat(editable.toString());
                                    mHolder.etShortFall.setText(shortFallDiff + "");
                                }
                                mHolder.etShortFall.setEnabled(false);
                            }
                        }else {
                            mHolder.etShortFall.setText("");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            mHolder.etShortFall.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        float QtyIssued = Float.parseFloat(mHolder.etQtyIssuedAtOrigin.getText().toString().trim());
                        float Shortfall = Float.parseFloat(mHolder.etShortFall.getText().toString().trim());

                        if (!mHolder.etShortFall.getText().toString().trim().isEmpty()) {
                            if (list.getJSONObject(position).getString("manual").equalsIgnoreCase("YES")){
                                mHolder.checkBox.setChecked(true);
                                reviewInterface.itemSelect(list.getJSONObject(position).getString("id"), list.getJSONObject(position).getString("category_id"), QtyIssued + "", Shortfall + "", list.getJSONObject(position).getString("penalty").trim(), list.getJSONObject(position).getString("required_quantity_of_item_per_janitor").trim());

                            }else {
                                float maxValue = InspectorReviewActivity.totalAssignedJanitor * Float.parseFloat(list.getJSONObject(position).getString("required_quantity_of_item_per_janitor").trim());
                                if (Float.parseFloat(editable.toString()) > maxValue)
                                    mHolder.etShortFall.setText(maxValue + "");

                                if (QtyIssued > 0 && Shortfall >= 0) {
                                    mHolder.checkBox.setChecked(true);
                                    reviewInterface.itemSelect(list.getJSONObject(position).getString("id"), list.getJSONObject(position).getString("category_id"), QtyIssued + "", Shortfall + "", list.getJSONObject(position).getString("penalty").trim(), list.getJSONObject(position).getString("required_quantity_of_item_per_janitor").trim());

                                } else if (QtyIssued == 0 && Shortfall > 0) {
                                    mHolder.checkBox.setChecked(true);
                                    reviewInterface.itemSelect(list.getJSONObject(position).getString("id"), list.getJSONObject(position).getString("category_id"), QtyIssued + "", Shortfall + "", list.getJSONObject(position).getString("penalty").trim(), list.getJSONObject(position).getString("required_quantity_of_item_per_janitor").trim());
                                }
                            }
                        }else {
                            mHolder.checkBox.setChecked(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (JSONException e){
            e.printStackTrace();
        }
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class FeedbackHolder extends RecyclerView.ViewHolder
    {
        private TextView tvIndex,tvIndexText,tvQtyPerJanitor;
        private CheckBox checkBox;
        private EditText etQtyIssuedAtOrigin, etShortFall;

        public FeedbackHolder(View itemView) {
            super(itemView);
            tvIndex=(TextView)itemView.findViewById(R.id.tv_index_number);
            tvIndexText=(TextView)itemView.findViewById(R.id.tv_index_text);
            tvQtyPerJanitor=(TextView)itemView.findViewById(R.id.tv_qty_per_janitor);
            checkBox=(CheckBox) itemView.findViewById(R.id.checkBox);
            etQtyIssuedAtOrigin =(EditText) itemView.findViewById(R.id.et_qty_issued_at_origin);
            etShortFall =(EditText) itemView.findViewById(R.id.et_shortfall);
        }
    }

    public interface InspectorReviewInterface{
        void itemSelect(String itemId, String catId, String qtyIssued,String shortFall,String penalityAmount,String qtyPerJanitor);
        void itemUnSelect(String itemId);
    }
}
