package khangtl.rantanplan.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MinimizeSignalDTO implements Serializable {
    @SerializedName("bssid")
    private String bssid;
    @SerializedName("signal_level")
    private int signalLevel;

    public MinimizeSignalDTO(String bssid, int signalLevel) {
        this.bssid = bssid;
        this.signalLevel = signalLevel;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public int getSignalLevel() {
        return signalLevel;
    }

    public void setSignalLevel(int signalLevel) {
        this.signalLevel = signalLevel;
    }
}
