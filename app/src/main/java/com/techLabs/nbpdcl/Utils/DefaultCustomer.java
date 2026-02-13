package com.techLabs.nbpdcl.Utils;

import com.google.gson.JsonObject;

public class DefaultCustomer {

    private String phase;
    private String phaseType;

    public DefaultCustomer(String phase, String phaseType) {
        this.phase = phase;
        this.phaseType = phaseType;
    }

    int randomNumber = (int) (Math.random() * 9000) + 1000;

    public void OneCustomer() {
        try {
            if (phaseType.equals("ThreePhase")) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("CustomerNumber", randomNumber);
                jsonObject.addProperty("CustomerType", "COMMERCIAL");
                jsonObject.addProperty("Status", "0");

                JsonObject phaseObject = new JsonObject();
                phaseObject.addProperty("1", "0");
                phaseObject.addProperty("2", "0");
                phaseObject.addProperty("3", "0");
                phaseObject.addProperty("7", "7");
                jsonObject.add("Phase", phaseObject);

                JsonObject actualKvaObject = new JsonObject();
                actualKvaObject.addProperty("1", "0");
                actualKvaObject.addProperty("2", "0");
                actualKvaObject.addProperty("3", "0");
                actualKvaObject.addProperty("7", "0.0");
                jsonObject.add("ActualKW", actualKvaObject);

                JsonObject connectedKvaObject = new JsonObject();
                connectedKvaObject.addProperty("1", "0");
                connectedKvaObject.addProperty("2", "0");
                connectedKvaObject.addProperty("3", "0");
                connectedKvaObject.addProperty("7", "0.0");
                jsonObject.add("ConnectedKVA", connectedKvaObject);

                JsonObject customerCountObject = new JsonObject();
                customerCountObject.addProperty("1", "0");
                customerCountObject.addProperty("2", "0");
                customerCountObject.addProperty("13", "0");
                customerCountObject.addProperty("7", "0.0");
                jsonObject.add("CustomerCount", customerCountObject);
                ListDataManager.sendDefaultData(jsonObject);
            } else {
                if (phase.equals("1")) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("CustomerNumber", randomNumber);
                    jsonObject.addProperty("CustomerType", "COMMERCIAL");
                    jsonObject.addProperty("Status", "0");

                    JsonObject phaseObject = new JsonObject();
                    phaseObject.addProperty("1", "1");
                    phaseObject.addProperty("2", "0");
                    phaseObject.addProperty("3", "0");
                    phaseObject.addProperty("7", "0");
                    jsonObject.add("Phase", phaseObject);

                    JsonObject actualKvaObject = new JsonObject();
                    actualKvaObject.addProperty("1", "0.0");
                    actualKvaObject.addProperty("2", "0");
                    actualKvaObject.addProperty("3", "0");
                    actualKvaObject.addProperty("7", "0");
                    jsonObject.add("ActualKW", actualKvaObject);

                    JsonObject connectedKvaObject = new JsonObject();
                    connectedKvaObject.addProperty("1", "0.0");
                    connectedKvaObject.addProperty("2", "0");
                    connectedKvaObject.addProperty("3", "0");
                    connectedKvaObject.addProperty("7", "0");
                    jsonObject.add("ConnectedKVA", connectedKvaObject);

                    JsonObject customerCountObject = new JsonObject();
                    customerCountObject.addProperty("1", "0.0");
                    customerCountObject.addProperty("2", "0");
                    customerCountObject.addProperty("13", "0");
                    customerCountObject.addProperty("7", "0");
                    jsonObject.add("CustomerCount", customerCountObject);
                    ListDataManager.sendDefaultData(jsonObject);
                } else if (phase.equals("2")) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("CustomerNumber", randomNumber);
                    jsonObject.addProperty("CustomerType", "COMMERCIAL");
                    jsonObject.addProperty("Status", "0");

                    JsonObject phaseObject = new JsonObject();
                    phaseObject.addProperty("1", "0");
                    phaseObject.addProperty("2", "1");
                    phaseObject.addProperty("3", "0");
                    phaseObject.addProperty("7", "0");
                    jsonObject.add("Phase", phaseObject);

                    JsonObject actualKvaObject = new JsonObject();
                    actualKvaObject.addProperty("1", "0");
                    actualKvaObject.addProperty("2", "0.0");
                    actualKvaObject.addProperty("3", "0");
                    actualKvaObject.addProperty("7", "0");
                    jsonObject.add("ActualKW", actualKvaObject);

                    JsonObject connectedKvaObject = new JsonObject();
                    connectedKvaObject.addProperty("1", "0");
                    connectedKvaObject.addProperty("2", "0.0");
                    connectedKvaObject.addProperty("3", "0");
                    connectedKvaObject.addProperty("7", "0");
                    jsonObject.add("ConnectedKVA", connectedKvaObject);

                    JsonObject customerCountObject = new JsonObject();
                    customerCountObject.addProperty("1", "0");
                    customerCountObject.addProperty("2", "0.0");
                    customerCountObject.addProperty("13", "0");
                    customerCountObject.addProperty("7", "0");
                    jsonObject.add("CustomerCount", customerCountObject);
                    ListDataManager.sendDefaultData(jsonObject);
                } else if (phase.equals("3")) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("CustomerNumber", randomNumber);
                    jsonObject.addProperty("CustomerType", "COMMERCIAL");
                    jsonObject.addProperty("Status", "0");

