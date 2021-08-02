package khangtl.rantanplan.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import khangtl.rantanplan.R;
import khangtl.rantanplan.dtos.IdentifiedPlaceDTO;
import khangtl.rantanplan.dtos.PlaceIDResponseDTO;
import khangtl.rantanplan.dtos.UnidentifiedPlaceDTO;
import khangtl.rantanplan.utils.APIUtils;
import khangtl.rantanplan.wifi.WifiScanReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowPlaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        ConstraintLayout currentLayout = findViewById(R.id.showPlace);
        currentLayout.setBackgroundColor(Color.RED);

        TextView txtPlaceName = findViewById(R.id.txtPlaceName);
        TextView txtPlaceAddress = findViewById(R.id.txtPlaceAddress);

        txtPlaceName.setText(WifiScanReceiver.placeName);
        txtPlaceAddress.setText(WifiScanReceiver.placeAddress);

        createAcceptButton();
        createRejectButton();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void createAcceptButton() {
        Button btnAccept = findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnidentifiedPlaceDTO unidentifiedPlaceDTO = WifiScanReceiver.dto;
                IdentifiedPlaceDTO dto = new IdentifiedPlaceDTO(WifiScanReceiver.placeName, WifiScanReceiver.placeAddress, unidentifiedPlaceDTO.getStartTime(), unidentifiedPlaceDTO.getEndTime(), unidentifiedPlaceDTO.getRoundCount(), unidentifiedPlaceDTO.getSignals());
                if (!isNetworkConnected()) {

                    return;
                }
                try {
                    APIUtils.getGeneralWifiAPI().postExistedPlace(dto, WifiScanReceiver.placeId).enqueue(new Callback<PlaceIDResponseDTO>() {
                        @Override
                        public void onResponse(Call<PlaceIDResponseDTO> call, Response<PlaceIDResponseDTO> response) {
                            PlaceIDResponseDTO dto = response.body();

                            Toast.makeText(getApplicationContext(), "Send successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ShowPlaceActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<PlaceIDResponseDTO> call, Throwable t) {
                            System.out.println(t.getMessage());
                            Toast.makeText(getApplicationContext(), "Send fail", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createRejectButton() {
        Button btnReject = findViewById(R.id.btnReject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowPlaceActivity.this, RegisterPlaceActivity.class);
                startActivity(intent);
            }
        });
    }
}