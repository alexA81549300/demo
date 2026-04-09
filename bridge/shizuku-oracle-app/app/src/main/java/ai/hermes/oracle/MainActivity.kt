package ai.hermes.oracle

import android.app.Activity
import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
  companion object {
    private const val TAG = "HermesOracleMain"
    private const val HERMES_ORACLE_OBSERVE_PATCH_V1 = "active"
  }
  override fun onCreate(savedInstanceState: Bundle?) {
    Log.i(TAG, "onCreate: enter")
    super.onCreate(savedInstanceState)

    try {
      try {
        Log.i(TAG, "onCreate: before startForegroundService")
        startForegroundService(Intent(this, OracleForegroundService::class.java))
        Log.i(TAG, "onCreate: startForegroundService returned ok")
      } catch (t: Throwable) {
        Log.e(TAG, "onCreate: startForegroundService failed", t)
        throw t
      }
    } catch (_: Throwable) {}

    val tv = TextView(this)
    tv.text = """
      Hermes Shizuku Oracle

      Runtime: OracleForegroundService
      Health:  http://127.0.0.1:17910/health
      Owner:   http://127.0.0.1:17910/owner?ports=17890,17891

      Status:
      - stage1 runtime split complete
      - real Shizuku-backed owner resolution not implemented yet
    """.trimIndent()
    setContentView(tv)
  }
}
