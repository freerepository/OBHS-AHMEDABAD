package com.sedulous.obhsadi.ModelLinen;

import java.io.Serializable;

public class QanswerData implements Serializable {
    public String quest_id="", cat_id="", quantity="", rate="",total_given="",total_return="",
            shortfall="", total_penalty_amount="", unit="";
    public QanswerData(){ }
    public QanswerData setQuestId(String id){
        this.quest_id=id;
        return this;
    }
    public QanswerData setCatId(String id){
        this.cat_id=id;
        return this;
    }
    public QanswerData setQuantity(String quantity){
        this.quantity=quantity;
        return this;
    }
    public QanswerData setShortfall(String shortfall){
        this.shortfall=shortfall;
        return this;
    }
    public QanswerData setTotalPAmount(String pamount){
        this.total_penalty_amount=pamount;
        return this;
    }

    public QanswerData setUnit(String unit){
        this.unit=unit;
        return this;
    }
    public QanswerData setRate(String rate){
        this.rate=rate;
        return this;
    }
    public QanswerData setTotal_given(String total_given){
        this.total_given=total_given;
        return this;
    }
    public QanswerData setTotal_return(String total_return){
        this.total_return=total_return;
        return this;
    }
}
