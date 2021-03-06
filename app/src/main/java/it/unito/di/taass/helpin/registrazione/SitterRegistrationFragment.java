package it.unito.di.taass.helpin.registrazione;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import it.unito.di.taass.helpin.Constants;
import it.unito.di.taass.helpin.R;
import it.unito.di.taass.helpin.oggetti.UtenteSitter;


public class SitterRegistrationFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    ArrayList<EditText> listaET = new ArrayList<>();
    View view;
    EditText usernameET, passwordET, confermaPasswordET, nomeET, cognomeET, emailET, numeroET, dataNascitaET, cittaET;
    RadioGroup genereRG;
    Switch autoSW;
    Button confRegistration;
    String genere = "";
    String auto = "1";
    Spinner nazioni;
    String arraypaesi[];

    private OnFragmentInteractionListener mListener;

    public SitterRegistrationFragment() {
    }

    // Creazione della stringa dalla data scelta con il DatePicker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dataNascitaET.setText(
                new StringBuilder()
                        .append(dayOfMonth).append("-")
                        .append(month + 1).append("-")
                        .append(year));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_registration_sitter, container, false);

        // Generatore dello spinner di scelta della nazione
        nazioni = (Spinner) view.findViewById(R.id.spinnerSitterReg);
        arraypaesi = getResources().getStringArray(R.array.paesi);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.paesi, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nazioni.setAdapter(adapter);

        // Inizializzazione del layout
        initialization();

        // Creazione del Date Picker
        Calendar c = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        // Visualizzazione del date picker al click del campo
        dataNascitaET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // Gestione della scelta del genere
        genereRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleSitterReg)
                    genere = "M";
                else if (checkedId == R.id.femaleSitterReg)
                    genere = "F";
            }
        });

        // Gestione dello switch auto
        autoSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    auto = "0";
                else
                    auto = "1";
            }
        });

        // Click listener di conferma registrazione
        confRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEmpty()) {
                    Toast.makeText(getContext(), R.string.missingFields, Toast.LENGTH_LONG).show();
                } else if (!confermaPassword(passwordET.getText().toString(), confermaPasswordET.getText().toString())) {
                    Toast.makeText(getContext(), R.string.checkPassword, Toast.LENGTH_LONG).show();
                } else if (!checkEmail(emailET.getText().toString())) {
                    Toast.makeText(getContext(), R.string.invalidEmail, Toast.LENGTH_LONG).show();
                } else {
                    UtenteSitter sitter = new UtenteSitter(usernameET.getText().toString(),
                            passwordET.getText().toString(),
                            nomeET.getText().toString(),
                            cognomeET.getText().toString(),
                            Constants.dateToSQL(dataNascitaET.getText().toString()),
                            emailET.getText().toString(),
                            numeroET.getText().toString(),
                            genere,
                            nazioni.getSelectedItem().toString(),
                            cittaET.getText().toString(),
                            auto);
                    mListener.onFragmentInteraction(sitter);
                }
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must override method");
        }
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(UtenteSitter sitter);
    }

    //inizializzazione delle view
    public void initialization() {
        usernameET = (EditText) view.findViewById(R.id.usernameSitterReg);
        listaET.add(usernameET);
        passwordET = (EditText) view.findViewById(R.id.passwordSitterReg);
        listaET.add(passwordET);
        confermaPasswordET = (EditText) view.findViewById(R.id.confPasswordSitterReg);
        listaET.add(confermaPasswordET);
        nomeET = (EditText) view.findViewById(R.id.nomeSitterReg);
        listaET.add(nomeET);
        cognomeET = (EditText) view.findViewById(R.id.cognomeSitterReg);
        listaET.add(cognomeET);
        emailET = (EditText) view.findViewById(R.id.emailSitterReg);
        listaET.add(emailET);
        numeroET = (EditText) view.findViewById(R.id.phoneSitterReg);
        listaET.add(numeroET);
        dataNascitaET = (EditText) view.findViewById(R.id.nascitaSitterReg);
        listaET.add(dataNascitaET);
        genereRG = (RadioGroup) view.findViewById(R.id.groupGenderSitterReg);
        cittaET = (EditText) view.findViewById(R.id.CittaSitterReg);
        listaET.add(cittaET);
        autoSW = (Switch) view.findViewById(R.id.switch1);
        confRegistration = (Button) view.findViewById(R.id.buttonReg);
    }

    //controllo che le password siano uguali
    private boolean confermaPassword(String password, String conferma) {
        return password.equals(conferma);
    }

    //controllo il formato della mail
    private boolean checkEmail(String email) {
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        return emailPattern.matcher(email).matches();
    }

    //controllo se i campi sono vuoti
    private boolean isEmpty() {

        boolean empty = false;

        for (EditText element : listaET) {
            if (element.getText().toString().equals(""))
                empty = true;
        }

        if (genere.equals(""))
            empty = true;

        return empty;
    }


}
