package me.sankalpchauhan.dscmait.adapters;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.wang.avi.AVLoadingIndicatorView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import me.sankalpchauhan.dscmait.Model.Team;
import me.sankalpchauhan.dscmait.R;

public class TeamAdapter extends FirestoreRecyclerAdapter<Team, TeamAdapter.TeamHolder> {
    private OnItemClickListener listener;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TeamAdapter(FirestoreRecyclerOptions<Team> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(TeamHolder holder, int position, Team model) {
        if(model.getName()!=null){
            holder.Name.setText(model.getName());
        }
        if(model.getPosition()!=null){
            holder.Position.setText(model.getPosition());
        }
        if(model.getImage()!=null){
            Glide.with(holder.picture.getContext()).load(model.getImage()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.avi.show();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.avi.hide();
                    return false;
                }
            }).thumbnail( 0.1f ).into(holder.picture);


        }
        else{
            holder.avi.hide();
            Glide.with(holder.picture.getContext()).load(R.drawable.logo).into(holder.picture);
        }

        }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }


    @NonNull
    @Override
    public TeamHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_item,
                parent,false);
        return new TeamHolder(v);
    }

    class TeamHolder extends RecyclerView.ViewHolder{

        TextView Name;
        TextView Position;
        ImageView picture;
        AVLoadingIndicatorView avi;

        public TeamHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.memberName);
            Position = itemView.findViewById(R.id.memberPosition);
            picture = itemView.findViewById(R.id.memberPic);
            avi = itemView.findViewById(R.id.avi);

            //For handling Click Events
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listener !=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }

    public void setOnItemClickListner(OnItemClickListener listner){
        this.listener = listner;
    }
}
