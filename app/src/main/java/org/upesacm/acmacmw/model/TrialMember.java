package org.upesacm.acmacmw.model;

public class TrialMember {
    private String sap;
    private String email;
    private String name;
    private String creationTimeStamp;

    public String getSap() {
        return sap;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public static class Builder {
        private String sap;
        private String email;
        private String name;
        private String timeStamp;
        public Builder(String timeStamp) {
            this.timeStamp=timeStamp;
        }
        public TrialMember build() {
            TrialMember trialMember=new TrialMember();
            trialMember.email=email;
            trialMember.sap=sap;
            trialMember.name=name;
            trialMember.creationTimeStamp=timeStamp;
            System.out.println("trial member time stamp : "+trialMember.getCreationTimeStamp());
            return trialMember;
        }

        public Builder setSap(String sap) {
            this.sap=sap;
            return this;
        }

        public Builder setName(String name) {
            this.name=name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email=email;
            return this;
        }
    }
}
