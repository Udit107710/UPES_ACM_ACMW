package org.upesacm.acmacmw.model;

public class TrialMember {
    private String sap;
    private String email;
    private String name;
    private String creationTimeStamp;
    private String imageUrl;
    private String otp;

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOtp() {
        return otp;
    }

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
        private String imageUrl;
        private String otp;
        public Builder(String timeStamp) {
            this.timeStamp=timeStamp;
        }
        public TrialMember build() {
            TrialMember trialMember=new TrialMember();
            trialMember.email=email;
            trialMember.sap=sap;
            trialMember.name=name;
            trialMember.creationTimeStamp=timeStamp;
            trialMember.imageUrl=imageUrl;
            trialMember.otp=otp;
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

        public Builder setImageUrl(String imageUrl) {
            this.imageUrl=imageUrl;
            return this;
        }

        public Builder setOtp(String otp) {
            this.otp = otp;
            return this;
        }
    }
}
