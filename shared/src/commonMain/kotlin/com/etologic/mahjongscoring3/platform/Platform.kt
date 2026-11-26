package com.etologic.mahjongscoring3.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.key
import androidx.compose.runtime.staticCompositionLocalOf
import com.etologic.mahjongscoring3.domain.usecases.ExportedFile

/** Things the shared UI needs from the operating system. One implementation per platform. */
interface PlatformActions {
    val appVersionName: String
    fun shareText(text: String, chooserTitle: String)
    fun shareFile(file: ExportedFile, chooserTitle: String)
    fun openUrl(url: String)
    /** Returns false when there is no app able to send emails. */
    fun sendEmail(address: String, subject: String, chooserTitle: String): Boolean
    fun requestInAppReview()
    /** Closes the app (Android back button on the main screen). No-op where not applicable. */
    fun closeApp()
}

@Composable
expect fun rememberPlatformActions(): PlatformActions

/** Lets the UI tests replace the operating system actions (share, review, e-mail...). Null in the app. */
val LocalPlatformActionsOverride = staticCompositionLocalOf<PlatformActions?> { null }

/** Returns a function that opens the system file picker; [onFilePicked] receives the file text, or null if cancelled. */
@Composable
expect fun rememberJsonFilePicker(onFilePicked: (String?) -> Unit): () -> Unit

/** Returns a function that plays the dice shaking sound. */
@Composable
expect fun rememberDiceSoundPlayer(): () -> Unit

/** Lets the app use a language different from the system one (null = system language). */
expect object LocalAppLocale {
    val current: String @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}

@Composable
fun AppLocaleEnvironment(language: String?, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppLocale provides language) {
        key(language) {
            content()
        }
    }
}

const val CHINESE = "zh"
const val DUTCH = "nl"
const val ENGLISH = "en"
const val FRENCH = "fr"
const val SPANISH = "es"
const val ITALIAN = "it"

/** Same order as Mahjong Scoring 2 language picker. */
val supportedLanguages = listOf(CHINESE, DUTCH, ENGLISH, FRENCH, SPANISH, ITALIAN)
