package com.techLabs.nbpdcl.Utils.callBack;

public interface SectionCallBack {
    void OnCableDataReceived(String sectionID, String phase, String fromNodeId, String fromX, String fromY, String toNodeID, String toNodeX, String toNodeY, String DeviceTypeLine, String DeviceLineNumber);
}
