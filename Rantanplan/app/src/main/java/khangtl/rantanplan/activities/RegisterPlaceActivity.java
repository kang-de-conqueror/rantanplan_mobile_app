package khangtl.rantanplan.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import khangtl.rantanplan.R;
import khangtl.rantanplan.daos.PlaceDAO;
import khangtl.rantanplan.dtos.IdentifiedPlaceDTO;
import khangtl.rantanplan.dtos.PlaceIDResponseDTO;
import khangtl.rantanplan.dtos.UnidentifiedPlaceDTO;
import khangtl.rantanplan.utils.APIUtils;
import khangtl.rantanplan.wifi.WifiScanReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPlaceActivity extends AppCompatActivity {

    private TextView txtPlaceName;
    private TextView txtPlaceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_place);

        ConstraintLayout currentLayout = findViewById(R.id.registerPlace);
        currentLayout.setBackgroundColor(Color.RED);

        txtPlaceName = findViewById(R.id.txtPlaceName);
        txtPlaceAddress = findViewById(R.id.txtPlaceAddress);

        createSubmitButton();
        createCancelButton();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void createSubmitButton() {
        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placeName = txtPlaceName.getText().toString().trim();
                String placeAddress = txtPlaceAddress.getText().toString().trim();
                if (placeName.isEmpty()) {
                    txtPlaceName.setError("Please input this field");
                }
                if (placeAddress.isEmpty()) {
                    txtPlaceAddress.setError("Please input this field");
                }
                if (!placeName.isEmpty() && !placeAddress.isEmpty()) {
                    if (!isNetworkConnected()) {
                        return;
                    }
                    UnidentifiedPlaceDTO unidentifiedPlaceDTO = WifiScanReceiver.dto;
                    IdentifiedPlaceDTO dto = new IdentifiedPlaceDTO(placeName, placeAddress, unidentifiedPlaceDTO.getStartTime(), unidentifiedPlaceDTO.getEndTime(), unidentifiedPlaceDTO.getRoundCount(), unidentifiedPlaceDTO.getSignals());
                    try {
                        APIUtils.getGeneralWifiAPI().postIdentifiedPlace(dto).enqueue(new Callback<PlaceIDResponseDTO>() {
                            @Override
                            public void onResponse(Call<PlaceIDResponseDTO> call, Response<PlaceIDResponseDTO> response) {
                                PlaceIDResponseDTO dto = response.body();

                                Toast.makeText(getApplicationContext(), "Send successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterPlaceActivity.this, MainActivity.class);
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
            }
        });
    }

    private void createCancelButton() {
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPlaceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}