package me.sankalpchauhan.dscmait.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.sankalpchauhan.dscmait.R;


public class RoomBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_roomdetails, container, false);
        String RoomId = getArguments().getString("RoomID");
        //String Password = getArguments().getString("Password");

        TextView roomID = v.findViewById(R.id.roomID);
        //TextView password = v.findViewById(R.id.roomPassword);
        Button goToPubg = v.findViewById(R.id.joinButton);

        roomID.setText(RoomId);
        //password.setText(Password);

        goToPubg.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity(), "Seat Confirmed", Toast.LENGTH_SHORT).show();
//        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.tencent.ig");
//        if (intent != null) {
//            // We found the activity now start the activity
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getActivity().startActivity(intent);
//        } else {
//            // Bring user to the market or let them choose an app?
//            intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setData(Uri.parse("market://details?id=" + "com.tencent.ig"));
//            getActivity().startActivity(intent);
//        }
    }
}
