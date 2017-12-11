package com.quickd.quickdestinations;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private static final int TIME_ID = 0;

    private EditText arrivalTime;
    String name;
    String location = "";
    LatLng latLng;
    //ROB ADDED MINUTE TIME
    int minuteTime = -1;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrivalTime = view.findViewById(R.id.etArrivalTime);

        final SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                name = place.getName().toString();
                location = place.getAddress().toString();
                latLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {

            }
        });

        arrivalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog(TIME_ID).show();
            }
        });

        //ROB EDITED THIS
        view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String time = arrivalTime.getText().toString();
                if (location.trim().length() > 0) {
                    ((MainActivity) getActivity()).setDestinations(new Pair(location, minuteTime));
                    if(minuteTime > -1)
                        ((MainActivity) getActivity()).setTimedLatLngs(name, latLng, minuteTime);
                    ((MainActivity) getActivity()).setLatLngs(new Pair(name, latLng));
                    Toast.makeText(getActivity(), "Successfully added destination", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "Please enter a destination", Toast.LENGTH_SHORT).show();
                autocompleteFragment.setText("");
                arrivalTime.setText("");
                minuteTime = -1;
                location = "";
            }
        });
    }

    public Dialog createDialog(int id) {
        // Get the calander
        Calendar c = Calendar.getInstance();

        // From calander get the hour, minute
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        if (id == TIME_ID) {
            // Open the timepicker dialog
            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(getActivity(), R.style.MySpinnerTimePickerStyle, time_listener, hour, minute, false);
            timePickerDialog.setButton(-3, "Clear", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    arrivalTime.setText("");
                    minuteTime = -1;
                }
            });
            return timePickerDialog;
        }
        return null;
    }

    TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {

        //ROB EDITED THIS, added minuteTIME
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            // store the data in one string and set it to text
            minuteTime = (hour*60) + minute;
            String time = String.valueOf(hour) + ":" + String.valueOf(minute);
            arrivalTime.setText(time);
        }
    };
}
