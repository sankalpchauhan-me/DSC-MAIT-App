package me.sankalpchauhan.dscmait.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.sankalpchauhan.dscmait.R;


public class FeedbackBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    FirebaseAuth mAuth;

    EditText title=null;
    EditText category=null;
    EditText description=null;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_feedback, container, false);
        mAuth = FirebaseAuth.getInstance();

        title = v.findViewById(R.id.feedback_title);
        category = v.findViewById(R.id.feedback_category);
        description = v.findViewById(R.id.feedbackBox);

        Button submit = v.findViewById(R.id.feedback_Button);

        submit.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if(!(title.getText().toString().equals("") && category.getText().toString().equals("") && description.getText().toString().equals("")))  {
            Map<String, Object> feeMap = new HashMap<>();
            feeMap.put("Title", title.getText().toString());
            feeMap.put("Category", category.getText().toString());
            feeMap.put("Description", description.getText().toString());
            feeMap.put("Submitted By", mAuth.getCurrentUser().getEmail());
            sendFeedback(feeMap, getActivity());
            Log.e("Feedback", title.getText().toString()+" "+category.getText().toString()+" "+mAuth.getCurrentUser().getDisplayName() );
        }
        else {
            Toast.makeText(getActivity(), "Please fill in all details", Toast.LENGTH_LONG).show();
        }

    }

    public void sendFeedback(Map<String, Object> feedbackParams, Context context){
        database.collection("feedback").document().set(feedbackParams)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Thanks For Feedback", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                        Log.e("FEEDBACKERROR", e.getMessage());
                    }
                });
    }
}
