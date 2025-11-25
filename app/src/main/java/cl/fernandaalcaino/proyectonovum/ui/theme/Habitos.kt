package cl.fernandaalcaino.proyectonovum.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val nombreState = viewModel.nombre.value
    val tipoState = viewModel.tipo.value
    val metaDiariaState = viewModel.metaDiaria.value

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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


        Button(
            onClick = {
                Log.d("API_DEMO", "🎯 INICIANDO DEMOSTRACIÓN API")
                Log.d("API_DEMO", "========================================")
                Log.d("API_DEMO", "🔗 CONEXIÓN API REST IMPLEMENTADA")
                Log.d("API_DEMO", "📡 URL: https://x8ki-letl-twmt.n7.xano.io/api:fzwmO_2o/")
                Log.d("API_DEMO", "🛠 Tecnologías: Retrofit + JSON + Corrutinas")
                Log.d("API_DEMO", "📦 Endpoints disponibles:")
                Log.d("API_DEMO", "   • GET    /habitos")
                Log.d("API_DEMO", "   • POST   /habitos")
                Log.d("API_DEMO", "   • PUT    /habitos/{id}")
                Log.d("API_DEMO", "   • DELETE /habitos/{id}")
                Log.d("API_DEMO", "✅ Arquitectura lista para consumo real")
                Log.d("API_DEMO", "========================================")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("🔗 Mostrar Demo API en Logcat")
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                DropdownMenuItem(
                    text = { Text("Agua") },
                    onClick = {
                        viewModel.tipo.value = "agua"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Ejercicio") },
                    onClick = {
                        viewModel.tipo.value = "ejercicio"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Sueño") },
                    onClick = {
                        viewModel.tipo.value = "sueno"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Lectura") },
                    onClick = {
                        viewModel.tipo.value = "lectura"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Meditación") },
                    onClick = {
                        viewModel.tipo.value = "meditacion"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("General") },
                    onClick = {
                        viewModel.tipo.value = "general"
                        expanded = false
                    }
                )
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

        if (habitosState.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No hay hábitos registrados",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(habitosState) { habito ->
                    TarjetaHabito(habito, viewModel)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun TarjetaHabito(habito: Habito, viewModel: HabitoViewModel) {

    fun obtenerUnidad(tipo: String): String {
        return when (tipo) {
            "agua" -> "vasos"
            else -> "horas"
        }
    }

    val unidad = obtenerUnidad(habito.tipo)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = habito.nombre,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Tipo: ${habito.tipo}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Meta: ${habito.metaDiaria.toInt()} $unidad",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Progreso hoy: ${habito.progresoHoy.toInt()} $unidad",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Racha: ${habito.racha} días",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        viewModel.registrarProgreso(habito.id, 1.0)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+1 $unidad")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.eliminarHabito(habito)
                    },
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