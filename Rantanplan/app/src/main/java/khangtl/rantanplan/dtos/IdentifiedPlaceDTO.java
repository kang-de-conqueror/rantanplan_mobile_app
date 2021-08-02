package khangtl.rantanplan.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class IdentifiedPlaceDTO extends UnidentifiedPlaceDTO implements Serializable {
    @SerializedName("place_id")
    private String id;
    @SerializedName("place_name")
    private String name;
    @SerializedName("place_address")
    private String address;

    public IdentifiedPlaceDTO() {
    }

    public IdentifiedPlaceDTO(String name, String address, String startTime, String endTime, int roundCount, List<SignalDTO> list) {
        super(startTime, endTime, roundCount, list);
        this.name = name;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
