package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.AppTab
import com.example.ui.components.AppTopBar
import com.example.ui.screens.AiVideoScreen
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.MapsDiscoveryScreen
import com.example.ui.screens.MultiViewPlayerScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TtsAnnouncerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(AppTab.BROWSER) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                currentTab = selectedTab,
                onMapsClick = { selectedTab = AppTab.MAPS },
                onProfileClick = { selectedTab = AppTab.PROFILE }
            )
        },
        bottomBar = {
            AppBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppTab.BROWSER -> {
                    BrowserScreen(
                        viewModel = viewModel,
                        onNavigateToMultiGrid = { selectedTab = AppTab.MULTI_VIEW }
                    )
                }
                AppTab.MULTI_VIEW -> {
                    MultiViewPlayerScreen(
                        viewModel = viewModel,
                        onBackToBrowser = { selectedTab = AppTab.BROWSER }
                    )
                }
                AppTab.AI_VIDEO -> {
                    AiVideoScreen(viewModel = viewModel)
                }
                AppTab.AI_VOICE -> {
                    TtsAnnouncerScreen(viewModel = viewModel)
                }
                AppTab.MAPS -> {
                    MapsDiscoveryScreen(viewModel = viewModel)
                }
                AppTab.PROFILE -> {
                    ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}
