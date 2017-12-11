package com.quickd.quickdestinations;


import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
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

        view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = arrivalTime.getText().toString();
                if (location.trim().length() > 0) {
                    ((MainActivity) getActivity()).setDestinations(new Pair(location, time));
                    ((MainActivity) getActivity()).setLatLngs(new Pair(name, latLng));
                    Toast.makeText(getActivity(), "Successfully added destination", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "Please enter a destination", Toast.LENGTH_SHORT).show();
                autocompleteFragment.setText("");
                arrivalTime.setText("");
                location = "";
            }
        });

        view.findViewById(R.id.btnSaved).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> itemList = new ArrayList<>();
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                if (firebaseUser != null){
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, itemList);
                    myRef.child("Users").child(firebaseUser.getUid()).child("Saved Location").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            String value = dataSnapshot.getValue(String.class);
                            itemList.add(value);
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    alertDialogBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            autocompleteFragment.setText(itemList.get(which));
                        }
                    });
                    alertDialogBuilder.show();
                } else{
                    Toast.makeText(getActivity(), "This feature for only registered Users",Toast.LENGTH_SHORT).show();
                }
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
