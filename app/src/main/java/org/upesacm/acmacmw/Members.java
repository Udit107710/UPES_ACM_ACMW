package org.upesacm.acmacmw;

import java.util.UUID;

public class Members {

    private UUID mId;
    private String mName;
    private String mDescription;


    public Members() {
        mId = UUID.randomUUID();
    }

    public UUID getmId() {
        return mId;
    }

    public void setmId(UUID mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }



}
