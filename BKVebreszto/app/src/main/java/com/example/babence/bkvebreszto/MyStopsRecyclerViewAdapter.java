package com.example.babence.bkvebreszto;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.babence.bkvebreszto.StopsearchFragment.OnListFragmentInteractionListener;

import java.text.DecimalFormat;
import java.util.List;

import static com.example.babence.bkvebreszto.MainActivity.latitude;
import static com.example.babence.bkvebreszto.MainActivity.longitude;


class MyStopsRecyclerViewAdapter extends RecyclerView.Adapter<MyStopsRecyclerViewAdapter.ViewHolder> {

    //private final List<DummyItem> mValues;
    private List<Stops> mValues;
    private final OnListFragmentInteractionListener mListener;

    MyStopsRecyclerViewAdapter(List<Stops> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void setStops(List<Stops> newStops) {
        mValues = newStops;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_stops, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).getId());
        holder.mContentView.setText(mValues.get(position).getStopName());

        if(longitude != 0 && latitude != 0)  {
            String distText = String.valueOf(new DecimalFormat("##.##").format(MainActivity.getDistanceFromLatLonInKm(
                    Double.parseDouble(mValues.get(position).getLongitude()),
                    Double.parseDouble(mValues.get(position).getLatitude()),
                    longitude,
                    latitude))) + " km";
            holder.mDistanceView.setText(distText);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(holder.mView.getContext() ,mValues.get(position).getStopName(), Toast.LENGTH_SHORT).show();
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        //public final TextView mIdView;
        final TextView mContentView;
        final TextView mDistanceView;
        Stops mItem;


        ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mDistanceView = (TextView) view.findViewById(R.id.distance);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
