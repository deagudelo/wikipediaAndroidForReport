package org.wikipedia.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.wikipedia.Constants;
import org.wikipedia.R;
import org.wikipedia.util.DimenUtil;
import org.wikipedia.util.ResourceUtil;
import org.wikipedia.util.StringUtil;

public final class NotificationPresenter {
    private static final int REQUEST_CODE_ACTIVITY = 0;
    private static final int REQUEST_CODE_ACTION = 1;
    private static final String CHANNEL_ID = "MEDIAWIKI_ECHO_CHANNEL";

    public static void showNotification(@NonNull Context context, @NonNull Notification n, @NonNull String wikiSiteName) {
        String title;
        @DrawableRes int icon = R.drawable.ic_wikipedia_w;
        @ColorRes int color = R.color.base30;

        NotificationCompat.Builder builder = getDefaultBuilder(context);

        title = StringUtil.fromHtml(n.getContents() != null ? n.getContents().getHeader() : "").toString();

        if (n.getContents() != null && n.getContents().getLinks() != null
                && n.getContents().getLinks().getPrimary() != null) {
            addAction(context, builder, n.getContents().getLinks().getPrimary(), REQUEST_CODE_ACTION);
        }
        if (n.getContents() != null && n.getContents().getLinks() != null
                && n.getContents().getLinks().getSecondary() != null && n.getContents().getLinks().getSecondary().size() > 0) {
            addAction(context, builder, n.getContents().getLinks().getSecondary().get(0), REQUEST_CODE_ACTION + 1);
        }
        if (n.getContents() != null && n.getContents().getLinks() != null
                && n.getContents().getLinks().getSecondary() != null && n.getContents().getLinks().getSecondary().size() > 1) {
            addAction(context, builder, n.getContents().getLinks().getSecondary().get(1), REQUEST_CODE_ACTION + 2);
        }

        Intent activityIntent = NotificationActivity.newIntent(context);

        switch (n.type()) {
            case Notification.TYPE_EDIT_USER_TALK:
                icon = R.drawable.ic_chat_white_24dp;
                color = R.color.accent50;
                break;
            case Notification.TYPE_REVERTED:
                icon = R.drawable.ic_rotate_left_white_24dp;
                color = R.color.red50;
                builder.setPriority(NotificationCompat.PRIORITY_MAX);
                break;
            case Notification.TYPE_EDIT_THANK:
                icon = R.drawable.ic_usertalk_constructive;
                color = R.color.green50;
                break;
            case Notification.TYPE_EDIT_MILESTONE:
                icon = R.drawable.ic_mode_edit_white_24dp;
                color = R.color.accent50;
                break;
            default:
                break;
        }

        showNotification(context, builder, (int) n.key(), wikiSiteName, title, title, icon, color, activityIntent);
    }

    public static NotificationCompat.Builder getDefaultBuilder(@NonNull Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(CHANNEL_ID,
                        context.getString(R.string.notification_echo_channel_description), importance);
                notificationChannel.setLightColor(ContextCompat.getColor(context, R.color.accent50));
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
    }

    @SuppressWarnings("checkstyle:parameternumber")
    public static void showNotification(@NonNull Context context, @NonNull NotificationCompat.Builder builder, int id,
                                        @NonNull String title, @NonNull String text, @NonNull CharSequence longText,
                                        @DrawableRes int icon, @ColorRes int color, @NonNull Intent bodyIntent) {
        builder.setContentIntent(PendingIntent.getActivity(context, REQUEST_CODE_ACTIVITY, bodyIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setLargeIcon(drawNotificationBitmap(context, color, icon))
                .setSmallIcon(R.drawable.ic_wikipedia_w)
                .setColor(ContextCompat.getColor(context, color))
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(longText));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    private static void addAction(Context context, NotificationCompat.Builder builder, Notification.Link link, int requestCode) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode,
                new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl())).putExtra(Constants.INTENT_EXTRA_VIEW_FROM_NOTIFICATION, true),
                PendingIntent.FLAG_UPDATE_CURRENT);
        String labelStr;
        if (!TextUtils.isEmpty(link.getTooltip())) {
            labelStr = StringUtil.fromHtml(link.getTooltip()).toString();
        } else {
            labelStr = StringUtil.fromHtml(link.getLabel()).toString();
        }
        builder.addAction(0, labelStr, pendingIntent);
    }

    private static Bitmap drawNotificationBitmap(@NonNull Context context, @ColorRes int color, @DrawableRes int icon) {
        final int bitmapHalfSize = DimenUtil.roundedDpToPx(20);
        final int iconHalfSize = DimenUtil.roundedDpToPx(12);
        Bitmap bmp = Bitmap.createBitmap(bitmapHalfSize * 2, bitmapHalfSize * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(ContextCompat.getColor(context, color));
        canvas.drawCircle(bitmapHalfSize, bitmapHalfSize, bitmapHalfSize, p);
        Bitmap iconBmp = ResourceUtil.bitmapFromVectorDrawable(context, icon, android.R.color.white);
        canvas.drawBitmap(iconBmp, null, new Rect(bitmapHalfSize - iconHalfSize, bitmapHalfSize - iconHalfSize,
                bitmapHalfSize + iconHalfSize, bitmapHalfSize + iconHalfSize), null);
        iconBmp.recycle();
        return bmp;
    }

    private NotificationPresenter() {
    }
}
