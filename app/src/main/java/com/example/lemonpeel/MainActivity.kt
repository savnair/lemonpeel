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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.lemonpeel.ui.theme.LemonPeelTheme

sealed class Screen(val title: String, val icon: ImageVector) {
    object Inventory : Screen("Inventory", Icons.Filled.List)
    object Recipes : Screen("Recipes", Icons.Filled.Star)
}

@Composable
fun LemonPeelApp() {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Inventory) }
    LemonPeelTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedScreen is Screen.Inventory,
                        onClick = { selectedScreen = Screen.Inventory },
                        icon = { Icon(Screen.Inventory.icon, contentDescription = "Inventory") },
                        label = { Text("Inventory") }
                    )
                    NavigationBarItem(
                        selected = selectedScreen is Screen.Recipes,
                        onClick = { selectedScreen = Screen.Recipes },
                        icon = { Icon(Screen.Recipes.icon, contentDescription = "Recipes") },
                        label = { Text("Recipes") }
                    )
                }
            }
        ) { innerPadding ->
            when (selectedScreen) {
                is Screen.Inventory -> InventoryScreen(Modifier.padding(innerPadding))
                is Screen.Recipes -> RecipesScreen(Modifier.padding(innerPadding))
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