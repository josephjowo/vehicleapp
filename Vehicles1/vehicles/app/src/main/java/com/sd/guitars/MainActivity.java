package com.sd.guitars;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sd.guitars.data.Vehicle;
import com.sd.guitars.data.VehicleData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // The database 'connection'
    // All interaction with the database goes through this object
    VehicleData vehicleData;

    // The list of unique ids from the Guitars table
    List ids = new ArrayList<Long>();

    // The index of the current Vehicle's id in the ids list
    Integer currentvehicleIndex = -1;

    // The EditTexts from the activity
    EditText edVehicleMake;
    EditText edVehicleModel;
    EditText edVehicleNoOfSeats;
    EditText edVehicleModelNumber;

    // The Buttons from the activity
    Button btnFirstVehicle;
    Button btnPrevVehicle;
    Button btnNextVehicle;
    Button btnLastVehicle;
    Button btnAddVehicle;
    Button btnDelVehicle;

    @Override
    // The Android system makes this method run when the activity is first created
    protected void onCreate(Bundle savedInstanceState) {
        // the usual set up stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create the database (if necessary) and connect to it
        vehicleData = new VehicleData(this.getApplicationContext());

        // get the list of ids from the Vehicle table
        // This list will be empty if there's no data there
        ids = vehicleData.getVehicleIDs();

        // if there are any guitars recorded then 'point' to the first one
        if (ids.size() > 0) {
            // 0 is the index of the first guitar
            currentvehicleIndex = 0;
        }

        // Load the EditTexts into memory
        edVehicleMake = findViewById(R.id.edMake);
        edVehicleModel = findViewById(R.id.edModel);
        edVehicleNoOfSeats = findViewById(R.id.edNumberOfStrings);
        edVehicleModelNumber = findViewById(R.id.edSerialNumber);

        // Load buttons and setup their event handlers
        loadButtonsAndSetHandlers();
    }

    private void loadButtonsAndSetHandlers() {
        // Load the buttons into memory
        btnFirstVehicle = findViewById(R.id.btnFirst);
        btnPrevVehicle = findViewById(R.id.btnPrev);
        btnNextVehicle = findViewById(R.id.btnNext);
        btnLastVehicle = findViewById(R.id.btnLast);
        btnAddVehicle = findViewById(R.id.btnAdd);
        btnDelVehicle = findViewById(R.id.btnDel);

        // set their event handlers (i.e. what they do when they're clicked
        btnFirstVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnFirst();
            }
        });

        btnPrevVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnPrev();
            }
        });

        btnNextVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnNext();
            }
        });

        btnLastVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnLast();
            }
        });

        btnAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnAddVehicle();
            }
        });

        btnDelVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnDelVehicle();
            }
        });
    }

    private void handleBtnFirst() {
        currentvehicleIndex = 0;
        showCurrentVehicle();
    }

    private void handleBtnPrev() {
        currentvehicleIndex = currentvehicleIndex - 1;
        showCurrentVehicle();
    }

    private void handleBtnNext() {
        currentvehicleIndex = currentvehicleIndex + 1;
        showCurrentVehicle();
    }

    private void handleBtnLast() {
        currentvehicleIndex = ids.size() - 1;
        showCurrentVehicle();
    }

    private void handleBtnAddVehicle() {

        // Load data from the activity
        String VehicleMake = edVehicleMake.getText().toString();
        String VehicleModel = edVehicleModel.getText().toString();
        String VehicleNoOfSeats = edVehicleNoOfSeats.getText().toString();
        String VehicleModelNumber = edVehicleModelNumber.getText().toString();

        // add the guitar
        vehicleData.addVehicle(VehicleMake, VehicleModel, VehicleNoOfSeats, VehicleModelNumber);

        // get the new set of ids
        ids = vehicleData.getVehicleIDs();
        currentvehicleIndex = 0;
        showCurrentVehicle();

        // update buttons statuses
        refreshButtonStatus();

        // message to the user
        showLongToast("New vehicle added");
    }

    private void handleBtnDelVehicle() {
        // delete the current vehicle
        Long VehicleId = (Long) ids.get(currentvehicleIndex);
        vehicleData.deleteVehicle(VehicleId);

        // get the new ids
        ids = vehicleData.getVehicleIDs();

        // if we've deleted the last one then set the currentVehicleIndex to -1
        if (ids.size() == 0) {
            currentvehicleIndex = -1;
        } else {
            // set it to the first vehicle
            currentvehicleIndex = 0;
        }

        // update buttons statuses
        refreshButtonStatus();

        showCurrentVehicle();

        // message to the user
        showLongToast("Vehicle deleted");
    }

    @Override
    // The Android system makes this method run just before the activity is shown
    // It is part of the Activity Life Cycle
    protected void onResume() {
        // call the superclass's onResume() method, thus linking our Activity
        // properly with the system
        super.onResume();

        showCurrentVehicle();

        // enable/disable buttons according to the data
        refreshButtonStatus();
    }

    private void showCurrentVehicle() {
        // if there are any guitars in the table, show the current guitar
        if (currentvehicleIndex != -1) {
            // Get the current guitar's data
            Long currentVehicleId = (Long) ids.get(currentvehicleIndex);
            Vehicle theVehicleToDisplay = vehicleData.getVehicle(currentVehicleId);

            // Show the current guitar's data
            edVehicleMake.setText(theVehicleToDisplay.make);
            edVehicleModel.setText(theVehicleToDisplay.model);
            edVehicleNoOfSeats.setText(theVehicleToDisplay.noOfSeats);
            edVehicleModelNumber.setText(theVehicleToDisplay.serialNo);
        }
        else {
            // display no data
            edVehicleMake.setText("");
            edVehicleModel.setText("");
            edVehicleNoOfSeats.setText("");
            edVehicleModelNumber.setText("");
        }
        // refresh the enabled//disabled status of the buttons
        refreshButtonStatus();
    }

    private void refreshButtonStatus() {
        btnDelVehicle.setEnabled(true);
        // 0 or 1 vehicles in the database
        if (ids.size() == 0 || ids.size() == 1) {
            // disable all (except the Add button - we can always add a vehicle)
            btnFirstVehicle.setEnabled(false);
            btnPrevVehicle.setEnabled(false);
            btnNextVehicle.setEnabled(false);
            btnLastVehicle.setEnabled(false);

            if (ids.size() == 0) {
                // this is the only time we can't delete (because there isn't one)
                btnDelVehicle.setEnabled(false);
            }
        } else {
            // if we're at the first vehicle
            if (currentvehicleIndex == 0) {
                btnFirstVehicle.setEnabled(false);
                btnPrevVehicle.setEnabled(false);
                btnNextVehicle.setEnabled(true);
                btnLastVehicle.setEnabled(true);
            } else {
                // if we're at the last guitar
                if (currentvehicleIndex == ids.size() - 1) {
                    btnFirstVehicle.setEnabled(true);
                    btnPrevVehicle.setEnabled(true);
                    btnNextVehicle.setEnabled(false);
                    btnLastVehicle.setEnabled(false);
                } else {
                    // we're somewhere in the middle
                    btnFirstVehicle.setEnabled(true);
                    btnPrevVehicle.setEnabled(true);
                    btnNextVehicle.setEnabled(true);
                    btnLastVehicle.setEnabled(true);
                }
            }
        }
    }

    private void showLongToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void hideKeyboard(View view) {
        Context context = getApplicationContext();
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}