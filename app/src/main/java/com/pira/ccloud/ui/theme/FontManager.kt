package com.pira.ccloud.ui.theme

import android.content.Context
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.pira.ccloud.data.model.FontSettings
import com.pira.ccloud.data.model.FontType
import com.pira.ccloud.utils.StorageUtils

object FontManager {
    private var vazirmatnFontFamily: FontFamily? = null
    private var yekanBakhFontFamily: FontFamily? = null

    fun loadFontFamily(context: Context, fontType: FontType): FontFamily? {
        return when (fontType) {
            FontType.DEFAULT -> null // Use system default
            FontType.VAZIRMATN -> {
                if (vazirmatnFontFamily == null) {
                    // Create a custom font family using resource identifiers
                    vazirmatnFontFamily = FontFamily(
                        androidx.compose.ui.text.font.Font(
                            com.pira.ccloud.R.font.vazirmatn_regular,
                            FontWeight.Normal,
                            FontStyle.Normal
                        ),
                        androidx.compose.ui.text.font.Font(
                            com.pira.ccloud.R.font.vazirmatn_bold,
                            FontWeight.Bold,
                            FontStyle.Normal
                        ),
                        androidx.compose.ui.text.font.Font(
                            com.pira.ccloud.R.font.vazirmatn_light,
                            FontWeight.Light,
                            FontStyle.Normal
                        )
                    )
                }
                vazirmatnFontFamily
            }
            FontType.YEKAN_BAKH -> {
                if (yekanBakhFontFamily == null) {
                    // Full weight range so every Typography style (body, title, label...)
                    // resolves to the matching Yekan Bakh weight automatically.
                    yekanBakhFontFamily = FontFamily(
                        androidx.compose.ui.text.font.Font(
                            com.pira.ccloud.R.font.yekan_bakh_thin,
                            FontWeight.Thin,
                            FontStyle.Normal
                        ),
                        androidx.compose.ui.text.font.Font(
                            com.pira.ccloud.R.font.yekan_bakh_light,
                            FontWeight.Light,
                            FontStyle.Normal
                        ),
                        androidx.compose.ui.text.font.Font(
                            com.pira.ccloud.R.font.yekan_bakh_regular,
                            FontWeight.Normal,
                            FontStyle.Normal
                        ),
                        androidx.compose.ui.text.font.Font(
                            com.pira.ccloud.R.font.yekan_bakh_semibold,
                            FontWeight.SemiBold,
                            FontStyle.Normal
                        ),
                        androidx.compose.ui.text.font.Font(
                            com.pira.ccloud.R.font.yekan_bakh_bold,
                            FontWeight.Bold,
                            FontStyle.Normal
                        ),
                        androidx.compose.ui.text.font.Font(
                            com.pira.ccloud.R.font.yekan_bakh_extrabold,
                            FontWeight.ExtraBold,
                            FontStyle.Normal
                        ),
                        androidx.compose.ui.text.font.Font(
                            com.pira.ccloud.R.font.yekan_bakh_black,
                            FontWeight.Black,
                            FontStyle.Normal
                        )
                    )
                }
                yekanBakhFontFamily
            }
        }
    }
}

/**
 * Steelfish is a bold, condensed Latin display face - not suitable as the
 * whole app's body font. It's exposed separately so specific composables
 * (the app logo/wordmark, big screen titles, CTA buttons) can opt into it
 * directly via `fontFamily = SteelfishFontFamily`, independent of whatever
 * the user picked in Font Settings.
 */
val SteelfishFontFamily = FontFamily(
    androidx.compose.ui.text.font.Font(
        com.pira.ccloud.R.font.steelfish_regular,
        FontWeight.Normal,
        FontStyle.Normal
    ),
    androidx.compose.ui.text.font.Font(
        com.pira.ccloud.R.font.steelfish_regular_italic,
        FontWeight.Normal,
        FontStyle.Italic
    ),
    androidx.compose.ui.text.font.Font(
        com.pira.ccloud.R.font.steelfish_bold,
        FontWeight.Bold,
        FontStyle.Normal
    ),
    androidx.compose.ui.text.font.Font(
        com.pira.ccloud.R.font.steelfish_bold_italic,
        FontWeight.Bold,
        FontStyle.Italic
    ),
    androidx.compose.ui.text.font.Font(
        com.pira.ccloud.R.font.steelfish_extrabold,
        FontWeight.ExtraBold,
        FontStyle.Normal
    ),
    androidx.compose.ui.text.font.Font(
        com.pira.ccloud.R.font.steelfish_extrabold_italic,
        FontWeight.ExtraBold,
        FontStyle.Italic
    )
)

/** The decorative outline cut of Steelfish, for special one-off branding moments. */
val SteelfishOutlineFontFamily = FontFamily(
    androidx.compose.ui.text.font.Font(
        com.pira.ccloud.R.font.steelfish_outline,
        FontWeight.Normal,
        FontStyle.Normal
    )
)

@Composable
fun rememberFontSettings(): FontSettings {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember { StorageUtils.loadFontSettings(context) }
}