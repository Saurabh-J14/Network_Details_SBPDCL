package com.techLabs.nbpdcl.models.device;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class ShuntCapacitor {

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
        @SerializedName("NetworkId")
        @Expose
        private String networkId;
        @SerializedName("EquipmentId")
        @Expose
        private String equipmentId;
        @SerializedName("Status")
        @Expose
        private Integer status;
        @SerializedName("ConnectionConfiguration")
        @Expose
        private String connectionConfiguration;
        @SerializedName("KVARA")
        @Expose
        private String kvara;
        @SerializedName("KVARB")
        @Expose
        private String kvarb;
        @SerializedName("KVARC")
        @Expose
        private String kvarc;
        @SerializedName("KVLN")
        @Expose
        private String kvln;
        @SerializedName("CapacitorControlType")
        @Expose
        private String capacitorControlType;
        @SerializedName("OnValueA")
        @Expose
        private String onValueA;
        @SerializedName("OnValueB")
        @Expose
        private String onValueB;
        @SerializedName("OnValueC")
        @Expose
        private String onValueC;
        @SerializedName("OffValueA")
        @Expose
        private String offValueA;
        @SerializedName("OffValueB")
        @Expose
        private String offValueB;
        @SerializedName("OffValueC")
        @Expose
        private String offValueC;
        @SerializedName("SwitchingMode")
        @Expose
        private String switchingMode;
        @SerializedName("InitiallyClosedPhase")
        @Expose
        private String initiallyClosedPhase;
        @SerializedName("CurrentClosedPhase")
        @Expose
        private String currentClosedPhase;
        @SerializedName("ControlledPhase")
        @Expose
        private String controlledPhase;
        @SerializedName("SensorLocation")
        @Expose
        private String sensorLocation;
        @SerializedName("ControlledNodeId")
        @Expose
        private String controlledNodeId;
        @SerializedName("SymbolSize")
        @Expose
        private String symbolSize;
        @SerializedName("LossesA")
        @Expose
        private String lossesA;
        @SerializedName("LossesB")
        @Expose
        private String lossesB;
        @SerializedName("LossesC")
        @Expose
        private String lossesC;
        @SerializedName("ByPhase")
        @Expose
        private String byPhase;
        @SerializedName("SwitchedKVARA")
        @Expose
        private String switchedKVARA;
        @SerializedName("SwitchedKVARB")
        @Expose
        private String switchedKVARB;
        @SerializedName("SwitchedKVARC")
        @Expose
        private String switchedKVARC;
        @SerializedName("SwitchedLossesA")
        @Expose
        private String switchedLossesA;
        @SerializedName("SwitchedLossesB")
        @Expose
        private String switchedLossesB;
        @SerializedName("SwitchedLossesC")
        @Expose
        private String switchedLossesC;
        @SerializedName("VoltageOverride")
        @Expose
        private String voltageOverride;
        @SerializedName("VoltageOverrideOn")
        @Expose
        private String voltageOverrideOn;
        @SerializedName("VoltageOverrideOff")
        @Expose
        private String voltageOverrideOff;
        @SerializedName("VoltageOverrideDeadband")
        @Expose
        private String voltageOverrideDeadband;
        @SerializedName("CTConnection")
        @Expose
        private String cTConnection;
        @SerializedName("InterruptingRating")
        @Expose
        private String interruptingRating;
        @SerializedName("PythonDeviceScriptID")
        @Expose
        private String pythonDeviceScriptID;
        @SerializedName("DeviceType")
        @Expose
        private Integer deviceType;
        @SerializedName("SectionId")
        @Expose
        private String sectionId;
        @SerializedName("Location")
        @Expose
        private Integer location;
        @SerializedName("DeviceStage")
        @Expose
        private String deviceStage;
        @SerializedName("Flags")
        @Expose
        private String flags;
        @SerializedName("ComponentMask")
        @Expose
        private String componentMask;
        @SerializedName("InitFromEquipFlags")
        @Expose
        private String initFromEquipFlags;
        @SerializedName("FromNodeId")
        @Expose
        private String fromNodeId;
        @SerializedName("FromNodeConnectorIndex")
        @Expose
        private String fromNodeConnectorIndex;
        @SerializedName("ToNodeId")
        @Expose
        private String toNodeId;
        @SerializedName("ToNodeConnectorIndex")
        @Expose
        private String toNodeConnectorIndex;
        @SerializedName("Phase")
        @Expose
        private Integer phase;
        @SerializedName("ZoneId")
        @Expose
        private String zoneId;
        @SerializedName("StructureId")
        @Expose
        private String structureId;
        @SerializedName("BreakPointIndex")
        @Expose
        private String breakPointIndex;
        @SerializedName("BreakPointLocation")
        @Expose
        private String breakPointLocation;
        @SerializedName("FROMX")
        @Expose
        private String fromx;
        @SerializedName("FROMY")
        @Expose
        private String fromy;
        @SerializedName("TOX")
        @Expose
        private String tox;
        @SerializedName("TOY")
        @Expose
        private String toy;
        @SerializedName("RatedKVAR")
        @Expose
        private String ratedKVAR;
        @SerializedName("RatedVoltageKVLL")
        @Expose
        private String ratedVoltageKVLL;
        @SerializedName("CostForFixedBank")
        @Expose
        private String costForFixedBank;
        @SerializedName("CostForSwitchedBank")
        @Expose
        private String costForSwitchedBank;
        @SerializedName("LossesKW")
        @Expose
        private String lossesKW;
        @SerializedName("PhaseType")
        @Expose
        private String phaseType;
        @SerializedName("Favorite")
        @Expose
        private String favorite;
        @SerializedName("ModifiedByUser")
        @Expose
        private String modifiedByUser;
        @SerializedName("LastChange")
        @Expose
        private String lastChange;
        @SerializedName("Comments")
        @Expose
        private String comments;
        @SerializedName("DeviceTypeLine")
        @Expose
        private Integer deviceTypeLine;
        @SerializedName("LineDeviceNumber")
        @Expose
        private String lineDeviceNumber;

        public String getDeviceNumber() {
            return deviceNumber;
        }

        public void setDeviceNumber(String deviceNumber) {
            this.deviceNumber = deviceNumber;
        }

        public String getNetworkId() {
            return networkId;
        }

        public void setNetworkId(String networkId) {
            this.networkId = networkId;
        }

        public String getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(String equipmentId) {
            this.equipmentId = equipmentId;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getConnectionConfiguration() {
            return connectionConfiguration;
        }

        public void setConnectionConfiguration(String connectionConfiguration) {
            this.connectionConfiguration = connectionConfiguration;
        }

        public String getKvara() {
            return kvara;
        }

        public void setKvara(String kvara) {
            this.kvara = kvara;
        }

        public String getKvarb() {
            return kvarb;
        }

        public void setKvarb(String kvarb) {
            this.kvarb = kvarb;
        }

        public String getKvarc() {
            return kvarc;
        }

        public void setKvarc(String kvarc) {
            this.kvarc = kvarc;
        }

        public String getKvln() {
            return kvln;
        }

        public void setKvln(String kvln) {
            this.kvln = kvln;
        }

        public String getCapacitorControlType() {
            return capacitorControlType;
        }

        public void setCapacitorControlType(String capacitorControlType) {
            this.capacitorControlType = capacitorControlType;
        }

        public String getOnValueA() {
            return onValueA;
        }

        public void setOnValueA(String onValueA) {
            this.onValueA = onValueA;
        }

        public String getOnValueB() {
            return onValueB;
        }

        public void setOnValueB(String onValueB) {
            this.onValueB = onValueB;
        }

        public String getOnValueC() {
            return onValueC;
        }

        public void setOnValueC(String onValueC) {
            this.onValueC = onValueC;
        }

        public String getOffValueA() {
            return offValueA;
        }

        public void setOffValueA(String offValueA) {
            this.offValueA = offValueA;
        }

        public String getOffValueB() {
            return offValueB;
        }

        public void setOffValueB(String offValueB) {
            this.offValueB = offValueB;
        }

        public String getOffValueC() {
            return offValueC;
        }

        public void setOffValueC(String offValueC) {
            this.offValueC = offValueC;
        }

        public String getSwitchingMode() {
            return switchingMode;
        }

        public void setSwitchingMode(String switchingMode) {
            this.switchingMode = switchingMode;
        }

        public String getInitiallyClosedPhase() {
            return initiallyClosedPhase;
        }

        public void setInitiallyClosedPhase(String initiallyClosedPhase) {
            this.initiallyClosedPhase = initiallyClosedPhase;
        }

        public String getCurrentClosedPhase() {
            return currentClosedPhase;
        }

        public void setCurrentClosedPhase(String currentClosedPhase) {
            this.currentClosedPhase = currentClosedPhase;
        }

        public String getControlledPhase() {
            return controlledPhase;
        }

        public void setControlledPhase(String controlledPhase) {
            this.controlledPhase = controlledPhase;
        }

        public String getSensorLocation() {
            return sensorLocation;
        }

        public void setSensorLocation(String sensorLocation) {
            this.sensorLocation = sensorLocation;
        }

        public String getControlledNodeId() {
            return controlledNodeId;
        }

        public void setControlledNodeId(String controlledNodeId) {
            this.controlledNodeId = controlledNodeId;
        }

        public String getSymbolSize() {
            return symbolSize;
        }

        public void setSymbolSize(String symbolSize) {
            this.symbolSize = symbolSize;
        }

        public String getLossesA() {
            return lossesA;
        }

        public void setLossesA(String lossesA) {
            this.lossesA = lossesA;
        }

        public String getLossesB() {
            return lossesB;
        }

        public void setLossesB(String lossesB) {
            this.lossesB = lossesB;
        }

        public String getLossesC() {
            return lossesC;
        }

        public void setLossesC(String lossesC) {
            this.lossesC = lossesC;
        }

        public String getByPhase() {
            return byPhase;
        }

        public void setByPhase(String byPhase) {
            this.byPhase = byPhase;
        }

        public String getSwitchedKVARA() {
            return switchedKVARA;
        }

        public void setSwitchedKVARA(String switchedKVARA) {
            this.switchedKVARA = switchedKVARA;
        }

        public String getSwitchedKVARB() {
            return switchedKVARB;
        }

        public void setSwitchedKVARB(String switchedKVARB) {
            this.switchedKVARB = switchedKVARB;
        }

        public String getSwitchedKVARC() {
            return switchedKVARC;
        }

        public void setSwitchedKVARC(String switchedKVARC) {
            this.switchedKVARC = switchedKVARC;
        }

        public String getSwitchedLossesA() {
            return switchedLossesA;
        }

        public void setSwitchedLossesA(String switchedLossesA) {
            this.switchedLossesA = switchedLossesA;
        }

        public String getSwitchedLossesB() {
            return switchedLossesB;
        }

        public void setSwitchedLossesB(String switchedLossesB) {
            this.switchedLossesB = switchedLossesB;
        }

        public String getSwitchedLossesC() {
            return switchedLossesC;
        }

        public void setSwitchedLossesC(String switchedLossesC) {
            this.switchedLossesC = switchedLossesC;
        }

        public String getVoltageOverride() {
            return voltageOverride;
        }

        public void setVoltageOverride(String voltageOverride) {
            this.voltageOverride = voltageOverride;
        }

        public String getVoltageOverrideOn() {
            return voltageOverrideOn;
        }

        public void setVoltageOverrideOn(String voltageOverrideOn) {
            this.voltageOverrideOn = voltageOverrideOn;
        }

        public String getVoltageOverrideOff() {
            return voltageOverrideOff;
        }

        public void setVoltageOverrideOff(String voltageOverrideOff) {
            this.voltageOverrideOff = voltageOverrideOff;
        }

        public String getVoltageOverrideDeadband() {
            return voltageOverrideDeadband;
        }

        public void setVoltageOverrideDeadband(String voltageOverrideDeadband) {
            this.voltageOverrideDeadband = voltageOverrideDeadband;
        }

        public String getCTConnection() {
            return cTConnection;
        }

        public void setCTConnection(String cTConnection) {
            this.cTConnection = cTConnection;
        }

        public String getInterruptingRating() {
            return interruptingRating;
        }

        public void setInterruptingRating(String interruptingRating) {
            this.interruptingRating = interruptingRating;
        }

        public String getPythonDeviceScriptID() {
            return pythonDeviceScriptID;
        }

        public void setPythonDeviceScriptID(String pythonDeviceScriptID) {
            this.pythonDeviceScriptID = pythonDeviceScriptID;
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

        public Integer getLocation() {
            return location;
        }

        public void setLocation(Integer location) {
            this.location = location;
        }

        public String getDeviceStage() {
            return deviceStage;
        }

        public void setDeviceStage(String deviceStage) {
            this.deviceStage = deviceStage;
        }

        public String getFlags() {
            return flags;
        }

        public void setFlags(String flags) {
            this.flags = flags;
        }

        public String getComponentMask() {
            return componentMask;
        }

        public void setComponentMask(String componentMask) {
            this.componentMask = componentMask;
        }

        public String getInitFromEquipFlags() {
            return initFromEquipFlags;
        }

        public void setInitFromEquipFlags(String initFromEquipFlags) {
            this.initFromEquipFlags = initFromEquipFlags;
        }

        public String getFromNodeId() {
            return fromNodeId;
        }

        public void setFromNodeId(String fromNodeId) {
            this.fromNodeId = fromNodeId;
        }

        public String getFromNodeConnectorIndex() {
            return fromNodeConnectorIndex;
        }

        public void setFromNodeConnectorIndex(String fromNodeConnectorIndex) {
            this.fromNodeConnectorIndex = fromNodeConnectorIndex;
        }

        public String getToNodeId() {
            return toNodeId;
        }

        public void setToNodeId(String toNodeId) {
            this.toNodeId = toNodeId;
        }

        public String getToNodeConnectorIndex() {
            return toNodeConnectorIndex;
        }

        public void setToNodeConnectorIndex(String toNodeConnectorIndex) {
            this.toNodeConnectorIndex = toNodeConnectorIndex;
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

        public String getStructureId() {
            return structureId;
        }

        public void setStructureId(String structureId) {
            this.structureId = structureId;
        }

        public String getBreakPointIndex() {
            return breakPointIndex;
        }

        public void setBreakPointIndex(String breakPointIndex) {
            this.breakPointIndex = breakPointIndex;
        }

        public String getBreakPointLocation() {
            return breakPointLocation;
        }

        public void setBreakPointLocation(String breakPointLocation) {
            this.breakPointLocation = breakPointLocation;
        }

        public String getFromx() {
            return fromx;
        }

        public void setFromx(String fromx) {
            this.fromx = fromx;
        }

        public String getFromy() {
            return fromy;
        }

        public void setFromy(String fromy) {
            this.fromy = fromy;
        }

        public String getTox() {
            return tox;
        }

        public void setTox(String tox) {
            this.tox = tox;
        }

        public String getToy() {
            return toy;
        }

        public void setToy(String toy) {
            this.toy = toy;
        }

        public String getRatedKVAR() {
            return ratedKVAR;
        }

        public void setRatedKVAR(String ratedKVAR) {
            this.ratedKVAR = ratedKVAR;
        }

        public String getRatedVoltageKVLL() {
            return ratedVoltageKVLL;
        }

        public void setRatedVoltageKVLL(String ratedVoltageKVLL) {
            this.ratedVoltageKVLL = ratedVoltageKVLL;
        }

        public String getCostForFixedBank() {
            return costForFixedBank;
        }

        public void setCostForFixedBank(String costForFixedBank) {
            this.costForFixedBank = costForFixedBank;
        }

        public String getCostForSwitchedBank() {
            return costForSwitchedBank;
        }

        public void setCostForSwitchedBank(String costForSwitchedBank) {
            this.costForSwitchedBank = costForSwitchedBank;
        }

        public String getLossesKW() {
            return lossesKW;
        }

        public void setLossesKW(String lossesKW) {
            this.lossesKW = lossesKW;
        }

        public String getPhaseType() {
            return phaseType;
        }

        public void setPhaseType(String phaseType) {
            this.phaseType = phaseType;
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

    }

}

