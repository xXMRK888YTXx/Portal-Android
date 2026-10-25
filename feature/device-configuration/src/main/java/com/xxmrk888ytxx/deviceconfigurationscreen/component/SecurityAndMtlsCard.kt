package com.xxmrk888ytxx.deviceconfigurationscreen.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.deviceconfigurationscreen.R
import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device
import com.xxmrk888ytxx.deviceconfigurationscreen.model.IdentifierType
import com.xxmrk888ytxx.corecompose.R as ComposeR

@Composable
fun SecurityAndMtlsCard(
    device: Device,
    onShowInfo: (title: String, type: IdentifierType, value: String, description: String?) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (device is Device.WifiDevice) {
                // 1. Блок: Сервер mTLS
                val serverTitle = stringResource(R.string.server_mtls)
                val serverCertModalTitle = stringResource(R.string.server_mtls_cert)
                val serverFp = device.serverCertificateFingerprint
                val serverDisplayFp = serverFp.truncateFingerprint()

                SecurityIdentifierItemCard(
                    icon = ComposeR.drawable.key,
                    title = serverTitle,
                    tag = stringResource(R.string.sha_256),
                    displayValue = serverDisplayFp,
                    fullValue = serverFp,
                    onInfoClick = {
                        onShowInfo(
                            serverCertModalTitle,
                            IdentifierType.SHA_256,
                            serverFp,
                            null
                        )
                    },
                    onCopyClick = { onCopy(serverFp) }
                )

                // 2. Блок: Клиент mTLS
                val clientTitle = stringResource(R.string.client_mtls)
                val clientCertModalTitle = stringResource(R.string.client_mtls_cert)
                val clientFp = device.clientCertificateFingerprint
                val clientDisplayFp = clientFp.truncateFingerprint()

                SecurityIdentifierItemCard(
                    icon = ComposeR.drawable.smartphone,
                    title = clientTitle,
                    tag = stringResource(R.string.sha_256),
                    displayValue = clientDisplayFp,
                    fullValue = clientFp,
                    onInfoClick = {
                        onShowInfo(
                            clientCertModalTitle,
                            IdentifierType.SHA_256,
                            clientFp,
                            null
                        )
                    },
                    onCopyClick = { onCopy(clientFp) }
                )
            }

            // 3. Блок: Device UUID (ИД устройства)
            val uuidTitle = stringResource(R.string.device_uuid)
            val uuidModalTitle = stringResource(R.string.device_id_title)
            val uuidDisplay = device.clientId.truncateUuid()

            SecurityIdentifierItemCard(
                icon = ComposeR.drawable.fingerprint,
                title = uuidTitle,
                tag = stringResource(R.string.uuid_v4),
                displayValue = uuidDisplay,
                fullValue = device.clientId,
                onInfoClick = {
                    onShowInfo(
                        uuidModalTitle,
                        IdentifierType.UUID_V4,
                        device.clientId,
                        null
                    )
                },
                onCopyClick = { onCopy(device.clientId) }
            )
        }
    }
}

private fun String.truncateFingerprint(): String {
    if (length <= 20) return this
    val parts = split(":")
    return if (parts.size >= 8) {
        "${parts.take(4).joinToString(":")}...${parts.takeLast(3).joinToString(":")}"
    } else {
        "${take(11)}...${takeLast(8)}"
    }
}

private fun String.truncateUuid(): String {
    if (length <= 16) return this
    return "${take(8)}...${takeLast(8)}"
}
