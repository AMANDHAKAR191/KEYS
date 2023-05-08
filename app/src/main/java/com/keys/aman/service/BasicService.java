/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.keys.aman.service;

import android.app.PendingIntent;
import android.app.assist.AssistStructure;
import android.app.assist.AssistStructure.ViewNode;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.CancellationSignal;
import android.service.autofill.AutofillService;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveInfo;
import android.service.autofill.SaveRequest;
import android.util.ArrayMap;
import android.util.Log;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.keys.aman.data.MyPreference;
import com.keys.aman.R;
import com.keys.aman.AES;
import com.keys.aman.authentication.PinLockActivity;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.iAES;
import com.keys.aman.signin_login.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class BasicService extends AutofillService {
    LogInActivity logInActivity = new LogInActivity();
    public static final String REQUEST_ID = "BasicService";

    private static final String TAG = "BasicService";

    private static final int NUMBER_DATASETS = 4;
    MyPreference myPreference;
    iAES iAES;

    @Override
    public void onFillRequest(FillRequest request, CancellationSignal cancellationSignal,
                              FillCallback callback) {
        Log.d(TAG, "onFillRequest()");

        myPreference = MyPreference.getInstance(getApplicationContext());
        iAES = AES.getInstance(myPreference.getAesKey(), myPreference.getAesIv());

        Log.d(TAG, "client name: " + request.getFillContexts().get(0).getStructure().getActivityComponent().getPackageName());
        // Find autofillable fields
        AssistStructure structure = getLatestAssistStructure(request);
        Log.d(TAG, "structure " + structure.getActivityComponent() + "\n+" + structure.describeContents());
        Map<String, AutofillId> fields = getAutofillableFields(structure);
        Log.d(TAG, "autofillable fields:" + fields);

        if (fields.isEmpty()) {
            toast("No autofill hints found");
            traverseStructure(structure);
        }

        // Create the base response
        FillResponse.Builder response = new FillResponse.Builder();

        // Get the package name
        String packageName = getApplicationContext().getPackageName();

        // Get the datasets from Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("addpassworddata")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //accounts_google_com
        //com_netflix_mediaclient
        ref.child("com_netflix_mediaclient").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Create a list to hold the datasets
//                List<Dataset.Builder> datasetBuilders = new ArrayList<>();
                // Step 1: Create an authentication Intent
                Context context = getApplicationContext();
                Intent authIntent = new Intent(context, PinLockActivity.class);
                authIntent.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
//                authIntent.putExtra(EXTRA_AUTOFILL_DATASET_ID, datasetId); // Pass any extras you might need in the authentication activity

                // Step 2: Create a PendingIntent from the authentication Intent
                PendingIntent authPendingIntent = PendingIntent.getActivity(context, 0, authIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                IntentSender intentSender = authPendingIntent.getIntentSender();
                // Loop through the data and create a dataset for each record
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println("snapshot: " + snapshot);
                    PasswordHelperClass record = snapshot.getValue(PasswordHelperClass.class);
                    if (record != null) {
                        Dataset.Builder dataset = new Dataset.Builder()
                                .setAuthentication(intentSender);
                        for (Entry<String, AutofillId> field : fields.entrySet()) {
                            String hint = field.getKey();
                            AutofillId id = field.getValue();

                            String dLogin, dPassword;
                            try {
                                dLogin = iAES.doubleDecryption(record.getAddDataLogin());
                                dPassword = iAES.doubleDecryption(record.getAddDataPassword());

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }


                            // We're simple - our dataset values are hardcoded as "N-hint" (for example,
                            // "1-username", "2-username") and they're displayed as such, except if they're a
                            // password
                            String displayValue = hint.contains("password") ? "password for " + dLogin : "username for " + dLogin;
                            RemoteViews presentation = newDatasetPresentation(packageName, displayValue);
                            String value = "";



                            if (hint.contains("username")) {
                                dataset.setValue(id, AutofillValue.forText(dLogin), presentation);
                            } else if (hint.contains("password")) {
                                dataset.setValue(id, AutofillValue.forText(dPassword), presentation);
                            }
                            Log.e(TAG, "Dataset: " + dataset);

                        }
//                        datasetBuilders.add(dataset);
                        response.addDataset(dataset.build());
                        Log.e(TAG, "Response: " + response);
                    }
                }

//                // Add the datasets to the response
//                for (int i = 0; i < datasetBuilders.size(); i++) {
//                    response.addDataset(datasetBuilders.get(i).build());
//                }

                // Add save info
                Collection<AutofillId> ids = fields.values();
                AutofillId[] requiredIds = new AutofillId[ids.size()];
                ids.toArray(requiredIds);
                response.setSaveInfo(
                        // We're simple, so we're generic
                        new SaveInfo.Builder(SaveInfo.SAVE_DATA_TYPE_USERNAME & SaveInfo.SAVE_DATA_TYPE_PASSWORD, requiredIds).build());

                // Profit!
                callback.onSuccess(response.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onFillRequest: onCancelled", error.toException());
            }
        });
    }


    @Override
    public void onSaveRequest(SaveRequest request, SaveCallback callback) {
        Log.d(TAG, "onSaveRequest()");
        // Get the structure from the request
        List<FillContext> context = request.getFillContexts();
        AssistStructure structure = context.get(context.size() - 1).getStructure();

        // Traverse the structure looking for data to save
        traverseStructure(structure);

        // Persist the data, if there are no errors, call onSuccess()
        callback.onSuccess();
    }

    @Override
    public void onConnected() {
        super.onConnected();
    }

    @Override
    public void onDisconnected() {
        super.onDisconnected();
    }

    @NonNull
    private Map<String, AutofillId> getAutofillableFields(@NonNull AssistStructure structure) {
        Map<String, AutofillId> fields = new ArrayMap<>();
        int nodes = structure.getWindowNodeCount();
        for (int i = 0; i < nodes; i++) {
            ViewNode node = structure.getWindowNodeAt(i).getRootViewNode();
            addAutofillableFields(fields, node);
        }
        return fields;
    }

    /**
     * Adds any autofillable view from the {@link ViewNode} and its descendants to the map.
     */
    private void addAutofillableFields(@NonNull Map<String, AutofillId> fields,
                                       @NonNull ViewNode node) {
        String[] hints = node.getAutofillHints();
        if (hints != null) {
            // We're simple, we only care about the first hint
            String hint = hints[0].toLowerCase();

            AutofillId id = node.getAutofillId();
            if (!fields.containsKey(hint)) {
                Log.v(TAG, "Setting hint '" + hint + "' on " + id);
                fields.put(hint, id);
            } else {
                Log.v(TAG, "Ignoring hint '" + hint + "' on " + id
                        + " because it was already set");
            }
        }
        int childrenSize = node.getChildCount();
        for (int i = 0; i < childrenSize; i++) {
            addAutofillableFields(fields, node.getChildAt(i));
        }
    }

    /**
     * Helper method to get the {@link AssistStructure} associated with the latest request
     * in an autofill context.
     */
    @NonNull
    static AssistStructure getLatestAssistStructure(@NonNull FillRequest request) {
        Log.d(TAG, "getFillContexts : " + request.getFillContexts());
        List<FillContext> fillContexts = request.getFillContexts();
        Log.d(TAG, "getFillContexts : " + fillContexts.get(fillContexts.size() - 1).getRequestId());
        Log.d(TAG, "getFillContexts : " + fillContexts.get(fillContexts.size() - 1).describeContents());
        return fillContexts.get(fillContexts.size() - 1).getStructure();
    }

    /**
     * Helper method to create a dataset presentation with the given text.
     */
    @NonNull
    static RemoteViews newDatasetPresentation(@NonNull String packageName,
                                              @NonNull CharSequence text) {
        RemoteViews presentation =
                new RemoteViews(packageName, R.layout.multidataset_service_list_item);
        presentation.setTextViewText(R.id.text, text);
        presentation.setImageViewResource(R.id.icon, R.drawable.person);
        return presentation;
    }

    /**
     * Displays a toast with the given message.
     */
    private void toast(@NonNull CharSequence message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void traverseStructure(AssistStructure structure) {
        Log.d(TAG, "traverseStructure");
        int nodes = structure.getWindowNodeCount();

        for (int i = 0; i < nodes; i++) {
            AssistStructure.WindowNode windowNode = structure.getWindowNodeAt(i);
            ViewNode viewNode = windowNode.getRootViewNode();
            traverseNode(viewNode);
        }
    }

    public void traverseNode(ViewNode viewNode) {
        Log.d(TAG, "traverseNode");
        if (viewNode.getAutofillHints() != null && viewNode.getAutofillHints().length > 0) {
            // If the client app provides autofill hints, you can obtain them using:
//             viewNode.getAutofillHints();
            Log.d(TAG, "viewNode.getAutofillHints(): " + viewNode.getAutofillHints().toString());
        } else {
            // Or use your own heuristics to describe the contents of a view
            // using methods such as getText() or getHint().
//            Log.d(TAG, "viewNode.getAutofillHints(): " + viewNode.get);
        }

        for (int i = 0; i < viewNode.getChildCount(); i++) {
            ViewNode childNode = viewNode.getChildAt(i);
            traverseNode(childNode);
        }
    }
}
