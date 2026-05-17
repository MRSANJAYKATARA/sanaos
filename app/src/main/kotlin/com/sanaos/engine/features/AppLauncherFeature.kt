package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import com.sanaos.engine.SanaBrain

class AppLauncherFeature : SanaFeature {
    private var cache: List<Pair<String, String>> = emptyList()

    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.LaunchApp -> launchApp(intent.appName, context)
            else -> FeatureResult(false, "Boss, app launch command samajh nahi aaya.")
        }
    }

    private fun launchApp(query: String, context: Context): FeatureResult {
        val apps = if (cache.isEmpty()) loadApps(context).also { cache = it } else cache
        val clean = query.lowercase().trim()
        val best = apps.maxByOrNull { score(clean, it.first) }
        if (best == null || score(clean, best.first) == 0) {
            return FeatureResult(false, "Boss, $query app nahi mili.")
        }
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(best.second)
                ?: return FeatureResult(false, "Boss, app launch intent nahi mila.")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            FeatureResult(true, "Boss, ${best.first} open kar diya.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, app open nahi ho paayi.")
        }
    }

    private fun loadApps(context: Context): List<Pair<String, String>> {
        return try {
            context.packageManager.getInstalledApplications(0).mapNotNull {
                val label = context.packageManager.getApplicationLabel(it).toString()
                if (label.isBlank()) null else label to it.packageName
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun score(q: String, c: String): Int {
        val lc = c.lowercase()
        return when {
            lc == q -> 100
            lc.startsWith(q) -> 80
            lc.contains(q) -> 50
            else -> 0
        }
    }
}
