package com.example.lemonpeel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Chat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.example.lemonpeel.ui.theme.LemonPeelTheme

sealed class Screen(val title: String, val icon: ImageVector? = null, val isHome: Boolean = false) {
    object Recipes : Screen("Recipes", Icons.Filled.Star)
    object Kitchen : Screen("Kitchen", Icons.Filled.List)
    object Home : Screen("Home", null, true) // Lemon icon, custom
    object Forums : Screen("Forums", Icons.Filled.Chat)
    object Profile : Screen("Profile", Icons.Filled.Person)
}

@Composable
fun LemonPeelApp() {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val screens = listOf(
        Screen.Recipes,
        Screen.Kitchen,
        Screen.Home,
        Screen.Forums,
        Screen.Profile
    )
    LemonPeelTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    screens.forEachIndexed { index, screen ->
                        if (screen.isHome) {
                            NavigationBarItem(
                                selected = selectedScreen is Screen.Home,
                                onClick = { selectedScreen = Screen.Home },
                                icon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_lemon),
                                        contentDescription = "Home",
                                        modifier = Modifier
                                            .size(40.dp) // Larger size for lemon
                                    )
                                },
                                label = { Text("Home") }
                            )
                        } else {
                            NavigationBarItem(
                                selected = selectedScreen == screen,
                                onClick = { selectedScreen = screen },
                                icon = {
                                    screen.icon?.let {
                                        Icon(it, contentDescription = screen.title)
                                    }
                                },
                                label = { Text(screen.title) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            when (selectedScreen) {
                is Screen.Recipes -> RecipesScreen(Modifier.padding(innerPadding))
                is Screen.Kitchen -> KitchenScreen(Modifier.padding(innerPadding))
                is Screen.Home -> HomeScreen(Modifier.padding(innerPadding))
                is Screen.Forums -> ForumsScreen(Modifier.padding(innerPadding))
                is Screen.Profile -> ProfileScreen(Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
fun InventoryScreen(modifier: Modifier = Modifier) {
    Text("Inventory (ingredients, tools, pots/pans)", modifier = modifier)
}

@Composable
fun RecipesScreen(modifier: Modifier = Modifier) {
    Text("Recipes (suggested based on your inventory)", modifier = modifier)
}

@Composable
fun KitchenScreen(modifier: Modifier = Modifier) {
    Text("Kitchen (ingredients, tools, pots/pans)", modifier = modifier)
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Text("Home (LemonPeel)", modifier = modifier)
}

@Composable
fun ForumsScreen(modifier: Modifier = Modifier) {
    Text("Forums (share & discuss)", modifier = modifier)
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Text("Profile (your info)", modifier = modifier)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LemonPeelApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryScreenPreview() {
    LemonPeelTheme {
        InventoryScreen()
    }
}