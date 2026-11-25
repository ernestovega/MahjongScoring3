package com.etologic.mahjongscoring3.platform

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.etologic.mahjongscoring3.domain.usecases.ExportedFile
import com.etologic.mahjongscoring3.resources.Res
import com.google.android.play.core.review.ReviewManagerFactory
import java.io.File
import java.util.Locale

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

private class AndroidPlatformActions(private val context: Context) : PlatformActions {

    override val appVersionName: String =
        runCatching {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull().orEmpty()

    private fun start(intent: Intent): Boolean =
        try {
            if (context !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }

    override fun shareText(text: String, chooserTitle: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        start(Intent.createChooser(shareIntent, chooserTitle))
    }

    override fun shareFile(file: ExportedFile, chooserTitle: String) {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val outFile = File(dir, file.fileName).apply { writeText(file.content) }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", outFile)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = file.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        start(Intent.createChooser(shareIntent, chooserTitle))
    }

    override fun openUrl(url: String) {
        start(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun sendEmail(address: String, subject: String, chooserTitle: String): Boolean {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        return start(Intent.createChooser(intent, chooserTitle))
    }

    override fun closeApp() {
        context.findActivity()?.finish()
    }

    override fun requestInAppReview() {
        val activity = context.findActivity() ?: return
        val manager = ReviewManagerFactory.create(activity)
        manager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                manager.launchReviewFlow(activity, task.result)
            }
        }
    }
}

@Composable
actual fun rememberPlatformActions(): PlatformActions {
    val context = LocalContext.current
    return remember(context) { AndroidPlatformActions(context) }
}

@Composable
actual fun rememberJsonFilePicker(onFilePicked: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val text = uri?.let {
            runCatching {
                context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader -> reader.readText() }
            }.getOrNull()
        }
        onFilePicked(text)
    }
    return { launcher.launch(arrayOf("*/*")) }
}

@Composable
actual fun rememberDiceSoundPlayer(): () -> Unit {
    val context = LocalContext.current
    val player = remember(context) {
        val soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
        val soundId = runCatching {
            // Compose resources live in the APK assets: "file:///android_asset/<path>"
            val assetPath = Res.getUri("files/shake_dice.mp3").removePrefix("file:///android_asset/")
            context.assets.openFd(assetPath).use { soundPool.load(it, 1) }
        }.getOrNull()
        soundPool to soundId
    }
    DisposableEffect(player) {
        onDispose { player.first.release() }
    }
    return {
        player.second?.let { player.first.play(it, 1f, 1f, 0, 0, 1f) }
    }
}

actual object LocalAppLocale {
    private var default: Locale? = null

    actual val current: String
        @Composable get() = Locale.getDefault().language

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current
        if (default == null) {
            default = Locale.getDefault()
        }
        val new = if (value == null) default!! else Locale(value)
        Locale.setDefault(new)
        configuration.setLocale(new)
        val resources = LocalContext.current.resources
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return LocalConfiguration.provides(configuration)
    }
}
