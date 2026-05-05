package com.example.clothingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clothingapp.data.AppDatabase
import com.example.clothingapp.data.ProductRepository
import com.example.clothingapp.ui.ProductViewModel
import com.example.clothingapp.ui.ProductViewModelFactory
import com.example.clothingapp.ui.screens.AddProductScreen
import com.example.clothingapp.ui.screens.HomeScreen
import com.example.clothingapp.ui.screens.ProductDetailScreen
import com.example.clothingapp.ui.screens.SettingsScreen
import com.example.clothingapp.ui.theme.ClothingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val repository = ProductRepository(database.productDao())
        val viewModelFactory = ProductViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]

        setContent {
            ClothingAppTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1A1A1E)),
                    color = Color(0xFF1A1A1E)
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(navController, viewModel)
                        }
                        composable("add") {
                            AddProductScreen(navController, viewModel)
                        }
                        composable("settings") {
                            SettingsScreen(navController, viewModel)
                        }
                        composable("detail/{productId}") { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                            if (productId != null) {
                                ProductDetailScreen(navController, viewModel, productId)
                            }
                        }
                    }
                }
            }
        }
    }
}
