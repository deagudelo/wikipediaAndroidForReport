package org.wikipedia.util;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;

public final class AnimationUtil {

    public static void setSharedElementTransitions(@NonNull Activity activity) {
        // For using shared element transitions with Fresco, we need to explicitly define
        // a DraweeTransition that will be automatically used by Drawees that are used in
        // transitions between activities.
        activity.getWindow().setSharedElementEnterTransition(DraweeTransition
                .createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
        activity.getWindow().setSharedElementReturnTransition(DraweeTransition
                .createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
    }

    public static class PagerTransformer implements ViewPager.PageTransformer {
        @SuppressWarnings("magicnumber")
        @Override
        public void transformPage(@NonNull View view, float position) {
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setRotation(0f);
                view.setTranslationX(0);
            } else if (position <= 0) { // [-1,0]
                float factor = position * 45f;
                view.setRotation(factor);
                view.setTranslationX((view.getWidth() * position / 2));
                view.setAlpha(1f);
            } else if (position <= 1) { // (0,1]
                // keep it in place (undo the default translation)
                view.setTranslationX(-(view.getWidth() * position));
                // but move it slightly down
                view.setTranslationY(DimenUtil.roundedDpToPx(12f) * position);
                // and make it translucent
                view.setAlpha(1f - position * 0.5f);
                view.setRotation(0f);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setRotation(0f);
                view.setTranslationX(0);
            }
        }
    }

    public static class PagerTransformerWithoutPreviews implements ViewPager.PageTransformer {
        @SuppressWarnings("magicnumber")
        @Override
        public void transformPage(@NonNull View view, float position) {
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setRotation(0f);
                view.setTranslationX(0);
            } else if (position <= 0) { // [-1,0]
                float factor = position * 45f;
                view.setRotation(factor);
                view.setTranslationX((view.getWidth() * position / 2));
                view.animate().alpha(1.0f).setDuration(100);
            } else if (position < 1) { // (0,1]
                // keep it in place (undo the default translation)
                view.setTranslationX(-(view.getWidth() * position));
                view.setRotation(0f);
                view.setAlpha(1.0f - position);
            } else { // (1,+Infinity]
                // and make it transparent
                view.setAlpha(0f);
                // This page is way off-screen to the right.
                view.setRotation(0f);
                view.setTranslationX(0);
            }
        }
    }

    private AnimationUtil() { }
}
