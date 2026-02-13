package com.techLabs.nbpdcl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EquipmentModel {

    @SerializedName("AllEquipmentId")
    @Expose
    private AllEquipmentId allEquipmentId;
    @SerializedName("EquipmentId")
    @Expose
    private EquipmentId equipmentId;

    public AllEquipmentId getAllEquipmentId() {
        return allEquipmentId;
    }

    public void setAllEquipmentId(AllEquipmentId allEquipmentId) {
        this.allEquipmentId = allEquipmentId;
    }

    public EquipmentId getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(EquipmentId equipmentId) {
        this.equipmentId = equipmentId;
    }

    public class AllEquipmentId {

        @SerializedName("EquipmentId")
        @Expose
        private List<String> equipmentId;

        public List<String> getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(List<String> equipmentId) {
            this.equipmentId = equipmentId;
        }

    }

    public class EquipmentId {

        @SerializedName("LineId")
        @Expose
        private List<String> lineId;

        public List<String> getLineId() {
            return lineId;
        }

        public void setLineId(List<String> lineId) {
            this.lineId = lineId;
        }

    }

}

/*public class EquipmentModel {

    @SerializedName("AllEquipmentId")
    @Expose
    private AllEquipmentId allEquipmentId;
    @SerializedName("EquipmentId")
    @Expose
    private EquipmentId equipmentId;

    public AllEquipmentId getAllEquipmentId() {
        return allEquipmentId;
    }

    public void setAllEquipmentId(AllEquipmentId allEquipmentId) {
        this.allEquipmentId = allEquipmentId;
    }

    public EquipmentId getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(EquipmentId equipmentId) {
        this.equipmentId = equipmentId;
    }

    public class AllEquipmentId {

        @SerializedName("EquipmentId")
        @Expose
        private List<String> equipmentId;

        public List<String> getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(List<String> equipmentId) {
            this.equipmentId = equipmentId;
        }

    }

    public class EquipmentId {

        @SerializedName("CableId")
        @Expose
        private List<String> cableId;

        public List<String> getCableId() {
            return cableId;
        }

        public void setCableId(List<String> cableId) {
            this.cableId = cableId;
        }

    }

}*/

