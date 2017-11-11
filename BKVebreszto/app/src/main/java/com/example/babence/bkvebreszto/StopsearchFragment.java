package com.example.babence.bkvebreszto;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.babence.bkvebreszto.dummy.DummyContent;
import com.example.babence.bkvebreszto.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class StopsearchFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    public List<Stops> mStops = new ArrayList<Stops>();
    public SearchView searchView;
    RecyclerView recyclerView;
    MyStopsRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StopsearchFragment() {
    }


    public static StopsearchFragment newInstance() {
        StopsearchFragment fragment = new StopsearchFragment();
        return fragment;
    }

    public static StopsearchFragment newInstance(ArrayList stops) {
        StopsearchFragment fragment = new StopsearchFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("megallok", stops);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stops_list, container, false);


        mStops = getArguments().getParcelableArrayList("megallok");

        // Set the adapter
        Context context = view.getContext();
        searchView = (SearchView) view.findViewById(R.id.searchText);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toString().toLowerCase();

                final List<Stops> filteredList = new ArrayList<>();

                for (Stops item : mStops) {

                    final String text = item.getStopName().toLowerCase();
                    if (text.contains(newText)) {

                        filteredList.add(item);
                    }
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mAdapter = new MyStopsRecyclerViewAdapter(filteredList, mListener);
                recyclerView.setAdapter(mAdapter);

                mAdapter.setStops(filteredList);  // data set changed
                return false;
            }


        });
        recyclerView = (RecyclerView) view.findViewById(R.id.stoplist);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new MyStopsRecyclerViewAdapter(mStops, mListener);
        recyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Stops item);
    }


}
