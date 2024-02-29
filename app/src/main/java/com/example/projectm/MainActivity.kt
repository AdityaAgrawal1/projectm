package com.example.projectm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectm.ui.theme.ProjectmTheme
import com.example.projectm.ui.theme.darkGrey
import com.example.projectm.ui.theme.lightBlue
import com.example.projectm.ui.theme.lightGrey
import com.example.projectm.ui.theme.lightGreyBg
import com.example.projectm.utils.constants.DropdownType
import com.example.projectm.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val analyticsViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnalyticsScreen(viewModel=analyticsViewModel)
                }
            }
        }
    }
}

@Composable
fun AnalyticsScreen(viewModel: MainViewModel) {
    val isLoading by viewModel.isLoading
    if(isLoading){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Please Wait While Loading...")
            }
        }
    }else{
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Mapsted Test Case #8",
                        modifier = Modifier.padding(vertical = 4.dp),
                        fontSize = 32.sp,
                        color = lightGrey
                    )
                    Text(
                        text = "Aditya Agrawal",
                        modifier = Modifier.padding(vertical = 4.dp),
                        fontSize = 24.sp,
                        color = lightGrey
                    )
                    Text(
                        text = "29/02/2024",
                        modifier = Modifier.padding(vertical = 4.dp),
                        fontSize = 24.sp,
                        color = lightGrey
                    )
                }
            }
            PurchaseCostsUI(viewModel = viewModel)
            PurchasesCountUI(viewModel = viewModel)
            MostPurchasesUI(viewModel = viewModel)
        }
    }
}

@Composable
fun PurchaseCostsUI(viewModel: MainViewModel) {
    val optionsMap = viewModel.results.collectAsState().value

    SubHeadingText(label = "Purchase Costs:")
    DropdownType.entries.subList(0,4).forEach{
        DropdownSelectorRow(
            viewModel = viewModel,
            label = it,
            options = optionsMap[it]?.keys!!.toList(),)
    }

}

@Composable
fun PurchasesCountUI(viewModel: MainViewModel){
    val optionsMap = viewModel.results.collectAsState().value
    SubHeadingText(label = "Number of Purchases")
    DropdownSelectorRow(
        viewModel=viewModel,
        label = DropdownType.ITEM,
        options = optionsMap[DropdownType.ITEM]?.keys!!.toList()
        )
}

@Composable
fun MostPurchasesUI(viewModel: MainViewModel){

    SubHeadingText(label = "Most Total Purchases")
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Building",
            modifier = Modifier
                .weight(1f)
                .background(lightGreyBg)
                .padding(10.dp),
            color = lightGrey
        )
        Text(
            text = viewModel.buildingName.value,
            modifier = Modifier
                .weight(1f)
                .background(lightGreyBg)
                .padding(10.dp),
            color = lightGrey
        )
    }
}


@Composable
fun SubHeadingText(label: String) {
    Text(
        text = label, modifier = Modifier
            .fillMaxWidth()
            .background(color = lightBlue)
            .padding(vertical = 16.dp, horizontal = 10.dp),
        fontSize = 20.sp,
        color = lightGrey
    )
}

@Composable
fun DropdownSelectorRow(
    viewModel: MainViewModel,
    label: DropdownType,
    options: List<String>,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options.firstOrNull() ?: "") }
    viewModel.onDropdownSelected(label,selectedOption)

    val results by viewModel.results.collectAsState()
    val result = results[label]

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(lightGreyBg),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label.displayName,
            modifier = Modifier
                .weight(1f)
                .padding(6.dp),
            color = darkGrey
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1.5f)
                .padding(6.dp),
        ) {
            TextField(
                value = selectedOption,
                readOnly = true,
                onValueChange = {},
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
                            viewModel.onDropdownSelected(label,option)
                            expanded = false
                        },
                        text = { Text(text = option) },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = result?.get(selectedOption)?:"N/A",
            modifier = Modifier
                .weight(0.8f)
                .background(lightGreyBg)
                .padding(8.dp),
            color = lightGrey
        )
    }
}