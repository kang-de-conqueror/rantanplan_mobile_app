package khangtl.rantanplan.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MatchPlaceDTO implements Serializable {
    @SerializedName("place_id")
    private String id;
    @SerializedName("place_name")
    private String name;
    @SerializedName("place_address")
    private String address;
    @SerializedName("score")
    private double score;

    public MatchPlaceDTO(String id, String name, String address, double score) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.score = score;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
