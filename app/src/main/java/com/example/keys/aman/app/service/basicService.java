package com.example.keys.aman.app.service;

import android.app.assist.AssistStructure;
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
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.keys.R;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class basicService extends AutofillService {
    private final int NUMBER_DATASETS = 4;

    @Override
    public void onFillRequest(@NonNull FillRequest request, @NonNull CancellationSignal cancellationSignal, @NonNull FillCallback fillCallback) {
        System.out.println("onFillRequest");

        // find autofillable fields
        AssistStructure structure = getLatestAssistStructure(request);
        Map<String, AutofillId> fields = getAutofillableFields(structure);
        System.out.println("autofillable fields: " + fields);
        toast("autofillable fields: " + fields);

        if (fields.isEmpty()) {
            toast("No autofill hints found");
            fillCallback.onSuccess(null);
            return;
        }

        // Create the base response
        FillResponse.Builder response = new FillResponse.Builder();

        // Traverse the structure looking for nodes to fill out

        // Fetch user data that match the fields
//        UserData userData = fetchUserData(fields);


        // 1.Add the dynamic dataset
        String packageName = getApplicationContext().getPackageName();
        for (int i = 1; i <= NUMBER_DATASETS; i++) {
            Dataset.Builder dataset = new Dataset.Builder();
            for (Map.Entry<String, AutofillId> field : fields.entrySet()) {
                String hint = field.getKey();
                AutofillId id = field.getValue();
                Collection<AutofillId> ids = fields.values();
                AutofillId[] requiredIds = new AutofillId[ids.size()];
                ids.toArray(requiredIds);
                String value = i + "-" + hint;
                // We're simple - our dataset values are hardcoded as "N-hint" (for example,
                // "1-username", "2-username") and they're displayed as such, except if they're a
                // password
                String displayValue = hint.contains("password") ? "password for #" + i : value;
                RemoteViews presentation = newDatasetPresentation(packageName, displayValue);
                response.addDataset(new Dataset.Builder()
                                .setValue(id, AutofillValue.forText(value), presentation)
                                .build())
                        .setSaveInfo(new SaveInfo.Builder(
                                SaveInfo.SAVE_DATA_TYPE_USERNAME | SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                                requiredIds)
                                .build())
                        .build();
                dataset.setValue(id, AutofillValue.forText(value), presentation);
            }
        }

//        // 2.Add save info
//        Collection<AutofillId> ids = fields.values();
//        AutofillId[] requiredIds = new AutofillId[ids.size()];
//        ids.toArray(requiredIds);
//        response.setSaveInfo(
//                // We're simple, so we're generic
//                new SaveInfo.Builder(SaveInfo.SAVE_DATA_TYPE_GENERIC, requiredIds).build());

        // 3.Profit!
        fillCallback.onSuccess(response.build());

    }

    private void toast(@NonNull CharSequence message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    static AssistStructure getLatestAssistStructure(@NonNull FillRequest request) {
        List<FillContext> fillContexts = request.getFillContexts();
        return fillContexts.get(fillContexts.size() - 1).getStructure();
    }

    @NonNull
    private Map<String, AutofillId> getAutofillableFields(@NonNull AssistStructure structure) {
        Map<String, AutofillId> fields = new ArrayMap<>();
        int nodes = structure.getWindowNodeCount();
        for (int i = 0; i < nodes; i++) {
            AssistStructure.ViewNode node = structure.getWindowNodeAt(i).getRootViewNode();
            addAutofillableFields(fields, node);
        }
        return fields;
    }

    private void addAutofillableFields(Map<String, AutofillId> fields, AssistStructure.ViewNode node) {
        String[] hints = node.getAutofillHints();
        if (hints != null) {
            String hint = hints[0].toLowerCase();
            if (hint != null) {
                AutofillId id = node.getAutofillId();
                if (!fields.containsKey(hint)) {
                    System.out.println("Setting hint " + hint + " on " + id);
                    fields.put(hint, id);
                } else {
                    System.out.println("Ignoring hint " + hint + " on " + id
                            + "because is was already set");
                }
            }
        }
        int childrenSize = node.getChildCount();
        for (int i = 0; i < childrenSize; i++) {
            addAutofillableFields(fields, node.getChildAt(i));
        }
    }

    @NonNull
    static RemoteViews newDatasetPresentation(@NonNull String packageName,
                                              @NonNull CharSequence text) {
        RemoteViews presentation =
                new RemoteViews(packageName, R.layout.multidataset_service_list_item);
        presentation.setTextViewText(R.id.text, text);
        presentation.setImageViewResource(R.id.icon, R.mipmap.ic_launcher);
        return presentation;
    }

    @Override
    public void onSaveRequest(@NonNull SaveRequest saveRequest, @NonNull SaveCallback saveCallback) {
        System.out.println("onSaveRequest()");
        toast("Save not supported");


        saveCallback.onSuccess();
    }

}
