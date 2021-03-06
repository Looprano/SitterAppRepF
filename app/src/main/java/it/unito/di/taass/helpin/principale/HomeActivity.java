package it.unito.di.taass.helpin.principale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

import it.unito.di.taass.helpin.adapter.NoticeAdapter;
import it.unito.di.taass.helpin.adapter.SitterAdapter;
import it.unito.di.taass.helpin.appuntamenti.DialogsNoticeDetails;
import it.unito.di.taass.helpin.Constants;
import it.unito.di.taass.helpin.oggetti.Notice;
import it.unito.di.taass.helpin.oggetti.UtenteSitter;
import it.unito.di.taass.helpin.Php;
import it.unito.di.taass.helpin.profilo.ProfiloPubblicoActivity;
import it.unito.di.taass.helpin.R;
import tr.xip.errorview.ErrorView;

/**
 * Activity per la Home
 */
public class HomeActivity extends DrawerActivity
        implements NoticeAdapter.NoticeAdapterListener, SitterAdapter.ContactsSitterAdapterListener, DialogFiltro.DialogListener {

    // Vista
    private RecyclerView recyclerView;

    //Items babysitter
    private List<Notice> noticeList;
    private NoticeAdapter noticeAdapter;

    //Items family
    private List<UtenteSitter> sitterList;
    private List<UtenteSitter> filteredSitterList; //per la lista dopo aver applicato un filtro
    private SitterAdapter sitterAdapter;


    FloatingActionButton cercaSitter;

    //richieste
    private static final String annunci = "ANNUNCI";

    // Filtro disponibilità
    Map<String, ArrayList<Integer>> dispTotali;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispTotali = new HashMap<>();
        //FAB per la ricerca delle baby sitter
        cercaSitter = (FloatingActionButton) findViewById(R.id.cercaSitter);
        if (sessionManager.getSessionType() == Constants.TYPE_FAMILY && cercaSitter.getVisibility() == View.GONE) {
            cercaSitter.show();
            cercaSitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ricerca
                    DialogFiltro dialogFiltro = DialogFiltro.newInstance();
                    dialogFiltro.show(getSupportFragmentManager(), "dialog");
                }
            });
        } else if (sessionManager.getSessionType() == Constants.TYPE_SITTER && cercaSitter.getVisibility() == View.VISIBLE) {
            cercaSitter.hide();
        }

        //CARICAMENTO DEGLI ANNUNCI
        recyclerView = (RecyclerView) findViewById(R.id.recyclerHome);

        if (sessionManager.checkLogin()) {
            loadHome();
        } else {
            loadDemo();
        }

    }

    //Carica la home in fase di Demo
    private void loadDemo() {
        int type = getIntent().getIntExtra(Constants.TYPE, -1);

        if (type == Constants.TYPE_SITTER) {

            noticeList = new ArrayList<>();
            noticeList.add(new Notice("1", "Ladisa", "23-06-2018", "17.00", "20.00", "Ho bisogno di qualcuno che badi ai miei figli mentre faccio la spesa."));
            noticeList.add(new Notice("2", "Luprano", "24-06-2018", "07.30", "12.30", "Cerco babysitter che badi a mio figlio durante il mio turno di lavoro."));
            noticeList.add(new Notice("3", "Deperte", "25-06-2018", "13.00", "16.00", "Cercasi babysitter per i miei due figli."));
            noticeList.add(new Notice("4", "Angarano", "25-06-2018", "19.00", "22.00", "Ho bisogno di una babysitter che prepari la cena per mia figlia"));
            noticeList.add(new Notice("5", "Cuccovillo", "26-06-2018", "18.00", "21.00", "Cercasi tata per i miei tre figli."));
            noticeList.add(new Notice("6", "Loiacono", "27-06-2018", "09.00", "13.00", "La mia tata è impegnata, e ho bisogno di una sostituta per un giorno."));
            noticeAdapter = new NoticeAdapter(HomeActivity.this, noticeList, HomeActivity.this);

            recyclerView.setAdapter(noticeAdapter);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        } else if (type == Constants.TYPE_FAMILY) {

            sitterList = new ArrayList<>();
            sitterList.add(new UtenteSitter("Francesca", "http://sitterapp.altervista.org/profilePicture/demoSitter/francesca.jpg", new Float(2.5), 4));
            sitterList.add(new UtenteSitter("Gabriella", "http://sitterapp.altervista.org/profilePicture/demoSitter/gabriella.jpg", 4, 3));
            sitterList.add(new UtenteSitter("Gianluca", "http://sitterapp.altervista.org/profilePicture/demoSitter/gianluca.jpg", new Float(3.5), 7));
            sitterList.add(new UtenteSitter("Davide", "http://sitterapp.altervista.org/profilePicture/demoSitter/davide.jpg", 3, 5));
            sitterList.add(new UtenteSitter("Monica", "http://sitterapp.altervista.org/profilePicture/demoSitter/monica.jpg", 2, 4));
            sitterList.add(new UtenteSitter("Giorgia", "http://sitterapp.altervista.org/profilePicture/demoSitter/giorgia.jpg", 5, 7));
            sitterAdapter = new SitterAdapter(HomeActivity.this, sitterList, HomeActivity.this);

            recyclerView.setAdapter(sitterAdapter);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        }
    }

    //Carica la home se è stato effettuato l'accesso
    private void loadHome() {
        if ((sessionManager.getSessionType() == Constants.TYPE_SITTER)) {

            noticeList = new ArrayList<>();
            noticeAdapter = new NoticeAdapter(HomeActivity.this, noticeList, HomeActivity.this);

            recyclerView.setAdapter(noticeAdapter);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            loadNotices();

        } else if (sessionManager.getSessionType() == Constants.TYPE_FAMILY) {

            sitterList = new ArrayList<>();
            sitterAdapter = new SitterAdapter(HomeActivity.this, sitterList, HomeActivity.this);

            recyclerView.setAdapter(sitterAdapter);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            loadSitter();
        }
    }

    //Caricamento degli annunci (per la home delle babysitter)
    private void loadNotices() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Php.ANNUNCI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        try {
                            JSONArray notice = new JSONArray(response);

                            if (notice.length() == 0) {
                                ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
                                errorView.setVisibility(View.VISIBLE);
                                errorView.setSubtitle(R.string.niente_annunci);
                            } else {

                                for (int i = 0; i < notice.length(); i++) {

                                    JSONObject noticeObject = notice.getJSONObject(i);
                                    String idAnnuncio = noticeObject.getString("idAnnuncio");
                                    String famiglia = noticeObject.getString("usernameFamiglia");
                                    String data = Constants.SQLtoDate(noticeObject.getString("data"));
                                    String oraInizio = noticeObject.getString("oraInizio");
                                    String oraFine = noticeObject.getString("oraFine");
                                    String descrizione = noticeObject.getString("descrizione");
                                    Notice n = new Notice(idAnnuncio, famiglia, data, oraInizio, oraFine, descrizione);
                                    noticeList.add(n);
                                }
                            }

                            noticeAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("richiesta", annunci);
                params.put("username", sessionManager.getSessionUsername());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
        progressDialog.hide();
    }

    //Caricamento dell'elenco babysitter (per la home della famiglia)
    private void loadSitter() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Php.ELENCO_SITTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        try {
                            JSONArray sitter = new JSONArray(response);
                            if (sitter.length() == 0) {
                                ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
                                errorView.setVisibility(View.VISIBLE);
                                errorView.setTitle(R.string.niente_sitter);
                            } else {
                                for (int i = 0; i < sitter.length(); i++) {
                                    JSONObject sitterObject = sitter.getJSONObject(i);
                                    String username = sitterObject.getString("username");
                                    String foto = sitterObject.getString("pathfoto");

                                    float rating;
                                    if (sitterObject.getString("rating").equals("null")) {
                                        rating = 0;
                                    } else {
                                        rating = (float) sitterObject.getDouble("rating");
                                    }

                                    int numLavori;
                                    if (sitterObject.getString("numLavori").equals("null")) {
                                        numLavori = 0;
                                    } else {
                                        numLavori = sitterObject.getInt("numLavori");
                                    }

                                    UtenteSitter s = new UtenteSitter(username, foto, rating, numLavori);
                                    sitterList.add(s);
                                    dispTotali.put(username, new ArrayList<Integer>());
                                    getDisponibilità(username);
                                }
                            }

                            sitterAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
        progressDialog.hide();
    }

    //al click su un annuncio visualizza i dettagli
    @Override
    public void onNoticeSelected(Notice notice) {

        if (sessionManager.checkLogin()) {
            DialogsNoticeDetails dialogs = DialogsNoticeDetails.newInstance(notice);
            dialogs.show(getSupportFragmentManager(), "dialog");
        } else {
            sessionManager.forceLogin(this);
        }

    }

    //al click su un sitter visualizza il profilo della sitter
    @Override
    public void onSitterSelected(UtenteSitter sitter) {

        if (sessionManager.checkLogin()) {
            Intent detailIntent = new Intent(HomeActivity.this, ProfiloPubblicoActivity.class);
            detailIntent.putExtra(Constants.TYPE, Constants.TYPE_SITTER);
            detailIntent.putExtra("username", sitter.getUsername());
            startActivity(detailIntent);
        } else {
            sessionManager.forceLogin(this);
        }

    }

    //filtro sulle baby sitter
    @Override
    public void settaFiltro(ArrayList<Integer> checkedBox, float rating, int minLavori) {

        filteredSitterList = new ArrayList<>();
        filteredSitterList.addAll(sitterList);
        ArrayList<UtenteSitter> removeList = new ArrayList<>();

        for (UtenteSitter sitter : filteredSitterList) {

            // CHECK SUL MINLAVORI
            if (sitter.getNumLavori() <= minLavori) {
                removeList.add(sitter);
                continue;
            }

            // CHECK SUL RATING
            if (sitter.getRating() < rating) {
                removeList.add(sitter);
                continue;
            }


            // CHECK SULLA DISPONIBILITA

            for (Integer checked : checkedBox) {
                if (!dispTotali.get(sitter.getUsername()).contains(checked)) {
                    removeList.add(sitter);
                    break;
                }
            }
        }

        filteredSitterList.removeAll(removeList);
        sitterAdapter.updateSitterList(filteredSitterList);
        sitterAdapter.notifyDataSetChanged();

        ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
        if (filteredSitterList.size() == 0) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setTitle(R.string.niente_sitter);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }

    //volley per la disponibilità delle sitter
    private void getDisponibilità(final String username) {

        StringRequest load = new StringRequest(Request.Method.POST, Php.DISPONIBILITA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int fascia = jsonObject.getInt("fascia");
                        dispTotali.get(username).add(fascia);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("operation", "load");
                params.put("username", username);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(load);
    }
}