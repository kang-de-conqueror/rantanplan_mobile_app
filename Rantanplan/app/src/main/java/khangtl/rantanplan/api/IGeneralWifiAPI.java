package khangtl.rantanplan.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import khangtl.rantanplan.dtos.IdentifiedPlaceDTO;
import khangtl.rantanplan.dtos.MatchPlaceDTO;
import khangtl.rantanplan.dtos.MinimizeSignalDTO;
import khangtl.rantanplan.dtos.PlaceIDResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IGeneralWifiAPI {
    @POST("/place/signal")
    Call<PlaceIDResponseDTO> postIdentifiedPlace(@Body IdentifiedPlaceDTO dto);

    @POST("/place/{place_id}/signal")
    Call<PlaceIDResponseDTO> postExistedPlace(@Body IdentifiedPlaceDTO dto, @Path("place_id") String id);

    @POST("/place/search")
    Call<List<MatchPlaceDTO>> postSearchPlace(@Body List<MinimizeSignalDTO> list);
}
