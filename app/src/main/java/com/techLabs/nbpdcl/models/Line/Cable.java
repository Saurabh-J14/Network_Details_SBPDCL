package com.techLabs.nbpdcl.models.Line;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cable {

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
        @SerializedName("CableId")
        @Expose
        private String cableId;
        @SerializedName("Length")
        @Expose
        private String length;
        @SerializedName("NumberOfCableInParallel")
        @Expose
        private String numberOfCableInParallel;
        @SerializedName("CTConnection")
        @Expose
        private String cTConnection;
        @SerializedName("X")
        @Expose
        private String x;
        @SerializedName("Y")
        @Expose
        private String y;
        @SerializedName("Status")
        @Expose
        private Integer status;
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
        @SerializedName("HarmonicModel")
        @Expose
        private String harmonicModel;
        @SerializedName("TCCRepositoryID")
        @Expose
        private String tCCRepositoryID;
        @SerializedName("OperatingTemperature")
        @Expose
        private String operatingTemperature;
        @SerializedName("Neutral1Type")
        @Expose
        private String neutral1Type;
        @SerializedName("Neutral2Type")
        @Expose
        private String neutral2Type;
        @SerializedName("Neutral3Type")
        @Expose
        private String neutral3Type;
        @SerializedName("Neutral1ID")
        @Expose
        private String neutral1ID;
        @SerializedName("Neutral2ID")
        @Expose
        private String neutral2ID;
        @SerializedName("Neutral3ID")
        @Expose
        private String neutral3ID;
        @SerializedName("AmpacityDeratingFactor")
        @Expose
        private String ampacityDeratingFactor;
        @SerializedName("FlowConstraintActive")
        @Expose
        private String flowConstraintActive;
        @SerializedName("FlowConstraintUnit")
        @Expose
        private String flowConstraintUnit;
        @SerializedName("MaximumFlow")
        @Expose
        private String maximumFlow;
        @SerializedName("DeviceType")
        @Expose
        private String deviceType;
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
        @SerializedName("FROM_NodeId")
        @Expose
        private String fROMNodeId;
        @SerializedName("FROM_ComponentMask")
        @Expose
        private String fROMComponentMask;
        @SerializedName("FROM_NodeId_X")
        @Expose
        private String fROMNodeIdX;
        @SerializedName("FROM_NodeId_Y")
        @Expose
        private String fROMNodeIdY;
        @SerializedName("FROM_ZoneId")
        @Expose
        private String fROMZoneId;
        @SerializedName("FROM_UserDefinedBaseVoltage")
        @Expose
        private String fROMUserDefinedBaseVoltage;
        @SerializedName("FROM_RatedVoltage")
        @Expose
        private String fROMRatedVoltage;
        @SerializedName("FROM_RatedCurrent")
        @Expose
        private String fROMRatedCurrent;
        @SerializedName("FROM_ANSISymCurrent")
        @Expose
        private String fROMANSISymCurrent;
        @SerializedName("FROM_ANSIAsymCurrent")
        @Expose
        private String fROMANSIAsymCurrent;
        @SerializedName("FROM_PeakCurrent")
        @Expose
        private String fROMPeakCurrent;
        @SerializedName("FROM_Standard")
        @Expose
        private String fROMStandard;
        @SerializedName("FROM_TestCircuitPowerFactor")
        @Expose
        private String fROMTestCircuitPowerFactor;
        @SerializedName("Installation")
        @Expose
        private String installation;
        @SerializedName("TO_NodeId")
        @Expose
        private String tONodeId;
        @SerializedName("TO_ComponentMask")
        @Expose
        private String tOComponentMask;
        @SerializedName("TO_NodeId_X")
        @Expose
        private String tONodeIdX;
        @SerializedName("TO_NodeId_Y")
        @Expose
        private String tONodeIdY;
        @SerializedName("TO_ZoneId")
        @Expose
        private String tOZoneId;
        @SerializedName("TO_UserDefinedBaseVoltage")
        @Expose
        private String tOUserDefinedBaseVoltage;
        @SerializedName("TO_RatedVoltage")
        @Expose
        private String tORatedVoltage;
        @SerializedName("TO_RatedCurrent")
        @Expose
        private String tORatedCurrent;
        @SerializedName("TO_ANSISymCurrent")
        @Expose
        private String tOANSISymCurrent;
        @SerializedName("TO_ANSIAsymCurrent")
        @Expose
        private String tOANSIAsymCurrent;
        @SerializedName("TO_PeakCurrent")
        @Expose
        private String tOPeakCurrent;
        @SerializedName("TO_Standard")
        @Expose
        private String tOStandard;
        @SerializedName("TO_TestCircuitPowerFactor")
        @Expose
        private String tOTestCircuitPowerFactor;
        @SerializedName("TO_Installation")
        @Expose
        private String tOInstallation;
        @SerializedName("EquipmentId")
        @Expose
        private String equipmentId;
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
        @SerializedName("WithstandRating")
        @Expose
        private String withstandRating;
        @SerializedName("ZeroSequenceShuntSusceptance")
        @Expose
        private String zeroSequenceShuntSusceptance;
        @SerializedName("LevelKV")
        @Expose
        private String levelKV;
        @SerializedName("Manufacturer")
        @Expose
        private String manufacturer;
        @SerializedName("Standard")
        @Expose
        private String standard;
        @SerializedName("CableType")
        @Expose
        private Integer cableType;
        @SerializedName("NumberOfGroundingConductors")
        @Expose
        private String numberOfGroundingConductors;
        @SerializedName("ConcentricNeutralBeforeSheath")
        @Expose
        private String concentricNeutralBeforeSheath;
        @SerializedName("UserDefinedImpedances")
        @Expose
        private String userDefinedImpedances;
        @SerializedName("Frequency")
        @Expose
        private String frequency;
        @SerializedName("Temperature")
        @Expose
        private String temperature;
        @SerializedName("ImpedancesNote")
        @Expose
        private String impedancesNote;
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
        @SerializedName("PosSeqShuntConductance")
        @Expose
        private String posSeqShuntConductance;
        @SerializedName("ZeroSequenceShuntConductance")
        @Expose
        private String zeroSequenceShuntConductance;
        @SerializedName("LockImpedance")
        @Expose
        private String lockImpedance;
        @SerializedName("CableConcentricNeutralLocation")
        @Expose
        private String cableConcentricNeutralLocation;
        @SerializedName("MaterialID")
        @Expose
        private String materialID;
        @SerializedName("LayerPosition")
        @Expose
        private String layerPosition;
        @SerializedName("Thickness")
        @Expose
        private String thickness;
        @SerializedName("ConcentricNeutralsType")
        @Expose
        private String concentricNeutralsType;
        @SerializedName("NumberOfWires")
        @Expose
        private String numberOfWires;
        @SerializedName("StrapWidth")
        @Expose
        private String strapWidth;
        @SerializedName("LayLength")
        @Expose
        private String layLength;
        @SerializedName("CableConductorLocation")
        @Expose
        private String cableConductorLocation;
        @SerializedName("CableSize")
        @Expose
        private String cableSize;
        @SerializedName("Size_mm2")
        @Expose
        private String sizeMm2;
        @SerializedName("Diameter")
        @Expose
        private String diameter;
        @SerializedName("ConstructionType")
        @Expose
        private String constructionType;
        @SerializedName("NumberOfStrands")
        @Expose
        private String numberOfStrands;
        @SerializedName("CableSheathLocation")
        @Expose
        private String cableSheathLocation;
        @SerializedName("SheathType")
        @Expose
        private String sheathType;
        @SerializedName("TapeThickness")
        @Expose
        private String tapeThickness;
        @SerializedName("NumberOfTapes")
        @Expose
        private String numberOfTapes;
        @SerializedName("TapeWidth")
        @Expose
        private String tapeWidth;
        @SerializedName("OverlapRatio")
        @Expose
        private String overlapRatio;

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

        public String getCableId() {
            return cableId;
        }

        public void setCableId(String cableId) {
            this.cableId = cableId;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getNumberOfCableInParallel() {
            return numberOfCableInParallel;
        }

        public void setNumberOfCableInParallel(String numberOfCableInParallel) {
            this.numberOfCableInParallel = numberOfCableInParallel;
        }

        public String getCTConnection() {
            return cTConnection;
        }

        public void setCTConnection(String cTConnection) {
            this.cTConnection = cTConnection;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
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

        public String getHarmonicModel() {
            return harmonicModel;
        }

        public void setHarmonicModel(String harmonicModel) {
            this.harmonicModel = harmonicModel;
        }

        public String getTCCRepositoryID() {
            return tCCRepositoryID;
        }

        public void setTCCRepositoryID(String tCCRepositoryID) {
            this.tCCRepositoryID = tCCRepositoryID;
        }

        public String getOperatingTemperature() {
            return operatingTemperature;
        }

        public void setOperatingTemperature(String operatingTemperature) {
            this.operatingTemperature = operatingTemperature;
        }

        public String getNeutral1Type() {
            return neutral1Type;
        }

        public void setNeutral1Type(String neutral1Type) {
            this.neutral1Type = neutral1Type;
        }

        public String getNeutral2Type() {
            return neutral2Type;
        }

        public void setNeutral2Type(String neutral2Type) {
            this.neutral2Type = neutral2Type;
        }

        public String getNeutral3Type() {
            return neutral3Type;
        }

        public void setNeutral3Type(String neutral3Type) {
            this.neutral3Type = neutral3Type;
        }

        public String getNeutral1ID() {
            return neutral1ID;
        }

        public void setNeutral1ID(String neutral1ID) {
            this.neutral1ID = neutral1ID;
        }

        public String getNeutral2ID() {
            return neutral2ID;
        }

        public void setNeutral2ID(String neutral2ID) {
            this.neutral2ID = neutral2ID;
        }

        public String getNeutral3ID() {
            return neutral3ID;
        }

        public void setNeutral3ID(String neutral3ID) {
            this.neutral3ID = neutral3ID;
        }

        public String getAmpacityDeratingFactor() {
            return ampacityDeratingFactor;
        }

        public void setAmpacityDeratingFactor(String ampacityDeratingFactor) {
            this.ampacityDeratingFactor = ampacityDeratingFactor;
        }

        public String getFlowConstraintActive() {
            return flowConstraintActive;
        }

        public void setFlowConstraintActive(String flowConstraintActive) {
            this.flowConstraintActive = flowConstraintActive;
        }

        public String getFlowConstraintUnit() {
            return flowConstraintUnit;
        }

        public void setFlowConstraintUnit(String flowConstraintUnit) {
            this.flowConstraintUnit = flowConstraintUnit;
        }

        public String getMaximumFlow() {
            return maximumFlow;
        }

        public void setMaximumFlow(String maximumFlow) {
            this.maximumFlow = maximumFlow;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
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

        public String getFROMNodeId() {
            return fROMNodeId;
        }

        public void setFROMNodeId(String fROMNodeId) {
            this.fROMNodeId = fROMNodeId;
        }

        public String getFROMComponentMask() {
            return fROMComponentMask;
        }

        public void setFROMComponentMask(String fROMComponentMask) {
            this.fROMComponentMask = fROMComponentMask;
        }

        public String getFROMNodeIdX() {
            return fROMNodeIdX;
        }

        public void setFROMNodeIdX(String fROMNodeIdX) {
            this.fROMNodeIdX = fROMNodeIdX;
        }

        public String getFROMNodeIdY() {
            return fROMNodeIdY;
        }

        public void setFROMNodeIdY(String fROMNodeIdY) {
            this.fROMNodeIdY = fROMNodeIdY;
        }

        public String getFROMZoneId() {
            return fROMZoneId;
        }

        public void setFROMZoneId(String fROMZoneId) {
            this.fROMZoneId = fROMZoneId;
        }

        public String getFROMUserDefinedBaseVoltage() {
            return fROMUserDefinedBaseVoltage;
        }

        public void setFROMUserDefinedBaseVoltage(String fROMUserDefinedBaseVoltage) {
            this.fROMUserDefinedBaseVoltage = fROMUserDefinedBaseVoltage;
        }

        public String getFROMRatedVoltage() {
            return fROMRatedVoltage;
        }

        public void setFROMRatedVoltage(String fROMRatedVoltage) {
            this.fROMRatedVoltage = fROMRatedVoltage;
        }

        public String getFROMRatedCurrent() {
            return fROMRatedCurrent;
        }

        public void setFROMRatedCurrent(String fROMRatedCurrent) {
            this.fROMRatedCurrent = fROMRatedCurrent;
        }

        public String getFROMANSISymCurrent() {
            return fROMANSISymCurrent;
        }

        public void setFROMANSISymCurrent(String fROMANSISymCurrent) {
            this.fROMANSISymCurrent = fROMANSISymCurrent;
        }

        public String getFROMANSIAsymCurrent() {
            return fROMANSIAsymCurrent;
        }

        public void setFROMANSIAsymCurrent(String fROMANSIAsymCurrent) {
            this.fROMANSIAsymCurrent = fROMANSIAsymCurrent;
        }

        public String getFROMPeakCurrent() {
            return fROMPeakCurrent;
        }

        public void setFROMPeakCurrent(String fROMPeakCurrent) {
            this.fROMPeakCurrent = fROMPeakCurrent;
        }

        public String getFROMStandard() {
            return fROMStandard;
        }

        public void setFROMStandard(String fROMStandard) {
            this.fROMStandard = fROMStandard;
        }

        public String getFROMTestCircuitPowerFactor() {
            return fROMTestCircuitPowerFactor;
        }

        public void setFROMTestCircuitPowerFactor(String fROMTestCircuitPowerFactor) {
            this.fROMTestCircuitPowerFactor = fROMTestCircuitPowerFactor;
        }

        public String getInstallation() {
            return installation;
        }

        public void setInstallation(String installation) {
            this.installation = installation;
        }

        public String getTONodeId() {
            return tONodeId;
        }

        public void setTONodeId(String tONodeId) {
            this.tONodeId = tONodeId;
        }

        public String getTOComponentMask() {
            return tOComponentMask;
        }

        public void setTOComponentMask(String tOComponentMask) {
            this.tOComponentMask = tOComponentMask;
        }

        public String getTONodeIdX() {
            return tONodeIdX;
        }

        public void setTONodeIdX(String tONodeIdX) {
            this.tONodeIdX = tONodeIdX;
        }

        public String getTONodeIdY() {
            return tONodeIdY;
        }

        public void setTONodeIdY(String tONodeIdY) {
            this.tONodeIdY = tONodeIdY;
        }

        public String getTOZoneId() {
            return tOZoneId;
        }

        public void setTOZoneId(String tOZoneId) {
            this.tOZoneId = tOZoneId;
        }

        public String getTOUserDefinedBaseVoltage() {
            return tOUserDefinedBaseVoltage;
        }

        public void setTOUserDefinedBaseVoltage(String tOUserDefinedBaseVoltage) {
            this.tOUserDefinedBaseVoltage = tOUserDefinedBaseVoltage;
        }

        public String getTORatedVoltage() {
            return tORatedVoltage;
        }

        public void setTORatedVoltage(String tORatedVoltage) {
            this.tORatedVoltage = tORatedVoltage;
        }

        public String getTORatedCurrent() {
            return tORatedCurrent;
        }

        public void setTORatedCurrent(String tORatedCurrent) {
            this.tORatedCurrent = tORatedCurrent;
        }

        public String getTOANSISymCurrent() {
            return tOANSISymCurrent;
        }

        public void setTOANSISymCurrent(String tOANSISymCurrent) {
            this.tOANSISymCurrent = tOANSISymCurrent;
        }

        public String getTOANSIAsymCurrent() {
            return tOANSIAsymCurrent;
        }

        public void setTOANSIAsymCurrent(String tOANSIAsymCurrent) {
            this.tOANSIAsymCurrent = tOANSIAsymCurrent;
        }

        public String getTOPeakCurrent() {
            return tOPeakCurrent;
        }

        public void setTOPeakCurrent(String tOPeakCurrent) {
            this.tOPeakCurrent = tOPeakCurrent;
        }

        public String getTOStandard() {
            return tOStandard;
        }

        public void setTOStandard(String tOStandard) {
            this.tOStandard = tOStandard;
        }

        public String getTOTestCircuitPowerFactor() {
            return tOTestCircuitPowerFactor;
        }

        public void setTOTestCircuitPowerFactor(String tOTestCircuitPowerFactor) {
            this.tOTestCircuitPowerFactor = tOTestCircuitPowerFactor;
        }

        public String getTOInstallation() {
            return tOInstallation;
        }

        public void setTOInstallation(String tOInstallation) {
            this.tOInstallation = tOInstallation;
        }

        public String getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(String equipmentId) {
            this.equipmentId = equipmentId;
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

        public String getWithstandRating() {
            return withstandRating;
        }

        public void setWithstandRating(String withstandRating) {
            this.withstandRating = withstandRating;
        }

        public String getZeroSequenceShuntSusceptance() {
            return zeroSequenceShuntSusceptance;
        }

        public void setZeroSequenceShuntSusceptance(String zeroSequenceShuntSusceptance) {
            this.zeroSequenceShuntSusceptance = zeroSequenceShuntSusceptance;
        }

        public String getLevelKV() {
            return levelKV;
        }

        public void setLevelKV(String levelKV) {
            this.levelKV = levelKV;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getStandard() {
            return standard;
        }

        public void setStandard(String standard) {
            this.standard = standard;
        }

        public Integer getCableType() {
            return cableType;
        }

        public void setCableType(Integer cableType) {
            this.cableType = cableType;
        }

        public String getNumberOfGroundingConductors() {
            return numberOfGroundingConductors;
        }

        public void setNumberOfGroundingConductors(String numberOfGroundingConductors) {
            this.numberOfGroundingConductors = numberOfGroundingConductors;
        }

        public String getConcentricNeutralBeforeSheath() {
            return concentricNeutralBeforeSheath;
        }

        public void setConcentricNeutralBeforeSheath(String concentricNeutralBeforeSheath) {
            this.concentricNeutralBeforeSheath = concentricNeutralBeforeSheath;
        }

        public String getUserDefinedImpedances() {
            return userDefinedImpedances;
        }

        public void setUserDefinedImpedances(String userDefinedImpedances) {
            this.userDefinedImpedances = userDefinedImpedances;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public String getImpedancesNote() {
            return impedancesNote;
        }

        public void setImpedancesNote(String impedancesNote) {
            this.impedancesNote = impedancesNote;
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

        public String getLockImpedance() {
            return lockImpedance;
        }

        public void setLockImpedance(String lockImpedance) {
            this.lockImpedance = lockImpedance;
        }

        public String getCableConcentricNeutralLocation() {
            return cableConcentricNeutralLocation;
        }

        public void setCableConcentricNeutralLocation(String cableConcentricNeutralLocation) {
            this.cableConcentricNeutralLocation = cableConcentricNeutralLocation;
        }

        public String getMaterialID() {
            return materialID;
        }

        public void setMaterialID(String materialID) {
            this.materialID = materialID;
        }

        public String getLayerPosition() {
            return layerPosition;
        }

        public void setLayerPosition(String layerPosition) {
            this.layerPosition = layerPosition;
        }

        public String getThickness() {
            return thickness;
        }

        public void setThickness(String thickness) {
            this.thickness = thickness;
        }

        public String getConcentricNeutralsType() {
            return concentricNeutralsType;
        }

        public void setConcentricNeutralsType(String concentricNeutralsType) {
            this.concentricNeutralsType = concentricNeutralsType;
        }

        public String getNumberOfWires() {
            return numberOfWires;
        }

        public void setNumberOfWires(String numberOfWires) {
            this.numberOfWires = numberOfWires;
        }

        public String getStrapWidth() {
            return strapWidth;
        }

        public void setStrapWidth(String strapWidth) {
            this.strapWidth = strapWidth;
        }

        public String getLayLength() {
            return layLength;
        }

        public void setLayLength(String layLength) {
            this.layLength = layLength;
        }

        public String getCableConductorLocation() {
            return cableConductorLocation;
        }

        public void setCableConductorLocation(String cableConductorLocation) {
            this.cableConductorLocation = cableConductorLocation;
        }

        public String getCableSize() {
            return cableSize;
        }

        public void setCableSize(String cableSize) {
            this.cableSize = cableSize;
        }

        public String getSizeMm2() {
            return sizeMm2;
        }

        public void setSizeMm2(String sizeMm2) {
            this.sizeMm2 = sizeMm2;
        }

        public String getDiameter() {
            return diameter;
        }

        public void setDiameter(String diameter) {
            this.diameter = diameter;
        }

        public String getConstructionType() {
            return constructionType;
        }

        public void setConstructionType(String constructionType) {
            this.constructionType = constructionType;
        }

        public String getNumberOfStrands() {
            return numberOfStrands;
        }

        public void setNumberOfStrands(String numberOfStrands) {
            this.numberOfStrands = numberOfStrands;
        }

        public String getCableSheathLocation() {
            return cableSheathLocation;
        }

        public void setCableSheathLocation(String cableSheathLocation) {
            this.cableSheathLocation = cableSheathLocation;
        }

        public String getSheathType() {
            return sheathType;
        }

        public void setSheathType(String sheathType) {
            this.sheathType = sheathType;
        }

        public String getTapeThickness() {
            return tapeThickness;
        }

        public void setTapeThickness(String tapeThickness) {
            this.tapeThickness = tapeThickness;
        }

        public String getNumberOfTapes() {
            return numberOfTapes;
        }

        public void setNumberOfTapes(String numberOfTapes) {
            this.numberOfTapes = numberOfTapes;
        }

        public String getTapeWidth() {
            return tapeWidth;
        }

        public void setTapeWidth(String tapeWidth) {
            this.tapeWidth = tapeWidth;
        }

        public String getOverlapRatio() {
            return overlapRatio;
        }

        public void setOverlapRatio(String overlapRatio) {
            this.overlapRatio = overlapRatio;
        }

    }

}
