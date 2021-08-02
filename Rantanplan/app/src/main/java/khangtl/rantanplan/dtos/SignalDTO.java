package khangtl.rantanplan.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SignalDTO implements Serializable {
    @SerializedName("bssid")
    private String bssid;
    @SerializedName("ssid")
    private String ssid;
    @SerializedName("frequency")
    private int frequency;
    @SerializedName("signal_level")
    private int signalLevel;
    @SerializedName("sample_count")
    private int sampleCount;
    private transient List<Integer> signalLevelList;

    public SignalDTO() {
    }

    public SignalDTO(String bssid, String ssid, int frequency, int signalLevel, int sampleCount) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.frequency = frequency;
        this.signalLevel = signalLevel;
        this.sampleCount = sampleCount;
    }

    public SignalDTO(String bssid, String ssid, int frequency, int signalLevel, int sampleCount, ArrayList<Integer> signalLevelList) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.frequency = frequency;
        this.signalLevel = signalLevel;
        this.sampleCount = sampleCount;
        this.signalLevelList = signalLevelList;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getSignalLevel() {
        return signalLevel;
    }

    public void setSignalLevel(int signalLevel) {
        this.signalLevel = signalLevel;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public List<Integer> getSignalLevelList() {
        return signalLevelList;
    }

    public void setSignalLevelList(List<Integer> signalLevelList) {
        this.signalLevelList = signalLevelList;
    }
}
