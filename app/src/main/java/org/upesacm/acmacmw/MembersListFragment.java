package org.upesacm.acmacmw;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MembersListFragment extends Fragment{
    private RecyclerView mMembersRecyclerView;
    private MembersAdaptor mAdaptor;
    private TextView mMembersName;
    private TextView mMembersDesc;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_members_list,container,false);
        mMembersRecyclerView = (RecyclerView) view.findViewById(R.id.members_recycler_view);
        mMembersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    private void updateUI(){
        MembersList membersList = MembersList.get(getActivity());
        List<Members> member =  membersList.getmMembers();
        mAdaptor = new MembersAdaptor(member);
        mMembersRecyclerView.setAdapter(mAdaptor);
    }

    private class MemberHolder extends RecyclerView.ViewHolder{
        private Members mMembers;

       public MemberHolder (LayoutInflater inflater, ViewGroup parent){
           super(inflater.inflate(R.layout.list_item_members,parent,false));

           mMembersName = (TextView) itemView.findViewById(R.id.members_name);
           mMembersDesc = (TextView) itemView.findViewById(R.id.members_desc);
       }
       public void bind(Members member){
           mMembers = member;
           mMembersName.setText(mMembers.getmName());
           mMembersDesc.setText(mMembers.getmDescription());
       }
    }
    private class MembersAdaptor extends RecyclerView.Adapter<MemberHolder>{
        private List<Members> mMembers;

        public MembersAdaptor (List<Members> member){
            mMembers = member;
        }

        @Override
        public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new MemberHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(MemberHolder holder, int position) {

            Members members = mMembers.get(position);
            holder.bind(members);


        }

        @Override
        public int getItemCount() {
            return mMembers.size();
        }
    }
}
