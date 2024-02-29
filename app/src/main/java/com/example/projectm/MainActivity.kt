package com.example.projectm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectm.ui.theme.ProjectmTheme
import com.example.projectm.ui.theme.darkGreyBg
import com.example.projectm.ui.theme.lightBlue
import com.example.projectm.ui.theme.lightGreyBg

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnalyticsScreen()

                }
            }
        }
    }
}

@Composable
fun AnalyticsScreen() {
    val analyticsViewModel :AnalyticsViewModel = viewModel()
    Column {
        Text(text = "Mapsted Test Case #8")
        Text(text = "Aditya Agrawal")
        Text(text = "Date")
        PurchaseCostsUI(viewModel = analyticsViewModel)
    }
}

@Composable
fun PurchaseCostsUI(viewModel: AnalyticsViewModel) {
    SubHeadingText(label = "Purchase Costs:")
    DropdownSelectorRow(label = "Manufacturer",
        options = listOf("Samsung", "Motorola"),
        onOptionSelected = { manufacturer ->
            viewModel.onManufacturerSelected(manufacturer)
        })
}

@Composable
fun SubHeadingText(label: String) {
    Text(
        text = label, modifier = Modifier
            .fillMaxWidth()
            .background(color = lightBlue)
    )
}

@Composable
fun DropdownSelectorRow(
    label: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options.firstOrNull() ?: "") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier
                .background(lightGreyBg)
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .background(darkGreyBg)
                .padding(8.dp),
        ) {
            TextField(
                value = selectedOption,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "dropdown",
                        modifier = Modifier.clickable { expanded = !expanded })
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOption = option
                            onOptionSelected(option)
                            expanded = false
                        },
                        text = { Text(text = option) },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Result",
            modifier = Modifier
                .background(lightGreyBg)
                .padding(8.dp)
        )
    }

}