package org.wikipedia.readinglist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import org.wikipedia.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReadingListItemActionsView extends LinearLayout {
    public interface Callback {
        void onToggleOffline();
        void onShare();
        void onAddToOther();
        void onDelete();
    }

    @BindView(R.id.reading_list_item_title) TextView titleView;
    @BindView(R.id.reading_list_item_remove_text) TextView removeTextView;
    @BindView(R.id.reading_list_item_offline_switch) SwitchCompat offlineSwitchView;

    @Nullable private Callback callback;

    public ReadingListItemActionsView(Context context) {
        super(context);
        init();
    }

    public ReadingListItemActionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReadingListItemActionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setState(@NonNull String pageTitle, @NonNull String removeFromListText, boolean offline) {
        offlineSwitchView.setChecked(offline);
        titleView.setText(pageTitle);
        removeTextView.setText(removeFromListText);
    }

    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    @OnClick(R.id.reading_list_item_offline) void onOfflineClick(View view) {
        if (callback != null) {
            callback.onToggleOffline();
        }
    }

    @OnClick(R.id.reading_list_item_share) void onShareClick(View view) {
        if (callback != null) {
            callback.onShare();
        }
    }

    @OnClick(R.id.reading_list_item_add_to_other) void onAddToOtherClick(View view) {
        if (callback != null) {
            callback.onAddToOther();
        }
    }

    @OnClick(R.id.reading_list_item_remove) void onRemoveClick(View view) {
        if (callback != null) {
            callback.onDelete();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.view_reading_list_page_actions, this);
        ButterKnife.bind(this);
        setOrientation(VERTICAL);
    }
}
