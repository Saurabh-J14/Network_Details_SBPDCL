package com.techLabs.nbpdcl.models.nsc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewConnectionModel {

    @SerializedName("output")
    @Expose
    private List<Output> output;

    public List<Output> getOutput() {
        return output;
    }

    public void setOutput(List<Output> output) {
        this.output = output;
    }

    public class Output {

        @SerializedName("ApplicationID")
        @Expose
        private Object applicationID;
        @SerializedName("BPNO")
        @Expose
        private Object bpno;
        @SerializedName("Date")
        @Expose
        private String date;
        @SerializedName("DTGISID")
        @Expose
        private String dtgisid;
        @SerializedName("PoleNo")
        @Expose
        private Object poleNo;
        @SerializedName("ContractDemand")
        @Expose
        private Object contractDemand;
        @SerializedName("NearestConsumerNumber")
        @Expose
        private String nearestConsumerNumber;
        @SerializedName("SanctionedLoad")
        @Expose
        private Object sanctionedLoad;
        @SerializedName("SanctionedPhase")
        @Expose
        private Object sanctionedPhase;
        @SerializedName("SanctionedCategory")
        @Expose
        private Object sanctionedCategory;
        @SerializedName("ServiceCableLength")
        @Expose
        private Float serviceCableLength;
        @SerializedName("Circle")
        @Expose
        private Object circle;
        @SerializedName("Division")
        @Expose
        private Object division;
        @SerializedName("Subdivision")
        @Expose
        private Object subdivision;
        @SerializedName("Section")
        @Expose
        private Object section;
        @SerializedName("SubstationID")
        @Expose
        private Object substationID;
        @SerializedName("SubstationName")
        @Expose
        private Object substationName;
        @SerializedName("Feederid")
        @Expose
        private String feederid;
        @SerializedName("OperationType")
        @Expose
        private Object operationType;
        @SerializedName("Status")
        @Expose
        private String status;
        @SerializedName("Feasibility")
        @Expose
        private String feasibility;
        @SerializedName("Before_Percentage_VR")
        @Expose
        private Object beforePercentageVR;
        @SerializedName("After_Percentage_VR")
        @Expose
        private Object afterPercentageVR;
        @SerializedName("Before_DTLoading")
        @Expose
        private Object beforeDTLoading;
        @SerializedName("After_DTLoading")
        @Expose
        private Object afterDTLoading;
        @SerializedName("Recomnd_Phase")
        @Expose
        private Object recomndPhase;
        @SerializedName("Remark")
        @Expose
        private String remark;

        public Object getApplicationID() {
            return applicationID;
        }

        public void setApplicationID(Object applicationID) {
            this.applicationID = applicationID;
        }

        public Object getBpno() {
            return bpno;
        }

        public void setBpno(Object bpno) {
            this.bpno = bpno;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDtgisid() {
            return dtgisid;
        }

        public void setDtgisid(String dtgisid) {
            this.dtgisid = dtgisid;
        }

        public Object getPoleNo() {
            return poleNo;
        }

        public void setPoleNo(Object poleNo) {
            this.poleNo = poleNo;
        }

        public Object getContractDemand() {
            return contractDemand;
        }

        public void setContractDemand(Object contractDemand) {
            this.contractDemand = contractDemand;
        }

        public String getNearestConsumerNumber() {
            return nearestConsumerNumber;
        }

        public void setNearestConsumerNumber(String nearestConsumerNumber) {
            this.nearestConsumerNumber = nearestConsumerNumber;
        }

        public Object getSanctionedLoad() {
            return sanctionedLoad;
        }

        public void setSanctionedLoad(Object sanctionedLoad) {
            this.sanctionedLoad = sanctionedLoad;
        }

        public Object getSanctionedPhase() {
            return sanctionedPhase;
        }

        public void setSanctionedPhase(Object sanctionedPhase) {
            this.sanctionedPhase = sanctionedPhase;
        }

        public Object getSanctionedCategory() {
            return sanctionedCategory;
        }

        public void setSanctionedCategory(Object sanctionedCategory) {
            this.sanctionedCategory = sanctionedCategory;
        }

        public Float getServiceCableLength() {
            return serviceCableLength;
        }

        public void setServiceCableLength(Float serviceCableLength) {
            this.serviceCableLength = serviceCableLength;
        }

        public Object getCircle() {
            return circle;
        }

        public void setCircle(Object circle) {
            this.circle = circle;
        }

        public Object getDivision() {
            return division;
        }

        public void setDivision(Object division) {
            this.division = division;
        }

        public Object getSubdivision() {
            return subdivision;
        }

        public void setSubdivision(Object subdivision) {
            this.subdivision = subdivision;
        }

        public Object getSection() {
            return section;
        }

        public void setSection(Object section) {
            this.section = section;
        }

        public Object getSubstationID() {
            return substationID;
        }

        public void setSubstationID(Object substationID) {
            this.substationID = substationID;
        }

        public Object getSubstationName() {
            return substationName;
        }

        public void setSubstationName(Object substationName) {
            this.substationName = substationName;
        }

        public String getFeederid() {
            return feederid;
        }

        public void setFeederid(String feederid) {
            this.feederid = feederid;
        }

        public Object getOperationType() {
            return operationType;
        }

        public void setOperationType(Object operationType) {
            this.operationType = operationType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFeasibility() {
            return feasibility;
        }

        public void setFeasibility(String feasibility) {
            this.feasibility = feasibility;
        }

        public Object getBeforePercentageVR() {
            return beforePercentageVR;
        }

        public void setBeforePercentageVR(Object beforePercentageVR) {
            this.beforePercentageVR = beforePercentageVR;
        }

        public Object getAfterPercentageVR() {
            return afterPercentageVR;
        }

        public void setAfterPercentageVR(Object afterPercentageVR) {
            this.afterPercentageVR = afterPercentageVR;
        }

        public Object getBeforeDTLoading() {
            return beforeDTLoading;
        }

        public void setBeforeDTLoading(Object beforeDTLoading) {
            this.beforeDTLoading = beforeDTLoading;
        }

        public Object getAfterDTLoading() {
            return afterDTLoading;
        }

        public void setAfterDTLoading(Object afterDTLoading) {
            this.afterDTLoading = afterDTLoading;
        }

        public Object getRecomndPhase() {
            return recomndPhase;
        }

        public void setRecomndPhase(Object recomndPhase) {
            this.recomndPhase = recomndPhase;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

    }

}
