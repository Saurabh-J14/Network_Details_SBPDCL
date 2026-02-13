package com.techLabs.nbpdcl.models.device;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.processing.Generated;


public class Transformer {

    @SerializedName("output")
    @Expose
    private Output output;

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    @Generated("jsonschema2pojo")
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
        @SerializedName("ComponentMask")
        @Expose
        private Integer componentMask;
        @SerializedName("PhaseType")
        @Expose
        private Integer phaseType;
        @SerializedName("WindingType")
        @Expose
        private Integer windingType;
        @SerializedName("NominalRatingKVA")
        @Expose
        private String nominalRatingKVA;
        @SerializedName("FirstLoadingLimitKVA")
        @Expose
        private String firstLoadingLimitKVA;
        @SerializedName("SecondLoadingLimitKVA")
        @Expose
        private String secondLoadingLimitKVA;
        @SerializedName("ThirdLoadingLimitKVA")
        @Expose
        private String thirdLoadingLimitKVA;
        @SerializedName("FourthLoadingLimitKVA")
        @Expose
        private String fourthLoadingLimitKVA;
        @SerializedName("VoltageUnit")
        @Expose
        private Integer voltageUnit;
        @SerializedName("PrimaryVoltageKVLL")
        @Expose
        private String primaryVoltageKVLL;
        @SerializedName("SecondaryVoltageKVLL")
        @Expose
        private String secondaryVoltageKVLL;
        @SerializedName("PosSeqImpedancePercent")
        @Expose
        private String posSeqImpedancePercent;
        @SerializedName("ZeroSeqImpedancePercent")
        @Expose
        private String zeroSeqImpedancePercent;
        @SerializedName("ZeroSeqImpedancePrimSecPercent")
        @Expose
        private String zeroSeqImpedancePrimSecPercent;
        @SerializedName("ZeroSeqImpedancePrimMagPercent")
        @Expose
        private String zeroSeqImpedancePrimMagPercent;
        @SerializedName("ZeroSeqImpedanceSecMagPercent")
        @Expose
        private String zeroSeqImpedanceSecMagPercent;
        @SerializedName("XRRatio")
        @Expose
        private String xRRatio;
        @SerializedName("TransformerConnection")
        @Expose
        private Integer transformerConnection;
        @SerializedName("PrimGroundingResistanceOhms")
        @Expose
        private String primGroundingResistanceOhms;
        @SerializedName("PrimGroundingReactanceOhms")
        @Expose
        private String primGroundingReactanceOhms;
        @SerializedName("SecGroundingResistanceOhms")
        @Expose
        private String secGroundingResistanceOhms;
        @SerializedName("SecGroundingReactanceOhms")
        @Expose
        private String secGroundingReactanceOhms;
        @SerializedName("NoLoadLossesKW")
        @Expose
        private String noLoadLossesKW;
        @SerializedName("Reversible")
        @Expose
        private Integer reversible;
        @SerializedName("XR0Ratio")
        @Expose
        private String xR0Ratio;
        @SerializedName("XR0PrimSecRatio")
        @Expose
        private String xR0PrimSecRatio;
        @SerializedName("XR0PrimMagRatio")
        @Expose
        private String xR0PrimMagRatio;
        @SerializedName("XR0SecMagRatio")
        @Expose
        private String xR0SecMagRatio;
        @SerializedName("MagnetizingCurrent")
        @Expose
        private String magnetizingCurrent;
        @SerializedName("PhaseShift")
        @Expose
        private Integer phaseShift;
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
        @SerializedName("InsulationType")
        @Expose
        private Integer insulationType;
        @SerializedName("Phase")
        @Expose
        private Integer phase;
        @SerializedName("ZoneId")
        @Expose
        private String zoneId;
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
        @SerializedName("Length")
        @Expose
        private String length;
        @SerializedName("CaDeviceNumber")
        @Expose
        private String caDeviceNumber;
        @SerializedName("CableId")
        @Expose
        private String cableId;
        @SerializedName("Status")
        @Expose
        private Integer status;
        @SerializedName("CableType")
        @Expose
        private Integer cableType;
        @SerializedName("PrimaryTapSettingPercent")
        @Expose
        private String primaryTapSettingPercent;
        @SerializedName("SecondaryTapSettingPercent")
        @Expose
        private String secondaryTapSettingPercent;
        @SerializedName("FaultIndicator")
        @Expose
        private Integer faultIndicator;
        @SerializedName("dtStatus")
        @Expose
        private Integer dtStatus;
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

