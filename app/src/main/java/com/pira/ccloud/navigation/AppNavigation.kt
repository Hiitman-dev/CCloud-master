package com.pira.ccloud.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pira.ccloud.screens.AboutScreen
import com.pira.ccloud.screens.CountryScreen
import com.pira.ccloud.screens.FavoritesScreen
import com.pira.ccloud.screens.HomeScreen
import com.pira.ccloud.screens.MoviesScreen
import com.pira.ccloud.screens.SearchScreen
import com.pira.ccloud.screens.SeriesScreen
import com.pira.ccloud.screens.SettingsScreen
import com.pira.ccloud.screens.SingleMovieScreen
import com.pira.ccloud.screens.SingleSeriesScreen
import com.pira.ccloud.screens.SplashScreen
import com.pira.ccloud.screens.WatchAnalyticsScreen
import com.pira.ccloud.ui.country.CountryViewModel
import com.pira.ccloud.ui.home.HomeViewModel
import com.pira.ccloud.ui.movies.MoviesViewModel
import com.pira.ccloud.ui.search.SearchViewModel
import com.pira.ccloud.ui.series.SeriesViewModel
<<<<<<< HEAD
import com.pira.ccloud.ui.theme.AppColors
=======
>>>>>>> a3b2b8d4583bd1a3fccae41b6a62baf99ea7570c
import com.pira.ccloud.ui.theme.ThemeSettings
import com.pira.ccloud.data.model.FontSettings

@Composable
fun AppNavigation(
    navController: NavHostController,
    themeSettings: ThemeSettings,
    onThemeSettingsChanged: (ThemeSettings) -> Unit = {},
    onFontSettingsChanged: (FontSettings) -> Unit = {}
) {
    val homeViewModel = viewModel<HomeViewModel>()
    val moviesViewModel = viewModel<MoviesViewModel>()
    val seriesViewModel = viewModel<SeriesViewModel>()
    val searchViewModel = viewModel<SearchViewModel>()
    val countryViewModel = viewModel<CountryViewModel>()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Splash.route,
        // A gentle fade+slide instead of the default hard cut between
        // screens. Kept short (220-260ms) and low-distance (a quarter of
        // the screen width) so it reads as smooth/premium rather than
        // slow or attention-grabbing.
        enterTransition = {
            fadeIn(animationSpec = tween(220)) +
                slideInHorizontally(animationSpec = tween(260)) { fullWidth -> fullWidth / 4 }
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200)) +
                slideOutHorizontally(animationSpec = tween(220)) { fullWidth -> -fullWidth / 6 }
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(220)) +
                slideInHorizontally(animationSpec = tween(260)) { fullWidth -> -fullWidth / 4 }
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) +
                slideOutHorizontally(animationSpec = tween(220)) { fullWidth -> fullWidth / 6 }
        }
    ) {
        composable(route = AppScreens.Splash.route) {
            val isSystemInDarkMode = isSystemInDarkTheme()
            SplashScreen(
                onTimeout = {
                    navController.popBackStack()
                    navController.navigate(AppScreens.Home.route) {
                        launchSingleTop = true
                    }
                },
                backgroundColor = when (themeSettings.themeMode) {
                    com.pira.ccloud.ui.theme.ThemeMode.DARK -> {
                        AppColors.current.splashBackground
                    }
                    com.pira.ccloud.ui.theme.ThemeMode.LIGHT -> {
                        AppColors.current.splashBackground
                    }
                    com.pira.ccloud.ui.theme.ThemeMode.SYSTEM -> {
                        AppColors.current.splashBackground
                    }
                }
            )
        }

        composable(route = AppScreens.Home.route) {
            HomeScreen(viewModel = homeViewModel, navController = navController)
        }

        composable(route = AppScreens.Movies.route) {
            MoviesScreen(viewModel = moviesViewModel, navController = navController)
        }

        composable(route = AppScreens.Series.route) {
            SeriesScreen(viewModel = seriesViewModel, navController = navController)
        }

        composable(route = AppScreens.Search.route) {
            SearchScreen(viewModel = searchViewModel, navController = navController)
        }

        composable(route = AppScreens.Settings.route) {
            SettingsScreen(themeSettings, onThemeSettingsChanged, onFontSettingsChanged, navController)
        }

        composable(route = AppScreens.Favorites.route) {
            FavoritesScreen(navController)
        }

        composable(route = AppScreens.About.route) {
            AboutScreen(navController)
        }

        composable(route = AppScreens.WatchAnalytics.route) {
            WatchAnalyticsScreen(navController)
        }

        composable(
            route = AppScreens.SingleMovie.route,
            arguments = listOf(navArgument("movieId") { defaultValue = "0" })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
            SingleMovieScreen(movieId = movieId, navController = navController)
        }

        composable(
            route = AppScreens.SingleSeries.route,
            arguments = listOf(navArgument("seriesId") { defaultValue = "0" })
        ) { backStackEntry ->
            val seriesId = backStackEntry.arguments?.getString("seriesId")?.toIntOrNull() ?: 0
            SingleSeriesScreen(seriesId = seriesId, navController = navController)
        }

        composable(
            route = AppScreens.Country.route,
            arguments = listOf(navArgument("countryId") { defaultValue = "0" })
        ) { backStackEntry ->
            val countryId = backStackEntry.arguments?.getString("countryId")?.toIntOrNull() ?: 0
            CountryScreen(countryId = countryId, viewModel = countryViewModel, navController = navController)
        }
    }
}
