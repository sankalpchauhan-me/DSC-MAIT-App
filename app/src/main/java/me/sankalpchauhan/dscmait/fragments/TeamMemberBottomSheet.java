package me.sankalpchauhan.dscmait.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.sankalpchauhan.dscmait.R;

public class TeamMemberBottomSheet extends BottomSheetDialogFragment {
    TableLayout memberLinksTable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_team, container, false);
        Bundle result = getArguments();
        Map<String, Object> membermap = (Map<String, Object>) result.getSerializable("Membermap");
        Map<String, String> memberLinks = (Map<String, String>) membermap.get("Links");
        TextView memberName = v.findViewById(R.id.aboutName);
        TextView memberDescription = v.findViewById(R.id.memberDescription);
        memberLinksTable = v.findViewById(R.id.memberLinks);
        TextView memberRole = v.findViewById(R.id.memberRole);
        memberName.setText("About " + (String) membermap.get("Name"));
        memberDescription.setText((String) membermap.get("Description"));
        memberRole.setText("("+(String) membermap.get("Role")+")");
        for (Map.Entry<String, String> entry : memberLinks.entrySet()) {
            initTable(entry.getKey(), entry.getValue(), v.getContext());

        }







            return v;
    }

    public boolean initTable(String Links, String Values, Context context) {

        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(10,10,10,10);


        TableRow row = new TableRow(context);
        row.setLayoutParams(lp);
        TextView tv = new TextView(context);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(18);
        //tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));


        tv.setText(Html.fromHtml("<B>" + Links + "</B>: <a href="+Values+">"+Values+"</a>"));
        Linkify.addLinks(tv, Linkify.ALL);
        //checkBox.setText(Html.fromHtml(" |  "));
        //tv.setBackgroundResource(R.drawable.rect_background);

        row.addView(tv);

        //Add rows to table

        memberLinksTable.addView(row);


        return true;


    }
}
