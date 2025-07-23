package com.example.lemonpeel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.Color
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
import androidx.compose.material3.Button
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.lemonpeel.RecipeHistoryManager
import com.example.lemonpeel.RecipeHistoryEntry
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment

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
    val context = LocalContext.current
    val recipeHistoryFlow = remember { RecipeHistoryManager.getRecipeHistory(context) }
    val recipeHistory = recipeHistoryFlow.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    var funPhrase by remember { mutableStateOf("Feeling Hungry?") }
    val funPhrases = listOf(
        "Feeling hungry?",
        "Ready for something tasty?",
        "What’s for dinner tonight?",
        "Craving something new?",
        "What can we make today?"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Welcome to LemonPeel!", style = MaterialTheme.typography.headlineMedium)
        Text(funPhrase, style = MaterialTheme.typography.titleMedium)
        Button(
            onClick = {
                // Change fun phrase and add a new recipe
                funPhrase = funPhrases.random()
                coroutineScope.launch {
                    val recipeName = "Recipe #${(1..1000).random()}"
                    RecipeHistoryManager.addRecipe(context, recipeName)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Recipes")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Recipe History:", style = MaterialTheme.typography.titleMedium)
        if (recipeHistory.value.isEmpty()) {
            Text("No recipes yet. Tap 'Generate Recipes' to get started!")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recipeHistory.value.forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${entry.name} — ${entry.date}")
                        IconButton(onClick = {
                            coroutineScope.launch {
                                RecipeHistoryManager.deleteRecipe(context, entry.name, entry.date)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
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