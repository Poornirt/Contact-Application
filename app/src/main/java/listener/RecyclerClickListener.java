package listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerClickListener implements RecyclerView.OnItemTouchListener {

    private onItemClickListener mListener;
    GestureDetector mGestureDetector;

    public interface onItemClickListener{
        void onClick(View pView, int pPosition);
        void onLongPress(View pView, int pPosition);
    }

    public RecyclerClickListener(Context pContext, final RecyclerView pRecyclerView, onItemClickListener pListener) {
        mListener = pListener;
        mGestureDetector=new GestureDetector(pContext, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent pMotionEvent) {
                super.onLongPress(pMotionEvent);
                View child=pRecyclerView.findChildViewUnder(pMotionEvent.getX(),pMotionEvent.getY());
                if(child!=null && mListener!=null){
                    mListener.onLongPress(child,pRecyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView pRecyclerView, @NonNull MotionEvent pMotionEvent) {
        View view=pRecyclerView.findChildViewUnder(pMotionEvent.getX(),pMotionEvent.getY());
        if(view!=null && mListener!=null && mGestureDetector.onTouchEvent(pMotionEvent)){
            mListener.onClick(view,pRecyclerView.getChildAdapterPosition(view));
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView pRecyclerView, @NonNull MotionEvent pMotionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
