package com.prototype.newkmm.presentation.screen

import androidx.compose.runtime.Composable
import com.prototype.newkmm.PlatformUtil
import com.prototype.newkmm.domain.JournyEntryDataSource
import com.prototype.newkmm.presentation.JournyNavHost
import moe.tlaster.precompose.navigation.rememberNavigator

@Composable
fun MainView(platformUtil: PlatformUtil, journyEntryDataSource: JournyEntryDataSource) {

    val navigator = rememberNavigator()
    JournyNavHost(platformUtil,navigator,journyEntryDataSource)

}