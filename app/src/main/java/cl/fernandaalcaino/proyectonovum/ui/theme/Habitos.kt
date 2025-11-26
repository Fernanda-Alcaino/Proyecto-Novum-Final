package cl.fernandaalcaino.proyectonovum.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.util.Log
import cl.fernandaalcaino.proyectonovum.model.Habito
import cl.fernandaalcaino.proyectonovum.viewmodel.HabitoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Habitos(
    viewModel: HabitoViewModel,
    onNavigateToHistorial: () -> Unit,
    onLogout: () -> Unit
) {
    val habitosState by viewModel.habitos.collectAsState()
    val habitosApiState by viewModel.habitosApi.collectAsState()
    val apiError by viewModel.apiError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val nombreState = viewModel.nombre.value
    val tipoState = viewModel.tipo.value
    val metaDiariaState = viewModel.metaDiaria.value

    var expanded by remember { mutableStateOf(false) }

    // COLUMN PRINCIPAL CON SCROLL
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // ESTO PERMITE EL SCROLL
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Hábitos Saludables",
                style = MaterialTheme.typography.headlineSmall
            )

            Row {
                TextButton(onClick = onNavigateToHistorial) {
                    Text("Historial")
                }

                IconButton(onClick = onLogout) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Estado de carga
        if (isLoading) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Cargando hábitos desde la API...")
                }
            }
        }

        // Botón de recarga API
        Button(
            onClick = { viewModel.cargarHabitosDesdeAPI() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("🔄 Recargar Datos API")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error de API
        apiError?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "❌ Error API",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Hábitos de la API
        if (habitosApiState.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💫 Hábitos Sugeridos desde la API (${habitosApiState.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    habitosApiState.forEach { habitoApi ->
                        TarjetaHabitoApi(habitoApi, viewModel)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Formulario para agregar hábitos
        OutlinedTextField(
            value = nombreState,
            onValueChange = { newValue -> viewModel.nombre.value = newValue },
            label = { Text("Nombre del hábito") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = when (tipoState) {
                    "agua" -> "Agua"
                    "ejercicio" -> "Ejercicio"
                    "sueno" -> "Sueño"
                    "lectura" -> "Lectura"
                    "meditacion" -> "Meditación"
                    else -> "General"
                },
                onValueChange = {},
                label = { Text("Tipo de hábito") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Seleccionar tipo")
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf(
                    "Agua" to "agua",
                    "Ejercicio" to "ejercicio",
                    "Sueño" to "sueno",
                    "Lectura" to "lectura",
                    "Meditación" to "meditacion",
                    "General" to "general"
                ).forEach { (texto, valor) ->
                    DropdownMenuItem(
                        text = { Text(texto) },
                        onClick = {
                            viewModel.tipo.value = valor
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = metaDiariaState,
            onValueChange = { newValue -> viewModel.metaDiaria.value = newValue },
            label = {
                Text(
                    when (tipoState) {
                        "agua" -> "Meta diaria (vasos)"
                        else -> "Meta diaria (horas)"
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nombreState.isNotBlank() && metaDiariaState.isNotBlank()) {
                    viewModel.agregarHabito(
                        Habito(
                            nombre = nombreState,
                            tipo = tipoState,
                            metaDiaria = metaDiariaState.toDoubleOrNull() ?: 0.0
                        )
                    )
                    viewModel.nombre.value = ""
                    viewModel.metaDiaria.value = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Hábito")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de hábitos locales
        if (habitosState.isEmpty() && habitosApiState.isEmpty() && !isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No hay hábitos registrados",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Agrega hábitos manualmente o recarga los de la API",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val habitosLocales = habitosState.filter { it.usuarioEmail != "api" }
            if (habitosLocales.isNotEmpty()) {
                Text(
                    text = "Mis Hábitos (${habitosLocales.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Column normal para que funcione con el scroll principal
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    habitosLocales.forEach { habito ->
                        TarjetaHabito(habito, viewModel)
                        Divider()
                    }
                }
            }
        }

        // Espacio adicional al final para mejor scroll
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun TarjetaHabito(habito: Habito, viewModel: HabitoViewModel) {
    val unidad = when (habito.tipo) {
        "agua" -> "vasos"
        else -> "horas"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = habito.nombre,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(text = "Tipo: ${habito.tipo}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Meta: ${habito.metaDiaria.toInt()} $unidad", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Progreso hoy: ${habito.progresoHoy.toInt()} $unidad", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Racha: ${habito.racha} días", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.registrarProgreso(habito.id, 1.0) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+1 $unidad")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { viewModel.eliminarHabito(habito) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
fun TarjetaHabitoApi(habito: Habito, viewModel: HabitoViewModel) {
    val unidad = when (habito.tipo) {
        "agua" -> "vasos"
        else -> "horas"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habito.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Meta: ${habito.metaDiaria.toInt()} $unidad • Progreso: ${habito.progresoHoy.toInt()} $unidad",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "📡 Desde la API",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    viewModel.agregarHabito(habito.copy(
                        id = 0,
                        progresoHoy = 0.0,
                        racha = 0
                    ))
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Agregar")
            }
        }
    }
}