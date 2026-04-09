package ai.hermes.oracle

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.util.Log
import android.content.Intent
import android.os.Build
import android.os.IBinder

class OracleForegroundService : Service() {
  companion object {
    private const val TAG = "HermesOracleSvc"
    private const val HERMES_ORACLE_OBSERVE_PATCH_V1 = "active"
  }
  private var server: OwnerServer? = null

  override fun onCreate() {
    Log.i(TAG, "onCreate: enter")
    super.onCreate()
    ensureChannel()
    try {
      Log.i(TAG, "onCreate: before startForeground")
      startForeground(17910, buildNotification())
      Log.i(TAG, "onCreate: startForeground ok")
    } catch (t: Throwable) {
      Log.e(TAG, "onCreate: startForeground failed", t)
      throw t
    }
    if (server == null) {
      Log.i(TAG, "onCreate: before OwnerServer(17910)")
      server = OwnerServer(applicationContext, 17910)
      Log.i(TAG, "onCreate: OwnerServer object created")
      try {
      Log.i(TAG, "onCreate/onStart: before server.start()")
      server?.start()
      Log.i(TAG, "onCreate/onStart: server.start() ok")
    } catch (t: Throwable) {
      Log.e(TAG, "onCreate/onStart: server.start() failed", t)
      throw t
    }
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    return START_STICKY
  }

  override fun onDestroy() {
    try { server?.stop() } catch (_: Throwable) {}
    server = null
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null

  private fun ensureChannel() {
    if (Build.VERSION.SDK_INT >= 26) {
      val nm = getSystemService(NotificationManager::class.java)
      val ch = NotificationChannel(
        "hermes_oracle",
        "Hermes Oracle",
        NotificationManager.IMPORTANCE_LOW
      )
      ch.description = "Hermes Shizuku Oracle runtime"
      nm.createNotificationChannel(ch)
    }
  }

  private fun buildNotification(): Notification {
    return Notification.Builder(this, "hermes_oracle")
      .setContentTitle("Hermes Shizuku Oracle")
      .setContentText("Oracle runtime on 127.0.0.1:17910")
      .setSmallIcon(android.R.drawable.stat_notify_sync)
      .build()
  }
}
