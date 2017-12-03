package com.quickd.quickdestinations;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private static final String[] COUNTRIES = new String[]{
            "Belgium", "France", "Italy", "Germany", "Spain"
    };
    private static final int TIME_ID = 0;

    private EditText arrivalTime;

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

        arrivalTime = (EditText) view.findViewById(R.id.etArrivalTime);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        final AutoCompleteTextView searchBar = (AutoCompleteTextView)
                view.findViewById(R.id.tvSearchBar);
        searchBar.setAdapter(adapter);

        arrivalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog(TIME_ID).show();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = searchBar.getText().toString();
                String time = arrivalTime.getText().toString();
                if (!location.isEmpty() && (location.trim().length() > 0))
                {
                    ((MainActivity) getActivity()).setDestinations(new Pair(location, time));
                }
                else
                    Toast.makeText(getActivity(), "INVALID LOCATION: Try again", Toast.LENGTH_SHORT).show();
                searchBar.setText("");
                arrivalTime.setText("");
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
            return new TimePickerDialog(getActivity(), R.style.MySpinnerTimePickerStyle, time_listener, hour, minute, false);
        }
        return null;
    }

    TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            // store the data in one string and set it to text
            String time = String.valueOf(hour) + ":" + String.valueOf(minute);
            arrivalTime.setText(time);
        }
    };
}
