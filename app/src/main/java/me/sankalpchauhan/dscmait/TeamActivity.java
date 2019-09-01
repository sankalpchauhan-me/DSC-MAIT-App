package me.sankalpchauhan.dscmait;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import me.sankalpchauhan.dscmait.Model.Team;
import me.sankalpchauhan.dscmait.adapters.TeamAdapter;
import me.sankalpchauhan.dscmait.fragments.TeamMemberBottomSheet;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.r0adkll.slidr.Slidr;

import java.io.Serializable;
import java.util.Map;

public class TeamActivity extends AppCompatActivity {
    TeamAdapter adapter;
    FirebaseFirestore database=FirebaseFirestore.getInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        SetUpRecyclerView();
        adapter.startListening();

        Slidr.attach(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void SetUpRecyclerView(){
        Query query = database.collection("teams").orderBy("Name", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<Team> options = new FirestoreRecyclerOptions.Builder<Team>()
                .setQuery(query, Team.class)
                .build();



        adapter = new TeamAdapter(options);
        //Animtions
        SlideInLeftAnimationAdapter alphaAdapter = new SlideInLeftAnimationAdapter(adapter);
        alphaAdapter.setDuration(400);
        //alphaAdapter.setFirstOnly(false);           //Sets the animation when going up also

        RecyclerView recyclerView = findViewById(R.id.teamMemberRecyclerView);

        //Animations
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.getItemAnimator().setAddDuration(400);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(alphaAdapter);

        adapter.setOnItemClickListner(new TeamAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Map<String, Object> memberMap = documentSnapshot.getData();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Membermap", (Serializable) memberMap);

                TeamMemberBottomSheet resultbottomsheet = new TeamMemberBottomSheet();
                resultbottomsheet.setArguments(bundle);
                resultbottomsheet.show(getSupportFragmentManager(), "announceBottomSheet");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }
}
