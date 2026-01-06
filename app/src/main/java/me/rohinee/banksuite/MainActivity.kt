package me.rohinee.banksuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.rohinee.banksuite.config.AppLogger
import me.rohinee.banksuite.config.FeatureConfig
import me.rohinee.banksuite.features.AutoPaymentResult
import me.rohinee.banksuite.features.PaymentFeatureGateway
import me.rohinee.banksuite.ui.theme.BankSuiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Log app info
        AppLogger.i("MainActivity", FeatureConfig.getAppInfo())

        setContent {
            BankSuiteTheme {
                Scaffold(
                    topBar = {
                        TopAppBar {
                            Text(
                                text = FeatureConfig.getBankName(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                ) { innerPadding ->
                    BankingDashboard(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(title: @Composable () -> Unit) {
    TopAppBar(
        title = title,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun BankingDashboard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Environment Info Card
        EnvironmentInfoCard()

        Spacer(modifier = Modifier.height(24.dp))

        // Features Section
        Text(
            text = "Available Services",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Feature Cards
        FeatureGrid()

        Spacer(modifier = Modifier.height(24.dp))

        // App Info Section
        Text(
            text = "App Information",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Auto Payment Feature Section (Bank A & B only)
        if (PaymentFeatureGateway.isAutoPaymentFeatureAvailable()) {
            AutoPaymentSection()

            Spacer(modifier = Modifier.height(24.dp))
        }

        AppInfoCard()

        // Add extra bottom padding for better scroll experience
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun EnvironmentInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Current Environment",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow("Environment", FeatureConfig.getEnvironment())
            InfoRow("Bank Code", FeatureConfig.getBankCode())
            InfoRow("API Base URL", FeatureConfig.getEnvironment())
        }
    }
}

@Composable
fun FeatureGrid() {
    val features = getFeatureItems()

    Column(modifier = Modifier.fillMaxWidth()) {
        features.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { feature ->
                    FeatureCard(
                        feature = feature,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty space if odd number
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun FeatureCard(feature: FeatureItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = feature.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun AppInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoRow("Version", "1.0")
            InfoRow("Logging Enabled", FeatureConfig.isLoggingEnabled().toString())
            InfoRow("Analytics Enabled", FeatureConfig.isAnalyticsEnabled().toString())
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

data class FeatureItem(
    val name: String,
    val icon: ImageVector,
    val enabled: Boolean
)

fun getFeatureItems(): List<FeatureItem> {
    return listOf(
        FeatureItem("Savings Account", Icons.Default.Star, FeatureConfig.isSavingsAccountEnabled()),
        FeatureItem("Current Account", Icons.Default.Home, FeatureConfig.isCurrentAccountEnabled()),
        FeatureItem("Loans", Icons.Default.ShoppingCart, FeatureConfig.isLoansEnabled()),
        FeatureItem("Credit Card", Icons.Default.Phone, FeatureConfig.isCreditCardEnabled()),
        FeatureItem("UPI", Icons.Default.Info, FeatureConfig.isUPIEnabled()),
        FeatureItem("Auto Payment", Icons.Default.CheckCircle, PaymentFeatureGateway.isAutoPaymentFeatureAvailable()),
        FeatureItem("Transactions", Icons.Default.Favorite, true),
    )
}

@Composable
fun AutoPaymentSection() {
    val coroutineScope = rememberCoroutineScope()
    val manager = remember { PaymentFeatureGateway.createAutoPaymentManager() }
    var autoPaymentEnabled by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = PaymentFeatureGateway.getFeatureDescription(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            manager?.let {
                Text(
                    text = "Service: ${it.getServiceName()}",
                    style = MaterialTheme.typography.bodyMedium
                )

                InfoRow("Transaction Fee", "$${it.getAutoPaymentFee()}")
                InfoRow("Max Amount", "$${it.getMaxAutoPaymentAmount()}")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val result = it.enableAutoPayment("ACC-12345")
                                resultMessage = result.message
                                autoPaymentEnabled = result.success
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !autoPaymentEnabled
                    ) {
                        Text("Enable")
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val result = it.disableAutoPayment("ACC-12345")
                                resultMessage = result.message
                                autoPaymentEnabled = !result.success
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        enabled = autoPaymentEnabled
                    ) {
                        Text("Disable")
                    }
                }

                if (resultMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (resultMessage.contains("success", ignoreCase = true))
                                Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (resultMessage.contains("success", ignoreCase = true))
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = resultMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (resultMessage.contains("success", ignoreCase = true))
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            } ?: run {
                Text(
                    text = "Feature not available for this bank variant",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}