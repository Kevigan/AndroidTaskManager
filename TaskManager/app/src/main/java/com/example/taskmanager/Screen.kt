package com.example.taskmanager

import androidx.annotation.DrawableRes

sealed class Screen(val route: String, val title: String, @DrawableRes val icon: Int) {
    object HomeScreen : Screen("home_screen", "Home", R.drawable.baseline_home_24)
    object AddScreen : Screen("add_screen", "Add", R.drawable.baseline_add_box_24)
    object AccountScreen : Screen("account_screen", "Account", R.drawable.baseline_account_circle_24)
    object TestScreen : Screen("test_screen", "Test", R.drawable.baseline_account_circle_24)
}

    val drawerScreens = listOf(
        Screen.HomeScreen,
        Screen.AccountScreen
    )
