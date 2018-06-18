package org.upesacm.acmacmw.listener;

import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.TrialMember;

public interface HomeActivityStateChangeListener {
    void onMemberLogin(Member member);
    void onMemberLogout();
    void onGoogleSignIn(TrialMember member);
    void onGoogleSignOut();
}
