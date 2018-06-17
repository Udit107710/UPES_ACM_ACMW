package org.upesacm.acmacmw.listener;

import org.upesacm.acmacmw.model.Member;

public interface HomeActivityStateChangeListener {
    void onMemberLogin(Member member);
    void onMemberLogout();
}
