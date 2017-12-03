package com.quickd.quickdestinations;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        ArrayList<String> destinations = new ArrayList<>();
        if (LogoutActivity.loggedIn == true)
        {
            for (Pair<String, String> temp : ((LogoutActivity) getActivity()).getDestinations())
                destinations.add(temp.first + " " + temp.second);
        }
        else
        {
            for (Pair<String, String> temp : ((LoginActivity) getActivity()).getDestinations())
                destinations.add(temp.first + " " + temp.second);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, destinations);
        ListView listView = (ListView) view.findViewById(R.id.lvList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
