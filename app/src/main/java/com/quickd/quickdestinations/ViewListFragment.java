package com.quickd.quickdestinations;


import android.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewListFragment extends Fragment {

    public ViewListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ArrayList<String> destinations = new ArrayList<>();
        for (Pair<String, String> temp : ((MainActivity) getActivity()).getDestinations())
            destinations.add(temp.first);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, destinations);
        final ListView listView = view.findViewById(R.id.lvList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                final String getSelectedItemOfList = destinations.get(i);
                alertDialogBuilder.setMessage("Remove or Save\n" + destinations.get(i) + " ?");
                alertDialogBuilder.setPositiveButton("Save",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                        if (firebaseUser != null){
                            (myRef.child("Users").child(firebaseUser.getUid().toString()).child("Saved Location")).push().setValue(getSelectedItemOfList);
                            Toast.makeText(getActivity(), firebaseUser.getEmail(),Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(getActivity(), "This feature for only registered Users",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("Remove",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        destinations.remove(getSelectedItemOfList);
                        listView.invalidateViews();
                        //removing item from the main list
                        ((MainActivity) getActivity()).rmDestinations(i);
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
