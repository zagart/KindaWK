package com.vvsemir.kindawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;


public abstract class ReceiverFragment extends Fragment implements IReceiverFragment<Parcelable> {
    static final String ARG_PARAM_PROVIDER_DATA = "provider_fragment_data";

    Context context;
    Parcelable paramProviderData;

    private OnFragmentInteractionListener activityListener;

    public ReceiverFragment() {
        // Required empty public constructor
    }


    public static Bundle initBundle(Parcelable data) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_PROVIDER_DATA, data);
        return args;
    }

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            paramProviderData = getArguments().getParcelable(ARG_PARAM_PROVIDER_DATA);
        }

        onPostCreate();
        loadData();
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (activityListener != null) {
            activityListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        //TODO
        /*
        if (context instanceof OnFragmentInteractionListener) {
            activityListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //activityListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_PROVIDER_DATA, paramProviderData);
        setArguments(args);

        super.onPause();
    }

    public void saveArguments(Parcelable data){
        getArguments().putParcelable(ARG_PARAM_PROVIDER_DATA, data);
    }

    //public class UpdaterBroadcastReceiver extends BroadcastReceiver {

        //public void onReceive(Context context, Intent intent) {
        //    Parcelable response = intent.getParcelableExtra(ProviderIntentService.EXTRA_RESPONSE_DATA);
        //    updateViews(response);
        //    saveArguments(response);
        //}
   // }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Fragment fragment);
    }
}


