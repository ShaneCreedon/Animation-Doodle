package ca326.com.activities;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RatingBar;
import android.widget.TextView;

import android.widget.VideoView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class MyCardAdapter extends RecyclerView.Adapter<MyCardAdapter.ViewHolder> {

    private Integer j=0;


    private Context context;

    private ImageLoader loadImage;

    private ItemClickListener mClickListener;

    public static RatingBar ratingBar;



    //List for videos
    List<Video> videos;


    public MyCardAdapter(List<Video> videos, Context context){
        super();
        this.videos = videos;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //get the video for the right position
        Video video =  videos.get(position);

        loadImage = MyVolleyRequest.getInstance(context).getImageLoader();
        //set temporary image i.e R.drawable.play
        loadImage.get(video.getImageUrl(), ImageLoader.getImageListener(holder.image, R.drawable.play, android.R.drawable.ic_dialog_alert));

        //Showing data on the views

        holder.image.setImageUrl(video.getImageUrl(), loadImage);



        String url = video.getVideoUrl();
        Uri videoUri = Uri.parse(url);

        Float rating = video.getRating();
        //have to replace this with the rating of the video stored in database
        ratingBar.setRating(rating);
        System.out.println(ratingBar);
        System.out.println("this is i : " + j);
        j++;


        System.out.println(videoUri);
        holder.videoView.setVideoURI(videoUri);
        holder.textViewName.setText(video.getName());
       // holder.textViewDescription.setText(video.getDescription());
                /*
                System.out.println("second click");
                if (isPlaying){
                    //holder.videoView.setBackground(null);
                    holder.videoView.start();
                    isPlaying = false;
                }
                else{
                    holder.videoView.pause();
                    isPlaying = true;
                }
                */
            }






    @Override
    public int getItemCount() {
        return videos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //Views

        //will be changing NetworkImageView to videoView later on
        public VideoView videoView;
        public NetworkImageView image;
        public TextView textViewName;
        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);
            image = (NetworkImageView) itemView.findViewById(R.id.imageView);
            videoView = (VideoView) itemView.findViewById(R.id.videoViews);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}


