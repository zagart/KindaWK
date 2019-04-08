package com.vvsemir.kindawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.vvsemir.kindawk.service.ProviderIntentService;


public abstract class ReceiverFragment extends Fragment implements IReceiverFragment<Parcelable> {
    static final String ARG_PARAM_PROVIDER_RESPONSE = "provider_fragment_response_action";
    static final String ARG_PARAM_PROVIDER_DATA = "provider_fragment_data";
    static final String ARG_PARAM_PROVIDER_PRESERVE_DATA = "provider_fragment_preserve_data";

    UpdaterBroadcastReceiver updaterBroadcastReceiver = new UpdaterBroadcastReceiver();
    IntentFilter intentFilter;

    Context context;
    String paramProviderResponseAction;
    Parcelable paramProviderData;
    Boolean paramPreserveProviderData;

    private OnFragmentInteractionListener activityListener;

    public ReceiverFragment() {
        // Required empty public constructor
    }


    public static Bundle initBundle(String responseAction, Parcelable data, Boolean preserveProviderData) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_PROVIDER_RESPONSE, responseAction);
        args.putParcelable(ARG_PARAM_PROVIDER_DATA, data);
        args.putBoolean(ARG_PARAM_PROVIDER_PRESERVE_DATA, preserveProviderData);
        return args;
    }

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            paramProviderResponseAction = getArguments().getString(ARG_PARAM_PROVIDER_RESPONSE);
            paramProviderData = getArguments().getParcelable(ARG_PARAM_PROVIDER_DATA);
            paramPreserveProviderData = getArguments().getBoolean(ARG_PARAM_PROVIDER_PRESERVE_DATA);
        }

        if(paramProviderResponseAction == null){
            return;
        }

        updaterBroadcastReceiver = new UpdaterBroadcastReceiver();
        intentFilter = new IntentFilter(paramProviderResponseAction);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
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
        activityListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(updaterBroadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(updaterBroadcastReceiver);
    }

    public void saveArguments(Parcelable data){
        getArguments().putString(ARG_PARAM_PROVIDER_RESPONSE, paramProviderResponseAction);
        getArguments().putBoolean(ARG_PARAM_PROVIDER_PRESERVE_DATA, paramPreserveProviderData);
        getArguments().putParcelable(ARG_PARAM_PROVIDER_DATA,
                (paramPreserveProviderData != null && paramPreserveProviderData)? data : null);
    }

    public class UpdaterBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Parcelable response = intent.getParcelableExtra(ProviderIntentService.EXTRA_RESPONSE_DATA);
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


