package com.example.tictactoe.logic;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import com.example.tictactoe.R;

public class AnimationsClass {


    // Background wave animation
    public static void waveAnimation(View rootView, Context context) {

        FrameLayout frameLayout = rootView.findViewById(R.id.waveRootLayout);
        frameLayout.post(() -> {
            // Get the fragment's height to determine the number of lines
            int fragmentHeight = frameLayout.getHeight();
            int fragmentWidth = frameLayout.getWidth();

            int lineSize = dpToPx(4, context); // Line thickness
            int spaceSize = dpToPx(35, context); // Space between lines

            int totalSize = lineSize + spaceSize;
            int delay = 300;

            // Calculate the number of lines
            int numberOfHorizontalLines = fragmentHeight / totalSize + 1;
            int numberOfVerticalLines = fragmentWidth / totalSize + 1;

            // Add Horizontal lines to the layout
            for (int i = 0; i < numberOfHorizontalLines; i++) {
                View horizontalLine = new View(context);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, lineSize);
                params.topMargin = i * totalSize;
                horizontalLine.setLayoutParams(params);
                horizontalLine.setBackgroundColor(Color.parseColor("#3EC5F3")); // Set line color

                // Set initial alpha to 25%
                horizontalLine.setAlpha(0.15f);

                frameLayout.addView(horizontalLine);

                ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(horizontalLine, "alpha", 0.15f, 1f, 0.15f); // from 1x to 2x
                ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(horizontalLine, "scaleY", 1f, 1.25f, 1f); // from 1x to 2x

                fadeAnimator.setDuration((long) numberOfHorizontalLines * delay);
                scaleAnimator.setDuration((long) numberOfHorizontalLines * delay);

                Interpolator interpolator = new AccelerateInterpolator();
                fadeAnimator.setInterpolator(interpolator);
                scaleAnimator.setInterpolator(interpolator);

                fadeAnimator.setRepeatCount(ValueAnimator.INFINITE);
                scaleAnimator.setRepeatCount(ValueAnimator.INFINITE);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(fadeAnimator, scaleAnimator);
                animatorSet.setStartDelay((long) (i) * delay);
                animatorSet.start();


            }

            // Add Vertical lines to layout
            for (int i = 0; i < numberOfVerticalLines; i++) {
                View verticalLine = new View(context);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(lineSize, FrameLayout.LayoutParams.MATCH_PARENT);
                params.leftMargin = i * (totalSize);
                params.rightMargin = i * (totalSize);
                verticalLine.setLayoutParams(params);
                verticalLine.setBackgroundColor(Color.parseColor("#403EC5F3")); // Set line color

                frameLayout.addView(verticalLine);

            }
        });
    }

    // searching pulse animation
    public static void searchAnimation(Context context, View rootView, int animationContainerId) {


        int shapeCount = 6;
        int searchDelay = 1200;
        int dpsize = 120;
        int size;
        Resources resources = context.getResources();
        float density = resources.getDisplayMetrics().density;
        size = Math.round(dpsize * density);
        FrameLayout animationContainer = rootView.findViewById(animationContainerId);
        for (int i = 0; i < shapeCount; i++) {

            android.widget.FrameLayout wave = new carbon.widget.FrameLayout(context);
            android.widget.FrameLayout.LayoutParams params = new carbon.widget.FrameLayout.LayoutParams(size, size);
            params.gravity = Gravity.CENTER;
            wave.setLayoutParams(params);
            wave.setBackground(ContextCompat.getDrawable(context, R.drawable.ring_shape));
            animationContainer.addView(wave);

// Create ObjectAnimators for scaling
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(wave, "scaleX", 1f, 2.5f); // from 1x to 2x
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(wave, "scaleY", 1f, 2.5f); // from 1x to 2x
            ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(wave, "alpha", 1f, 0f); // from 1x to 2x

// Set duration for the animation
            scaleXAnimator.setDuration(shapeCount * searchDelay); // 500ms for example
            scaleYAnimator.setDuration(shapeCount * searchDelay);
            fadeAnimator.setDuration(shapeCount * searchDelay);

// Apply an interpolator (e.g., AccelerateDecelerateInterpolator)
            Interpolator interpolator = new AccelerateDecelerateInterpolator();
            scaleXAnimator.setInterpolator(interpolator);
            scaleYAnimator.setInterpolator(interpolator);
            fadeAnimator.setInterpolator(interpolator);

// Set repeat mode to restart from the beginning after the end of each cycle
            scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scaleXAnimator.setRepeatMode(ValueAnimator.RESTART);
            scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scaleYAnimator.setRepeatMode(ValueAnimator.RESTART);
            fadeAnimator.setRepeatCount(ValueAnimator.INFINITE);
            fadeAnimator.setRepeatMode(ValueAnimator.RESTART);

// Start the animations together
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleXAnimator, scaleYAnimator, fadeAnimator);
            animatorSet.setStartDelay(i * searchDelay);
            animatorSet.start();
        }
    }


    // Convert dp to px
    public static int dpToPx(int dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
