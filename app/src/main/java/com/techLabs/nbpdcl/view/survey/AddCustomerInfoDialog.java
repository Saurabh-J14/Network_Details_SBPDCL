package com.techLabs.nbpdcl.view.survey;

import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.DataBase.Room.AppDatabase;
import com.techLabs.nbpdcl.DataBase.Room.CustomerData;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.ListDataManager;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.databinding.AddCustomerLayoutBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddCustomerInfoDialog extends Dialog {

    private final String phaseValue;
    private final String phaseType;
    private final String loadType;
    private final List<View> viewList = new ArrayList<>();
    private final Context mainContext;
    private AddCustomerLayoutBinding binding;
    private LayoutInflater layoutInflater;
    private int a = 1;
    private PrefManager prefManager;
    private AppDatabase appDatabase;

    public AddCustomerInfoDialog(@NonNull Context context, String phaseType, String phase, String loadType) {
        super(context);
        this.mainContext = context;
        this.phaseType = phaseType;
        this.phaseValue = phase;
        this.loadType = loadType;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddCustomerLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        layoutInflater = getLayoutInflater();
        prefManager = new PrefManager(mainContext);
        appDatabase = AppDatabase.getInstance(mainContext);

        @SuppressLint("InflateParams")
        View child = layoutInflater.inflate(R.layout.add_header_title_layout, null);
        binding.dynamicLayout.addView(child);
        final int random = new Random().nextInt(61) + 20;
        TextView actualKva = child.findViewById(R.id.actual_kva);
        TextView actualPf = child.findViewById(R.id.actual_pf);
        TextView connectedKva = child.findViewById(R.id.connected_kva);
        TextView kwh = child.findViewById(R.id.kwh);
        TextView customer = child.findViewById(R.id.customer);

        if (loadType.equalsIgnoreCase("1")) {
            //KVA & PF
            actualKva.setText("Actual kVa");
            actualPf.setText("Actual PF");
            connectedKva.setText("Conn kVa");
            kwh.setText("kWh");
            customer.setText("Cust.#");
        } else if (loadType.equalsIgnoreCase("0")) {
            //KW & Kvar
            actualKva.setText("Actual kW");
            actualPf.setText("Actual kVar");
            connectedKva.setText("Conn kVa");
            kwh.setText("kWh");
            customer.setText("Cust.#");
        } else {
            //KW & PF
            actualKva.setText("Actual kW");
            actualPf.setText("Actual PF");
            connectedKva.setText("Conn kVa");
            kwh.setText("kWh");
            customer.setText("Cust.#");
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                final List<CustomerData> list = appDatabase.customerDataDao().getUniqueCustomers();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (binding != null) {
                        if (list.isEmpty()) {
                            AddDefaultCustomer(random);
                        } else {
                            updateUi(list);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("SpotLoad", "Database Error", e);
            }
        });

        ListDataManager.clearData();
        ListDataManager.clearDefaultData();

        binding.okbtn.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                appDatabase.customerDataDao().deleteAll();
                checkDetails();

            });
        });

        binding.add.setOnClickListener(v -> {
            addDynamicViews(random, a++);
        });

        binding.remove.setOnClickListener(v -> {
            removeDynamicViews();
        });

        binding.canclebtn.setOnClickListener(v -> {
            dismiss();
        });

    }

    private void AddDefaultCustomer(int random) {
        if (phaseType.equals("ThreePhase")) {
            @SuppressLint("InflateParams")
            View child1 = layoutInflater.inflate(R.layout.three_phase_layout, null);
            binding.dynamicLayout.addView(child1);
            viewList.add(child1);
            EditText custNo = child1.findViewById(R.id.customerNumber);
            String combinedData = random + "-" + a;
            custNo.setText(combinedData);
            Spinner spinner = child1.findViewById(R.id.cust_type_spinnerBar);
            if (prefManager.getConsumerClasses() != null) {
                ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                spinner.setAdapter(locationAdapters);
            }
        } else {
            if (phaseValue.equals("1")) {
                @SuppressLint("InflateParams")
                View byPhaseALayout = layoutInflater.inflate(R.layout.by_phase_a_layout, null);
                binding.dynamicLayout.addView(byPhaseALayout);
                viewList.add(byPhaseALayout);

                EditText editText = byPhaseALayout.findViewById(R.id.customerNumber);
                String combinedData = random + "-" + a;
                editText.setText(combinedData);
                Spinner spinner = byPhaseALayout.findViewById(R.id.cust_type_spinnerBar);
                if (prefManager.getConsumerClasses() != null) {
                    ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                    spinner.setAdapter(locationAdapters);
                }
            } else if (phaseValue.equals("2")) {
                @SuppressLint("InflateParams")
                View byPhaseBLayout = layoutInflater.inflate(R.layout.by_phase_b_layout, null);
                binding.dynamicLayout.addView(byPhaseBLayout);
                viewList.add(byPhaseBLayout);

                EditText editText = byPhaseBLayout.findViewById(R.id.customerNumber);
                String combinedData = random + "-" + a;
                editText.setText(combinedData);
                Spinner spinner = byPhaseBLayout.findViewById(R.id.cust_type_spinnerBar);
                if (prefManager.getConsumerClasses() != null) {
                    ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                    spinner.setAdapter(locationAdapters);
                }
            } else if (phaseValue.equals("3")) {
                @SuppressLint("InflateParams")
                View byPhaseCLayout = layoutInflater.inflate(R.layout.by_phase_c_layout, null);
                binding.dynamicLayout.addView(byPhaseCLayout);
                viewList.add(byPhaseCLayout);

                EditText editText = byPhaseCLayout.findViewById(R.id.customerNumber);
                String combinedData = random + "-" + a;
                editText.setText(combinedData);
                Spinner spinner = byPhaseCLayout.findViewById(R.id.cust_type_spinnerBar);
                if (prefManager.getConsumerClasses() != null) {
                    ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                    spinner.setAdapter(locationAdapters);
                }
            } else if (phaseValue.equals("4")) {
                @SuppressLint("InflateParams")
                View byPhaseABLayout = layoutInflater.inflate(R.layout.by_phase_ab_layout, null);
                binding.dynamicLayout.addView(byPhaseABLayout);
                viewList.add(byPhaseABLayout);

                EditText editText = byPhaseABLayout.findViewById(R.id.customerNumber);
                String combinedData = random + "-" + a;
                editText.setText(combinedData);
                Spinner spinner = byPhaseABLayout.findViewById(R.id.cust_type_spinnerBar);
                if (prefManager.getConsumerClasses() != null) {
                    ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                    spinner.setAdapter(locationAdapters);
                }
            } else if (phaseValue.equals("5")) {
                @SuppressLint("InflateParams")
                View byPhaseACLayout = layoutInflater.inflate(R.layout.by_phase_ac_layout, null);
                binding.dynamicLayout.addView(byPhaseACLayout);
                viewList.add(byPhaseACLayout);

                EditText editText = byPhaseACLayout.findViewById(R.id.customerNumber);
                String combinedData = random + "-" + a;
                editText.setText(combinedData);

                Spinner spinner = byPhaseACLayout.findViewById(R.id.cust_type_spinnerBar);
                if (prefManager.getConsumerClasses() != null) {
                    ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                    spinner.setAdapter(locationAdapters);
                }

            } else if (phaseValue.equals("6")) {
                @SuppressLint("InflateParams")
                View byPhaseBCLayout = layoutInflater.inflate(R.layout.by_phase_bc_layout, null);
                binding.dynamicLayout.addView(byPhaseBCLayout);
                viewList.add(byPhaseBCLayout);

                EditText editText = byPhaseBCLayout.findViewById(R.id.customerNumber);
                String combinedData = random + "-" + a;
                editText.setText(combinedData);

                Spinner spinner = byPhaseBCLayout.findViewById(R.id.cust_type_spinnerBar);
                if (prefManager.getConsumerClasses() != null) {
                    ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                    spinner.setAdapter(locationAdapters);
                }

            } else {
                @SuppressLint("InflateParams")
                View byPhaseABCLayout = layoutInflater.inflate(R.layout.by_phase_abc_layout, null);
                binding.dynamicLayout.addView(byPhaseABCLayout);
                viewList.add(byPhaseABCLayout);

                EditText editText = byPhaseABCLayout.findViewById(R.id.customerNumber);
                String combinedData = random + "-" + a;
                editText.setText(combinedData);
                Spinner spinner = byPhaseABCLayout.findViewById(R.id.cust_type_spinnerBar);
                if (prefManager.getConsumerClasses() != null) {
                    ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                    spinner.setAdapter(locationAdapters);
                }
            }
        }
    }

    private void updateUi(List<CustomerData> list) {
        if (list == null || list.isEmpty()) return;
        if (list.get(0).getPhaseType().equalsIgnoreCase("ThreePhase")) {
            for (int i = 0; i < list.size(); i++) {
                @SuppressLint("InflateParams")
                View child1 = layoutInflater.inflate(R.layout.three_phase_layout, null);
                binding.dynamicLayout.addView(child1);
                viewList.add(child1);
                EditText custNo = child1.findViewById(R.id.customerNumber);
                custNo.setText(list.get(i).getCustomerNumber());

                Spinner spinner = child1.findViewById(R.id.cust_type_spinnerBar);
                if (prefManager.getConsumerClasses() != null) {
                    ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                    spinner.setAdapter(locationAdapters);
                    if (list.get(i).getCustomerType() != null && !list.get(i).getCustomerType().isEmpty()) {
                        String item = list.get(i).getCustomerType();
                        int spinnerPosition = locationAdapters.getPosition(item);
                        if (spinnerPosition != -1) {
                            spinner.setSelection(spinnerPosition);
                        }
                    }
                }

                Spinner statusSpinner = child1.findViewById(R.id.statusSpinnerBar);
                int savedStatus = list.get(i).getStatus();
                String targetStatus = (savedStatus == 0) ? "Connected" : "DisConnected";
                for (int j = 0; j < statusSpinner.getCount(); j++) {
                    if (statusSpinner.getItemAtPosition(j).toString().equalsIgnoreCase(targetStatus)) {
                        statusSpinner.setSelection(j);
                        break;
                    }
                }

                TextView phase = child1.findViewById(R.id.Phase);
                phase.setText("ABC");

                JsonObject kwObject = list.get(i).getActualKWObject();
                JsonObject pfObject = list.get(i).getActualPfObject();
                JsonObject kVaObject = list.get(i).getConnKVAObject();
                JsonObject custObject = list.get(i).getCustNoObject();

                EditText actualKw = child1.findViewById(R.id.actualKva);
                actualKw.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "7")));

                //Actual PF
                EditText actualPf = child1.findViewById(R.id.actualpf);
                actualPf.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "7")));

                EditText connectedKva = child1.findViewById(R.id.connectedKva);
                connectedKva.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "7")));

                //Kwh
                EditText kwh = child1.findViewById(R.id.KWh);

                EditText custCount = child1.findViewById(R.id.custCount);
                custCount.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "7")));

            }
        } else {
            if (phaseValue.equalsIgnoreCase("1")) {
                for (int i = 0; i < list.size(); i++) {
                    @SuppressLint("InflateParams")
                    View byPhaseALayout = layoutInflater.inflate(R.layout.by_phase_a_layout, null);
                    binding.dynamicLayout.addView(byPhaseALayout);
                    viewList.add(byPhaseALayout);

                    EditText editText = byPhaseALayout.findViewById(R.id.customerNumber);
                    editText.setText(list.get(i).getCustomerNumber());

                    Spinner spinner = byPhaseALayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                        if (list.get(i).getCustomerType() != null && !list.get(i).getCustomerType().isEmpty()) {
                            String item = list.get(i).getCustomerType();
                            int spinnerPosition = locationAdapters.getPosition(item);
                            if (spinnerPosition != -1) {
                                spinner.setSelection(spinnerPosition);
                            }
                        }
                    }

                    Spinner statusSpinner = byPhaseALayout.findViewById(R.id.statusSpinnerBar);
                    int savedStatus = list.get(i).getStatus();
                    String targetStatus = (savedStatus == 0) ? "Connected" : "DisConnected";
                    for (int j = 0; j < statusSpinner.getCount(); j++) {
                        if (statusSpinner.getItemAtPosition(j).toString().equalsIgnoreCase(targetStatus)) {
                            statusSpinner.setSelection(j);
                            break;
                        }
                    }

                    TextView phase = byPhaseALayout.findViewById(R.id.Phase);
                    phase.setText("A");

                    JsonObject kwObject = list.get(i).getActualKWObject();
                    JsonObject pfObject = list.get(i).getActualPfObject();
                    JsonObject kVaObject = list.get(i).getConnKVAObject();
                    JsonObject custObject = list.get(i).getCustNoObject();

                    EditText actualKw = byPhaseALayout.findViewById(R.id.actualKva);
                    actualKw.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "1")));

                    //Actual PF
                    EditText actualPf = byPhaseALayout.findViewById(R.id.aActualPf);
                    actualPf.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "1")));

                    EditText connectedKva = byPhaseALayout.findViewById(R.id.connectedKva);
                    connectedKva.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "1")));

                    EditText custCount = byPhaseALayout.findViewById(R.id.customerCount);
                    custCount.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "1")));

                }
            } else if (phaseValue.equalsIgnoreCase("2")) {
                for (int i = 0; i < list.size(); i++) {
                    @SuppressLint("InflateParams")
                    View byPhaseBLayout = layoutInflater.inflate(R.layout.by_phase_b_layout, null);
                    binding.dynamicLayout.addView(byPhaseBLayout);
                    viewList.add(byPhaseBLayout);

                    EditText editText = byPhaseBLayout.findViewById(R.id.customerNumber);
                    editText.setText(list.get(i).getCustomerNumber());

                    Spinner spinner = byPhaseBLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                        if (list.get(i).getCustomerType() != null && !list.get(i).getCustomerType().isEmpty()) {
                            String item = list.get(i).getCustomerType();
                            int spinnerPosition = locationAdapters.getPosition(item);
                            if (spinnerPosition != -1) {
                                spinner.setSelection(spinnerPosition);
                            }
                        }
                    }

                    Spinner statusSpinner = byPhaseBLayout.findViewById(R.id.statusSpinnerBar);
                    int savedStatus = list.get(i).getStatus();
                    String targetStatus = (savedStatus == 0) ? "Connected" : "DisConnected";
                    for (int j = 0; j < statusSpinner.getCount(); j++) {
                        if (statusSpinner.getItemAtPosition(j).toString().equalsIgnoreCase(targetStatus)) {
                            statusSpinner.setSelection(j);
                            break;
                        }
                    }

                    TextView phase = byPhaseBLayout.findViewById(R.id.bPhase);
                    phase.setText("B");

                    JsonObject kwObject = list.get(i).getActualKWObject();
                    JsonObject pfObject = list.get(i).getActualPfObject();
                    JsonObject kVaObject = list.get(i).getConnKVAObject();
                    JsonObject custObject = list.get(i).getCustNoObject();

                    EditText actualKw = byPhaseBLayout.findViewById(R.id.actualBKva);
                    actualKw.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "2")));

                    //Actual PF
                    EditText actualPf = byPhaseBLayout.findViewById(R.id.actualBPF);
                    actualPf.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "2")));

                    EditText connectedKva = byPhaseBLayout.findViewById(R.id.connectedBKva);
                    connectedKva.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "2")));

                    EditText custCount = byPhaseBLayout.findViewById(R.id.customerBCount);
                    custCount.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "2")));

                }

            } else if (phaseValue.equalsIgnoreCase("3")) {
                for (int i = 0; i < list.size(); i++) {
                    @SuppressLint("InflateParams")
                    View byPhaseCLayout = layoutInflater.inflate(R.layout.by_phase_c_layout, null);
                    binding.dynamicLayout.addView(byPhaseCLayout);
                    viewList.add(byPhaseCLayout);

                    EditText editText = byPhaseCLayout.findViewById(R.id.customerNumber);
                    editText.setText(list.get(i).getCustomerNumber());

                    Spinner spinner = byPhaseCLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                        if (list.get(i).getCustomerType() != null && !list.get(i).getCustomerType().isEmpty()) {
                            String item = list.get(i).getCustomerType();
                            int spinnerPosition = locationAdapters.getPosition(item);
                            if (spinnerPosition != -1) {
                                spinner.setSelection(spinnerPosition);
                            }
                        }
                    }

                    Spinner statusSpinner = byPhaseCLayout.findViewById(R.id.statusSpinnerBar);
                    int savedStatus = list.get(i).getStatus();
                    String targetStatus = (savedStatus == 0) ? "Connected" : "DisConnected";
                    for (int j = 0; j < statusSpinner.getCount(); j++) {
                        if (statusSpinner.getItemAtPosition(j).toString().equalsIgnoreCase(targetStatus)) {
                            statusSpinner.setSelection(j);
                            break;
                        }
                    }

                    TextView phase = byPhaseCLayout.findViewById(R.id.cPhase);
                    phase.setText("C");

                    JsonObject kwObject = list.get(i).getActualKWObject();
                    JsonObject pfObject = list.get(i).getActualPfObject();
                    JsonObject kVaObject = list.get(i).getConnKVAObject();
                    JsonObject custObject = list.get(i).getCustNoObject();

                    EditText actualKw = byPhaseCLayout.findViewById(R.id.actualCKva);
                    actualKw.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "3")));

                    //Actual PF
                    EditText actualPf = byPhaseCLayout.findViewById(R.id.actualCPf);
                    actualPf.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "3")));

                    EditText connectedKva = byPhaseCLayout.findViewById(R.id.connectedCKva);
                    connectedKva.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "3")));

                    EditText custCount = byPhaseCLayout.findViewById(R.id.customerCCount);
                    custCount.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "3")));

                }

            } else if (phaseValue.equalsIgnoreCase("4")) {
                for (int i = 0; i < list.size(); i++) {
                    @SuppressLint("InflateParams")
                    View byPhaseABLayout = layoutInflater.inflate(R.layout.by_phase_ab_layout, null);
                    binding.dynamicLayout.addView(byPhaseABLayout);
                    viewList.add(byPhaseABLayout);

                    EditText editText = byPhaseABLayout.findViewById(R.id.customerNumber);
                    editText.setText(list.get(i).getCustomerNumber());

                    Spinner spinner = byPhaseABLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                        if (list.get(i).getCustomerType() != null && !list.get(i).getCustomerType().isEmpty()) {
                            String item = list.get(i).getCustomerType();
                            int spinnerPosition = locationAdapters.getPosition(item);
                            if (spinnerPosition != -1) {
                                spinner.setSelection(spinnerPosition);
                            }
                        }
                    }

                    Spinner statusSpinner = byPhaseABLayout.findViewById(R.id.statusSpinnerBar);
                    int savedStatus = list.get(i).getStatus();
                    String targetStatus = (savedStatus == 0) ? "Connected" : "DisConnected";
                    for (int j = 0; j < statusSpinner.getCount(); j++) {
                        if (statusSpinner.getItemAtPosition(j).toString().equalsIgnoreCase(targetStatus)) {
                            statusSpinner.setSelection(j);
                            break;
                        }
                    }

                    TextView phaseA = byPhaseABLayout.findViewById(R.id.aPhase);
                    TextView phaseB = byPhaseABLayout.findViewById(R.id.bPhase);
                    phaseA.setText("A");
                    phaseB.setText("B");

                    JsonObject kwObject = list.get(i).getActualKWObject();
                    JsonObject pfObject = list.get(i).getActualPfObject();
                    JsonObject kVaObject = list.get(i).getConnKVAObject();
                    JsonObject custObject = list.get(i).getCustNoObject();

                    EditText actualKwA = byPhaseABLayout.findViewById(R.id.actualAKva);
                    EditText actualKwB = byPhaseABLayout.findViewById(R.id.actualBKva);
                    actualKwA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "1")));
                    actualKwB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "2")));

                    //Actual PF
                    EditText actualPfA = byPhaseABLayout.findViewById(R.id.actualAPf);
                    EditText actualPfB = byPhaseABLayout.findViewById(R.id.actualBPf);
                    actualPfA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "1")));
                    actualPfB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "2")));

                    EditText connectedKvaA = byPhaseABLayout.findViewById(R.id.connectedAKva);
                    EditText connectedKvaB = byPhaseABLayout.findViewById(R.id.connectedBKva);
                    connectedKvaA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "1")));
                    connectedKvaB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "2")));

                    EditText custCountA = byPhaseABLayout.findViewById(R.id.customerACount);
                    EditText custCountB = byPhaseABLayout.findViewById(R.id.customerBCount);
                    custCountA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "1")));
                    custCountB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "2")));

                }

            } else if (phaseValue.equalsIgnoreCase("5")) {
                for (int i = 0; i < list.size(); i++) {
                    @SuppressLint("InflateParams")
                    View byPhaseACLayout = layoutInflater.inflate(R.layout.by_phase_ac_layout, null);
                    binding.dynamicLayout.addView(byPhaseACLayout);
                    viewList.add(byPhaseACLayout);

                    EditText editText = byPhaseACLayout.findViewById(R.id.customerNumber);
                    editText.setText(list.get(i).getCustomerNumber());

                    Spinner spinner = byPhaseACLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                        if (list.get(i).getCustomerType() != null && !list.get(i).getCustomerType().isEmpty()) {
                            String item = list.get(i).getCustomerType();
                            int spinnerPosition = locationAdapters.getPosition(item);
                            if (spinnerPosition != -1) {
                                spinner.setSelection(spinnerPosition);
                            }
                        }
                    }

                    Spinner statusSpinner = byPhaseACLayout.findViewById(R.id.statusSpinnerBar);
                    int savedStatus = list.get(i).getStatus();
                    String targetStatus = (savedStatus == 0) ? "Connected" : "DisConnected";
                    for (int j = 0; j < statusSpinner.getCount(); j++) {
                        if (statusSpinner.getItemAtPosition(j).toString().equalsIgnoreCase(targetStatus)) {
                            statusSpinner.setSelection(j);
                            break;
                        }
                    }

                    TextView phaseA = byPhaseACLayout.findViewById(R.id.aPhase);
                    TextView phaseC = byPhaseACLayout.findViewById(R.id.bPhase);
                    phaseA.setText("A");
                    phaseC.setText("C");

                    JsonObject kwObject = list.get(i).getActualKWObject();
                    JsonObject pfObject = list.get(i).getActualPfObject();
                    JsonObject kVaObject = list.get(i).getConnKVAObject();
                    JsonObject custObject = list.get(i).getCustNoObject();

                    EditText actualKwA = byPhaseACLayout.findViewById(R.id.actualAKva);
                    EditText actualKwC = byPhaseACLayout.findViewById(R.id.actualCKva);
                    actualKwA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "1")));
                    actualKwC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "3")));

                    //Actual PF
                    EditText actualPfA = byPhaseACLayout.findViewById(R.id.actualAPf);
                    EditText actualPfC = byPhaseACLayout.findViewById(R.id.actualCPf);
                    actualPfA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "1")));
                    actualPfC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "3")));

                    EditText connectedKvaA = byPhaseACLayout.findViewById(R.id.connectedAKva);
                    EditText connectedKvaC = byPhaseACLayout.findViewById(R.id.connectedCKva);
                    connectedKvaA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "1")));
                    connectedKvaC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "3")));

                    EditText custCountA = byPhaseACLayout.findViewById(R.id.customerACount);
                    EditText custCountC = byPhaseACLayout.findViewById(R.id.customerCCount);
                    custCountA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "1")));
                    custCountC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "3")));
                }
            } else if (phaseValue.equalsIgnoreCase("6")) {
                for (int i = 0; i < list.size(); i++) {
                    @SuppressLint("InflateParams")
                    View byPhaseBCLayout = layoutInflater.inflate(R.layout.by_phase_bc_layout, null);
                    binding.dynamicLayout.addView(byPhaseBCLayout);
                    viewList.add(byPhaseBCLayout);

                    EditText editText = byPhaseBCLayout.findViewById(R.id.customerNumber);
                    editText.setText(list.get(i).getCustomerNumber());

                    Spinner spinner = byPhaseBCLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                        if (list.get(i).getCustomerType() != null && !list.get(i).getCustomerType().isEmpty()) {
                            String item = list.get(i).getCustomerType();
                            int spinnerPosition = locationAdapters.getPosition(item);
                            if (spinnerPosition != -1) {
                                spinner.setSelection(spinnerPosition);
                            }
                        }
                    }

                    Spinner statusSpinner = byPhaseBCLayout.findViewById(R.id.statusSpinnerBar);
                    int savedStatus = list.get(i).getStatus();
                    String targetStatus = (savedStatus == 0) ? "Connected" : "DisConnected";
                    for (int j = 0; j < statusSpinner.getCount(); j++) {
                        if (statusSpinner.getItemAtPosition(j).toString().equalsIgnoreCase(targetStatus)) {
                            statusSpinner.setSelection(j);
                            break;
                        }
                    }

                    TextView phaseB = byPhaseBCLayout.findViewById(R.id.bPhase);
                    TextView phaseC = byPhaseBCLayout.findViewById(R.id.cPhase);
                    phaseB.setText("B");
                    phaseC.setText("C");

                    JsonObject kwObject = list.get(i).getActualKWObject();
                    JsonObject pfObject = list.get(i).getActualPfObject();
                    JsonObject kVaObject = list.get(i).getConnKVAObject();
                    JsonObject custObject = list.get(i).getCustNoObject();

                    EditText actualKwB = byPhaseBCLayout.findViewById(R.id.actualBKva);
                    EditText actualKwC = byPhaseBCLayout.findViewById(R.id.actualCKva);
                    actualKwB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "2")));
                    actualKwC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "3")));

                    //Actual PF
                    EditText actualPfB = byPhaseBCLayout.findViewById(R.id.actualBPf);
                    EditText actualPfC = byPhaseBCLayout.findViewById(R.id.actualCPf);
                    actualPfB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "2")));
                    actualPfC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "3")));

                    EditText connectedKvaB = byPhaseBCLayout.findViewById(R.id.connectedBKva);
                    EditText connectedKvaC = byPhaseBCLayout.findViewById(R.id.connectedCKva);
                    connectedKvaB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "2")));
                    connectedKvaC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "3")));

                    EditText custCountB = byPhaseBCLayout.findViewById(R.id.customerBCount);
                    EditText custCountC = byPhaseBCLayout.findViewById(R.id.customerCCount);
                    custCountB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "2")));
                    custCountC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "3")));
                }


            } else {
                for (int i = 0; i < list.size(); i++) {
                    @SuppressLint("InflateParams")
                    View byPhaseABCLayout = layoutInflater.inflate(R.layout.by_phase_abc_layout, null);
                    binding.dynamicLayout.addView(byPhaseABCLayout);
                    viewList.add(byPhaseABCLayout);

                    EditText editText = byPhaseABCLayout.findViewById(R.id.customerNumber);
                    editText.setText(list.get(i).getCustomerNumber());

                   /* Spinner spinner = byPhaseABCLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                    }*/

                    Spinner spinner = byPhaseABCLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                        if (list.get(i).getCustomerType() != null && !list.get(i).getCustomerType().isEmpty()) {
                            String item = list.get(i).getCustomerType();
                            int spinnerPosition = locationAdapters.getPosition(item);
                            if (spinnerPosition != -1) {
                                spinner.setSelection(spinnerPosition);
                            }
                        }
                    }

                    Spinner statusSpinner = byPhaseABCLayout.findViewById(R.id.statusSpinnerBar);
                    int savedStatus = list.get(i).getStatus();
                    String targetStatus = (savedStatus == 0) ? "Connected" : "DisConnected";
                    for (int j = 0; j < statusSpinner.getCount(); j++) {
                        if (statusSpinner.getItemAtPosition(j).toString().equalsIgnoreCase(targetStatus)) {
                            statusSpinner.setSelection(j);
                            break;
                        }
                    }

                    TextView phaseA = byPhaseABCLayout.findViewById(R.id.aPhase);
                    TextView phaseB = byPhaseABCLayout.findViewById(R.id.bPhase);
                    TextView phaseC = byPhaseABCLayout.findViewById(R.id.cPhase);
                    phaseA.setText("A");
                    phaseB.setText("B");
                    phaseC.setText("C");

                    JsonObject kwObject = list.get(i).getActualKWObject();
                    JsonObject pfObject = list.get(i).getActualPfObject();
                    JsonObject kVaObject = list.get(i).getConnKVAObject();
                    JsonObject custObject = list.get(i).getCustNoObject();

                    EditText actualKwA = byPhaseABCLayout.findViewById(R.id.actualAKva);
                    EditText actualKwB = byPhaseABCLayout.findViewById(R.id.actualBKva);
                    EditText actualKwC = byPhaseABCLayout.findViewById(R.id.actualCKva);
                    actualKwA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "1")));
                    actualKwB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "2")));
                    actualKwC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kwObject, "3")));

                    //Actual PF
                    EditText actualPfA = byPhaseABCLayout.findViewById(R.id.actualAPf);
                    EditText actualPfB = byPhaseABCLayout.findViewById(R.id.actualBPf);
                    EditText actualPfC = byPhaseABCLayout.findViewById(R.id.actualCPf);
                    actualPfA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "1")));
                    actualPfB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "2")));
                    actualPfC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(pfObject, "3")));

                    EditText connectedKvaA = byPhaseABCLayout.findViewById(R.id.connectedAKva);
                    EditText connectedKvaB = byPhaseABCLayout.findViewById(R.id.connectedBKva);
                    EditText connectedKvaC = byPhaseABCLayout.findViewById(R.id.connectedCKva);
                    connectedKvaA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "1")));
                    connectedKvaB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "2")));
                    connectedKvaC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(kVaObject, "3")));

                    EditText custCountA = byPhaseABCLayout.findViewById(R.id.customerACount);
                    EditText custCountB = byPhaseABCLayout.findViewById(R.id.customerBCount);
                    EditText custCountC = byPhaseABCLayout.findViewById(R.id.customerCCount);
                    custCountA.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "1")));
                    custCountB.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "2")));
                    custCountC.setText(String.valueOf(ResponseDataUtils.getSafeDouble(custObject, "3")));

                }

            }

        }

    }

    private void checkDetails() {
        if (phaseType.equals("ThreePhase")) {
            try {
                List<CustomerData> customerList = new ArrayList<>();
                String customerNumber = "";
                String customerType = "";
                int status = 0;
                int viewSize = viewList.size();
                for (int i = 0; i < viewSize; i++) {

                    JsonObject jsonObject = new JsonObject();

                    EditText custNumber = viewList.get(i).findViewById(R.id.customerNumber);
                    if (!custNumber.getText().toString().isEmpty()) {
                        jsonObject.addProperty("CustomerNumber", custNumber.getText().toString().trim());
                        customerNumber = custNumber.getText().toString().trim();
                    }

                    Spinner custSpinner = viewList.get(i).findViewById(R.id.cust_type_spinnerBar);
                    if (!custSpinner.getSelectedItem().toString().isEmpty()) {
                        jsonObject.addProperty("CustomerType", custSpinner.getSelectedItem().toString().trim());
                        customerType = custSpinner.getSelectedItem().toString().trim();
                    }

                    Spinner statusSpinner = viewList.get(i).findViewById(R.id.statusSpinnerBar);
                    if (!statusSpinner.getSelectedItem().toString().isEmpty()) {
                        if (statusSpinner.getSelectedItem().equals("Connected")) {
                            jsonObject.addProperty("Status", "0");
                            status = 0;
                        } else {
                            jsonObject.addProperty("Status", "1");
                            status = 1;
                        }
                    }

                    TextView phase = viewList.get(i).findViewById(R.id.Phase);
                    JsonObject phaseObject = new JsonObject();
                    if (!phase.getText().toString().isEmpty()) {
                        phaseObject.addProperty("1", "0");
                        phaseObject.addProperty("2", "0");
                        phaseObject.addProperty("3", "0");
                        phaseObject.addProperty("7", "7");
                        jsonObject.add("Phase", phaseObject);
                    }

                    EditText actualKw = viewList.get(i).findViewById(R.id.actualKva);
                    JsonObject actualKwObject = new JsonObject();
                    if (!actualKw.getText().toString().isEmpty()) {
                        actualKwObject.addProperty("1", "0");
                        actualKwObject.addProperty("2", "0");
                        actualKwObject.addProperty("3", "0");
                        actualKwObject.addProperty("7", actualKw.getText().toString().trim());
                        jsonObject.add("ActualKW", actualKwObject);
                    } else {
                        actualKwObject.addProperty("1", "0");
                        actualKwObject.addProperty("2", "0");
                        actualKwObject.addProperty("3", "0");
                        actualKwObject.addProperty("7", "0.0");
                        jsonObject.add("ActualKW", actualKwObject);
                    }

                    //Actual PF
                    EditText actualPf = viewList.get(i).findViewById(R.id.actualpf);
                    JsonObject actualPfObject = new JsonObject();
                    if (!actualPf.getText().toString().isEmpty()) {
                        actualPfObject.addProperty("1", "0");
                        actualPfObject.addProperty("2", "0");
                        actualPfObject.addProperty("3", "0");
                        actualPfObject.addProperty("7", actualPf.getText().toString().trim());
                        jsonObject.add("ActualPF", actualPfObject);
                    } else {
                        actualPfObject.addProperty("1", "0");
                        actualPfObject.addProperty("2", "0");
                        actualPfObject.addProperty("3", "0");
                        actualPfObject.addProperty("7", "0.0");
                        jsonObject.add("ActualPF", actualPfObject);
                    }

                    EditText connectedKva = viewList.get(i).findViewById(R.id.connectedKva);
                    JsonObject connectedKvaObject = new JsonObject();
                    if (!connectedKva.getText().toString().isEmpty()) {
                        connectedKvaObject.addProperty("1", "0");
                        connectedKvaObject.addProperty("2", "0");
                        connectedKvaObject.addProperty("3", "0");
                        connectedKvaObject.addProperty("7", connectedKva.getText().toString().trim());
                        jsonObject.add("ConnectedKVA", connectedKvaObject);
                    } else {
                        connectedKvaObject.addProperty("1", "0");
                        connectedKvaObject.addProperty("2", "0");
                        connectedKvaObject.addProperty("3", "0");
                        connectedKvaObject.addProperty("7", "0.0");
                        jsonObject.add("ConnectedKVA", connectedKvaObject);
                    }

                    //Kwh
                    EditText kwh = viewList.get(i).findViewById(R.id.KWh);
                    JsonObject kwhObject = new JsonObject();
                    if (!kwh.getText().toString().isEmpty()) {
                        kwhObject.addProperty("1", "0");
                        kwhObject.addProperty("2", "0");
                        kwhObject.addProperty("3", "0");
                        kwhObject.addProperty("7", kwh.getText().toString().trim());
                        jsonObject.add("kWh", kwhObject);
                    } else {
                        kwhObject.addProperty("1", "0");
                        kwhObject.addProperty("2", "0");
                        kwhObject.addProperty("3", "0");
                        kwhObject.addProperty("7", "0.0");
                        jsonObject.add("kWh", kwhObject);
                    }

                    EditText custCount = viewList.get(i).findViewById(R.id.custCount);
                    JsonObject customerCountObject = new JsonObject();
                    if (!custCount.getText().toString().isEmpty()) {
                        customerCountObject.addProperty("1", "0");
                        customerCountObject.addProperty("2", "0");
                        customerCountObject.addProperty("3", "0");
                        customerCountObject.addProperty("7", custCount.getText().toString().trim());
                        jsonObject.add("CustomerCount", customerCountObject);
                    } else {
                        customerCountObject.addProperty("1", "0");
                        customerCountObject.addProperty("2", "0");
                        customerCountObject.addProperty("13", "0");
                        customerCountObject.addProperty("7", "0.0");
                        jsonObject.add("CustomerCount", customerCountObject);
                    }

                    ListDataManager.sendData(jsonObject);
                    CustomerData customerData = new CustomerData(phaseType, Integer.parseInt(phaseValue), customerNumber, customerType, status, phaseObject, actualKwObject, actualPfObject, connectedKvaObject, customerCountObject);
                    customerList.add(customerData);
                }

                if (!customerList.isEmpty()) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            appDatabase.customerDataDao().insertAll(customerList);
                            Log.d("Database", "Successfully inserted " + customerList.size() + " customers");
                            dismiss();
                        } catch (Exception e) {
                            dismiss();
                            Log.e("DatabaseError", "Bulk insert failed", e);
                        }
                    });
                } else {
                    dismiss();
                }

            } catch (Exception e) {
                e.getLocalizedMessage();
            }
        } else {
            if (phaseValue.equals("1")) {
                try {
                    List<CustomerData> customerList = new ArrayList<>();
                    String customerNumber = "";
                    String customerType = "";
                    int status = 0;
                    int viewSize = viewList.size();
                    for (int i = 0; i < viewSize; i++) {
                        JsonObject jsonObject = new JsonObject();
                        EditText custNumber = viewList.get(i).findViewById(R.id.customerNumber);
                        if (!custNumber.getText().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerNumber", custNumber.getText().toString().trim());
                            customerNumber = custNumber.getText().toString().trim();
                        }

                        Spinner custSpinner = viewList.get(i).findViewById(R.id.cust_type_spinnerBar);
                        if (!custSpinner.getSelectedItem().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerType", custSpinner.getSelectedItem().toString().trim());
                            customerType = custSpinner.getSelectedItem().toString().trim();
                        }

                        Spinner statusSpinner = viewList.get(i).findViewById(R.id.statusSpinnerBar);
                        if (!statusSpinner.getSelectedItem().toString().isEmpty()) {
                            if (statusSpinner.getSelectedItem().equals("Connected")) {
                                jsonObject.addProperty("Status", "0");
                                status = 0;
                            } else {
                                jsonObject.addProperty("Status", "1");
                                status = 1;
                            }
                        }

                        TextView phase = viewList.get(i).findViewById(R.id.Phase);
                        JsonObject phaseObject = new JsonObject();
                        if (!phase.getText().toString().isEmpty()) {
                            phaseObject.addProperty("1", "1");
                            phaseObject.addProperty("2", "0");
                            phaseObject.addProperty("3", "0");
                            phaseObject.addProperty("7", "0");
                            jsonObject.add("Phase", phaseObject);
                        }

                        EditText actualKw = viewList.get(i).findViewById(R.id.actualKva);
                        JsonObject actualKwObject = new JsonObject();
                        if (!actualKw.getText().toString().isEmpty()) {
                            actualKwObject.addProperty("1", actualKw.getText().toString().trim());
                            actualKwObject.addProperty("2", "0");
                            actualKwObject.addProperty("3", "0");
                            actualKwObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualKwObject);
                        } else {
                            actualKwObject.addProperty("1", "0.0");
                            actualKwObject.addProperty("2", "0");
                            actualKwObject.addProperty("3", "0");
                            actualKwObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualKwObject);
                        }

                        EditText actualPf = viewList.get(i).findViewById(R.id.aActualPf);
                        JsonObject actualPfObject = new JsonObject();
                        if (!actualPf.getText().toString().isEmpty()) {
                            actualPfObject.addProperty("1", actualPf.getText().toString().trim());
                            actualPfObject.addProperty("2", "0");
                            actualPfObject.addProperty("3", "0");
                            actualPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualPfObject);
                        } else {
                            actualPfObject.addProperty("1", "0.0");
                            actualPfObject.addProperty("2", "0");
                            actualPfObject.addProperty("3", "0");
                            actualPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualPfObject);
                        }

                        EditText connectedKva = viewList.get(i).findViewById(R.id.connectedKva);
                        JsonObject connectedKvaObject = new JsonObject();
                        if (!connectedKva.getText().toString().isEmpty()) {
                            connectedKvaObject.addProperty("1", connectedKva.getText().toString().trim());
                            connectedKvaObject.addProperty("2", "0");
                            connectedKvaObject.addProperty("3", "0");
                            connectedKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedKvaObject);
                        } else {
                            connectedKvaObject.addProperty("1", "0.0");
                            connectedKvaObject.addProperty("2", "0");
                            connectedKvaObject.addProperty("3", "0");
                            connectedKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedKvaObject);
                        }

                        EditText Kwh = viewList.get(i).findViewById(R.id.aKwh);
                        JsonObject KwhObject = new JsonObject();
                        if (!Kwh.getText().toString().isEmpty()) {
                            KwhObject.addProperty("1", Kwh.getText().toString().trim());
                            KwhObject.addProperty("2", "0");
                            KwhObject.addProperty("3", "0");
                            KwhObject.addProperty("7", "0");
                            jsonObject.add("kWh", KwhObject);
                        } else {
                            KwhObject.addProperty("1", "0.0");
                            KwhObject.addProperty("2", "0");
                            KwhObject.addProperty("3", "0");
                            KwhObject.addProperty("7", "0");
                            jsonObject.add("kWh", KwhObject);
                        }

                        EditText custCount = viewList.get(i).findViewById(R.id.customerCount);
                        JsonObject custCountObject = new JsonObject();
                        if (!custCount.getText().toString().isEmpty()) {
                            custCountObject.addProperty("1", custCount.getText().toString().trim());
                            custCountObject.addProperty("2", "0");
                            custCountObject.addProperty("3", "0");
                            custCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custCountObject);
                        } else {
                            custCountObject.addProperty("1", "0.0");
                            custCountObject.addProperty("2", "0");
                            custCountObject.addProperty("13", "0");
                            custCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custCountObject);
                        }
                        ListDataManager.sendData(jsonObject);
                        CustomerData customerData = new CustomerData(phaseType, Integer.parseInt(phaseValue), customerNumber, customerType, status, phaseObject, actualKwObject, actualPfObject, connectedKvaObject, custCountObject);
                        customerList.add(customerData);
                    }

                    if (!customerList.isEmpty()) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                appDatabase.customerDataDao().insertAll(customerList);
                                Log.d("Database", "Successfully inserted " + customerList.size() + " customers");
                                dismiss();
                            } catch (Exception e) {
                                dismiss();
                                Log.e("DatabaseError", "Bulk insert failed", e);
                            }
                        });
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    e.getLocalizedMessage();
                    dismiss();
                }
            } else if (phaseValue.equals("2")) {
                try {
                    List<CustomerData> customerList = new ArrayList<>();
                    String customerNumber = "";
                    String customerType = "";
                    int status = 0;
                    int viewSize = viewList.size();
                    for (int i = 0; i < viewSize; i++) {
                        JsonObject jsonObject = new JsonObject();
                        EditText custNumber = viewList.get(i).findViewById(R.id.customerNumber);
                        if (!custNumber.getText().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerNumber", custNumber.getText().toString().trim());
                            customerNumber = custNumber.getText().toString().trim();
                        }

                        Spinner custSpinner = viewList.get(i).findViewById(R.id.cust_type_spinnerBar);
                        if (!custSpinner.getSelectedItem().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerType", custSpinner.getSelectedItem().toString().trim());
                            customerType = custSpinner.getSelectedItem().toString().trim();
                        }

                        Spinner statusSpinner = viewList.get(i).findViewById(R.id.statusSpinnerBar);
                        if (!statusSpinner.getSelectedItem().toString().isEmpty()) {
                            if (statusSpinner.getSelectedItem().equals("Connected")) {
                                jsonObject.addProperty("Status", "0");
                                status = 0;
                            } else {
                                jsonObject.addProperty("Status", "1");
                                status = 1;
                            }
                        }

                        TextView phase = viewList.get(i).findViewById(R.id.bPhase);
                        JsonObject phaseObject = new JsonObject();
                        if (!phase.getText().toString().isEmpty()) {
                            phaseObject.addProperty("1", "0");
                            phaseObject.addProperty("2", "1");
                            phaseObject.addProperty("3", "0");
                            phaseObject.addProperty("7", "0");
                            jsonObject.add("Phase", phaseObject);
                        }

                        EditText actualKw = viewList.get(i).findViewById(R.id.actualBKva);
                        JsonObject actualKwObject = new JsonObject();
                        if (!actualKw.getText().toString().isEmpty()) {
                            actualKwObject.addProperty("1", "0");
                            actualKwObject.addProperty("2", actualKw.getText().toString().trim());
                            actualKwObject.addProperty("3", "0");
                            actualKwObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualKwObject);
                        } else {
                            actualKwObject.addProperty("1", "0");
                            actualKwObject.addProperty("2", "0.0");
                            actualKwObject.addProperty("3", "0");
                            actualKwObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualKwObject);
                        }

                        EditText actualPf = viewList.get(i).findViewById(R.id.actualBPF);
                        JsonObject actualPfObject = new JsonObject();
                        if (!actualPf.getText().toString().isEmpty()) {
                            actualPfObject.addProperty("1", "0");
                            actualPfObject.addProperty("2", actualPf.getText().toString().trim());
                            actualPfObject.addProperty("3", "0");
                            actualPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualPfObject);
                        } else {
                            actualPfObject.addProperty("1", "0");
                            actualPfObject.addProperty("2", "0.0");
                            actualPfObject.addProperty("3", "0");
                            actualPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualPfObject);
                        }

                        EditText connectedKva = viewList.get(i).findViewById(R.id.connectedBKva);
                        JsonObject connectedKvaObject = new JsonObject();
                        if (!connectedKva.getText().toString().isEmpty()) {
                            connectedKvaObject.addProperty("1", "0");
                            connectedKvaObject.addProperty("2", connectedKva.getText().toString().trim());
                            connectedKvaObject.addProperty("3", "0");
                            connectedKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedKvaObject);
                        } else {
                            connectedKvaObject.addProperty("1", "0");
                            connectedKvaObject.addProperty("2", "0.0");
                            connectedKvaObject.addProperty("3", "0");
                            connectedKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedKvaObject);
                        }

                        EditText kWh = viewList.get(i).findViewById(R.id.BkWh);
                        JsonObject kWhObject = new JsonObject();
                        if (!kWh.getText().toString().isEmpty()) {
                            kWhObject.addProperty("1", "0");
                            kWhObject.addProperty("2", kWh.getText().toString().trim());
                            kWhObject.addProperty("3", "0");
                            kWhObject.addProperty("7", "0");
                            jsonObject.add("kWh", kWhObject);
                        } else {
                            kWhObject.addProperty("1", "0");
                            kWhObject.addProperty("2", "0.0");
                            kWhObject.addProperty("3", "0");
                            kWhObject.addProperty("7", "0");
                            jsonObject.add("kWh", kWhObject);
                        }

                        EditText custCount = viewList.get(i).findViewById(R.id.customerBCount);
                        JsonObject custCountObject = new JsonObject();
                        if (!custCount.getText().toString().isEmpty()) {
                            custCountObject.addProperty("1", "0");
                            custCountObject.addProperty("2", custCount.getText().toString().trim());
                            custCountObject.addProperty("3", "0");
                            custCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custCountObject);
                        } else {
                            custCountObject.addProperty("1", "0");
                            custCountObject.addProperty("2", "0.0");
                            custCountObject.addProperty("13", "0");
                            custCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custCountObject);
                        }
                        ListDataManager.sendData(jsonObject);
                        CustomerData customerData = new CustomerData(phaseType, Integer.parseInt(phaseValue), customerNumber, customerType, status, phaseObject, actualKwObject, actualPfObject, connectedKvaObject, custCountObject);
                        customerList.add(customerData);
                    }

                    if (!customerList.isEmpty()) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                appDatabase.customerDataDao().insertAll(customerList);
                                Log.d("Database", "Successfully inserted " + customerList.size() + " customers");
                                dismiss();
                            } catch (Exception e) {
                                dismiss();
                                Log.e("DatabaseError", "Bulk insert failed", e);
                            }
                        });
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    e.getLocalizedMessage();
                    dismiss();
                }
            } else if (phaseValue.equals("3")) {
                try {
                    List<CustomerData> customerList = new ArrayList<>();
                    String customerNumber = "";
                    String customerType = "";
                    int status = 0;
                    int viewSize = viewList.size();
                    for (int i = 0; i < viewSize; i++) {
                        JsonObject jsonObject = new JsonObject();
                        EditText custNumber = viewList.get(i).findViewById(R.id.customerNumber);
                        if (!custNumber.getText().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerNumber", custNumber.getText().toString().trim());
                            customerNumber = custNumber.getText().toString().trim();
                        }

                        Spinner custSpinner = viewList.get(i).findViewById(R.id.cust_type_spinnerBar);
                        if (!custSpinner.getSelectedItem().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerType", custSpinner.getSelectedItem().toString().trim());
                            customerType = custSpinner.getSelectedItem().toString().trim();
                        }

                        Spinner statusSpinner = viewList.get(i).findViewById(R.id.statusSpinnerBar);
                        if (!statusSpinner.getSelectedItem().toString().isEmpty()) {
                            if (statusSpinner.getSelectedItem().equals("Connected")) {
                                jsonObject.addProperty("Status", "0");
                                status = 0;
                            } else {
                                jsonObject.addProperty("Status", "1");
                                status = 1;
                            }
                        }

                        TextView phase = viewList.get(i).findViewById(R.id.cPhase);
                        JsonObject phaseObject = new JsonObject();
                        if (!phase.getText().toString().isEmpty()) {
                            phaseObject.addProperty("1", "0");
                            phaseObject.addProperty("2", "0");
                            phaseObject.addProperty("3", "1");
                            phaseObject.addProperty("7", "0");
                            jsonObject.add("Phase", phaseObject);
                        }

                        EditText actualKw = viewList.get(i).findViewById(R.id.actualCKva);
                        JsonObject actualKwObject = new JsonObject();
                        if (!actualKw.getText().toString().isEmpty()) {
                            actualKwObject.addProperty("1", "0");
                            actualKwObject.addProperty("2", "0");
                            actualKwObject.addProperty("3", actualKw.getText().toString().trim());
                            actualKwObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualKwObject);
                        } else {
                            actualKwObject.addProperty("1", "0");
                            actualKwObject.addProperty("2", "0");
                            actualKwObject.addProperty("3", "0.0");
                            actualKwObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualKwObject);
                        }

                        EditText actualPf = viewList.get(i).findViewById(R.id.actualCPf);
                        JsonObject actualPfObject = new JsonObject();
                        if (!actualPf.getText().toString().isEmpty()) {
                            actualPfObject.addProperty("1", "0");
                            actualPfObject.addProperty("2", "0");
                            actualPfObject.addProperty("3", actualPf.getText().toString().trim());
                            actualPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualPfObject);
                        } else {
                            actualPfObject.addProperty("1", "0");
                            actualPfObject.addProperty("2", "0");
                            actualPfObject.addProperty("3", "0.0");
                            actualPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualPfObject);
                        }

                        EditText connectedKva = viewList.get(i).findViewById(R.id.connectedCKva);
                        JsonObject connectedKvaObject = new JsonObject();
                        if (!connectedKva.getText().toString().isEmpty()) {
                            connectedKvaObject.addProperty("1", "0");
                            connectedKvaObject.addProperty("2", "0");
                            connectedKvaObject.addProperty("3", connectedKva.getText().toString().trim());
                            connectedKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedKvaObject);
                        } else {
                            connectedKvaObject.addProperty("1", "0");
                            connectedKvaObject.addProperty("2", "0");
                            connectedKvaObject.addProperty("3", "0.0");
                            connectedKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedKvaObject);
                        }

                        EditText kWh = viewList.get(i).findViewById(R.id.CkWh);
                        JsonObject kWhObject = new JsonObject();
                        if (!kWh.getText().toString().isEmpty()) {
                            kWhObject.addProperty("1", "0");
                            kWhObject.addProperty("2", "0");
                            kWhObject.addProperty("3", kWh.getText().toString().trim());
                            kWhObject.addProperty("7", "0");
                            jsonObject.add("kWh", kWhObject);
                        } else {
                            kWhObject.addProperty("1", "0");
                            kWhObject.addProperty("2", "0");
                            kWhObject.addProperty("3", "0.0");
                            kWhObject.addProperty("7", "0");
                            jsonObject.add("kWh", kWhObject);
                        }

                        EditText custCount = viewList.get(i).findViewById(R.id.customerCCount);
                        JsonObject customerCountObject = new JsonObject();
                        if (!custCount.getText().toString().isEmpty()) {
                            customerCountObject.addProperty("1", "0");
                            customerCountObject.addProperty("2", "0");
                            customerCountObject.addProperty("3", custCount.getText().toString().trim());
                            customerCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", customerCountObject);
                        } else {
                            customerCountObject.addProperty("1", "0");
                            customerCountObject.addProperty("2", "0");
                            customerCountObject.addProperty("13", "0");
                            customerCountObject.addProperty("7", "0.0");
                            jsonObject.add("CustomerCount", customerCountObject);
                        }
                        ListDataManager.sendData(jsonObject);
                        CustomerData customerData = new CustomerData(phaseType, Integer.parseInt(phaseValue), customerNumber, customerType, status, phaseObject, actualKwObject, actualPfObject, connectedKvaObject, customerCountObject);
                        customerList.add(customerData);
                    }

                    if (!customerList.isEmpty()) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                appDatabase.customerDataDao().insertAll(customerList);
                                Log.d("Database", "Successfully inserted " + customerList.size() + " customers");
                                dismiss();
                            } catch (Exception e) {
                                dismiss();
                                Log.e("DatabaseError", "Bulk insert failed", e);
                            }
                        });
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    e.getLocalizedMessage();
                    dismiss();
                }
            } else if (phaseValue.equals("4")) {
                try {
                    List<CustomerData> customerList = new ArrayList<>();
                    String customerNumber = "";
                    String customerType = "";
                    int status = 0;
                    int viewSize = viewList.size();
                    for (int i = 0; i < viewSize; i++) {
                        JsonObject jsonObject = new JsonObject();
                        EditText custNumber = viewList.get(i).findViewById(R.id.customerNumber);
                        if (!custNumber.getText().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerNumber", custNumber.getText().toString().trim());
                            customerNumber = custNumber.getText().toString().trim();
                        }

                        Spinner custSpinner = viewList.get(i).findViewById(R.id.cust_type_spinnerBar);
                        if (!custSpinner.getSelectedItem().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerType", custSpinner.getSelectedItem().toString().trim());
                            customerType = custSpinner.getSelectedItem().toString().trim();
                        }

                        Spinner statusSpinner = viewList.get(i).findViewById(R.id.statusSpinnerBar);
                        if (!statusSpinner.getSelectedItem().toString().isEmpty()) {
                            if (statusSpinner.getSelectedItem().equals("Connected")) {
                                jsonObject.addProperty("Status", "0");
                                status = 0;
                            } else {
                                jsonObject.addProperty("Status", "1");
                                status = 1;
                            }
                        }

                        TextView aPhase = viewList.get(i).findViewById(R.id.aPhase);
                        TextView bPhase = viewList.get(i).findViewById(R.id.bPhase);
                        JsonObject phaseObject = new JsonObject();
                        if (!aPhase.getText().toString().isEmpty() && !bPhase.getText().toString().isEmpty()) {
                            phaseObject.addProperty("1", "1");
                            phaseObject.addProperty("2", "1");
                            phaseObject.addProperty("3", "0");
                            phaseObject.addProperty("7", "0");
                            jsonObject.add("Phase", phaseObject);
                        }

                        EditText actualAKva = viewList.get(i).findViewById(R.id.actualAKva);
                        EditText actualBKva = viewList.get(i).findViewById(R.id.actualBKva);
                        JsonObject actualABKvaObject = new JsonObject();
                        if (!actualAKva.getText().toString().isEmpty() && !actualBKva.getText().toString().isEmpty()) {
                            actualABKvaObject.addProperty("1", actualAKva.getText().toString().trim());
                            actualABKvaObject.addProperty("2", actualBKva.getText().toString().trim());
                            actualABKvaObject.addProperty("3", "0");
                            actualABKvaObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualABKvaObject);
                        } else {
                            actualABKvaObject.addProperty("1", "0.0");
                            actualABKvaObject.addProperty("2", "0.0");
                            actualABKvaObject.addProperty("3", "0");
                            actualABKvaObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualABKvaObject);
                        }

                        EditText actualAPf = viewList.get(i).findViewById(R.id.actualAPf);
                        EditText actualBPf = viewList.get(i).findViewById(R.id.actualBPf);
                        JsonObject actualABPfObject = new JsonObject();
                        if (!actualAPf.getText().toString().isEmpty() && !actualBPf.getText().toString().isEmpty()) {
                            actualABPfObject.addProperty("1", actualAPf.getText().toString().trim());
                            actualABPfObject.addProperty("2", actualBPf.getText().toString().trim());
                            actualABPfObject.addProperty("3", "0");
                            actualABPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualABPfObject);
                        } else {
                            actualABPfObject.addProperty("1", "0.0");
                            actualABPfObject.addProperty("2", "0.0");
                            actualABPfObject.addProperty("3", "0");
                            actualABPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualABPfObject);
                        }

                        EditText connectedAKva = viewList.get(i).findViewById(R.id.connectedAKva);
                        EditText connectedBKva = viewList.get(i).findViewById(R.id.connectedBKva);
                        JsonObject connectedABKvaObject = new JsonObject();
                        if (!connectedAKva.getText().toString().isEmpty() && !connectedBKva.getText().toString().isEmpty()) {
                            connectedABKvaObject.addProperty("1", connectedAKva.getText().toString().trim());
                            connectedABKvaObject.addProperty("2", connectedBKva.getText().toString().trim());
                            connectedABKvaObject.addProperty("3", "0");
                            connectedABKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedABKvaObject);
                        } else {
                            connectedABKvaObject.addProperty("1", "0.0");
                            connectedABKvaObject.addProperty("2", "0.0");
                            connectedABKvaObject.addProperty("3", "0");
                            connectedABKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedABKvaObject);
                        }

                        EditText AkWh = viewList.get(i).findViewById(R.id.AKwh);
                        EditText BkWh = viewList.get(i).findViewById(R.id.BKwh);
                        JsonObject ABkWhObject = new JsonObject();
                        if (!AkWh.getText().toString().isEmpty() && !BkWh.getText().toString().isEmpty()) {
                            ABkWhObject.addProperty("1", AkWh.getText().toString().trim());
                            ABkWhObject.addProperty("2", BkWh.getText().toString().trim());
                            ABkWhObject.addProperty("3", "0");
                            ABkWhObject.addProperty("7", "0");
                            jsonObject.add("kWh", ABkWhObject);
                        } else {
                            ABkWhObject.addProperty("1", "0.0");
                            ABkWhObject.addProperty("2", "0.0");
                            ABkWhObject.addProperty("3", "0");
                            ABkWhObject.addProperty("7", "0");
                            jsonObject.add("kWh", ABkWhObject);
                        }

                        EditText custACount = viewList.get(i).findViewById(R.id.customerACount);
                        EditText custBCount = viewList.get(i).findViewById(R.id.customerBCount);
                        JsonObject custABCountObject = new JsonObject();
                        if (!custACount.getText().toString().isEmpty() && !custBCount.getText().toString().isEmpty()) {
                            custABCountObject.addProperty("1", custACount.getText().toString());
                            custABCountObject.addProperty("2", custBCount.getText().toString());
                            custABCountObject.addProperty("3", "0");
                            custABCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custABCountObject);
                        } else {
                            custABCountObject.addProperty("1", "0.0");
                            custABCountObject.addProperty("2", "0.0");
                            custABCountObject.addProperty("13", "0");
                            custABCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custABCountObject);
                        }
                        ListDataManager.sendData(jsonObject);
                        CustomerData customerData = new CustomerData(phaseType, Integer.parseInt(phaseValue), customerNumber, customerType, status, phaseObject, actualABKvaObject, actualABPfObject, connectedABKvaObject, custABCountObject);
                        customerList.add(customerData);
                    }

                    if (!customerList.isEmpty()) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                appDatabase.customerDataDao().insertAll(customerList);
                                Log.d("Database", "Successfully inserted " + customerList.size() + " customers");
                                dismiss();
                            } catch (Exception e) {
                                dismiss();
                                Log.e("DatabaseError", "Bulk insert failed", e);
                            }
                        });
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    e.getLocalizedMessage();
                    dismiss();
                }
            } else if (phaseValue.equals("5")) {
                try {
                    List<CustomerData> customerList = new ArrayList<>();
                    String customerNumber = "";
                    String customerType = "";
                    int status = 0;
                    int viewSize = viewList.size();
                    for (int i = 0; i < viewSize; i++) {

                        JsonObject jsonObject = new JsonObject();

                        EditText custNumber = viewList.get(i).findViewById(R.id.customerNumber);
                        if (!custNumber.getText().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerNumber", custNumber.getText().toString().trim());
                            customerNumber = custNumber.getText().toString().trim();
                        }

                        Spinner custSpinner = viewList.get(i).findViewById(R.id.cust_type_spinnerBar);
                        if (!custSpinner.getSelectedItem().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerType", custSpinner.getSelectedItem().toString().trim());
                            customerType = custSpinner.getSelectedItem().toString().trim();
                        }

                        Spinner statusSpinner = viewList.get(i).findViewById(R.id.statusSpinnerBar);
                        if (!statusSpinner.getSelectedItem().toString().isEmpty()) {
                            if (statusSpinner.getSelectedItem().equals("Connected")) {
                                jsonObject.addProperty("Status", "0");
                                status = 0;
                            } else {
                                jsonObject.addProperty("Status", "1");
                                status = 1;
                            }
                        }

                        TextView aPhase = viewList.get(i).findViewById(R.id.aPhase);
                        TextView cPhase = viewList.get(i).findViewById(R.id.cPhase);
                        JsonObject phaseObject = new JsonObject();
                        if (!aPhase.getText().toString().isEmpty() && !cPhase.getText().toString().isEmpty()) {
                            phaseObject.addProperty("1", "1");
                            phaseObject.addProperty("2", "0");
                            phaseObject.addProperty("3", "1");
                            phaseObject.addProperty("7", "0");
                            jsonObject.add("Phase", phaseObject);
                        }

                        EditText actualAKva = viewList.get(i).findViewById(R.id.actualAKva);
                        EditText actualCKva = viewList.get(i).findViewById(R.id.actualBKva);
                        JsonObject actualACKvaObject = new JsonObject();
                        if (!actualAKva.getText().toString().isEmpty() && !actualCKva.getText().toString().isEmpty()) {
                            actualACKvaObject.addProperty("1", actualAKva.getText().toString().trim());
                            actualACKvaObject.addProperty("2", "0");
                            actualACKvaObject.addProperty("3", actualCKva.getText().toString().trim());
                            actualACKvaObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualACKvaObject);
                        } else {
                            actualACKvaObject.addProperty("1", "0.0");
                            actualACKvaObject.addProperty("2", "0");
                            actualACKvaObject.addProperty("3", "0.0");
                            actualACKvaObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualACKvaObject);
                        }

                        EditText actualAPf = viewList.get(i).findViewById(R.id.actualAPf);
                        EditText actualCPf = viewList.get(i).findViewById(R.id.actualCPf);
                        JsonObject actualACPfObject = new JsonObject();
                        if (!actualAKva.getText().toString().isEmpty() && !actualCKva.getText().toString().isEmpty()) {
                            actualACPfObject.addProperty("1", actualAPf.getText().toString().trim());
                            actualACPfObject.addProperty("2", "0");
                            actualACPfObject.addProperty("3", actualCPf.getText().toString().trim());
                            actualACPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualACPfObject);
                        } else {
                            actualACPfObject.addProperty("1", "0.0");
                            actualACPfObject.addProperty("2", "0");
                            actualACPfObject.addProperty("3", "0.0");
                            actualACPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualACPfObject);
                        }

                        EditText connectedAKva = viewList.get(i).findViewById(R.id.connectedAKva);
                        EditText connectedCKva = viewList.get(i).findViewById(R.id.connectedCKva);
                        JsonObject connectedAKvaObject = new JsonObject();
                        if (!connectedAKva.getText().toString().isEmpty() && !connectedCKva.getText().toString().isEmpty()) {
                            connectedAKvaObject.addProperty("1", connectedAKva.getText().toString().trim());
                            connectedAKvaObject.addProperty("2", "0");
                            connectedAKvaObject.addProperty("3", connectedCKva.getText().toString().trim());
                            connectedAKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedAKvaObject);
                        } else {
                            connectedAKvaObject.addProperty("1", "0.0");
                            connectedAKvaObject.addProperty("2", "0");
                            connectedAKvaObject.addProperty("3", "0.0");
                            connectedAKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedAKvaObject);
                        }

                        EditText AkWh = viewList.get(i).findViewById(R.id.AKwh);
                        EditText CkWh = viewList.get(i).findViewById(R.id.CKwh);
                        JsonObject ACkWhObject = new JsonObject();
                        if (!AkWh.getText().toString().isEmpty() && !CkWh.getText().toString().isEmpty()) {
                            ACkWhObject.addProperty("1", AkWh.getText().toString().trim());
                            ACkWhObject.addProperty("2", "0");
                            ACkWhObject.addProperty("3", CkWh.getText().toString().trim());
                            ACkWhObject.addProperty("7", "0");
                            jsonObject.add("kWh", ACkWhObject);
                        } else {
                            ACkWhObject.addProperty("1", "0.0");
                            ACkWhObject.addProperty("2", "0");
                            ACkWhObject.addProperty("3", "0.0");
                            ACkWhObject.addProperty("7", "0");
                            jsonObject.add("kWh", ACkWhObject);
                        }

                        EditText custACount = viewList.get(i).findViewById(R.id.customerACount);
                        EditText custCCount = viewList.get(i).findViewById(R.id.customerCCount);
                        JsonObject custACCountObject = new JsonObject();
                        if (!custACount.getText().toString().isEmpty() && !custCCount.getText().toString().isEmpty()) {
                            custACCountObject.addProperty("1", custACount.getText().toString().trim());
                            custACCountObject.addProperty("2", "0");
                            custACCountObject.addProperty("3", custCCount.getText().toString().trim());
                            custACCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custACCountObject);
                        } else {
                            custACCountObject.addProperty("1", "0.0");
                            custACCountObject.addProperty("2", "0");
                            custACCountObject.addProperty("13", "0.0");
                            custACCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custACCountObject);
                        }
                        ListDataManager.sendData(jsonObject);
                        CustomerData customerData = new CustomerData(phaseType, Integer.parseInt(phaseValue), customerNumber, customerType, status, phaseObject, actualACKvaObject, actualACPfObject, connectedAKvaObject, custACCountObject);
                        customerList.add(customerData);
                    }

                    if (!customerList.isEmpty()) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                appDatabase.customerDataDao().insertAll(customerList);
                                Log.d("Database", "Successfully inserted " + customerList.size() + " customers");
                                dismiss();
                            } catch (Exception e) {
                                dismiss();
                                Log.e("DatabaseError", "Bulk insert failed", e);
                            }
                        });
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    e.getLocalizedMessage();
                    dismiss();
                }
            } else if (phaseValue.equals("6")) {
                try {
                    List<CustomerData> customerList = new ArrayList<>();
                    String customerNumber = "";
                    String customerType = "";
                    int status = 0;
                    int viewSize = viewList.size();
                    for (int i = 0; i < viewSize; i++) {
                        JsonObject jsonObject = new JsonObject();
                        EditText custNumber = viewList.get(i).findViewById(R.id.customerNumber);
                        if (!custNumber.getText().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerNumber", custNumber.getText().toString().trim());
                            customerNumber = custNumber.getText().toString().trim();
                        }

                        Spinner custSpinner = viewList.get(i).findViewById(R.id.cust_type_spinnerBar);
                        if (!custSpinner.getSelectedItem().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerType", custSpinner.getSelectedItem().toString().trim());
                            customerType = custSpinner.getSelectedItem().toString().trim();
                        }

                        Spinner statusSpinner = viewList.get(i).findViewById(R.id.statusSpinnerBar);
                        if (!statusSpinner.getSelectedItem().toString().isEmpty()) {
                            if (statusSpinner.getSelectedItem().equals("Connected")) {
                                jsonObject.addProperty("Status", "0");
                                status = 0;
                            } else {
                                jsonObject.addProperty("Status", "1");
                                status = 1;
                            }
                        }

                        TextView bPhase = viewList.get(i).findViewById(R.id.bPhase);
                        TextView cPhase = viewList.get(i).findViewById(R.id.cPhase);
                        JsonObject phaseObject = new JsonObject();
                        if (!bPhase.getText().toString().isEmpty() && !cPhase.getText().toString().isEmpty()) {
                            phaseObject.addProperty("1", "0");
                            phaseObject.addProperty("2", "1");
                            phaseObject.addProperty("3", "1");
                            phaseObject.addProperty("7", "0");
                            jsonObject.add("Phase", phaseObject);
                        }

                        EditText actualBKva = viewList.get(i).findViewById(R.id.actualBKva);
                        EditText actualCKva = viewList.get(i).findViewById(R.id.actualKva);
                        JsonObject actualBCKvaObject = new JsonObject();
                        if (!actualBKva.getText().toString().isEmpty() && !actualCKva.getText().toString().isEmpty()) {
                            actualBCKvaObject.addProperty("1", "0");
                            actualBCKvaObject.addProperty("2", actualBKva.getText().toString().trim());
                            actualBCKvaObject.addProperty("3", actualCKva.getText().toString().trim());
                            actualBCKvaObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualBCKvaObject);
                        } else {
                            actualBCKvaObject.addProperty("1", "0");
                            actualBCKvaObject.addProperty("2", "0.0");
                            actualBCKvaObject.addProperty("3", "0.0");
                            actualBCKvaObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualBCKvaObject);
                        }

                        EditText actualBPf = viewList.get(i).findViewById(R.id.actualBPf);
                        EditText actualCPf = viewList.get(i).findViewById(R.id.actualCPf);
                        JsonObject actualBCPfObject = new JsonObject();
                        if (!actualBPf.getText().toString().isEmpty() && !actualCPf.getText().toString().isEmpty()) {
                            actualBCPfObject.addProperty("1", "0");
                            actualBCPfObject.addProperty("2", actualBPf.getText().toString().trim());
                            actualBCPfObject.addProperty("3", actualCPf.getText().toString().trim());
                            actualBCPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualBCPfObject);
                        } else {
                            actualBCPfObject.addProperty("1", "0");
                            actualBCPfObject.addProperty("2", "0.0");
                            actualBCPfObject.addProperty("3", "0.0");
                            actualBCPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualBCPfObject);
                        }

                        EditText connectedBKva = viewList.get(i).findViewById(R.id.connectedBKva);
                        EditText connectedCKva = viewList.get(i).findViewById(R.id.connectedCKva);
                        JsonObject connectedBCKvaObject = new JsonObject();
                        if (!connectedBKva.getText().toString().isEmpty() && !connectedCKva.getText().toString().isEmpty()) {
                            connectedBCKvaObject.addProperty("1", "0");
                            connectedBCKvaObject.addProperty("2", connectedBKva.getText().toString().trim());
                            connectedBCKvaObject.addProperty("3", connectedCKva.getText().toString().trim());
                            connectedBCKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedBCKvaObject);
                        } else {
                            connectedBCKvaObject.addProperty("1", "0");
                            connectedBCKvaObject.addProperty("2", "0.0");
                            connectedBCKvaObject.addProperty("3", "0.0");
                            connectedBCKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedBCKvaObject);
                        }

                        EditText BkWh = viewList.get(i).findViewById(R.id.BKwh);
                        EditText CkWh = viewList.get(i).findViewById(R.id.CKwh);
                        if (!BkWh.getText().toString().isEmpty() && !CkWh.getText().toString().isEmpty()) {
                            JsonObject connectedKvaObject = new JsonObject();
                            connectedKvaObject.addProperty("1", "0");
                            connectedKvaObject.addProperty("2", BkWh.getText().toString().trim());
                            connectedKvaObject.addProperty("3", CkWh.getText().toString().trim());
                            connectedKvaObject.addProperty("7", "0");
                            jsonObject.add("kWh", connectedKvaObject);
                        } else {
                            JsonObject connectedKvaObject = new JsonObject();
                            connectedKvaObject.addProperty("1", "0");
                            connectedKvaObject.addProperty("2", "0.0");
                            connectedKvaObject.addProperty("3", "0.0");
                            connectedKvaObject.addProperty("7", "0");
                            jsonObject.add("kWh", connectedKvaObject);
                        }

                        EditText custBCount = viewList.get(i).findViewById(R.id.customerBCount);
                        EditText custCCount = viewList.get(i).findViewById(R.id.customerCCount);
                        JsonObject custBCCountObject = new JsonObject();
                        if (!custBCount.getText().toString().isEmpty() && !custCCount.getText().toString().isEmpty()) {
                            custBCCountObject.addProperty("1", "0");
                            custBCCountObject.addProperty("2", custBCount.getText().toString().trim());
                            custBCCountObject.addProperty("3", custCCount.getText().toString().trim());
                            custBCCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custBCCountObject);
                        } else {
                            custBCCountObject.addProperty("1", "0");
                            custBCCountObject.addProperty("2", "0.0");
                            custBCCountObject.addProperty("13", "0.0");
                            custBCCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custBCCountObject);
                        }
                        ListDataManager.sendData(jsonObject);
                        CustomerData customerData = new CustomerData(phaseType, Integer.parseInt(phaseValue), customerNumber, customerType, status, phaseObject, actualBCKvaObject, actualBCPfObject, connectedBCKvaObject, custBCCountObject);
                        customerList.add(customerData);
                    }

                    if (!customerList.isEmpty()) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                appDatabase.customerDataDao().insertAll(customerList);
                                Log.d("Database", "Successfully inserted " + customerList.size() + " customers");
                                dismiss();
                            } catch (Exception e) {
                                dismiss();
                                Log.e("DatabaseError", "Bulk insert failed", e);
                            }
                        });
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    e.getLocalizedMessage();
                    dismiss();
                }
            } else {
                try {
                    List<CustomerData> customerList = new ArrayList<>();
                    String customerNumber = "";
                    String customerType = "";
                    int status = 0;
                    int viewSize = viewList.size();
                    for (int i = 0; i < viewSize; i++) {
                        JsonObject jsonObject = new JsonObject();
                        EditText custNumber = viewList.get(i).findViewById(R.id.customerNumber);
                        if (!custNumber.getText().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerNumber", custNumber.getText().toString().trim());
                            customerNumber = custNumber.getText().toString().trim();
                        }

                        Spinner custSpinner = viewList.get(i).findViewById(R.id.cust_type_spinnerBar);
                        if (!custSpinner.getSelectedItem().toString().isEmpty()) {
                            jsonObject.addProperty("CustomerType", custSpinner.getSelectedItem().toString().trim());
                            customerType = custSpinner.getSelectedItem().toString().trim();
                        }

                        Spinner statusSpinner = viewList.get(i).findViewById(R.id.statusSpinnerBar);
                        if (!statusSpinner.getSelectedItem().toString().isEmpty()) {
                            if (statusSpinner.getSelectedItem().equals("Connected")) {
                                jsonObject.addProperty("Status", "0");
                                status = 0;
                            } else {
                                jsonObject.addProperty("Status", "1");
                                status = 1;
                            }
                        }

                        TextView aPhase = viewList.get(i).findViewById(R.id.aPhase);
                        TextView bPhase = viewList.get(i).findViewById(R.id.bPhase);
                        TextView cPhase = viewList.get(i).findViewById(R.id.cPhase);
                        JsonObject phaseObject = new JsonObject();
                        if (!aPhase.getText().toString().isEmpty() && !bPhase.getText().toString().isEmpty() && !cPhase.getText().toString().isEmpty()) {
                            phaseObject.addProperty("1", "1");
                            phaseObject.addProperty("2", "1");
                            phaseObject.addProperty("3", "1");
                            phaseObject.addProperty("7", "0");
                            jsonObject.add("Phase", phaseObject);
                        }

                        EditText actualAKva = viewList.get(i).findViewById(R.id.actualAKva);
                        EditText actualBKva = viewList.get(i).findViewById(R.id.actualBKva);
                        EditText actualCKva = viewList.get(i).findViewById(R.id.actualCKva);
                        JsonObject actualABCKvaObject = new JsonObject();
                        if (!actualAKva.getText().toString().isEmpty() && !actualBKva.getText().toString().isEmpty() && !actualCKva.getText().toString().isEmpty()) {
                            actualABCKvaObject.addProperty("1", actualAKva.getText().toString().trim());
                            actualABCKvaObject.addProperty("2", actualBKva.getText().toString().trim());
                            actualABCKvaObject.addProperty("3", actualCKva.getText().toString().trim());
                            actualABCKvaObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualABCKvaObject);
                        } else {
                            actualABCKvaObject.addProperty("1", "0.0");
                            actualABCKvaObject.addProperty("2", "0.0");
                            actualABCKvaObject.addProperty("3", "0.0");
                            actualABCKvaObject.addProperty("7", "0");
                            jsonObject.add("ActualKW", actualABCKvaObject);
                        }

                        EditText actualAPf = viewList.get(i).findViewById(R.id.actualAPf);
                        EditText actualBPf = viewList.get(i).findViewById(R.id.actualBPf);
                        EditText actualCPf = viewList.get(i).findViewById(R.id.actualCPf);
                        JsonObject actualABCPfObject = new JsonObject();
                        if (!actualAPf.getText().toString().isEmpty() && !actualBPf.getText().toString().isEmpty() && !actualCPf.getText().toString().isEmpty()) {
                            actualABCPfObject.addProperty("1", actualAPf.getText().toString().trim());
                            actualABCPfObject.addProperty("2", actualBPf.getText().toString().trim());
                            actualABCPfObject.addProperty("3", actualCPf.getText().toString().trim());
                            actualABCPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualABCPfObject);
                        } else {
                            actualABCPfObject.addProperty("1", "0.0");
                            actualABCPfObject.addProperty("2", "0.0");
                            actualABCPfObject.addProperty("3", "0.0");
                            actualABCPfObject.addProperty("7", "0");
                            jsonObject.add("ActualPF", actualABCPfObject);
                        }

                        EditText connectedAKva = viewList.get(i).findViewById(R.id.connectedAKva);
                        EditText connectedBKva = viewList.get(i).findViewById(R.id.connectedBKva);
                        EditText connectedCKva = viewList.get(i).findViewById(R.id.connectedCKva);
                        JsonObject connectedABCKvaObject = new JsonObject();
                        if (!connectedAKva.getText().toString().isEmpty() && !connectedBKva.getText().toString().isEmpty() && !connectedCKva.getText().toString().isEmpty()) {
                            connectedABCKvaObject.addProperty("1", connectedAKva.getText().toString().trim());
                            connectedABCKvaObject.addProperty("2", connectedBKva.getText().toString().trim());
                            connectedABCKvaObject.addProperty("3", connectedCKva.getText().toString().trim());
                            connectedABCKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedABCKvaObject);
                        } else {
                            connectedABCKvaObject.addProperty("1", "0.0");
                            connectedABCKvaObject.addProperty("2", "0.0");
                            connectedABCKvaObject.addProperty("3", "0.0");
                            connectedABCKvaObject.addProperty("7", "0");
                            jsonObject.add("ConnectedKVA", connectedABCKvaObject);
                        }

                        EditText AkWh = viewList.get(i).findViewById(R.id.connectedAKva);
                        EditText BkWh = viewList.get(i).findViewById(R.id.connectedBKva);
                        EditText CkWh = viewList.get(i).findViewById(R.id.connectedCKva);
                        if (!AkWh.getText().toString().isEmpty() && !BkWh.getText().toString().isEmpty() && !CkWh.getText().toString().isEmpty()) {
                            JsonObject connectedKvaObject = new JsonObject();
                            connectedKvaObject.addProperty("1", AkWh.getText().toString().trim());
                            connectedKvaObject.addProperty("2", BkWh.getText().toString().trim());
                            connectedKvaObject.addProperty("3", CkWh.getText().toString().trim());
                            connectedKvaObject.addProperty("7", "0");
                            jsonObject.add("kWh", connectedKvaObject);
                        } else {
                            JsonObject connectedKvaObject = new JsonObject();
                            connectedKvaObject.addProperty("1", "0.0");
                            connectedKvaObject.addProperty("2", "0.0");
                            connectedKvaObject.addProperty("3", "0.0");
                            connectedKvaObject.addProperty("7", "0");
                            jsonObject.add("kWh", connectedKvaObject);
                        }

                        EditText custACount = viewList.get(i).findViewById(R.id.customerACount);
                        EditText custBCount = viewList.get(i).findViewById(R.id.customerBCount);
                        EditText custCCount = viewList.get(i).findViewById(R.id.customerCCount);
                        JsonObject custABCCountObject = new JsonObject();
                        if (!custACount.getText().toString().isEmpty() && !custBCount.getText().toString().isEmpty() && !custCCount.getText().toString().isEmpty()) {
                            custABCCountObject.addProperty("1", custACount.getText().toString().trim());
                            custABCCountObject.addProperty("2", custBCount.getText().toString().trim());
                            custABCCountObject.addProperty("3", custCCount.getText().toString().trim());
                            custABCCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custABCCountObject);
                        } else {
                            custABCCountObject.addProperty("1", "0.0");
                            custABCCountObject.addProperty("2", "0.0");
                            custABCCountObject.addProperty("13", "0.0");
                            custABCCountObject.addProperty("7", "0");
                            jsonObject.add("CustomerCount", custABCCountObject);
                        }
                        ListDataManager.sendData(jsonObject);
                        CustomerData customerData = new CustomerData(phaseType, Integer.parseInt(phaseValue), customerNumber, customerType, status, phaseObject, actualABCKvaObject, actualABCPfObject, connectedABCKvaObject, custABCCountObject);
                        customerList.add(customerData);
                    }

                    if (!customerList.isEmpty()) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                appDatabase.customerDataDao().insertAll(customerList);
                                Log.d("Database", "Successfully inserted " + customerList.size() + " customers");
                                dismiss();
                            } catch (Exception e) {
                                dismiss();
                                Log.e("DatabaseError", "Bulk insert failed", e);
                            }
                        });
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    e.getLocalizedMessage();
                    dismiss();
                }
            }
        }
    }

    private void removeDynamicViews() {
        try {
            if (viewList.size() > 1) {
                View lastView = viewList.get(viewList.size() - 1);
                binding.dynamicLayout.removeView(lastView);
                viewList.remove(viewList.size() - 1);
            } else {
                Snackbar.make(binding.getRoot(), "The load must have at least one customer.", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
    }

    private void addDynamicViews(int random, int a) {
        try {
            if (phaseType.equals("ThreePhase")) {
                @SuppressLint("InflateParams")
                View child1 = layoutInflater.inflate(R.layout.three_phase_layout, null);
                binding.dynamicLayout.addView(child1);
                viewList.add(child1);
                EditText custNo = child1.findViewById(R.id.customerNumber);
                String combinedData = random + "-" + ++a;
                custNo.setText(combinedData);

                Spinner spinner = child1.findViewById(R.id.cust_type_spinnerBar);
                if (prefManager.getConsumerClasses() != null) {
                    ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                    spinner.setAdapter(locationAdapters);
                }
            } else {
                if (phaseValue.equals("1")) {
                    @SuppressLint("InflateParams")
                    View byPhaseALayout = layoutInflater.inflate(R.layout.by_phase_a_layout, null);
                    binding.dynamicLayout.addView(byPhaseALayout);
                    viewList.add(byPhaseALayout);

                    EditText editText = byPhaseALayout.findViewById(R.id.customerNumber);
                    String combinedData = random + "-" + ++a;
                    editText.setText(combinedData);

                    Spinner spinner = byPhaseALayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                    }

                } else if (phaseValue.equals("2")) {
                    @SuppressLint("InflateParams")
                    View byPhaseBLayout = layoutInflater.inflate(R.layout.by_phase_b_layout, null);
                    binding.dynamicLayout.addView(byPhaseBLayout);
                    viewList.add(byPhaseBLayout);

                    EditText editText = byPhaseBLayout.findViewById(R.id.customerNumber);
                    String combinedData = random + "-" + ++a;
                    editText.setText(combinedData);

                    Spinner spinner = byPhaseBLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                    }

                } else if (phaseValue.equals("3")) {
                    @SuppressLint("InflateParams")
                    View byPhaseCLayout = layoutInflater.inflate(R.layout.by_phase_c_layout, null);
                    binding.dynamicLayout.addView(byPhaseCLayout);
                    viewList.add(byPhaseCLayout);

                    EditText editText = byPhaseCLayout.findViewById(R.id.customerNumber);
                    String combinedData = random + "-" + ++a;
                    editText.setText(combinedData);

                    Spinner spinner = byPhaseCLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                    }

                } else if (phaseValue.equals("4")) {
                    @SuppressLint("InflateParams")
                    View byPhaseABLayout = layoutInflater.inflate(R.layout.by_phase_ab_layout, null);
                    binding.dynamicLayout.addView(byPhaseABLayout);
                    viewList.add(byPhaseABLayout);

                    EditText editText = byPhaseABLayout.findViewById(R.id.customerNumber);
                    String combinedData = random + "-" + ++a;
                    editText.setText(combinedData);

                    Spinner spinner = byPhaseABLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                    }

                } else if (phaseValue.equals("5")) {
                    @SuppressLint("InflateParams")
                    View byPhaseACLayout = layoutInflater.inflate(R.layout.by_phase_ac_layout, null);
                    binding.dynamicLayout.addView(byPhaseACLayout);
                    viewList.add(byPhaseACLayout);

                    EditText editText = byPhaseACLayout.findViewById(R.id.customerNumber);
                    String combinedData = random + "-" + ++a;
                    editText.setText(combinedData);

                    Spinner spinner = byPhaseACLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                    }

                } else if (phaseValue.equals("6")) {
                    @SuppressLint("InflateParams")
                    View byPhaseBCLayout = layoutInflater.inflate(R.layout.by_phase_bc_layout, null);
                    binding.dynamicLayout.addView(byPhaseBCLayout);
                    viewList.add(byPhaseBCLayout);

                    EditText editText = byPhaseBCLayout.findViewById(R.id.customerNumber);
                    String combinedData = random + "-" + ++a;
                    editText.setText(combinedData);

                    Spinner spinner = byPhaseBCLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                    }

                } else {
                    @SuppressLint("InflateParams")
                    View byPhaseABCLayout = layoutInflater.inflate(R.layout.by_phase_abc_layout, null);
                    binding.dynamicLayout.addView(byPhaseABCLayout);
                    viewList.add(byPhaseABCLayout);

                    EditText editText = byPhaseABCLayout.findViewById(R.id.customerNumber);
                    String combinedData = random + "-" + ++a;
                    editText.setText(combinedData);

                    Spinner spinner = byPhaseABCLayout.findViewById(R.id.cust_type_spinnerBar);
                    if (prefManager.getConsumerClasses() != null) {
                        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, prefManager.getConsumerClasses());
                        spinner.setAdapter(locationAdapters);
                    }
                }
            }
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        binding = null;
    }

}
