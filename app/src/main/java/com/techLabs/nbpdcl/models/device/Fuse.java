package com.techLabs.nbpdcl.models.device;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class Fuse {

    @SerializedName("output")
    @Expose
    private Output output;

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public class Output {

        @SerializedName("DeviceNumber")
        @Expose
        private String deviceNumber;
        @SerializedName("DeviceType")
        @Expose
        private Integer deviceType;
        @SerializedName("SectionId")
        @Expose
        private String sectionId;
        @SerializedName("NetworkId")
        @Expose
        private String networkId;
        @SerializedName("Location")
        @Expose
        private Integer location;
        @SerializedName("EquipmentId")
        @Expose
        private String equipmentId;
        @SerializedName("ClosedPhase")
        @Expose
        private Integer closedPhase;
        @SerializedName("ComponentMask")
        @Expose
        private Integer componentMask;
        @SerializedName("RatedCurrent")
        @Expose
        private String ratedCurrent;
        @SerializedName("FirstRatedCurrent")
        @Expose
        private String firstRatedCurrent;
        @SerializedName("SecondRatedCurrent")
        @Expose
        private String secondRatedCurrent;
        @SerializedName("ThirdRatedCurrent")
        @Expose
        private String thirdRatedCurrent;
        @SerializedName("FourthRatedCurrent")
        @Expose
        private String fourthRatedCurrent;
        @SerializedName("RatedVoltage")
        @Expose
        private String ratedVoltage;
        @SerializedName("Reversible")
        @Expose
        private Integer reversible;
        @SerializedName("SinglePhaseLocking")
        @Expose
        private Integer singlePhaseLocking;
        @SerializedName("InterruptingRating")
        @Expose
        private String interruptingRating;
        @SerializedName("TestCircuitPF")
        @Expose
        private String testCircuitPF;
        @SerializedName("VoltageClassification")
        @Expose
        private Integer voltageClassification;
        @SerializedName("Standard")
        @Expose
        private Integer standard;
        @SerializedName("Favorite")
        @Expose
        private Integer favorite;
        @SerializedName("ModifiedByUser")
        @Expose
        private String modifiedByUser;
        @SerializedName("Flags")
        @Expose
        private Integer flags;
        @SerializedName("LastChange")
        @Expose
        private Integer lastChange;
        @SerializedName("Comments")
        @Expose
        private String comments;
        @SerializedName("Manufacturer")
        @Expose
        private String manufacturer;
        @SerializedName("Model")
        @Expose
        private String model;
        @SerializedName("TCCRating")
        @Expose
        private String tCCRating;
        @SerializedName("Phase")
        @Expose
        private Integer phase;
        @SerializedName("ZoneId")
        @Expose
        private String zoneId;
        @SerializedName("Status")
        @Expose
        private Integer status;
        @SerializedName("IsTotalDemand")
        @Expose
        private String isTotalDemand;
        @SerializedName("DemandType")
        @Expose
        private String demandType;
        @SerializedName("Val1Total")
        @Expose
        private String val1Total;
        @SerializedName("Val2Total")
        @Expose
        private String val2Total;
        @SerializedName("Val1A")
        @Expose
        private String val1A;
        @SerializedName("Val2A")
        @Expose
        private String val2A;
        @SerializedName("Val1B")
        @Expose
        private String val1B;
        @SerializedName("Val2B")
        @Expose
        private String val2B;
        @SerializedName("Val1C")
        @Expose
        private String val1C;
        @SerializedName("Val2C")
        @Expose
        private String val2C;
        @SerializedName("DisconnectedPhase")
        @Expose
        private String disconnectedPhase;
        @SerializedName("FromNodeId")
        @Expose
        private String fromNodeId;
        @SerializedName("FromNode_X")
        @Expose
        private String fromNodeX;
        @SerializedName("FromNode_Y")
        @Expose
        private String fromNodeY;
        @SerializedName("ToNodeId")
        @Expose
        private String toNodeId;
        @SerializedName("ToNode_X")
        @Expose
        private String toNodeX;
        @SerializedName("ToNode_Y")
        @Expose
        private String toNodeY;
        @SerializedName("DeviceTypeLine")
        @Expose
        private Integer deviceTypeLine;
        @SerializedName("LineDeviceNumber")
        @Expose
        private String lineDeviceNumber;

        @SerializedName("MeterIndex")
        @Expose
        private String meterIndex;

        @SerializedName("ReferenceTime")
        @Expose
        private String refrenceTime;

        public String getDeviceNumber() {
            return deviceNumber;
        }

        public void setDeviceNumber(String deviceNumber) {
            this.deviceNumber = deviceNumber;
        }

        public Integer getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(Integer deviceType) {
            this.deviceType = deviceType;
        }

        public String getSectionId() {
            return sectionId;
        }

        public void setSectionId(String sectionId) {
            this.sectionId = sectionId;
        }

        public String getNetworkId() {
            return networkId;
        }

        public void setNetworkId(String networkId) {
            this.networkId = networkId;
        }

        public Integer getLocation() {
            return location;
        }

        public void setLocation(Integer location) {
            this.location = location;
        }

        public String getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(String equipmentId) {
            this.equipmentId = equipmentId;
        }

        public Integer getClosedPhase() {
            return closedPhase;
        }

        public void setClosedPhase(Integer closedPhase) {
            this.closedPhase = closedPhase;
        }

        public Integer getComponentMask() {
            return componentMask;
        }

        public void setComponentMask(Integer componentMask) {
            this.componentMask = componentMask;
        }

        public String getRatedCurrent() {
            return ratedCurrent;
        }

        public void setRatedCurrent(String ratedCurrent) {
            this.ratedCurrent = ratedCurrent;
        }

        public String getFirstRatedCurrent() {
            return firstRatedCurrent;
        }

        public void setFirstRatedCurrent(String firstRatedCurrent) {
            this.firstRatedCurrent = firstRatedCurrent;
        }

        public String getSecondRatedCurrent() {
            return secondRatedCurrent;
        }

        public void setSecondRatedCurrent(String secondRatedCurrent) {
            this.secondRatedCurrent = secondRatedCurrent;
        }

        public String getThirdRatedCurrent() {
            return thirdRatedCurrent;
        }

        public void setThirdRatedCurrent(String thirdRatedCurrent) {
            this.thirdRatedCurrent = thirdRatedCurrent;
        }

        public String getFourthRatedCurrent() {
            return fourthRatedCurrent;
        }

        public void setFourthRatedCurrent(String fourthRatedCurrent) {
            this.fourthRatedCurrent = fourthRatedCurrent;
        }

        public String getRatedVoltage() {
            return ratedVoltage;
        }

        public void setRatedVoltage(String ratedVoltage) {
            this.ratedVoltage = ratedVoltage;
        }

        public Integer getReversible() {
            return reversible;
        }

        public void setReversible(Integer reversible) {
            this.reversible = reversible;
        }

        public Integer getSinglePhaseLocking() {
            return singlePhaseLocking;
        }

        public void setSinglePhaseLocking(Integer singlePhaseLocking) {
            this.singlePhaseLocking = singlePhaseLocking;
        }

        public String getInterruptingRating() {
            return interruptingRating;
        }

        public void setInterruptingRating(String interruptingRating) {
            this.interruptingRating = interruptingRating;
        }

        public String getTestCircuitPF() {
            return testCircuitPF;
        }

        public void setTestCircuitPF(String testCircuitPF) {
            this.testCircuitPF = testCircuitPF;
        }

        public Integer getVoltageClassification() {
            return voltageClassification;
        }

        public void setVoltageClassification(Integer voltageClassification) {
            this.voltageClassification = voltageClassification;
        }

        public Integer getStandard() {
            return standard;
        }

        public void setStandard(Integer standard) {
            this.standard = standard;
        }

        public Integer getFavorite() {
            return favorite;
        }

        public void setFavorite(Integer favorite) {
            this.favorite = favorite;
        }

        public String getModifiedByUser() {
            return modifiedByUser;
        }

        public void setModifiedByUser(String modifiedByUser) {
            this.modifiedByUser = modifiedByUser;
        }

        public Integer getFlags() {
            return flags;
        }

        public void setFlags(Integer flags) {
            this.flags = flags;
        }

        public Integer getLastChange() {
            return lastChange;
        }

        public void setLastChange(Integer lastChange) {
            this.lastChange = lastChange;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getTCCRating() {
            return tCCRating;
        }

        public void setTCCRating(String tCCRating) {
            this.tCCRating = tCCRating;
        }

        public Integer getPhase() {
            return phase;
        }

        public void setPhase(Integer phase) {
            this.phase = phase;
        }

        public String getZoneId() {
            return zoneId;
        }

        public void setZoneId(String zoneId) {
            this.zoneId = zoneId;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getIsTotalDemand() {
            return isTotalDemand;
        }

        public void setIsTotalDemand(String isTotalDemand) {
            this.isTotalDemand = isTotalDemand;
        }

        public String getDemandType() {
            return demandType;
        }

        public void setDemandType(String demandType) {
            this.demandType = demandType;
        }

        public String getVal1Total() {
            return val1Total;
        }

        public void setVal1Total(String val1Total) {
            this.val1Total = val1Total;
        }

        public String getVal2Total() {
            return val2Total;
        }

        public void setVal2Total(String val2Total) {
            this.val2Total = val2Total;
        }

        public String getVal1A() {
            return val1A;
        }

        public void setVal1A(String val1A) {
            this.val1A = val1A;
        }

        public String getVal2A() {
            return val2A;
        }

        public void setVal2A(String val2A) {
            this.val2A = val2A;
        }

        public String getVal1B() {
            return val1B;
        }

        public void setVal1B(String val1B) {
            this.val1B = val1B;
        }

        public String getVal2B() {
            return val2B;
        }

        public void setVal2B(String val2B) {
            this.val2B = val2B;
        }

        public String getVal1C() {
            return val1C;
        }

        public void setVal1C(String val1C) {
            this.val1C = val1C;
        }

        public String getVal2C() {
            return val2C;
        }

        public void setVal2C(String val2C) {
            this.val2C = val2C;
        }

        public String getDisconnectedPhase() {
            return disconnectedPhase;
        }

        public void setDisconnectedPhase(String disconnectedPhase) {
            this.disconnectedPhase = disconnectedPhase;
        }

        public String getFromNodeId() {
            return fromNodeId;
        }

        public void setFromNodeId(String fromNodeId) {
            this.fromNodeId = fromNodeId;
        }

        public String getFromNodeX() {
            return fromNodeX;
        }

        public void setFromNodeX(String fromNodeX) {
            this.fromNodeX = fromNodeX;
        }

        public String getFromNodeY() {
            return fromNodeY;
        }

        public void setFromNodeY(String fromNodeY) {
            this.fromNodeY = fromNodeY;
        }

        public String getToNodeId() {
            return toNodeId;
        }

        public void setToNodeId(String toNodeId) {
            this.toNodeId = toNodeId;
        }

        public String getToNodeX() {
            return toNodeX;
        }

        public void setToNodeX(String toNodeX) {
            this.toNodeX = toNodeX;
        }

        public String getToNodeY() {
            return toNodeY;
        }

        public void setToNodeY(String toNodeY) {
            this.toNodeY = toNodeY;
        }

        public Integer getDeviceTypeLine() {
            return deviceTypeLine;
        }

        public void setDeviceTypeLine(Integer deviceTypeLine) {
            this.deviceTypeLine = deviceTypeLine;
        }

        public String getLineDeviceNumber() {
            return lineDeviceNumber;
        }

        public void setLineDeviceNumber(String lineDeviceNumber) {
            this.lineDeviceNumber = lineDeviceNumber;
        }

        public void setMeterIndex(String meterIndex) {
            this.meterIndex = meterIndex;
        }

        public String getMeterIndex() {
            return meterIndex;
        }

        public void setRefrenceTime(String refrenceTime) {
            this.refrenceTime = refrenceTime;
        }

        public String getRefrenceTime() {
            return refrenceTime;
        }

    }

}


