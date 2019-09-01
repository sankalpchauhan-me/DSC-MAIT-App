package me.sankalpchauhan.dscmait.adapters;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import me.sankalpchauhan.dscmait.MainScreenActivity;
import me.sankalpchauhan.dscmait.R;
import me.sankalpchauhan.dscmait.Model.Event;

public class EventAdapter extends FirestoreRecyclerAdapter<Event, EventAdapter.EventHolder> {
    private OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public EventAdapter(@NonNull FirestoreRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull Event model) {



        Currency currency = Currency.getInstance("INR");
        String symbol = currency.getSymbol();

        holder.tvViewEventName.setText(model.getEventName());

        if(model.getRegistrationFee()!=null) {
            holder.tvViewRegistrationFee.setText(String.format("%s%s", symbol, model.getRegistrationFee()));
        }
        else{
            holder.tvViewRegistrationFee.setText("Free");
        }
        //holder.ivViewEventImage
        if(model.getPrizeMoney()!=null) {
            holder.tvViewPrizeMoney.setText(model.getPrizeMoney());
        }
        else{
            holder.tvViewPrizeMoney.setText("Free");
        }

        if(model.getRegisteredUsers()!=null) {
            HashMap<String, String> hashMap = model.getRegisteredUsers();
            holder.tvViewRegisteredUsers.setText(String.valueOf(hashMap.size())+"/100");
        }
        else {
            holder.tvViewRegisteredUsers.setText("0"+"/100");
        }

        if(model.getEventTime()!=null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy",Locale.getDefault());
            String eventdate = simpleDateFormat.format(model.getEventTime());

             SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
             String time = simpleTimeFormat.format(model.getEventTime());

            holder.tvViewEventDate.setText(String.valueOf(eventdate));
            holder.tvViewEventTime.setText(String.valueOf(time));
        }
        else{
            holder.tvViewEventDate.setText("Open All");
            holder.tvViewEventTime.setText("Time");
        }

        if(model.getEventImage()!=null || String.valueOf(model.getEventImage()).isEmpty()){
            Glide.with(holder.ivViewEventImage.getContext()).load(model.getEventImage()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.loadProgress.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.loadProgress.setVisibility(View.GONE);
                    return false;
                }
            }).thumbnail( 0.1f ).into(holder.ivViewEventImage);


        }
        else{
            holder.loadProgress.setVisibility(View.GONE);
            Glide.with(holder.ivViewEventImage.getContext()).load(R.drawable.profilebackground).into(holder.ivViewEventImage);
        }

//Apply Jugaad if sort by date

//        //TODO: Remove this jugaad
//
//        Date currentTime;
//
//        currentTime = Calendar.getInstance().getTime();
//        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
//        sfd.format(currentTime);
//
//        if(currentTime.after(model.getEventTime())){
//            holder.itemView.setVisibility(View.GONE);
//            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
//
//        }






    }

    @Override
    public void onDataChanged() {
        MainScreenActivity.shimmering();
    }


    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,
                parent,false);
        return new EventHolder(v);
    }

    class EventHolder extends RecyclerView.ViewHolder{
        TextView tvViewEventName;
        TextView tvViewEventDesciption;
        TextView tvViewPrizeMoney;
        TextView tvViewRegisteredUsers;
        TextView tvViewRegistrationFee;
        TextView tvViewEventTime;
        ImageView ivViewEventImage;
        TextView tvViewEventDate;



        ProgressBar loadProgress;

        public EventHolder(@NonNull View itemView) {
            super(itemView);

            tvViewEventName = itemView.findViewById(R.id.EventName);
            tvViewEventTime = itemView.findViewById(R.id.EventTime);
            tvViewPrizeMoney = itemView.findViewById(R.id.PrizeMoney);
            tvViewRegisteredUsers = itemView.findViewById(R.id.RegisteredUsers);
            tvViewRegistrationFee = itemView.findViewById(R.id.RegistrationFee);
            ivViewEventImage = itemView.findViewById(R.id.EventImage);
            tvViewEventDate = itemView.findViewById(R.id.EventDate);
            loadProgress = itemView.findViewById(R.id.eventProgress);






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

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }



    public void setOnItemClickListner(OnItemClickListener listner){
        this.listener = listner;
    }

//    public List<HashMap<String,String>> getHashMapOfUserRegisteredEvents(String Email) {
//        List<HashMap<String,String>> regEvents = new ArrayList<>();
//
//        for (int i = 0; i < getItemCount(); i++) {
//            if(getItem(i).getRegisteredUsers().containsValue(Email)) {
//                regEvents.add(getItem(i).getRegisteredUsers());
//                Log.e("CHECKER2", getItem(i).getRegisteredUsers().get("velocityRaptor"));
//            }
//        }
//        return regEvents;
//    }



}