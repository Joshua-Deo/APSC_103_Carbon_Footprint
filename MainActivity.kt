package com.example.project103

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.project103.ui.theme.Project103Theme

import androidx.compose.foundation.layout.*;
import androidx.compose.material3.*;
import androidx.compose.ui.text.*;
import androidx.compose.ui.text.font.*;
import androidx.compose.ui.unit.*;
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp

import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryModelOf



//temp variables, need to call backend
var name="Anish"
var carbonimpactcurrent=95
var carbonimpactlast=arrayOf(102, 105,101,109);
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project103Theme {
                Project103App()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun Project103App() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        when(currentDestination){
            AppDestinations.DASHBOARD -> DashboardScreen()
            AppDestinations.COMMUNITY -> CommunityScreen()
            AppDestinations.ENTRIES -> EntriesScreen()
            AppDestinations.PROFILE -> ProfileScreen()
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    DASHBOARD("Your Dashboard", Icons.Default.Home),
    COMMUNITY("Friends and Leaderboard", Icons.Default.Favorite),
    ENTRIES("Record Entries", Icons.Default.AddCircle),
    PROFILE("Profile", Icons.Default.AccountBox),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Project103Theme {
        Greeting("Android")
    }
}

@Composable
fun DashboardScreen(){
    var tbutton by rememberSaveable {
        mutableStateOf(false)
    }
    val chartEntryModel=entryModelOf(4f, 7f, 2f, 9f, 5f)
    Scaffold(modifier=Modifier.fillMaxSize()){innerPadding ->
        Column(modifier=Modifier.padding(innerPadding)){
            Text(
                text = buildAnnotatedString {
                    append("Welcome to your Dashboard, ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(name)
                    }
                    append("!")
                },
                fontSize = 20.sp)
            Spacer(modifier = Modifier.height(56.dp))
            Text("Subtitle")
            Button(onClick={tbutton=true}) { Text("Click me") }
            if(tbutton){
                Text("Click registered!")
            }
            Spacer(modifier =Modifier.height(125.dp))

            Chart(
                chart = columnChart(spacing = 2.dp),
                model = chartEntryModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}
@Composable
fun CommunityScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Text("Community Screen", modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun EntriesScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Text("Entries Screen", modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ProfileScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Text("Profile Screen", modifier = Modifier.padding(innerPadding))
    }
}