package com.techLabs.nbpdcl.models.Line;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Overhead {

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
        @SerializedName("LineId")
        @Expose
        private String lineId;
        @SerializedName("Length")
        @Expose
        private String length;
        @SerializedName("ComponentMask")
        @Expose
        private String componentMask;
        @SerializedName("PhaseConductorId")
        @Expose
        private String phaseConductorId;
        @SerializedName("NeutralConductorId")
        @Expose
        private String neutralConductorId;
        @SerializedName("ConductorSpacingId")
        @Expose
        private String conductorSpacingId;
        @SerializedName("NominalRating")
        @Expose
        private String nominalRating;
        @SerializedName("FirstRating")
        @Expose
        private String firstRating;
        @SerializedName("SecondRating")
        @Expose
        private String secondRating;
        @SerializedName("ThirdRating")
        @Expose
        private String thirdRating;
        @SerializedName("FourthRating")
        @Expose
        private String fourthRating;
        @SerializedName("UserDefinedImpedances")
        @Expose
        private String userDefinedImpedances;
        @SerializedName("PositiveSequenceResistance")
        @Expose
        private String positiveSequenceResistance;
        @SerializedName("PositiveSequenceReactance")
        @Expose
        private String positiveSequenceReactance;
        @SerializedName("ZeroSequenceResistance")
        @Expose
        private String zeroSequenceResistance;
        @SerializedName("ZeroSequenceReactance")
        @Expose
        private String zeroSequenceReactance;
        @SerializedName("PosSeqShuntSusceptance")
        @Expose
        private String posSeqShuntSusceptance;
        @SerializedName("ZeroSequenceShuntSusceptance")
        @Expose
        private String zeroSequenceShuntSusceptance;
        @SerializedName("LockImpedance")
        @Expose
        private String lockImpedance;
        @SerializedName("Temperature")
        @Expose
        private String temperature;
        @SerializedName("Frequency")
        @Expose
        private String frequency;
        @SerializedName("Favorite")
        @Expose
        private String favorite;
        @SerializedName("ModifiedByUser")
        @Expose
        private String modifiedByUser;
        @SerializedName("Flags")
        @Expose
        private String flags;
        @SerializedName("LastChange")
        @Expose
        private String lastChange;
        @SerializedName("Comments")
        @Expose
        private String comments;
        @SerializedName("PosSeqShuntConductance")
        @Expose
        private String posSeqShuntConductance;
        @SerializedName("ZeroSequenceShuntConductance")
        @Expose
        private String zeroSequenceShuntConductance;
        @SerializedName("Diameter")
        @Expose
        private String diameter;
        @SerializedName("GMR")
        @Expose
        private String gmr;
        @SerializedName("R25")
        @Expose
        private String r25;
        @SerializedName("R50")
        @Expose
        private String r50;
        @SerializedName("WithstandRating")
        @Expose
        private String withstandRating;
        @SerializedName("CodeWord")
        @Expose
        private String codeWord;
        @SerializedName("Size_mm2")
        @Expose
        private String sizeMm2;
        @SerializedName("ConstructionType")
        @Expose
        private String constructionType;
        @SerializedName("FirstResistanceDC")
        @Expose
        private String firstResistanceDC;
        @SerializedName("SecondResistanceDC")
        @Expose
        private String secondResistanceDC;
        @SerializedName("MaterialId")
        @Expose
        private String materialId;
        @SerializedName("AWGSize")
        @Expose
        private String aWGSize;
        @SerializedName("SizeUnit")
        @Expose
        private String sizeUnit;
        @SerializedName("OutsideArea")
        @Expose
        private String outsideArea;
        @SerializedName("NumberOfStrands")
        @Expose
        private String numberOfStrands;
        @SerializedName("TemperatureAC1")
        @Expose
        private String temperatureAC1;
        @SerializedName("TemperatureAC2")
        @Expose
        private String temperatureAC2;
        @SerializedName("TemperatureDC1")
        @Expose
        private String temperatureDC1;
        @SerializedName("TemperatureDC2")
        @Expose
        private String temperatureDC2;
        @SerializedName("Phase")
        @Expose
        private Integer phase;
        @SerializedName("ZoneId")
        @Expose
        private String zoneId;
        @SerializedName("Status")
        @Expose
        private Integer status;
        @SerializedName("FromNodeId")
        @Expose
        private String fromNodeId;
        @SerializedName("ToNodeId")
        @Expose
        private String toNodeId;
        @SerializedName("FromNode_X")
        @Expose
        private String fromNodeX;
        @SerializedName("FromNode_Y")
        @Expose
        private String fromNodeY;
        @SerializedName("ToNode_X")
        @Expose
        private String toNodeX;
        @SerializedName("ToNode_Y")
        @Expose
        private String toNodeY;

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

        public String getLineId() {
            return lineId;
        }

        public void setLineId(String lineId) {
            this.lineId = lineId;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getComponentMask() {
            return componentMask;
        }

        public void setComponentMask(String componentMask) {
            this.componentMask = componentMask;
        }

        public String getPhaseConductorId() {
            return phaseConductorId;
        }

        public void setPhaseConductorId(String phaseConductorId) {
            this.phaseConductorId = phaseConductorId;
        }

        public String getNeutralConductorId() {
            return neutralConductorId;
        }

        public void setNeutralConductorId(String neutralConductorId) {
            this.neutralConductorId = neutralConductorId;
        }

        public String getConductorSpacingId() {
            return conductorSpacingId;
        }

        public void setConductorSpacingId(String conductorSpacingId) {
            this.conductorSpacingId = conductorSpacingId;
        }

        public String getNominalRating() {
            return nominalRating;
        }

        public void setNominalRating(String nominalRating) {
            this.nominalRating = nominalRating;
        }

        public String getFirstRating() {
            return firstRating;
        }

        public void setFirstRating(String firstRating) {
            this.firstRating = firstRating;
        }

        public String getSecondRating() {
            return secondRating;
        }

        public void setSecondRating(String secondRating) {
            this.secondRating = secondRating;
        }

        public String getThirdRating() {
            return thirdRating;
        }

        public void setThirdRating(String thirdRating) {
            this.thirdRating = thirdRating;
        }

        public String getFourthRating() {
            return fourthRating;
        }

        public void setFourthRating(String fourthRating) {
            this.fourthRating = fourthRating;
        }

        public String getUserDefinedImpedances() {
            return userDefinedImpedances;
        }

        public void setUserDefinedImpedances(String userDefinedImpedances) {
            this.userDefinedImpedances = userDefinedImpedances;
        }

        public String getPositiveSequenceResistance() {
            return positiveSequenceResistance;
        }

        public void setPositiveSequenceResistance(String positiveSequenceResistance) {
            this.positiveSequenceResistance = positiveSequenceResistance;
        }

        public String getPositiveSequenceReactance() {
            return positiveSequenceReactance;
        }

        public void setPositiveSequenceReactance(String positiveSequenceReactance) {
            this.positiveSequenceReactance = positiveSequenceReactance;
        }

        public String getZeroSequenceResistance() {
            return zeroSequenceResistance;
        }

        public void setZeroSequenceResistance(String zeroSequenceResistance) {
            this.zeroSequenceResistance = zeroSequenceResistance;
        }

        public String getZeroSequenceReactance() {
            return zeroSequenceReactance;
        }

        public void setZeroSequenceReactance(String zeroSequenceReactance) {
            this.zeroSequenceReactance = zeroSequenceReactance;
        }

        public String getPosSeqShuntSusceptance() {
            return posSeqShuntSusceptance;
        }

        public void setPosSeqShuntSusceptance(String posSeqShuntSusceptance) {
            this.posSeqShuntSusceptance = posSeqShuntSusceptance;
        }

        public String getZeroSequenceShuntSusceptance() {
            return zeroSequenceShuntSusceptance;
        }

        public void setZeroSequenceShuntSusceptance(String zeroSequenceShuntSusceptance) {
            this.zeroSequenceShuntSusceptance = zeroSequenceShuntSusceptance;
        }

        public String getLockImpedance() {
            return lockImpedance;
        }

        public void setLockImpedance(String lockImpedance) {
            this.lockImpedance = lockImpedance;
        }

        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public String getFavorite() {
            return favorite;
        }

        public void setFavorite(String favorite) {
            this.favorite = favorite;
        }

        public String getModifiedByUser() {
            return modifiedByUser;
        }

        public void setModifiedByUser(String modifiedByUser) {
            this.modifiedByUser = modifiedByUser;
        }

        public String getFlags() {
            return flags;
        }

        public void setFlags(String flags) {
            this.flags = flags;
        }

        public String getLastChange() {
            return lastChange;
        }

        public void setLastChange(String lastChange) {
            this.lastChange = lastChange;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getPosSeqShuntConductance() {
            return posSeqShuntConductance;
        }

        public void setPosSeqShuntConductance(String posSeqShuntConductance) {
            this.posSeqShuntConductance = posSeqShuntConductance;
        }

        public String getZeroSequenceShuntConductance() {
            return zeroSequenceShuntConductance;
        }

        public void setZeroSequenceShuntConductance(String zeroSequenceShuntConductance) {
            this.zeroSequenceShuntConductance = zeroSequenceShuntConductance;
        }

        public String getDiameter() {
            return diameter;
        }

        public void setDiameter(String diameter) {
            this.diameter = diameter;
        }

        public String getGmr() {
            return gmr;
        }

        public void setGmr(String gmr) {
            this.gmr = gmr;
        }

        public String getR25() {
            return r25;
        }

        public void setR25(String r25) {
            this.r25 = r25;
        }

        public String getR50() {
            return r50;
        }

        public void setR50(String r50) {
            this.r50 = r50;
        }

        public String getWithstandRating() {
            return withstandRating;
        }

        public void setWithstandRating(String withstandRating) {
            this.withstandRating = withstandRating;
        }

        public String getCodeWord() {
            return codeWord;
        }

        public void setCodeWord(String codeWord) {
            this.codeWord = codeWord;
        }

        public String getSizeMm2() {
            return sizeMm2;
        }

        public void setSizeMm2(String sizeMm2) {
            this.sizeMm2 = sizeMm2;
        }

        public String getConstructionType() {
            return constructionType;
        }

        public void setConstructionType(String constructionType) {
            this.constructionType = constructionType;
        }

        public String getFirstResistanceDC() {
            return firstResistanceDC;
        }

        public void setFirstResistanceDC(String firstResistanceDC) {
            this.firstResistanceDC = firstResistanceDC;
        }

        public String getSecondResistanceDC() {
            return secondResistanceDC;
        }

        public void setSecondResistanceDC(String secondResistanceDC) {
            this.secondResistanceDC = secondResistanceDC;
        }

        public String getMaterialId() {
            return materialId;
        }

        public void setMaterialId(String materialId) {
            this.materialId = materialId;
        }

        public String getAWGSize() {
            return aWGSize;
        }

        public void setAWGSize(String aWGSize) {
            this.aWGSize = aWGSize;
        }

        public String getSizeUnit() {
            return sizeUnit;
        }

        public void setSizeUnit(String sizeUnit) {
            this.sizeUnit = sizeUnit;
        }

        public String getOutsideArea() {
            return outsideArea;
        }

        public void setOutsideArea(String outsideArea) {
            this.outsideArea = outsideArea;
        }

        public String getNumberOfStrands() {
            return numberOfStrands;
        }

        public void setNumberOfStrands(String numberOfStrands) {
            this.numberOfStrands = numberOfStrands;
        }

        public String getTemperatureAC1() {
            return temperatureAC1;
        }

        public void setTemperatureAC1(String temperatureAC1) {
            this.temperatureAC1 = temperatureAC1;
        }

        public String getTemperatureAC2() {
            return temperatureAC2;
        }

        public void setTemperatureAC2(String temperatureAC2) {
            this.temperatureAC2 = temperatureAC2;
        }

        public String getTemperatureDC1() {
            return temperatureDC1;
        }

        public void setTemperatureDC1(String temperatureDC1) {
            this.temperatureDC1 = temperatureDC1;
        }

        public String getTemperatureDC2() {
            return temperatureDC2;
        }

        public void setTemperatureDC2(String temperatureDC2) {
            this.temperatureDC2 = temperatureDC2;
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

        public String getFromNodeId() {
            return fromNodeId;
        }

        public void setFromNodeId(String fromNodeId) {
            this.fromNodeId = fromNodeId;
        }

        public String getToNodeId() {
            return toNodeId;
        }

        public void setToNodeId(String toNodeId) {
            this.toNodeId = toNodeId;
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
    }
}