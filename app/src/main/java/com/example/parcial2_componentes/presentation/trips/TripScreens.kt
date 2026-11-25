package com.example.parcial2_componentes.presentation.trips

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parcial2_componentes.domain.model.Expense
import com.example.parcial2_componentes.domain.model.Trip
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    viewModel: TripViewModel,
    onNavigateToCreateTrip: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadTrips() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis viajes") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateTrip) {
                Icon(Icons.Default.Add, contentDescription = "Crear viaje")
            }
        }
    ) { padding ->
        if (state.trips.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No tienes viajes registrados. Presiona + para crear uno.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.trips) { trip ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onNavigateToDetail(trip.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(trip.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(trip.destination, style = MaterialTheme.typography.bodyMedium)
                            Text("${trip.startDate} al ${trip.endDate}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    viewModel: TripViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var startDateText by remember { mutableStateOf("2026-12-01") }
    var endDateText by remember { mutableStateOf("2026-12-10") }

    LaunchedEffect(state.tripCreatedSuccess) {
        if (state.tripCreatedSuccess) {
            viewModel.resetCreateState()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear viaje") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del viaje *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destino *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = startDateText,
                onValueChange = { startDateText = it },
                label = { Text("Fecha de inicio (YYYY-MM-DD) *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = endDateText,
                onValueChange = { endDateText = it },
                label = { Text("Fecha de fin (YYYY-MM-DD) *") },
                modifier = Modifier.fillMaxWidth()
            )

            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = {
                    val start = runCatching { LocalDate.parse(startDateText) }.getOrElse { LocalDate.now() }
                    val end = runCatching { LocalDate.parse(endDateText) }.getOrElse { LocalDate.now() }
                    viewModel.createTrip(name, destination, start, end)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: String,
    viewModel: TripDetailViewModel,
    onBack: () -> Unit,
    onNavigateToAddExpense: (Trip) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) { viewModel.loadTripDetail(tripId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.trip?.name ?: "Detalle del viaje") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            state.trip?.let { trip ->
                FloatingActionButton(onClick = { onNavigateToAddExpense(trip) }) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar gasto")
                }
            }
        }
    ) { padding ->
        state.trip?.let { trip ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text(trip.destination, style = MaterialTheme.typography.titleMedium)
                Text("${trip.startDate} - ${trip.endDate}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total de gastos", style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = "$ ${String.format("%.2f", trip.totalExpenses)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Gastos registrados (${trip.expenses.size})", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(trip.expenses) { expense ->
                        ExpenseRow(expense)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseRow(expense: Expense) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(expense.category.name, fontWeight = FontWeight.Bold)
                Text(expense.description, style = MaterialTheme.typography.bodyMedium)
                Text(expense.date.toString(), style = MaterialTheme.typography.bodySmall)
            }
            Text("$ ${String.format("%.2f", expense.amount)}", fontWeight = FontWeight.Bold)
        }
    }
}