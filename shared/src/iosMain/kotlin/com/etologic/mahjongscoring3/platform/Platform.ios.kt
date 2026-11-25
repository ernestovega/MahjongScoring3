package com.etologic.mahjongscoring3.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import com.etologic.mahjongscoring3.domain.usecases.ExportedFile
import com.etologic.mahjongscoring3.resources.Res
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryAmbient
import platform.AVFAudio.setActive
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.Foundation.preferredLanguages
import platform.Foundation.stringWithContentsOfURL
import platform.Foundation.writeToURL
import platform.StoreKit.SKStoreReviewController
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowScene
import platform.UIKit.popoverPresentationController
import platform.UniformTypeIdentifiers.UTTypeData
import platform.UniformTypeIdentifiers.UTTypeJSON
import platform.darwin.NSObject

private fun topViewController(): UIViewController? {
    var controller = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (controller?.presentedViewController != null) {
        controller = controller.presentedViewController
    }
    return controller
}

private fun present(controller: UIViewController) {
    val top = topViewController() ?: return
    // iPad needs an anchor for popovers
    controller.popoverPresentationController?.sourceView = top.view
    top.presentViewController(controller, animated = true, completion = null)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class IosPlatformActions : PlatformActions {

    override val appVersionName: String =
        NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: ""

    override fun shareText(text: String, chooserTitle: String) {
        present(UIActivityViewController(activityItems = listOf(text), applicationActivities = null))
    }

    override fun shareFile(file: ExportedFile, chooserTitle: String) {
        val url = NSURL.fileURLWithPath(NSTemporaryDirectory()).URLByAppendingPathComponent(file.fileName) ?: return
        @Suppress("CAST_NEVER_SUCCEEDS")
        (file.content as NSString).writeToURL(url, atomically = true, encoding = NSUTF8StringEncoding, error = null)
        present(UIActivityViewController(activityItems = listOf(url), applicationActivities = null))
    }

    override fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        UIApplication.sharedApplication.openURL(nsUrl, options = emptyMap<Any?, Any>(), completionHandler = null)
    }

    override fun sendEmail(address: String, subject: String, chooserTitle: String): Boolean {
        val encodedSubject = subject.replace(" ", "%20")
        val url = NSURL.URLWithString("mailto:$address?subject=$encodedSubject") ?: return false
        if (!UIApplication.sharedApplication.canOpenURL(url)) return false
        UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
        return true
    }

    override fun closeApp() {
        // iOS apps are not closed programmatically
    }

    override fun requestInAppReview() {
        val scene = UIApplication.sharedApplication.connectedScenes.firstOrNull { it is UIWindowScene } as? UIWindowScene
        if (scene != null) {
            SKStoreReviewController.requestReviewInScene(scene)
        }
    }
}

@Composable
actual fun rememberPlatformActions(): PlatformActions = remember { IosPlatformActions() }

@OptIn(ExperimentalForeignApi::class)
private class DocumentPickerDelegate(
    private val onResult: (String?) -> Unit,
) : NSObject(), UIDocumentPickerDelegateProtocol {

    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        val text = url?.let {
            val accessing = it.startAccessingSecurityScopedResource()
            val content = NSString.stringWithContentsOfURL(it, encoding = NSUTF8StringEncoding, error = null)
            if (accessing) it.stopAccessingSecurityScopedResource()
            content
        }
        onResult(text)
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onResult(null)
    }
}

@Composable
actual fun rememberJsonFilePicker(onFilePicked: (String?) -> Unit): () -> Unit {
    val currentOnFilePicked = rememberUpdatedState(onFilePicked)
    // The picker keeps only a weak reference to its delegate, so we keep it here.
    val delegate = remember { DocumentPickerDelegate { currentOnFilePicked.value(it) } }
    return {
        val picker = UIDocumentPickerViewController(forOpeningContentTypes = listOf(UTTypeJSON, UTTypeData), asCopy = true)
        picker.delegate = delegate
        picker.allowsMultipleSelection = false
        present(picker)
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberDiceSoundPlayer(): () -> Unit {
    val player = remember {
        runCatching {
            AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryAmbient, error = null)
            AVAudioSession.sharedInstance().setActive(true, error = null)
            val url = NSURL.URLWithString(Res.getUri("files/shake_dice.mp3"))
            url?.let { AVAudioPlayer(contentsOfURL = it, error = null) }?.apply { prepareToPlay() }
        }.getOrNull()
    }
    return {
        player?.let {
            it.currentTime = 0.0
            it.play()
        }
    }
}

actual object LocalAppLocale {
    private const val LANG_KEY = "AppleLanguages"
    private val default: String = NSLocale.preferredLanguages.first() as String
    private val LocalAppLocaleValue = staticCompositionLocalOf { default }

    actual val current: String
        @Composable get() = LocalAppLocaleValue.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val new = value ?: default
        if (value == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LANG_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(listOf(new), LANG_KEY)
        }
        return LocalAppLocaleValue.provides(new)
    }
}
