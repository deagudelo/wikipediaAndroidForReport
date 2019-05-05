package org.wikipedia.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.PopupWindowCompat;

import org.wikipedia.R;
import org.wikipedia.page.tabs.Tab;
import org.wikipedia.util.FeedbackUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PageActionOverflowView extends FrameLayout {

    public interface Callback {
        void forwardClick();
        void backwardClick();
        void openNewTabClick();
        void readingListsClick();
        void recentlyViewedClick();
    }

    @Nullable private Callback callback;
    @Nullable private PopupWindow popupWindowHost;
    @BindView(R.id.page_action_overflow_forward) AppCompatImageView forwardButton;
    @BindView(R.id.page_action_overflow_back) AppCompatImageView backButton;

    public PageActionOverflowView(Context context) {
        super(context);
        init();
    }

    public void show(@NonNull View anchorView, @Nullable Callback callback, @NonNull Tab currentTab) {
        this.callback = callback;
        popupWindowHost = new PopupWindow(this, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowHost.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        PopupWindowCompat.setOverlapAnchor(popupWindowHost, true);
        PopupWindowCompat.showAsDropDown(popupWindowHost, anchorView, 0, 0, Gravity.END);


        final float disabledAlpha = 0.5f;
        backButton.setEnabled(currentTab.canGoBack());
        backButton.setAlpha(backButton.isEnabled() ? 1.0f : disabledAlpha);
        forwardButton.setEnabled(currentTab.canGoForward());
        forwardButton.setAlpha(forwardButton.isEnabled() ? 1.0f : disabledAlpha);
    }

    @OnClick({R.id.page_action_overflow_forward, R.id.page_action_overflow_back,
            R.id.page_action_overflow_open_a_new_tab, R.id.page_action_overflow_reading_lists,
            R.id.page_action_overflow_recently_viewed})
    void onItemClick(View view) {
        if (popupWindowHost != null) {
            popupWindowHost.dismiss();
            popupWindowHost = null;
        }
        if (callback == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.page_action_overflow_forward:
                callback.forwardClick();
                break;
            case R.id.page_action_overflow_back:
                callback.backwardClick();
                break;
            case R.id.page_action_overflow_open_a_new_tab:
                callback.openNewTabClick();
                break;
            case R.id.page_action_overflow_reading_lists:
                callback.readingListsClick();
                break;
            case R.id.page_action_overflow_recently_viewed:
                callback.recentlyViewedClick();
                break;
            default:
                break;
        }
    }

    private void init() {
        inflate(getContext(), R.layout.view_page_action_overflow, this);
        ButterKnife.bind(this);

        FeedbackUtil.setToolbarButtonLongPressToast(forwardButton, backButton);
    }
}
