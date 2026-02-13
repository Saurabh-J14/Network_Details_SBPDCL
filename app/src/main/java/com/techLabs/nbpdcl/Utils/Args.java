package com.techLabs.nbpdcl.Utils;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class Args {

    public static MutableLiveData<Boolean> isSectionValidate = new MutableLiveData<>();
    public static MutableLiveData<Bundle> sectionParameter = new MutableLiveData<>();

    /* Device Parameter*/
    public static MutableLiveData<Boolean> isBreakerValidate = new MutableLiveData<>();
    public static MutableLiveData<Bundle> breakerParameter = new MutableLiveData<>();

    public static MutableLiveData<Boolean> isSwitchValidate = new MutableLiveData<>();
    public static MutableLiveData<Bundle> switchParameter = new MutableLiveData<>();

    public static MutableLiveData<Boolean> isTransformerValidate = new MutableLiveData<>();
    public static MutableLiveData<Bundle> transformerParameter = new MutableLiveData<>();

    public static MutableLiveData<Boolean> isCapacitorValidate = new MutableLiveData<>();
    public static MutableLiveData<Bundle> capacitorParameter = new MutableLiveData<>();

    public static MutableLiveData<Boolean> isFuseValidate = new MutableLiveData<>();
    public static MutableLiveData<Bundle> fuseParameter = new MutableLiveData<>();

    public static MutableLiveData<Boolean> isSpotloadValidate = new MutableLiveData<>();
    public static MutableLiveData<Bundle> spotloadParameter = new MutableLiveData<>();

    public static MutableLiveData<String> isPhaseValidate = new MutableLiveData<>();

    public static LiveData<Boolean> getIsSectionValidate() {
        return isSectionValidate;
    }

    public static void setSectionValidate(boolean value) {
        isSectionValidate.setValue(value);
    }

    public static LiveData<Bundle> getSectionParameter() {
        return sectionParameter;
    }

    public static void setSectionParameter(Bundle bundle) {
        sectionParameter.setValue(bundle);
    }

    public static LiveData<Boolean> getIsBreakerValidate() {
        return isBreakerValidate;
    }

    public static void setISBreakerValidate(boolean value) {
        isBreakerValidate.setValue(value);
    }

    public static LiveData<Bundle> getBreakerParameter() {
        return breakerParameter;
    }

    public static void setBreakerParameter(Bundle bundle) {
        breakerParameter.setValue(bundle);
    }

    public static LiveData<Boolean> getIsSwitchValidate() {
        return isSwitchValidate;
    }

    public static void setIsSwitchValidate(boolean value) {
        isSwitchValidate.setValue(value);
    }

    public static LiveData<Bundle> getSwitchParameter() {
        return switchParameter;
    }

    public static void setSwitchParameter(Bundle bundle) {
        switchParameter.setValue(bundle);
    }

    public static LiveData<Boolean> getIsTransformerValidate() {
        return isTransformerValidate;
    }

    public static void setIsTransformerValidate(boolean value) {
        isTransformerValidate.setValue(value);
    }

    public static LiveData<Bundle> getTransformerParameter() {
        return transformerParameter;
    }

    public static void setTransformerParameter(Bundle bundle) {
        transformerParameter.setValue(bundle);
    }

    public static LiveData<Boolean> getIsCapacitorValidate() {
        return isCapacitorValidate;
    }

    public static void setIsCapacitorValidate(boolean value) {
        isCapacitorValidate.setValue(value);
    }

    public static LiveData<Bundle> getCapacitorParameter() {
        return capacitorParameter;
    }

    public static void setCapacitorParameter(Bundle bundle) {
        capacitorParameter.setValue(bundle);
    }

    public static LiveData<Boolean> getIsSpotloadValidate() {
        return isSpotloadValidate;
    }

    public static void setIsSpotloadValidate(boolean value) {
        isSpotloadValidate.setValue(value);
    }

    public static LiveData<Bundle> getSpotloadParameter() {
        return spotloadParameter;
    }

    public static void setSpotloadParameter(Bundle bundle) {
        spotloadParameter.setValue(bundle);
    }

    public static LiveData<Boolean> getIsFuseValidate() {
        return isFuseValidate;
    }

    public static void setIsFuseValidate(boolean value) {
        isFuseValidate.setValue(value);
    }

    public static LiveData<Bundle> getFuseParameter() {
        return fuseParameter;
    }

    public static void setFuseParameter(Bundle bundle) {
        fuseParameter.setValue(bundle);
    }

    public static MutableLiveData<String> networkId = new MutableLiveData<>();

    public static LiveData<String> getNetworkID() {
        return networkId;
    }

    public static void setNetworkID(String value) {
        networkId.postValue(value);
    }

    public static LiveData<String> getIsPhaseValidate() {
        return isPhaseValidate;
    }

    public static void setPhaseValidate(String value) {
        isPhaseValidate.setValue(value);
    }

}
