package org.upesacm.acmacmw;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MembersList {
    private static MembersList sMemberList;
    private List<Members> mMembers;

    public MembersList(Context context) {
        mMembers = new ArrayList<>();
        for(int i = 0; i<100; i++){
            Members members = new Members();
            //set name and description here...
            mMembers.add(members);
        }

    }

    public static MembersList get(Context context){
        if (sMemberList == null){
            sMemberList = new MembersList(context);
        }
        return sMemberList;
    }

    public List<Members> getmMembers() {
        return mMembers;
    }

    public Members getMembers(UUID id){
        for (Members member : mMembers){
            if (member.getmId().equals(id)) {
                return member;
            }
        }
        return null;
    }
}
