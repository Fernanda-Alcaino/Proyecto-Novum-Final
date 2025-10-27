package cl.fernandaalcaino.proyectonovum.ui.theme



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.fernandaalcaino.proyectonovum.model.Habito
import cl.fernandaalcaino.proyectonovum.viewmodel.HabitoViewModel

import kotlin.math.min

@Composable
fun HabitoHistorial(
    viewModel: HabitoViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val habitos by viewModel.habitos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
            }

            Text(
                text = "Historial de Hábitos",
                style = MaterialTheme.typography.headlineSmall
            )

            IconButton(onClick = onLogout) {
                Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (habitos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No hay hábitos registrados",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Agrega hábitos en la pantalla principal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Resumen General",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Total de hábitos: ${habitos.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Hábitos completados hoy: ${habitos.count { it.progresoHoy >= it.metaDiaria }}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }


            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(habitos) { habito ->
                    TarjetaHabitoHistorial(habito = habito)
                }
            }
        }
    }
}

private fun LazyListScope.items(
    count: Any,
    itemContent: (LazyItemScope, Int) -> Unit
) {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaHabitoHistorial(habito: Habito) {

    fun obtenerUnidad(tipo: String): String {
        return when (tipo) {
            "agua" -> "vasos"
            else -> "horas"
        }
    }

    val unidad = obtenerUnidad(habito.tipo)
    val cumpleMeta = habito.progresoHoy >= habito.metaDiaria
    val porcentaje = if (habito.metaDiaria > 0) {
        min((habito.progresoHoy / habito.metaDiaria * 100).toInt(), 100)
    } else {
        0
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (cumpleMeta) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habito.nombre,
                    style = MaterialTheme.typography.headlineSmall
                )
                if (cumpleMeta) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "COMPLETADO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                InfoRow("Tipo:", habito.tipo)
                // META con unidad personalizada
                InfoRow("Meta diaria:", "${habito.metaDiaria.toInt()} $unidad")
                // PROGRESO HOY con unidad personalizada
                InfoRow("Progreso hoy:", "${habito.progresoHoy.toInt()} $unidad")
                InfoRow("Racha actual:", "${habito.racha} días")

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = if (habito.metaDiaria > 0) {
                        min(habito.progresoHoy / habito.metaDiaria, 1.0).toFloat()
                    } else {
                        0f
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (cumpleMeta) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "$porcentaje% completado",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun InfoRow(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}