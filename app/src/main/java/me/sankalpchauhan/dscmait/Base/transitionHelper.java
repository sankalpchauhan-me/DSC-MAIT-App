package me.sankalpchauhan.dscmait.Base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import me.sankalpchauhan.dscmait.Animations.OnRevealAnimationListener;
import me.sankalpchauhan.dscmait.R;

public class transitionHelper extends AppCompatActivity {
    public void StartAnimation(View rootview) {
        rootview.setVisibility(View.INVISIBLE);
        ViewTreeObserver viewTreeObserver = rootview.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    revealActivity(rootview.getWidth() / 2, rootview.getHeight() / 2, rootview);
                    rootview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }


    protected void revealActivity(int x, int y, View rootview) {
        float finalRadius = (float) (Math.max(rootview.getWidth(), rootview.getHeight()) * 1.1);

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootview, x, y, 0, finalRadius);
        circularReveal.setDuration(1000);
        circularReveal.setInterpolator(new AccelerateInterpolator());

        // make the view visible and start the animation
        rootview.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    public static void animateRevealHide(final Context ctx, final View view, @ColorRes final int color,
                                         final int finalRadius, final OnRevealAnimationListener listener) {
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;
        int initialRadius = view.getWidth();

        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, finalRadius);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setBackgroundColor(ctx.getResources().getColor(color));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
                listener.onRevealHide();
            }
        });
        anim.setDuration(ctx.getResources().getInteger(R.integer.animation_duration));
        anim.start();
    }

    public void shakeAnimation(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.Shake).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void slideInLeft(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.SlideInLeft).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void slideInRight(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.SlideInRight).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void slideInUp(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.SlideInUp).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void slideInDown(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.SlideInDown).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void standUp(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.StandUp).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void rollIn(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.RollIn).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void rotateIn(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.RotateIn).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void rollInUpLeft(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.RotateInUpLeft).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void fadeIn(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.FadeIn).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void dropOut(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.DropOut).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void tadaHi(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.Tada).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void takeOff(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.TakingOff).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void rotateOutDownRight(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.RotateOutDownRight).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void rubberBand(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.RubberBand).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void zoomIn(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.ZoomIn).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void zoomOut(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.ZoomOut).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void flash(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.Flash).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void wave(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.Wave).duration(duration).repeat(repeatitions).playOn(view);
    }

    public void wobble(int duration, int repeatitions, View view) {
        YoYo.with(Techniques.Wobble).duration(duration).repeat(repeatitions).playOn(view);
    }

}
