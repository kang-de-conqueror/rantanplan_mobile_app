package khangtl.rantanplan.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UnidentifiedPlaceDTO implements Serializable {
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("round_count")
    private int roundCount;
    @SerializedName("signals")
    private List<SignalDTO> signals;

    public UnidentifiedPlaceDTO() {
    }

    public UnidentifiedPlaceDTO(String startTime, String endTime, int roundCount, List<SignalDTO> signals) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.roundCount = roundCount;
        this.signals = signals;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public void setRoundCount(int roundCount) {
        this.roundCount = roundCount;
    }

    public List<SignalDTO> getSignals() {
        return signals;
    }

    public void setSignals(List<SignalDTO> signals) {
        this.signals = signals;
    }
}
