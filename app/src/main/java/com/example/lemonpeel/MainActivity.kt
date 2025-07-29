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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.IconButton

// Import the KitchenManager
import com.example.lemonpeel.KitchenManager
import com.example.lemonpeel.KitchenItem
import com.example.lemonpeel.KitchenCategory

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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<KitchenItem?>(null) }
    
    // Get persistent kitchen items
    val allKitchenItems = KitchenManager.getKitchenItems(context).collectAsState(initial = emptyList())
    
    // Filter items by category
    val ingredients = allKitchenItems.value.filter { it.category == KitchenCategory.INGREDIENTS }
    val tools = allKitchenItems.value.filter { it.category == KitchenCategory.TOOLS }
    val cookware = allKitchenItems.value.filter { it.category == KitchenCategory.COOKWARE }
    
    val tabs = listOf("Ingredients", "Tools", "Cookware")
    
    Column(modifier = modifier.fillMaxSize()) {
        // Modern Header with Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFB347), // Softer orange
                            Color(0xFFFFA94D)  // Slightly darker orange
                        ),
                        tileMode = TileMode.Clamp
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                text = "Kitchen",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default
                ),
                color = Color.White
            )
        }
        
        // Tab Row with modern styling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF4F4F4),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tabs.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (selectedTab == index) Color(0xFFFFB347) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTab = index }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (selectedTab == index) Color.White else Color(0xFF757575)
                        )
                    }
                }
            }
        }
        
        // Content based on selected tab
        when (selectedTab) {
            0 -> KitchenItemList(
                items = ingredients,
                onAddItem = { showAddDialog = true },
                onEditItem = { item -> 
                    editingItem = item
                    showAddDialog = true 
                },
                onDeleteItem = { item ->
                    coroutineScope.launch {
                        KitchenManager.deleteKitchenItem(context, item.id)
                    }
                },
                category = KitchenCategory.INGREDIENTS
            )
            1 -> KitchenItemList(
                items = tools,
                onAddItem = { showAddDialog = true },
                onEditItem = { item -> 
                    editingItem = item
                    showAddDialog = true 
                },
                onDeleteItem = { item ->
                    coroutineScope.launch {
                        KitchenManager.deleteKitchenItem(context, item.id)
                    }
                },
                category = KitchenCategory.TOOLS
            )
            2 -> KitchenItemList(
                items = cookware,
                onAddItem = { showAddDialog = true },
                onEditItem = { item -> 
                    editingItem = item
                    showAddDialog = true 
                },
                onDeleteItem = { item ->
                    coroutineScope.launch {
                        KitchenManager.deleteKitchenItem(context, item.id)
                    }
                },
                category = KitchenCategory.COOKWARE
            )
        }
    }
    
    // Add/Edit Dialog
    if (showAddDialog) {
        AddEditKitchenItemDialog(
            item = editingItem,
            category = when (selectedTab) {
                0 -> KitchenCategory.INGREDIENTS
                1 -> KitchenCategory.TOOLS
                else -> KitchenCategory.COOKWARE
            },
            onDismiss = { 
                showAddDialog = false
                editingItem = null
            },
            onSave = { name, quantity ->
                val newItem = KitchenItem(
                    id = editingItem?.id ?: java.util.UUID.randomUUID().toString(),
                    name = name,
                    quantity = quantity,
                    category = when (selectedTab) {
                        0 -> KitchenCategory.INGREDIENTS
                        1 -> KitchenCategory.TOOLS
                        else -> KitchenCategory.COOKWARE
                    }
                )
                
                coroutineScope.launch {
                    KitchenManager.saveKitchenItem(context, newItem)
                }
                showAddDialog = false
                editingItem = null
            }
        )
    }
}

@Composable
fun KitchenItemList(
    items: List<KitchenItem>,
    onAddItem: () -> Unit,
    onEditItem: (KitchenItem) -> Unit,
    onDeleteItem: (KitchenItem) -> Unit,
    category: KitchenCategory
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Modern icon with shadow effect
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = Color(0xFFFFB347).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFFFFB347),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "No ${category.name.lowercase().replaceFirstChar { it.uppercase() }} yet",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF424242)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tap the + button to add your first item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(horizontal = 40.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    KitchenItemCard(
                        item = item,
                        onEdit = { onEditItem(item) },
                        onDelete = { onDeleteItem(item) }
                    )
                }
            }
        }
        
        FloatingActionButton(
            onClick = onAddItem,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(56.dp),
            containerColor = Color(0xFFFFB347),
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.Default.Add, 
                contentDescription = "Add Item",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun KitchenItemCard(
    item: KitchenItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Quantity: ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Blue
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditKitchenItemDialog(
    item: KitchenItem?,
    category: KitchenCategory,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var quantity by remember { mutableStateOf(item?.quantity?.toIntOrNull() ?: 1) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (item == null) "Add ${category.name.lowercase().replaceFirstChar { it.uppercase() }}" 
                else "Edit ${category.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quantity Stepper
                Text(
                    text = "Quantity",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF424242),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color(0xFFF4F4F4),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = if (quantity > 1) Color(0xFFFFB347) else Color(0xFFBDBDBD)
                        )
                    }
                    
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF424242),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    
                    IconButton(
                        onClick = { quantity++ },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color(0xFFF4F4F4),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = Color(0xFFFFB347)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, quantity.toString())
                    }
                }
            ) {
                Text(
                    "Save",
                    color = Color(0xFFFFB347),
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = Color(0xFF757575)
                )
            }
        }
    )
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