package com.vvsemir.kindawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvsemir.kindawk.Models.VkApiIntentService;
import com.vvsemir.kindawk.http.HttpResponse;

public abstract class ReceiverFragment extends Fragment implements IReceiverFragment<Parcelable> {
    static final String ARG_PARAM_DATA = "receiver_fragment_data";
    static final String ARG_PARAM_INTENT_RESPONSE = "receiver_fragment_intent_response";

    ApiBroadcastReceiver apiBroadcastReceiver = new ApiBroadcastReceiver();
    IntentFilter intentFilter;

    Context context;
    private String initParamIntentResponse;
    private Parcelable initParamData;



    private OnFragmentInteractionListener activityListener;

    public ReceiverFragment() {
        // Required empty public constructor
    }

    public static Bundle initBundle(String response, Parcelable data) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_INTENT_RESPONSE, response);
        args.putParcelable(ARG_PARAM_DATA, data);
        return args;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initParamIntentResponse = getArguments().getString(ARG_PARAM_INTENT_RESPONSE);
            initParamData = getArguments().getParcelable(ARG_PARAM_DATA);
        }
        apiBroadcastReceiver = new ApiBroadcastReceiver();
        intentFilter = new IntentFilter(initParamIntentResponse);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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
        activityListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(apiBroadcastReceiver, intentFilter);
        startLoadDataService();
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(apiBroadcastReceiver);
    }

    public void saveArguments(Parcelable data){
        getArguments().putParcelable( ARG_PARAM_DATA, data );
    }

    public class ApiBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            HttpResponse response = intent.getParcelableExtra(VkApiIntentService.EXTRA_PROFILE_KEY_OUT);
            updateViews(response);
            saveArguments(response);
        }
    }


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


