package it.unito.di.taass.helpin.recensioni;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unito.di.taass.helpin.adapter.RecensioniAdapter;
import it.unito.di.taass.helpin.Constants;
import it.unito.di.taass.helpin.oggetti.Recensione;
import it.unito.di.taass.helpin.Php;
import it.unito.di.taass.helpin.R;
import it.unito.di.taass.helpin.SessionManager;
import tr.xip.errorview.ErrorView;


public class RecensioniScritteFragment extends Fragment {


    private RecyclerView recycler;
    private RecensioniAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Recensione> recensioneList;


    private SessionManager sessionManager;
    ErrorView errorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_home, container, false);

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        recycler = (RecyclerView) view.findViewById(R.id.recyclerHome);
        recycler.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());

        recycler.setLayoutManager(layoutManager);

        recensioneList = new ArrayList<>();
        adapter = new RecensioniAdapter(recensioneList);
        recycler.setAdapter(adapter);

        errorView = (ErrorView) view.findViewById(R.id.errorView);
        loadFeedback();

        return view;
    }


    private void loadFeedback() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Php.RECENSIONI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        try {
                            JSONArray recensione = new JSONArray(response);

                            if (recensione.length() == 0) {

                                errorView.setSubtitle(R.string.niente_recensioni_scritte);
                                errorView.setVisibility(View.VISIBLE);
                            } else {
                                for (int i = 0; i < recensione.length(); i++) {

                                    JSONObject recensioneObject = recensione.getJSONObject(i);
                                    String username;

                                    if (sessionManager.getSessionType() == Constants.TYPE_SITTER) {
                                        username = recensioneObject.getString("famiglia");
                                    } else {
                                        username = recensioneObject.getString("babysitter");
                                    }


                                    String descrizione = recensioneObject.getString("commento");
                                    Float rating = (float) recensioneObject.getDouble("rating");

                                    Recensione r = new Recensione(username, descrizione, rating);
                                    recensioneList.add(r);
                                }

                            }
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", sessionManager.getSessionUsername());
                params.put("tipoUtente", String.valueOf(sessionManager.getSessionType()));
                params.put("operation", "sent");
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }
}

