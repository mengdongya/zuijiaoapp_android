package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xiaqibo on 2015/8/11.
 */
public class SellerStatus implements Serializable {
    @SerializedName("applicationStatus")
    private String applyStatus;
    @SerializedName("bankStatus")
    private String bankStatus;
    @SerializedName("profileStatus")
    private String profileStatus;
    @SerializedName("failReason")
    private String faiReason;
    @SerializedName("application")
    private SellerApplication application;

    private SellerStatus(String applyStatus, String bankStatus, String profileStatus, String faiReason, SellerApplication application) {
        this.applyStatus = applyStatus;
        this.bankStatus = bankStatus;
        this.profileStatus = profileStatus;
        this.faiReason = faiReason;
        this.application = application;
    }

    public static SellerStatus INSTANCE_EDITING_PROFILE = new SellerStatus("editing" , "unfinished" , "finished" ,"" ,null) ;
    public static SellerStatus INSTANCE_EDITING_NONE_PROFILE = new SellerStatus("editing" , "unfinished" , "unfinished" ,"" ,null) ;
    public static SellerStatus INSTANCE_REVIEWING = new SellerStatus("reviewing" , "unfinished" , "finished" ,"" ,null) ;
    public static SellerStatus INSTANCE_FAILED = new SellerStatus("fail" , "unfinished" , "finished" ,"ugly" ,null) ;
    public static SellerStatus INSTANCE_SUCCESS_NONE_BANK= new SellerStatus("passed" , "finished" , "finished" ,"" ,null) ;
    public static SellerStatus INSTANCE_SELLER = new SellerStatus("passed" , "finished" , "finished" ,"" ,null) ;

    public ApplyStatus getApplyStatus() {
        return ApplyStatus.fromString(applyStatus);
    }

    public enum ApplyStatus {
        editing, waiting, reviewing, fail, passed, unknown;

        public static ApplyStatus fromString(String status) {
            switch (status) {
                case "editing":
                    return editing;
                case "waiting":
                    return waiting;
                case "reviewing":
                    return reviewing;
                case "fail":
                    return fail;
                case "passed":
                    return passed;
                default:
                    return unknown;
            }
        }


        public String toString(ApplyStatus status) {
            switch (this) {
                case editing:
                    return "editing";
                case waiting:
                    return "waiting";
                case reviewing:
                    return "reviewing";
                case fail:
                    return "fail";
                case passed:
                    return "passed";
                default:
                    return "unknown";
            }
        }
    }
    public BankStatus getBankStatus() {
        return BankStatus.fromString(bankStatus);
    }

    public ProfileStatus getProfileStatus() {
        return ProfileStatus.fromString(profileStatus);
    }

    public String getFaiReason() {
        return faiReason;
    }

    public SellerApplication getApplication() {
        return application;
    }

    public enum ProfileStatus {
        unfinished, finished;

        public static ProfileStatus fromString(String status) {
            switch (status) {
                case "unfinished":
                    return unfinished;
                case "finished":
                    return finished;
                default:
                    return null;
            }
        }


        public static String toString(ProfileStatus status) {
            switch (status) {
                case unfinished:
                    return "unfinished";
                case finished:
                    return "finished";
                default:
                    return null;
            }
        }
    }


    public enum BankStatus {
        unfinished, finished;

        public static BankStatus fromString(String status) {
            switch (status) {
                case "unfinished":
                    return unfinished;
                case "finished":
                    return finished;
                default:
                    return null;
            }
        }


        public static String toString(BankStatus status) {
            switch (status) {
                case unfinished:
                    return "unfinished";
                case finished:
                    return "finished";
                default:
                    return null;
            }
        }
    }




    public class SellerApplication {
        @SerializedName("ID")
        private Integer id;
        @SerializedName("cookLevel")
        private String cookLevel;
        @SerializedName("cookFaction")
        private String cookFaction;
        @SerializedName("siteType")
        private String siteType;
        @SerializedName("eventSize")
        private String eventSize;
        @SerializedName("eventFrequency")
        private String eventFrequency;
        @SerializedName("provinceID")
        private Integer provinceID;
        @SerializedName("cityID")
        private Integer cityID;
        @SerializedName("countyID")
        private Integer countryID;
        @SerializedName("siteAddress")
        private String siteAddress;
        @SerializedName("cookPhotos")
        private ArrayList<String> cookPhotos;
        @SerializedName("cookStory")
        private String cookStory;


        public Integer getId() {
            return id;
        }

        public String getCookLevel() {
            return cookLevel;
        }

        public String getCookFaction() {
            return cookFaction;
        }

        public String getSiteType() {
            return siteType;
        }

        public String getEventSize() {
            return eventSize;
        }

        public String getEventFrequency() {
            return eventFrequency;
        }

        public Integer getProvinceID() {
            return provinceID;
        }

        public Integer getCityID() {
            return cityID;
        }

        public Integer getCountryID() {
            return countryID;
        }

        public String getSiteAddress() {
            return siteAddress;
        }

        public ArrayList<String> getCookPhotos() {
            return cookPhotos;
        }

        public String getCookStory() {
            return cookStory;
        }

    }

}