        public Integer getComponentMask() {
            return componentMask;
        }

        public void setComponentMask(Integer componentMask) {
            this.componentMask = componentMask;
        }

        public Integer getPhaseType() {
            return phaseType;
        }

        public void setPhaseType(Integer phaseType) {
            this.phaseType = phaseType;
        }

        public Integer getWindingType() {
            return windingType;
        }

        public void setWindingType(Integer windingType) {
            this.windingType = windingType;
        }

        public String getNominalRatingKVA() {
            return nominalRatingKVA;
        }

        public void setNominalRatingKVA(String nominalRatingKVA) {
            this.nominalRatingKVA = nominalRatingKVA;
        }

        public String getFirstLoadingLimitKVA() {
            return firstLoadingLimitKVA;
        }

        public void setFirstLoadingLimitKVA(String firstLoadingLimitKVA) {
            this.firstLoadingLimitKVA = firstLoadingLimitKVA;
        }

        public String getSecondLoadingLimitKVA() {
            return secondLoadingLimitKVA;
        }

        public void setSecondLoadingLimitKVA(String secondLoadingLimitKVA) {
            this.secondLoadingLimitKVA = secondLoadingLimitKVA;
        }

        public String getThirdLoadingLimitKVA() {
            return thirdLoadingLimitKVA;
        }

        public void setThirdLoadingLimitKVA(String thirdLoadingLimitKVA) {
            this.thirdLoadingLimitKVA = thirdLoadingLimitKVA;
        }

        public String getFourthLoadingLimitKVA() {
            return fourthLoadingLimitKVA;
        }

        public void setFourthLoadingLimitKVA(String fourthLoadingLimitKVA) {
            this.fourthLoadingLimitKVA = fourthLoadingLimitKVA;
        }

        public Integer getVoltageUnit() {
            return voltageUnit;
        }

        public void setVoltageUnit(Integer voltageUnit) {
            this.voltageUnit = voltageUnit;
        }

        public String getPrimaryVoltageKVLL() {
            return primaryVoltageKVLL;
        }

        public void setPrimaryVoltageKVLL(String primaryVoltageKVLL) {
            this.primaryVoltageKVLL = primaryVoltageKVLL;
        }

        public String getSecondaryVoltageKVLL() {
            return secondaryVoltageKVLL;
        }

        public void setSecondaryVoltageKVLL(String secondaryVoltageKVLL) {
            this.secondaryVoltageKVLL = secondaryVoltageKVLL;
        }

        public String getPosSeqImpedancePercent() {
            return posSeqImpedancePercent;
        }

        public void setPosSeqImpedancePercent(String posSeqImpedancePercent) {
            this.posSeqImpedancePercent = posSeqImpedancePercent;
        }

        public String getZeroSeqImpedancePercent() {
            return zeroSeqImpedancePercent;
        }

        public void setZeroSeqImpedancePercent(String zeroSeqImpedancePercent) {
            this.zeroSeqImpedancePercent = zeroSeqImpedancePercent;
        }

        public String getZeroSeqImpedancePrimSecPercent() {
            return zeroSeqImpedancePrimSecPercent;
        }

        public void setZeroSeqImpedancePrimSecPercent(String zeroSeqImpedancePrimSecPercent) {
            this.zeroSeqImpedancePrimSecPercent = zeroSeqImpedancePrimSecPercent;
        }

        public String getZeroSeqImpedancePrimMagPercent() {
            return zeroSeqImpedancePrimMagPercent;
        }

        public void setZeroSeqImpedancePrimMagPercent(String zeroSeqImpedancePrimMagPercent) {
            this.zeroSeqImpedancePrimMagPercent = zeroSeqImpedancePrimMagPercent;
        }

        public String getZeroSeqImpedanceSecMagPercent() {
            return zeroSeqImpedanceSecMagPercent;
        }

        public void setZeroSeqImpedanceSecMagPercent(String zeroSeqImpedanceSecMagPercent) {
            this.zeroSeqImpedanceSecMagPercent = zeroSeqImpedanceSecMagPercent;
        }

        public String getXRRatio() {
            return xRRatio;
        }

        public void setXRRatio(String xRRatio) {
            this.xRRatio = xRRatio;
        }

