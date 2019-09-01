package me.sankalpchauhan.dscmait.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.sankalpchauhan.dscmait.R;


public class AnnounceBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_announcement, container, false);
        String result = getArguments().getString("Announcement");

        TextView Result = v.findViewById(R.id.resultBox);

        Result.setText(Html.fromHtml(result));


        return v;
    }
}
