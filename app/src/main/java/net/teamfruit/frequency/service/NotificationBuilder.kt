package net.teamfruit.frequency.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import net.teamfruit.frequency.R
import net.teamfruit.frequency.util.CHANNEL_ID

class NotificationBuilder(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val skipToPrevAction = NotificationCompat.Action(
            R.drawable.exo_notification_previous,
            context.getString(R.string.action_prev),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
    )
    private val playAction = NotificationCompat.Action(
            R.drawable.exo_notification_play,
            context.getString(R.string.action_play),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context, PlaybackStateCompat.ACTION_PLAY)
    )
    private val pauseAction = NotificationCompat.Action(
            R.drawable.exo_notification_pause,
            context.getString(R.string.action_pause),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context, PlaybackStateCompat.ACTION_PAUSE)
    )
    private val skipToNextAction = NotificationCompat.Action(
            R.drawable.exo_notification_next,
            context.getString(R.string.action_next),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
    )
    private val stopPendingIntent = MediaButtonReceiver
            .buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)

    fun buildNotification(token: MediaSessionCompat.Token): Notification {
        createChannel()

        val controller = MediaControllerCompat(context, token)
        val description = controller.metadata.description
        val playbackState = controller.playbackState.state

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)

        builder.apply {
            setContentTitle(description.title)
            setSmallIcon(R.drawable.exo_notification_small_icon)
            setLargeIcon(description.iconBitmap)
            setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(token)
                    .setShowActionsInCompactView(0,1,2)
                    .setCancelButtonIntent(stopPendingIntent)
                    .setShowCancelButton(true)
            )
            setVisibility(Notification.VISIBILITY_PUBLIC)
        }

        builder.apply {
            addAction(skipToPrevAction)
            addAction(when (playbackState) {
                PlaybackStateCompat.STATE_PLAYING -> pauseAction
                PlaybackStateCompat.STATE_PAUSED -> playAction
                else -> playAction })
            addAction(skipToNextAction)
        }
        return builder.build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(
                    CHANNEL_ID, context.getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_LOW).apply {
                description = context.getString(R.string.notification_description) }

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}