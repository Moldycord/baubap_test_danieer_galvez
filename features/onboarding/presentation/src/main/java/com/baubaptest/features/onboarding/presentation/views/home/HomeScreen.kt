package com.baubaptest.features.onboarding.presentation.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.baubaptest.core.model.UiState
import com.baubaptest.core.model.User
import com.baubaptest.features.onboarding.presentation.views.general.CardItem
import com.baubaptest.features.onboarding.presentation.views.general.LabeledText
import com.baubaptest.features.onboarding.presentation.views.general.Loader


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val userState by viewModel.loggedUserState.collectAsState()

    val primaryPurple = Color(0xFF9B4DFF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Datos personales") },
            )
        }
    ) { padding ->
        when (userState) {
            is UiState.Loading -> Loader()

            is UiState.Error -> {
                val errorMessage = (userState as UiState.Error).message
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                }
            }

            is UiState.Success -> {
                val user = (userState as UiState.Success<User>).data

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(Color(0xFFFDF9FF))
                        .padding(24.dp)
                ) {
                    OutlinedTextField(
                        value = user.phone,
                        onValueChange = {},
                        label = { Text("Número celular") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = primaryPurple
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = user.email,
                        onValueChange = {},
                        label = { Text("Correo electrónico") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = primaryPurple
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LabeledText("Nombre completo", user.name)
                    LabeledText("CURP", user.curp)
                    LabeledText("Domicilio", "Sin dirección registrada")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Cuentas de depósito", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Podrás dar de alta nuevas cuentas al aceptar tu siguiente préstamo.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CardItem()
                    CardItem()

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.closeSession()
                            {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryPurple)
                    ) {
                        Text("Cerrar sesión")
                    }
                }
            }

            else -> Unit
        }
    }
}