        public Integer getTransformerConnection() {
            return transformerConnection;
        }

        public void setTransformerConnection(Integer transformerConnection) {
            this.transformerConnection = transformerConnection;
        }

        public String getPrimGroundingResistanceOhms() {
            return primGroundingResistanceOhms;
        }

        public void setPrimGroundingResistanceOhms(String primGroundingResistanceOhms) {
            this.primGroundingResistanceOhms = primGroundingResistanceOhms;
        }

        public String getPrimGroundingReactanceOhms() {
            return primGroundingReactanceOhms;
        }

        public void setPrimGroundingReactanceOhms(String primGroundingReactanceOhms) {
            this.primGroundingReactanceOhms = primGroundingReactanceOhms;
        }

        public String getSecGroundingResistanceOhms() {
            return secGroundingResistanceOhms;
        }

        public void setSecGroundingResistanceOhms(String secGroundingResistanceOhms) {
            this.secGroundingResistanceOhms = secGroundingResistanceOhms;
        }

        public String getSecGroundingReactanceOhms() {
            return secGroundingReactanceOhms;
        }

        public void setSecGroundingReactanceOhms(String secGroundingReactanceOhms) {
            this.secGroundingReactanceOhms = secGroundingReactanceOhms;
        }

        public String getNoLoadLossesKW() {
            return noLoadLossesKW;
        }

        public void setNoLoadLossesKW(String noLoadLossesKW) {
            this.noLoadLossesKW = noLoadLossesKW;
        }

        public Integer getReversible() {
            return reversible;
        }

        public void setReversible(Integer reversible) {
            this.reversible = reversible;
        }

        public String getXR0Ratio() {
            return xR0Ratio;
        }

        public void setXR0Ratio(String xR0Ratio) {
            this.xR0Ratio = xR0Ratio;
        }

        public String getXR0PrimSecRatio() {
            return xR0PrimSecRatio;
        }

        public void setXR0PrimSecRatio(String xR0PrimSecRatio) {
            this.xR0PrimSecRatio = xR0PrimSecRatio;
        }

        public String getXR0PrimMagRatio() {
            return xR0PrimMagRatio;
        }

        public void setXR0PrimMagRatio(String xR0PrimMagRatio) {
            this.xR0PrimMagRatio = xR0PrimMagRatio;
        }

        public String getXR0SecMagRatio() {
            return xR0SecMagRatio;
        }

        public void setXR0SecMagRatio(String xR0SecMagRatio) {
            this.xR0SecMagRatio = xR0SecMagRatio;
        }

        public String getMagnetizingCurrent() {
            return magnetizingCurrent;
        }

        public void setMagnetizingCurrent(String magnetizingCurrent) {
            this.magnetizingCurrent = magnetizingCurrent;
        }

        public Integer getPhaseShift() {
            return phaseShift;
        }

        public void setPhaseShift(Integer phaseShift) {
            this.phaseShift = phaseShift;
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

        public Integer getInsulationType() {
            return insulationType;
        }

        public void setInsulationType(Integer insulationType) {
            this.insulationType = insulationType;
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

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getCaDeviceNumber() {
            return caDeviceNumber;
        }

        public void setCaDeviceNumber(String caDeviceNumber) {
            this.caDeviceNumber = caDeviceNumber;
        }

        public String getCableId() {
            return cableId;
        }

        public void setCableId(String cableId) {
            this.cableId = cableId;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Integer getCableType() {
            return cableType;
        }

        public void setCableType(Integer cableType) {
            this.cableType = cableType;
        }

        public String getPrimaryTapSettingPercent() {
            return primaryTapSettingPercent;
        }

        public void setPrimaryTapSettingPercent(String primaryTapSettingPercent) {
            this.primaryTapSettingPercent = primaryTapSettingPercent;
        }

        public String getSecondaryTapSettingPercent() {
            return secondaryTapSettingPercent;
        }

        public void setSecondaryTapSettingPercent(String secondaryTapSettingPercent) {
            this.secondaryTapSettingPercent = secondaryTapSettingPercent;
        }

        public Integer getFaultIndicator() {
            return faultIndicator;
        }

        public void setFaultIndicator(Integer faultIndicator) {
            this.faultIndicator = faultIndicator;
        }

        public Integer getDtStatus() {
            return dtStatus;
        }

        public void setDtStatus(Integer dtStatus) {
            this.dtStatus = dtStatus;
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



