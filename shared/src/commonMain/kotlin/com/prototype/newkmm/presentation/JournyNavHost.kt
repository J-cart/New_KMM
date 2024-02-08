package com.prototype.newkmm.presentation

import androidx.compose.runtime.Composable
import com.prototype.newkmm.PlatformUtil
import com.prototype.newkmm.domain.JournyEntryDataSource
import com.prototype.newkmm.presentation.screen.HomeScreen
import com.prototype.newkmm.presentation.screen.RecordScreen
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.transition.NavTransition

@Composable
fun JournyNavHost(
    platformUtil: PlatformUtil,
    navigator: Navigator,
    journyEntryDataSource: JournyEntryDataSource
) {

    NavHost(
        navigator = navigator,
        navTransition = NavTransition(),
        initialRoute = JournyNavigationRoute.HomeScreen.route
    ) {
        scene(
            JournyNavigationRoute.HomeScreen.route
        ) {
            HomeScreen(navigator)
        }

        scene(
            JournyNavigationRoute.RecordScreen.route
        ) {
            RecordScreen(
                platformUtil = platformUtil,
                journyEntryDataSource = journyEntryDataSource,
                onNavigateUp = {
                    navigator.popBackStack()
                })
        }
    }

}


sealed class JournyNavigationRoute(val route: String) {
    object HomeScreen : JournyNavigationRoute("HomeScreen")

    object RecordScreen : JournyNavigationRoute("RecordScreen")
}