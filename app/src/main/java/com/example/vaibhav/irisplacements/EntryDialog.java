package com.example.vaibhav.irisplacements;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class EntryDialog extends DialogFragment {
    private static final String TAG = "EntryDialog";

    public EntryDialog(){

    }

    public static EntryDialog getInstance(Entry entry){
        Bundle bundle = new Bundle();
        bundle.putParcelable("entry",entry);
        EntryDialog entryDialog = new EntryDialog();
        entryDialog.setArguments(bundle);
        return entryDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_entry,container,false);
        TextView companyName, recruitmentDate, deadline, url, companyType, recruitmentType;
        companyName = rootView.findViewById(R.id.dialog_tv_company_name);
        recruitmentDate = rootView.findViewById(R.id.dialog_tv_recruitment_date);
        deadline = rootView.findViewById(R.id.dialog_tv_deadline);
        url = rootView.findViewById(R.id.dialog_tv_url);
        companyType = rootView.findViewById(R.id.dialog_tv_company_type);
        recruitmentType = rootView.findViewById(R.id.dialog_tv_recruitment_type);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Entry entry = getArguments().getParcelable("entry");
        companyName.setText(entry.getCompany().getName());
        companyType.setText(entry.getCompany().getCompany_type());
        recruitmentDate.setText(entry.getRecruitment_date());
        deadline.setText(entry.getDeadline());
        url.setText(entry.getUrl());
        recruitmentType.setText(entry.getRecruitment_type());

        return rootView;
    }
}
