package jakub.remotecontrol;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class AutoRepeatButton extends android.support.v7.widget.AppCompatButton {

    private long initialRepeatDelay = 100;
    private long repeatIntervalInMilliseconds = 100;

    private Runnable repeatClickWhileButtonHeldRunnable = new Runnable() {
        @Override
        public void run() {
            //Perform the present repetition of the click action provided by the user
            // in setOnClickListener().
            performClick();

            //Schedule the next repetitions of the click action, using a faster repeat
            // interval than the initial repeat delay interval.
            if(repeatIntervalInMilliseconds>20) repeatIntervalInMilliseconds -= 5;
            postDelayed(repeatClickWhileButtonHeldRunnable, repeatIntervalInMilliseconds);
        }
    };

    private void commonConstructorCode() {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(action == MotionEvent.ACTION_DOWN)
                {
                    //Just to be sure that we removed all callbacks,
                    // which should have occurred in the ACTION_UP
                    removeCallbacks(repeatClickWhileButtonHeldRunnable);

                    //Perform the default click action.
                    performClick();

                    //Schedule the start of repetitions after a one half second delay.
                    postDelayed(repeatClickWhileButtonHeldRunnable, initialRepeatDelay);
                }
                else if(action == MotionEvent.ACTION_UP) {
                    //Cancel any repetition in progress.
                    removeCallbacks(repeatClickWhileButtonHeldRunnable);
                }

                //Returning true here prevents performClick() from getting called
                // in the usual manner, which would be redundant, given that we are
                // already calling it above.
                return true;
            }
        });
    }

    public AutoRepeatButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        commonConstructorCode();
    }


    public AutoRepeatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonConstructorCode();
    }

    public AutoRepeatButton(Context context) {
        super(context);
        commonConstructorCode();
    }
}