package com.pira.ccloud

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pira.ccloud.navigation.AppNavigation
import com.pira.ccloud.navigation.AppScreens
import com.pira.ccloud.navigation.BottomNavigationBar
import com.pira.ccloud.navigation.SidebarNavigation
import com.pira.ccloud.components.AmbientBackground
import com.pira.ccloud.ui.theme.CCloudTheme
import com.pira.ccloud.ui.theme.ThemeManager
import com.pira.ccloud.ui.theme.ThemeSettings
import com.pira.ccloud.utils.StorageUtils
import com.pira.ccloud.data.model.FontSettings
import com.pira.ccloud.utils.DeviceUtils
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!DeviceUtils.isTv(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            enableEdgeToEdge()
        }

        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val themeManager = ThemeManager(LocalContext.current)
    var themeSettings by remember { mutableStateOf(themeManager.loadThemeSettings()) }
    val context = LocalContext.current
    var fontSettings by remember { mutableStateOf(StorageUtils.loadFontSettings(context)) }

    LaunchedEffect(themeSettings) {
        themeManager.saveThemeSettings(themeSettings)
    }

    CCloudTheme(themeSettings, fontSettings) {
        Box(modifier = Modifier.fillMaxSize()) {
            AmbientBackground(modifier = Modifier.fillMaxSize())
            MainScreen(
                themeSettings = themeSettings,
                onThemeSettingsChanged = { themeSettings = it },
                onFontSettingsChanged = { fontSettings = it }
            )
        }
    }
}

@Composable
fun MainScreen(
    themeSettings: ThemeSettings,
    onThemeSettingsChanged: (ThemeSettings) -> Unit = {},
    onFontSettingsChanged: (FontSettings) -> Unit = {}
) {
    val navController = rememberNavController()
    val isTv = DeviceUtils.isTv(LocalContext.current)

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val currentScreen = when {
        currentRoute?.startsWith("single_movie") == true -> AppScreens.SingleMovie
        currentRoute?.startsWith("single_series") == true -> AppScreens.SingleSeries
        currentRoute?.startsWith("country") == true -> AppScreens.Country
        currentRoute == "favorites" -> AppScreens.Favorites
        currentRoute == "about" -> AppScreens.About
        currentRoute == "search" -> AppScreens.Search
        else -> AppScreens.bottomNavScreens.find { it.route == currentRoute } ?: AppScreens.Home
    }

    // Status/navigation bar icon appearance is already handled reactively,
    // correctly (including resolving ThemeMode.SYSTEM against the real
    // system setting), inside CCloudTheme's own SideEffect via
    // WindowCompat's insets controller. A second, independent
    // SystemUiController call here used to re-load its own stale copy of
    // the theme settings and always assumed "not dark" for System Default
    // mode - fighting with CCloudTheme's correct result and being the
    // actual cause of dark mode "not applying"/looking wrong. Removed
    // rather than duplicated.

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // Scaffold normally paints a solid MaterialTheme.colorScheme.background
        // rectangle behind the bottomBar slot. Since BottomNavigationBar is a
        // small floating glass pill (not an edge-to-edge bar), that solid
        // rectangle used to show through as a flat "page" behind and around
        // it. Making the Scaffold itself transparent removes that backing
        // panel so only the app content - visible through the glass - sits
        // behind the bar.
        containerColor = Color.Transparent,
        bottomBar = {
            if (!isTv && currentScreen.showBottomBar && currentRoute != AppScreens.Splash.route) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        if (isTv) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                SidebarNavigation(navController)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 24.dp, top = 24.dp, bottom = 24.dp)
                ) {
                    AppNavigation(navController, themeSettings, onThemeSettingsChanged, onFontSettingsChanged)
                }
            }
        } else {
            // Deliberately only consume the top inset here, not the bottom
            // one Scaffold reserves for the bottomBar. That lets each
            // screen's own scrollable content extend all the way to the
            // real bottom of the screen and scroll underneath the floating
            // glass nav bar, instead of stopping short in a hard rectangle
            // above it. Screens that show the bottom bar reserve their own
            // bottom content padding so their last item still ends up fully
            // visible once scrolled past the bar.
            Box(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .fillMaxSize()
            ) {
                AppNavigation(navController, themeSettings, onThemeSettingsChanged, onFontSettingsChanged)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val themeManager = ThemeManager(LocalContext.current)
    val themeSettings = themeManager.loadThemeSettings()

    CCloudTheme(themeSettings) {
        MainScreen(themeSettings = themeSettings)
    }
}