                    JsonObject phaseObject = new JsonObject();
                    phaseObject.addProperty("1", "0");
                    phaseObject.addProperty("2", "0");
                    phaseObject.addProperty("3", "1");
                    phaseObject.addProperty("7", "0");
                    jsonObject.add("Phase", phaseObject);

                    JsonObject actualKvaObject = new JsonObject();
                    actualKvaObject.addProperty("1", "0");
                    actualKvaObject.addProperty("2", "0");
                    actualKvaObject.addProperty("3", "0.0");
                    actualKvaObject.addProperty("7", "0");
                    jsonObject.add("ActualKW", actualKvaObject);

                    JsonObject connectedKvaObject = new JsonObject();
                    connectedKvaObject.addProperty("1", "0");
                    connectedKvaObject.addProperty("2", "0");
                    connectedKvaObject.addProperty("3", "0.0");
                    connectedKvaObject.addProperty("7", "0");
                    jsonObject.add("ConnectedKVA", connectedKvaObject);

                    JsonObject customerCountObject = new JsonObject();
                    customerCountObject.addProperty("1", "0");
                    customerCountObject.addProperty("2", "0");
                    customerCountObject.addProperty("13", "0");
                    customerCountObject.addProperty("7", "0.0");
                    jsonObject.add("CustomerCount", customerCountObject);
                    ListDataManager.sendDefaultData(jsonObject);
                } else if (phase.equals("4")) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("CustomerNumber", randomNumber);
                    jsonObject.addProperty("CustomerType", "COMMERCIAL");
                    jsonObject.addProperty("Status", "0");

                    JsonObject phaseObject = new JsonObject();
                    phaseObject.addProperty("1", "1");
                    phaseObject.addProperty("2", "1");
                    phaseObject.addProperty("3", "0");
                    phaseObject.addProperty("7", "0");
                    jsonObject.add("Phase", phaseObject);

                    JsonObject actualKvaObject = new JsonObject();
                    actualKvaObject.addProperty("1", "0.0");
                    actualKvaObject.addProperty("2", "0.0");
                    actualKvaObject.addProperty("3", "0");
                    actualKvaObject.addProperty("7", "0");
                    jsonObject.add("ActualKW", actualKvaObject);

                    JsonObject connectedKvaObject = new JsonObject();
                    connectedKvaObject.addProperty("1", "0.0");
                    connectedKvaObject.addProperty("2", "0.0");
                    connectedKvaObject.addProperty("3", "0");
                    connectedKvaObject.addProperty("7", "0");
                    jsonObject.add("ConnectedKVA", connectedKvaObject);

                    JsonObject customerCountObject = new JsonObject();
                    customerCountObject.addProperty("1", "0.0");
                    customerCountObject.addProperty("2", "0.0");
                    customerCountObject.addProperty("13", "0");
                    customerCountObject.addProperty("7", "0");
                    jsonObject.add("CustomerCount", customerCountObject);
                    ListDataManager.sendDefaultData(jsonObject);
                } else if (phase.equals("5")) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("CustomerNumber", randomNumber);
                    jsonObject.addProperty("CustomerType", "COMMERCIAL");
                    jsonObject.addProperty("Status", "0");

                    JsonObject phaseObject = new JsonObject();
                    phaseObject.addProperty("1", "1");
                    phaseObject.addProperty("2", "0");
                    phaseObject.addProperty("3", "1");
                    phaseObject.addProperty("7", "0");
                    jsonObject.add("Phase", phaseObject);

                    JsonObject actualKvaObject = new JsonObject();
                    actualKvaObject.addProperty("1", "0.0");
                    actualKvaObject.addProperty("2", "0");
                    actualKvaObject.addProperty("3", "0.0");
                    actualKvaObject.addProperty("7", "0");
                    jsonObject.add("ActualKW", actualKvaObject);

                    JsonObject connectedKvaObject = new JsonObject();
                    connectedKvaObject.addProperty("1", "0.0");
                    connectedKvaObject.addProperty("2", "0");
                    connectedKvaObject.addProperty("3", "0.0");
                    connectedKvaObject.addProperty("7", "0");
                    jsonObject.add("ConnectedKVA", connectedKvaObject);

                    JsonObject customerCountObject = new JsonObject();
                    customerCountObject.addProperty("1", "0.0");
                    customerCountObject.addProperty("2", "0");
                    customerCountObject.addProperty("13", "0.0");
                    customerCountObject.addProperty("7", "0");
                    jsonObject.add("CustomerCount", customerCountObject);
                    ListDataManager.sendDefaultData(jsonObject);
                } else if (phase.equals("6")) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("CustomerNumber", randomNumber);
                    jsonObject.addProperty("CustomerType", "COMMERCIAL");
                    jsonObject.addProperty("Status", "0");

                    JsonObject phaseObject = new JsonObject();
                    phaseObject.addProperty("1", "0");
                    phaseObject.addProperty("2", "1");
                    phaseObject.addProperty("3", "1");
                    phaseObject.addProperty("7", "0");
                    jsonObject.add("Phase", phaseObject);

                    JsonObject actualKvaObject = new JsonObject();
                    actualKvaObject.addProperty("1", "0");
                    actualKvaObject.addProperty("2", "0.0");
                    actualKvaObject.addProperty("3", "0.0");
                    actualKvaObject.addProperty("7", "0");
                    jsonObject.add("ActualKW", actualKvaObject);

                    JsonObject connectedKvaObject = new JsonObject();
                    connectedKvaObject.addProperty("1", "0");
                    connectedKvaObject.addProperty("2", "0.0");
                    connectedKvaObject.addProperty("3", "0.0");
                    connectedKvaObject.addProperty("7", "0");
                    jsonObject.add("ConnectedKVA", connectedKvaObject);

                    JsonObject customerCountObject = new JsonObject();
                    customerCountObject.addProperty("1", "0");
                    customerCountObject.addProperty("2", "0.0");
                    customerCountObject.addProperty("13", "0.0");
                    customerCountObject.addProperty("7", "0");
                    jsonObject.add("CustomerCount", customerCountObject);
                    ListDataManager.sendDefaultData(jsonObject);
                } else {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("CustomerNumber", randomNumber);
                    jsonObject.addProperty("CustomerType", "COMMERCIAL");

                    jsonObject.addProperty("Status", "0");

                    JsonObject phaseObject = new JsonObject();
                    phaseObject.addProperty("1", "1");
                    phaseObject.addProperty("2", "1");
                    phaseObject.addProperty("3", "1");
                    phaseObject.addProperty("7", "0");
                    jsonObject.add("Phase", phaseObject);

                    JsonObject actualKvaObject = new JsonObject();
                    actualKvaObject.addProperty("1", "0.0");
                    actualKvaObject.addProperty("2", "0.0");
                    actualKvaObject.addProperty("3", "0.0");
                    actualKvaObject.addProperty("7", "0");
                    jsonObject.add("ActualKW", actualKvaObject);

                    JsonObject connectedKvaObject = new JsonObject();
                    connectedKvaObject.addProperty("1", "0.0");
                    connectedKvaObject.addProperty("2", "0.0");
                    connectedKvaObject.addProperty("3", "0.0");
                    connectedKvaObject.addProperty("7", "0");
                    jsonObject.add("ConnectedKVA", connectedKvaObject);

                    JsonObject customerCountObject = new JsonObject();
                    customerCountObject.addProperty("1", "0.0");
                    customerCountObject.addProperty("2", "0.0");
                    customerCountObject.addProperty("13", "0.0");
                    customerCountObject.addProperty("7", "0");
                    jsonObject.add("CustomerCount", customerCountObject);
                    ListDataManager.sendDefaultData(jsonObject);
                }
            }
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
    }

}
