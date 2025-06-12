package com.dark.neuroverse.compose.screens.setup.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dark.neuroverse.compose.components.CheckBX
import com.dark.neuroverse.ui.theme.Warning
import com.dark.neuroverse.ui.theme.onWarning

@Composable
fun PermissionScreen(paddingValues: PaddingValues) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 34.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Final Setup..!",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.displayMedium,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )

        Text(
            "thank you for patiently \nwaiting....!",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Light
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp)),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PermissionCard(
                    "Accessibility Permission",
                    "Accessibility Permission is Optional but, not giving it Android may Block Some of your Favourite Plugin",
                    true
                )

                PermissionCard(
                    "Set As Default Assistant",
                    "This Is Also Option If You don’t want to change your default assistant then you can just use NeuroV within the NeuroV app or assign a HW Button ",
                    false
                )
            }

        }

        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    "Neuro V",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )

                Button(onClick = {

                }, enabled = false, colors = ButtonDefaults.buttonColors()) {
                    Text("Install & Proceed", fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PermissionCard(title: String, description: String, isSkipAble: Boolean = false) {
    var checked by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    modifier = Modifier
                        .weight(1f),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Serif,
                )

                CheckBX(
                    checked,
                    isReadOnly = true,
                    onCheckStateChange = { checked = it },
                )
            }

            Text(
                description,
                style = MaterialTheme.typography.titleMediumEmphasized,
                fontFamily = FontFamily.Serif,
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (isSkipAble)
                    Button(
                        onClick = {

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = onWarning,
                            contentColor = Warning
                        ),
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text("Skip", fontFamily = FontFamily.Serif)
                    }

                Spacer(Modifier.width(10.dp))

                Button(
                    onClick = {
                        checked = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Grant", fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}

