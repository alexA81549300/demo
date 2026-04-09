package ai.hermes.oracle

import fi.iki.elonen.NanoHTTPD

import android.content.Context
import android.os.Process

class OwnerServer(
  private val appContext: Context,
  port: Int
) : NanoHTTPD("127.0.0.1", port) {

  override fun serve(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
    return when (session.uri) {
      "/health" -> {
        NanoHTTPD.newFixedLengthResponse(
          NanoHTTPD.Response.Status.OK,
          "application/json",
          """{"ok":true,"service":"ai.hermes.oracle","resolver":"shizuku_reflection_v1","ts":${System.currentTimeMillis()}}"""
        )
      }

      "/owner" -> {
        val ports = session.parameters["ports"]?.firstOrNull() ?: ""
        NanoHTTPD.newFixedLengthResponse(
          NanoHTTPD.Response.Status.OK,
          "application/json",
          ownerJson(ports)
        )
      }

      else -> NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "not found")
    }
  }

  private fun ownerJson(ports: String): String {
    val uid = Process.myUid()
    val pid = Process.myPid()
    val packageName = appContext.packageName
    val packagesForUid = try {
      appContext.packageManager.getPackagesForUid(uid)?.toList().orEmpty()
    } catch (_: Throwable) {
      emptyList()
    }

    val shizukuAvailable = shizukuPingBinder()
    val shizukuPermissionGranted = shizukuPermissionGranted()

    return buildString {
      append("{")
      append("\"ok\":true,")
      append("\"resolver\":\"shizuku_reflection_v1\",")
      append("\"mode\":\"service_self_probe\",")
      append("\"requested_ports\":\"").append(jsonEscape(ports)).append("\",")
      append("\"package_name\":\"").append(jsonEscape(packageName)).append("\",")
      append("\"uid\":").append(uid).append(",")
      append("\"pid\":").append(pid).append(",")
      append("\"packages_for_uid\":[")
      packagesForUid.forEachIndexed { i, pkg ->
        if (i > 0) append(",")
        append("\"").append(jsonEscape(pkg)).append("\"")
      }
      append("],")
      append("\"shizuku_ping_binder\":").append(jsonBoolOrNull(shizukuAvailable)).append(",")
      append("\"shizuku_permission_granted\":").append(jsonBoolOrNull(shizukuPermissionGranted)).append(",")
      append("\"note\":\"stage2 runtime probe active; explicit port->owner resolution not yet implemented\"")
      append("}")
    }
  }

  private fun shizukuPingBinder(): Boolean? {
    return try {
      val cls = Class.forName("rikka.shizuku.Shizuku")
      val m = cls.getMethod("pingBinder")
      (m.invoke(null) as? Boolean)
    } catch (_: Throwable) {
      null
    }
  }

  private fun shizukuPermissionGranted(): Boolean? {
    return try {
      val cls = Class.forName("rikka.shizuku.Shizuku")
      val m = cls.getMethod("checkSelfPermission")
      when (val v = m.invoke(null)) {
        is Int -> v == 0
        else -> null
      }
    } catch (_: Throwable) {
      null
    }
  }

  private fun jsonBoolOrNull(v: Boolean?): String {
    return when (v) {
      true -> "true"
      false -> "false"
      null -> "null"
    }
  }

  private fun jsonEscape(input: String): String {
    val sb = StringBuilder(input.length + 16)
    input.forEach { ch ->
      when (ch) {
        '\\' -> sb.append("\\\\")
        '"' -> sb.append("\\\"")
        '\b' -> sb.append("\\b")
        '\u000C' -> sb.append("\\f")
        '\n' -> sb.append("\\n")
        '\r' -> sb.append("\\r")
        '\t' -> sb.append("\\t")
        else -> {
          if (ch.code < 0x20) {
            sb.append("\\u%04x".format(ch.code))
          } else {
            sb.append(ch)
          }
        }
      }
    }
    return sb.toString()
  }
}
