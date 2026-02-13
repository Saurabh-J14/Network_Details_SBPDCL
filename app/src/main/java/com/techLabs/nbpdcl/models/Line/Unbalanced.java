package com.techLabs.nbpdcl.models.Line;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Unbalanced {

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
        @SerializedName("PhaseConductorIdA")
        @Expose
        private String phaseConductorIdA;
        @SerializedName("PhaseConductorIdB")
        @Expose
        private String phaseConductorIdB;
        @SerializedName("PhaseConductorIdC")
        @Expose
        private String phaseConductorIdC;
        @SerializedName("NeutralConductorId")
        @Expose
        private String neutralConductorId;
        @SerializedName("NeutralConductorId2")
        @Expose
        private String neutralConductorId2;
        @SerializedName("ConductorSpacingId")
        @Expose
        private String conductorSpacingId;
        @SerializedName("UserDefinedImpedances")
        @Expose
        private Integer userDefinedImpedances;
        @SerializedName("NominalRatingA")
        @Expose
        private String nominalRatingA;
        @SerializedName("NominalRatingB")
        @Expose
        private String nominalRatingB;
        @SerializedName("NominalRatingC")
        @Expose
        private String nominalRatingC;
        @SerializedName("FirstRatingA")
        @Expose
        private String firstRatingA;
        @SerializedName("FirstRatingB")
        @Expose
        private String firstRatingB;
        @SerializedName("FirstRatingC")
        @Expose
        private String firstRatingC;
        @SerializedName("SecondRatingA")
        @Expose
        private String secondRatingA;
        @SerializedName("SecondRatingB")
        @Expose
        private String secondRatingB;
        @SerializedName("SecondRatingC")
        @Expose
        private String secondRatingC;
        @SerializedName("ThirdRatingA")
        @Expose
        private String thirdRatingA;
        @SerializedName("ThirdRatingB")
        @Expose
        private String thirdRatingB;
        @SerializedName("ThirdRatingC")
        @Expose
        private String thirdRatingC;
        @SerializedName("FourthRatingA")
        @Expose
        private String fourthRatingA;
        @SerializedName("FourthRatingB")
        @Expose
        private String fourthRatingB;
        @SerializedName("FourthRatingC")
        @Expose
        private String fourthRatingC;
        @SerializedName("SelfResistanceA")
        @Expose
        private String selfResistanceA;
        @SerializedName("SelfResistanceB")
        @Expose
        private String selfResistanceB;
        @SerializedName("SelfResistanceC")
        @Expose
        private String selfResistanceC;
        @SerializedName("SelfReactanceA")
        @Expose
        private String selfReactanceA;
        @SerializedName("SelfReactanceB")
        @Expose
        private String selfReactanceB;
        @SerializedName("SelfReactanceC")
        @Expose
        private String selfReactanceC;
        @SerializedName("ShuntSusceptanceA")
        @Expose
        private String shuntSusceptanceA;
        @SerializedName("ShuntSusceptanceB")
        @Expose
        private String shuntSusceptanceB;
        @SerializedName("ShuntSusceptanceC")
        @Expose
        private String shuntSusceptanceC;
        @SerializedName("LockImpedance")
        @Expose
        private Integer lockImpedance;
        @SerializedName("Temperature")
        @Expose
        private String temperature;
        @SerializedName("Frequency")
        @Expose
        private String frequency;
        @SerializedName("Transposed")
        @Expose
        private Integer transposed;
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
        @SerializedName("ShuntConductanceA")
        @Expose
        private String shuntConductanceA;
        @SerializedName("ShuntConductanceB")
        @Expose
        private String shuntConductanceB;
        @SerializedName("ShuntConductanceC")
        @Expose
        private String shuntConductanceC;
        @SerializedName("MutualResistanceAB")
        @Expose
        private String mutualResistanceAB;
        @SerializedName("MutualResistanceBC")
        @Expose
        private String mutualResistanceBC;
        @SerializedName("MutualResistanceCA")
        @Expose
        private String mutualResistanceCA;
        @SerializedName("MutualReactanceAB")
        @Expose
        private String mutualReactanceAB;
        @SerializedName("MutualReactanceBC")
        @Expose
        private String mutualReactanceBC;
        @SerializedName("MutualReactanceCA")
        @Expose
        private String mutualReactanceCA;
        @SerializedName("MutualShuntSusceptanceAB")
        @Expose
        private String mutualShuntSusceptanceAB;
        @SerializedName("MutualShuntSusceptanceBC")
        @Expose
        private String mutualShuntSusceptanceBC;
        @SerializedName("MutualShuntSusceptanceCA")
        @Expose
        private String mutualShuntSusceptanceCA;
        @SerializedName("MutualShuntConductanceAB")
        @Expose
        private String mutualShuntConductanceAB;
        @SerializedName("MutualShuntConductanceBC")
        @Expose
        private String mutualShuntConductanceBC;
        @SerializedName("MutualShuntConductanceCA")
        @Expose
        private String mutualShuntConductanceCA;
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
        @SerializedName("Status")
        @Expose
        private Integer status;

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

        public String getPhaseConductorIdA() {
            return phaseConductorIdA;
        }

        public void setPhaseConductorIdA(String phaseConductorIdA) {
            this.phaseConductorIdA = phaseConductorIdA;
        }

        public String getPhaseConductorIdB() {
            return phaseConductorIdB;
        }

        public void setPhaseConductorIdB(String phaseConductorIdB) {
            this.phaseConductorIdB = phaseConductorIdB;
        }

        public String getPhaseConductorIdC() {
            return phaseConductorIdC;
        }

        public void setPhaseConductorIdC(String phaseConductorIdC) {
            this.phaseConductorIdC = phaseConductorIdC;
        }

        public String getNeutralConductorId() {
            return neutralConductorId;
        }

        public void setNeutralConductorId(String neutralConductorId) {
            this.neutralConductorId = neutralConductorId;
        }

        public String getNeutralConductorId2() {
            return neutralConductorId2;
        }

        public void setNeutralConductorId2(String neutralConductorId2) {
            this.neutralConductorId2 = neutralConductorId2;
        }

        public String getConductorSpacingId() {
            return conductorSpacingId;
        }

        public void setConductorSpacingId(String conductorSpacingId) {
            this.conductorSpacingId = conductorSpacingId;
        }

        public Integer getUserDefinedImpedances() {
            return userDefinedImpedances;
        }

        public void setUserDefinedImpedances(Integer userDefinedImpedances) {
            this.userDefinedImpedances = userDefinedImpedances;
        }

        public String getNominalRatingA() {
            return nominalRatingA;
        }

        public void setNominalRatingA(String nominalRatingA) {
            this.nominalRatingA = nominalRatingA;
        }

        public String getNominalRatingB() {
            return nominalRatingB;
        }

        public void setNominalRatingB(String nominalRatingB) {
            this.nominalRatingB = nominalRatingB;
        }

        public String getNominalRatingC() {
            return nominalRatingC;
        }

        public void setNominalRatingC(String nominalRatingC) {
            this.nominalRatingC = nominalRatingC;
        }

        public String getFirstRatingA() {
            return firstRatingA;
        }

        public void setFirstRatingA(String firstRatingA) {
            this.firstRatingA = firstRatingA;
        }

        public String getFirstRatingB() {
            return firstRatingB;
        }

        public void setFirstRatingB(String firstRatingB) {
            this.firstRatingB = firstRatingB;
        }

        public String getFirstRatingC() {
            return firstRatingC;
        }

        public void setFirstRatingC(String firstRatingC) {
            this.firstRatingC = firstRatingC;
        }

        public String getSecondRatingA() {
            return secondRatingA;
        }

        public void setSecondRatingA(String secondRatingA) {
            this.secondRatingA = secondRatingA;
        }

        public String getSecondRatingB() {
            return secondRatingB;
        }

        public void setSecondRatingB(String secondRatingB) {
            this.secondRatingB = secondRatingB;
        }

        public String getSecondRatingC() {
            return secondRatingC;
        }

        public void setSecondRatingC(String secondRatingC) {
            this.secondRatingC = secondRatingC;
        }

        public String getThirdRatingA() {
            return thirdRatingA;
        }

        public void setThirdRatingA(String thirdRatingA) {
            this.thirdRatingA = thirdRatingA;
        }

        public String getThirdRatingB() {
            return thirdRatingB;
        }

        public void setThirdRatingB(String thirdRatingB) {
            this.thirdRatingB = thirdRatingB;
        }

        public String getThirdRatingC() {
            return thirdRatingC;
        }

        public void setThirdRatingC(String thirdRatingC) {
            this.thirdRatingC = thirdRatingC;
        }

        public String getFourthRatingA() {
            return fourthRatingA;
        }

        public void setFourthRatingA(String fourthRatingA) {
            this.fourthRatingA = fourthRatingA;
        }

        public String getFourthRatingB() {
            return fourthRatingB;
        }

        public void setFourthRatingB(String fourthRatingB) {
            this.fourthRatingB = fourthRatingB;
        }

        public String getFourthRatingC() {
            return fourthRatingC;
        }

        public void setFourthRatingC(String fourthRatingC) {
            this.fourthRatingC = fourthRatingC;
        }

        public String getSelfResistanceA() {
            return selfResistanceA;
        }

        public void setSelfResistanceA(String selfResistanceA) {
            this.selfResistanceA = selfResistanceA;
        }

        public String getSelfResistanceB() {
            return selfResistanceB;
        }

        public void setSelfResistanceB(String selfResistanceB) {
            this.selfResistanceB = selfResistanceB;
        }

        public String getSelfResistanceC() {
            return selfResistanceC;
        }

        public void setSelfResistanceC(String selfResistanceC) {
            this.selfResistanceC = selfResistanceC;
        }

        public String getSelfReactanceA() {
            return selfReactanceA;
        }

        public void setSelfReactanceA(String selfReactanceA) {
            this.selfReactanceA = selfReactanceA;
        }

        public String getSelfReactanceB() {
            return selfReactanceB;
        }

        public void setSelfReactanceB(String selfReactanceB) {
            this.selfReactanceB = selfReactanceB;
        }

        public String getSelfReactanceC() {
            return selfReactanceC;
        }

        public void setSelfReactanceC(String selfReactanceC) {
            this.selfReactanceC = selfReactanceC;
        }

        public String getShuntSusceptanceA() {
            return shuntSusceptanceA;
        }

        public void setShuntSusceptanceA(String shuntSusceptanceA) {
            this.shuntSusceptanceA = shuntSusceptanceA;
        }

        public String getShuntSusceptanceB() {
            return shuntSusceptanceB;
        }

        public void setShuntSusceptanceB(String shuntSusceptanceB) {
            this.shuntSusceptanceB = shuntSusceptanceB;
        }

        public String getShuntSusceptanceC() {
            return shuntSusceptanceC;
        }

        public void setShuntSusceptanceC(String shuntSusceptanceC) {
            this.shuntSusceptanceC = shuntSusceptanceC;
        }

        public Integer getLockImpedance() {
            return lockImpedance;
        }

        public void setLockImpedance(Integer lockImpedance) {
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

        public Integer getTransposed() {
            return transposed;
        }

        public void setTransposed(Integer transposed) {
            this.transposed = transposed;
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

        public String getShuntConductanceA() {
            return shuntConductanceA;
        }

        public void setShuntConductanceA(String shuntConductanceA) {
            this.shuntConductanceA = shuntConductanceA;
        }

        public String getShuntConductanceB() {
            return shuntConductanceB;
        }

        public void setShuntConductanceB(String shuntConductanceB) {
            this.shuntConductanceB = shuntConductanceB;
        }

        public String getShuntConductanceC() {
            return shuntConductanceC;
        }

        public void setShuntConductanceC(String shuntConductanceC) {
            this.shuntConductanceC = shuntConductanceC;
        }

        public String getMutualResistanceAB() {
            return mutualResistanceAB;
        }

        public void setMutualResistanceAB(String mutualResistanceAB) {
            this.mutualResistanceAB = mutualResistanceAB;
        }

        public String getMutualResistanceBC() {
            return mutualResistanceBC;
        }

        public void setMutualResistanceBC(String mutualResistanceBC) {
            this.mutualResistanceBC = mutualResistanceBC;
        }

        public String getMutualResistanceCA() {
            return mutualResistanceCA;
        }

        public void setMutualResistanceCA(String mutualResistanceCA) {
            this.mutualResistanceCA = mutualResistanceCA;
        }

        public String getMutualReactanceAB() {
            return mutualReactanceAB;
        }

        public void setMutualReactanceAB(String mutualReactanceAB) {
            this.mutualReactanceAB = mutualReactanceAB;
        }

        public String getMutualReactanceBC() {
            return mutualReactanceBC;
        }

        public void setMutualReactanceBC(String mutualReactanceBC) {
            this.mutualReactanceBC = mutualReactanceBC;
        }

        public String getMutualReactanceCA() {
            return mutualReactanceCA;
        }

        public void setMutualReactanceCA(String mutualReactanceCA) {
            this.mutualReactanceCA = mutualReactanceCA;
        }

        public String getMutualShuntSusceptanceAB() {
            return mutualShuntSusceptanceAB;
        }

        public void setMutualShuntSusceptanceAB(String mutualShuntSusceptanceAB) {
            this.mutualShuntSusceptanceAB = mutualShuntSusceptanceAB;
        }

        public String getMutualShuntSusceptanceBC() {
            return mutualShuntSusceptanceBC;
        }

        public void setMutualShuntSusceptanceBC(String mutualShuntSusceptanceBC) {
            this.mutualShuntSusceptanceBC = mutualShuntSusceptanceBC;
        }

        public String getMutualShuntSusceptanceCA() {
            return mutualShuntSusceptanceCA;
        }

        public void setMutualShuntSusceptanceCA(String mutualShuntSusceptanceCA) {
            this.mutualShuntSusceptanceCA = mutualShuntSusceptanceCA;
        }

        public String getMutualShuntConductanceAB() {
            return mutualShuntConductanceAB;
        }

        public void setMutualShuntConductanceAB(String mutualShuntConductanceAB) {
            this.mutualShuntConductanceAB = mutualShuntConductanceAB;
        }

        public String getMutualShuntConductanceBC() {
            return mutualShuntConductanceBC;
        }

        public void setMutualShuntConductanceBC(String mutualShuntConductanceBC) {
            this.mutualShuntConductanceBC = mutualShuntConductanceBC;
        }

        public String getMutualShuntConductanceCA() {
            return mutualShuntConductanceCA;
        }

        public void setMutualShuntConductanceCA(String mutualShuntConductanceCA) {
            this.mutualShuntConductanceCA = mutualShuntConductanceCA;
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

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}