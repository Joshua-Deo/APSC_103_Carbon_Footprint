package com.example.carbonfootprintstuff


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlin.math.abs

// ─── Color Palette ──────────────────────────────────────────────────────────

object AppColors {
    val Background      = Color(0xFF0D1B12)   // Deep forest night
    val Surface         = Color(0xFF132018)
    val Card            = Color(0xFF1A2D20)
    val CardBorder      = Color(0xFF2A4A30)
    val Primary         = Color(0xFF4ADE80)   // Vivid leaf green
    val PrimaryDim      = Color(0xFF22C55E)
    val Accent          = Color(0xFFBEF264)   // Lime spark
    val TextPrimary     = Color(0xFFF0FDF4)
    val TextSecondary   = Color(0xFF86EFAC)
    val TextMuted       = Color(0xFF4ADE80).copy(alpha = 0.5f)
    val Danger          = Color(0xFFF87171)
    val Warning         = Color(0xFFFBBF24)
    val Chart1          = Color(0xFF4ADE80)
    val Chart2          = Color(0xFF34D399)
    val Chart3          = Color(0xFF6EE7B7)
    val Overlay         = Color(0xFF0D1B12).copy(alpha = 0.85f)
}

// ─── Data Models ────────────────────────────────────────────────────────────

data class FootprintEntry(
    val category: String,
    val icon: String,
    val kg: Float,
    val color: Color,
    val trend: Float   // % change vs last period
)

data class WeeklyData(val day: String, val kg: Float)

data class BadgeItem(val emoji: String, val label: String, val earned: Boolean)

// ─── Sample Data ────────────────────────────────────────────────────────────

val sampleEntries = listOf(
    FootprintEntry("Transport",  "🚗", 4.2f, AppColors.Danger,   -8f),
    FootprintEntry("Food",       "🥦", 2.1f, AppColors.Warning,  -12f),
    FootprintEntry("Energy",     "⚡", 3.7f, AppColors.Chart2,    5f),
    FootprintEntry("Shopping",   "🛍️", 1.5f, AppColors.Chart3,  -3f),
    FootprintEntry("Flights",    "✈️", 0.0f, AppColors.TextMuted, 0f),
)

val sampleWeekly = listOf(
    WeeklyData("M", 12.4f),
    WeeklyData("T", 9.8f),
    WeeklyData("W", 11.2f),
    WeeklyData("T", 8.1f),
    WeeklyData("F", 10.5f),
    WeeklyData("S", 7.3f),
    WeeklyData("S", 6.9f),
)

val badges = listOf(
    BadgeItem("🌱", "First Log",      true),
    BadgeItem("🚲", "Bike Week",      true),
    BadgeItem("🌍", "10% Reduction",  true),
    BadgeItem("⚡", "Solar User",     false),
    BadgeItem("🥗", "Plant Month",    false),
    BadgeItem("🏆", "Carbon Neutral", false),
)

// ─── MainActivity ───────────────────────────────────────────────────────────

class MainActivity1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarbonApp()
        }
    }
}

@Composable
fun CarbonApp() {
    val navController = rememberNavController()

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = AppColors.Background,
            surface    = AppColors.Surface,
            primary    = AppColors.Primary,
        )
    ) {
        Scaffold(
            containerColor = AppColors.Background,
            bottomBar      = { BottomNav(navController) }
        ) { padding ->
            NavHost(
                navController    = navController,
                startDestination = "dashboard",
                modifier         = Modifier.padding(padding)
            ) {
                composable("dashboard") { DashboardScreen() }
                composable("log")       { LogScreen() }
                composable("insights")  { InsightsScreen() }
                composable("profile")   { ProfileScreen() }
            }
        }
    }
}

// ─── Bottom Navigation ───────────────────────────────────────────────────────

@Composable
fun BottomNav(nav: NavHostController) {
    val current by nav.currentBackStackEntryAsState()
    val route   = current?.destination?.route

    NavigationBar(
        containerColor = AppColors.Surface,
        tonalElevation = 0.dp,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = AppColors.CardBorder,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        listOf(
            Triple("dashboard", Icons.Filled.Home,        "Home"),
            Triple("log",       Icons.Filled.Add,         "Log"),
            Triple("insights",  Icons.Filled.BarChart,    "Insights"),
            Triple("profile",   Icons.Filled.Person,      "Profile"),
        ).forEach { (dest, icon, label) ->
            val selected = route == dest
            NavigationBarItem(
                selected = selected,
                onClick  = { nav.navigate(dest) { launchSingleTop = true } },
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(if (selected) 48.dp else 40.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected)
                                    AppColors.Primary.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (selected) AppColors.Primary else AppColors.TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        label,
                        color    = if (selected) AppColors.Primary else AppColors.TextMuted,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

// ─── Dashboard Screen ────────────────────────────────────────────────────────

@Composable
fun DashboardScreen() {
    val totalKg = sampleEntries.sumOf { it.kg.toDouble() }.toFloat()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(52.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Good morning,", fontSize = 14.sp, color = AppColors.TextSecondary)
                Text("Alex 🌿",       fontSize = 26.sp, fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppColors.Primary.copy(alpha = 0.15f))
                    .border(1.dp, AppColors.Primary.copy(alpha = 0.4f), CircleShape)
            ) {
                Text("🔔", fontSize = 20.sp)
            }
        }

        Spacer(Modifier.height(28.dp))

        // Hero Card
        HeroCard(totalKg)

        Spacer(Modifier.height(24.dp))

        // Weekly Chart
        Text("This Week", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary)
        Spacer(Modifier.height(12.dp))
        WeeklyBarChart(sampleWeekly)

        Spacer(Modifier.height(24.dp))

        // Categories
        Text("By Category", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary)
        Spacer(Modifier.height(12.dp))
        sampleEntries.forEach { entry ->
            CategoryRow(entry)
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun HeroCard(totalKg: Float) {
    val animVal by animateFloatAsState(
        targetValue = totalKg,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "hero"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF14532D), Color(0xFF052E16))
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(listOf(AppColors.Primary.copy(alpha = 0.6f), Color.Transparent)),
                RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            Text("Today's Footprint", fontSize = 13.sp, color = AppColors.TextSecondary)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "%.1f".format(animVal),
                    fontSize    = 52.sp,
                    fontWeight  = FontWeight.ExtraBold,
                    color       = AppColors.Primary,
                )
                Text(" kg CO₂", fontSize = 18.sp, color = AppColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp))
            }
            Spacer(Modifier.height(12.dp))

            // Goal progress
            val goal = 15f
            val progress = (totalKg / goal).coerceIn(0f, 1f)
            Text("Daily goal: ${goal.toInt()} kg", fontSize = 12.sp,
                color = AppColors.TextSecondary)
            Spacer(Modifier.height(6.dp))
            LinearProgressBar(progress)

            Spacer(Modifier.height(8.dp))
            val remaining = goal - totalKg
            Text(
                if (remaining > 0) "✅ ${remaining.format1()} kg under goal!"
                else "⚠️ ${(-remaining).format1()} kg over goal",
                fontSize = 13.sp,
                color    = if (remaining > 0) AppColors.Accent else AppColors.Danger
            )
        }
    }
}

@Composable
fun LinearProgressBar(progress: Float) {
    val animProgress by animateFloatAsState(progress, tween(1000), label = "prog")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(AppColors.CardBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animProgress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(AppColors.PrimaryDim, AppColors.Accent)
                    )
                )
        )
    }
}

@Composable
fun WeeklyBarChart(data: List<WeeklyData>) {
    val maxKg = data.maxOf { it.kg }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
        border   = BorderStroke(1.dp, AppColors.CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom
        ) {
            data.forEach { d ->
                val isToday = d.day == "S" && data.indexOf(d) == 5
                BarColumn(d, maxKg, isToday)
            }
        }
    }
}

@Composable
fun BarColumn(d: WeeklyData, maxKg: Float, highlight: Boolean) {
    val heightFraction = d.kg / maxKg
    val animH by animateFloatAsState(heightFraction, tween(800), label = "bar")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(32.dp)
    ) {
        Text("${d.kg.toInt()}", fontSize = 9.sp,
            color = if (highlight) AppColors.Accent else AppColors.TextMuted)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(18.dp)
                .height((animH * 80).dp)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(
                    if (highlight)
                        Brush.verticalGradient(listOf(AppColors.Accent, AppColors.PrimaryDim))
                    else
                        Brush.verticalGradient(listOf(AppColors.Primary.copy(0.7f), AppColors.Primary.copy(0.3f)))
                )
        )
        Spacer(Modifier.height(6.dp))
        Text(d.day, fontSize = 11.sp,
            color = if (highlight) AppColors.Primary else AppColors.TextSecondary)
    }
}

@Composable
fun CategoryRow(entry: FootprintEntry) {
    val max = sampleEntries.maxOf { it.kg }
    val progress by animateFloatAsState(entry.kg / max.coerceAtLeast(1f), tween(900), label = "cat")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
        border   = BorderStroke(1.dp, AppColors.CardBorder)
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(entry.icon, fontSize = 24.sp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(
                    modifier                  = Modifier.fillMaxWidth(),
                    horizontalArrangement     = Arrangement.SpaceBetween
                ) {
                    Text(entry.category, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary)
                    Text("${entry.kg} kg", fontSize = 13.sp, color = AppColors.TextSecondary)
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(AppColors.CardBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(entry.color)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            val trendColor = if (entry.trend <= 0) AppColors.Primary else AppColors.Danger
            Text(
                "${if (entry.trend <= 0) "↓" else "↑"}${abs(entry.trend).toInt()}%",
                fontSize  = 12.sp,
                color     = trendColor,
                fontWeight= FontWeight.SemiBold
            )
        }
    }
}

// ─── Log Screen ──────────────────────────────────────────────────────────────

@Composable
fun LogScreen() {
    var selected by remember { mutableStateOf("Transport") }
    val categories = listOf("Transport","Food","Energy","Shopping","Flights")
    var sliderVal by remember { mutableStateOf(5f) }
    var note by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(52.dp))
        Text("Log Activity", fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary)
        Text("Add today's carbon sources", fontSize = 14.sp,
            color = AppColors.TextSecondary)

        Spacer(Modifier.height(28.dp))

        // Category chips
        Text("Category", fontSize = 14.sp, color = AppColors.TextSecondary)
        Spacer(Modifier.height(10.dp))
        val rows = categories.chunked(3)
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { cat ->
                    val active = selected == cat
                    FilterChip(
                        selected = active,
                        onClick  = { selected = cat },
                        label    = { Text(cat, fontSize = 13.sp) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor    = AppColors.Primary.copy(alpha = 0.2f),
                            selectedLabelColor        = AppColors.Primary,
                            containerColor            = AppColors.Card,
                            labelColor                = AppColors.TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled               = true,
                            selected              = active,
                            borderColor           = AppColors.CardBorder,
                            selectedBorderColor   = AppColors.Primary
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(24.dp))

        // Slider
        Text("Amount: ${"%.1f".format(sliderVal)} kg CO₂",
            fontSize = 14.sp, color = AppColors.TextSecondary)
        Spacer(Modifier.height(8.dp))
        Slider(
            value         = sliderVal,
            onValueChange = { sliderVal = it },
            valueRange    = 0f..30f,
            colors        = SliderDefaults.colors(
                thumbColor        = AppColors.Primary,
                activeTrackColor  = AppColors.Primary,
                inactiveTrackColor= AppColors.CardBorder
            )
        )

        Spacer(Modifier.height(24.dp))

        // Note
        Text("Note (optional)", fontSize = 14.sp, color = AppColors.TextSecondary)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value         = note,
            onValueChange = { note = it },
            placeholder   = { Text("e.g. Drove 30km to work", color = AppColors.TextMuted) },
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(14.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppColors.Primary,
                unfocusedBorderColor = AppColors.CardBorder,
                focusedTextColor     = AppColors.TextPrimary,
                unfocusedTextColor   = AppColors.TextPrimary,
                cursorColor          = AppColors.Primary
            )
        )

        Spacer(Modifier.height(32.dp))

        // Submit
        Button(
            onClick = { submitted = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                contentColor   = AppColors.Background
            )
        ) {
            Text("Log Activity", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (submitted) {
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(containerColor = AppColors.Primary.copy(0.15f)),
                border   = BorderStroke(1.dp, AppColors.Primary.copy(0.4f))
            ) {
                Row(
                    modifier          = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✅", fontSize = 22.sp)
                    Spacer(Modifier.width(12.dp))
                    Text("Logged ${"%.1f".format(sliderVal)} kg for $selected!",
                        color = AppColors.TextPrimary, fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

// ─── Insights Screen ─────────────────────────────────────────────────────────

@Composable
fun InsightsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(52.dp))
        Text("Insights", fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary)
        Text("Your impact over time", fontSize = 14.sp,
            color = AppColors.TextSecondary)

        Spacer(Modifier.height(28.dp))

        // Stats row
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Monthly",    "284 kg",  "↓9%",  AppColors.Primary, Modifier.weight(1f))
            StatCard("vs Average", "+12 kg",  "lower",AppColors.Accent,  Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        // Donut-style breakdown
        Text("Category Split", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary)
        Spacer(Modifier.height(12.dp))
        DonutChart()

        Spacer(Modifier.height(24.dp))

        // Tips
        Text("Tips for You", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary)
        Spacer(Modifier.height(12.dp))
        listOf(
            "🚲" to "Try cycling short distances to cut transport emissions by up to 30%",
            "🥗" to "One plant-based day per week saves ~2.5 kg CO₂",
            "💡" to "Switch to LED bulbs — saves ~0.5 kg CO₂ per bulb/month",
        ).forEach { (emoji, tip) ->
            TipCard(emoji, tip)
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun StatCard(label: String, value: String, sub: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
        border   = BorderStroke(1.dp, AppColors.CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = AppColors.TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(value,  fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Text(sub,    fontSize = 12.sp, color = AppColors.TextSecondary)
        }
    }
}

@Composable
fun DonutChart() {
    val segments = listOf(
        "Transport" to AppColors.Danger,
        "Energy"    to AppColors.Chart2,
        "Food"      to AppColors.Warning,
        "Shopping"  to AppColors.Chart3,
    )
    val values = listOf(36f, 32f, 18f, 14f)
    val total = values.sum()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
        border   = BorderStroke(1.dp, AppColors.CardBorder)
    ) {
        Row(
            modifier          = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Canvas donut
            Canvas(modifier = Modifier.size(100.dp)) {
                var startAngle = -90f
                values.forEachIndexed { i, v ->
                    val sweep = v / total * 360f
                    drawArc(
                        color      = segments[i].second,
                        startAngle = startAngle,
                        sweepAngle = sweep - 2f,
                        useCenter  = false,
                        style      = Stroke(width = 22f)
                    )
                    startAngle += sweep
                }
            }
            Spacer(Modifier.width(20.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                segments.forEachIndexed { i, (name, color) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("$name  ${values[i].toInt()}%",
                            fontSize = 13.sp, color = AppColors.TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun TipCard(emoji: String, tip: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
        border   = BorderStroke(1.dp, AppColors.CardBorder)
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.width(12.dp))
            Text(tip, fontSize = 13.sp, color = AppColors.TextSecondary,
                lineHeight = 20.sp)
        }
    }
}

// ─── Profile Screen ──────────────────────────────────────────────────────────

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(52.dp))

        // Avatar
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(AppColors.Primary.copy(0.4f), AppColors.PrimaryDim.copy(0.1f))
                    )
                )
                .border(2.dp, AppColors.Primary.copy(0.6f), CircleShape)
        ) {
            Text("🌿", fontSize = 40.sp)
        }

        Spacer(Modifier.height(12.dp))
        Text("Alex Johnson", fontSize = 22.sp, fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary)
        Text("Member since Jan 2024", fontSize = 13.sp, color = AppColors.TextSecondary)

        Spacer(Modifier.height(24.dp))

        // Stats
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStat("38", "Day Streak 🔥")
            ProfileStat("284 kg", "Saved CO₂")
            ProfileStat("3", "Badges")
        }

        Spacer(Modifier.height(28.dp))

        // Badges
        Text("Badges", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary,
            modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(14.dp))

        val rows = badges.chunked(3)
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { badge ->
                    BadgeCard(badge, Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(28.dp))

        // Settings list
        listOf(
            "🔔" to "Notifications",
            "🎯" to "Daily Goal",
            "🌍" to "Region",
            "❓" to "Help & FAQ",
            "🚪" to "Sign Out",
        ).forEach { (icon, label) ->
            SettingsRow(icon, label)
            Divider(color = AppColors.CardBorder, thickness = 0.5.dp)
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
        Text(label, fontSize = 11.sp, color = AppColors.TextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
fun BadgeCard(badge: BadgeItem, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = if (badge.earned) AppColors.Primary.copy(0.12f) else AppColors.Card
        ),
        border   = BorderStroke(
            1.dp,
            if (badge.earned) AppColors.Primary.copy(0.5f) else AppColors.CardBorder
        )
    ) {
        Column(
            modifier              = Modifier.padding(12.dp),
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            Text(
                badge.emoji,
                fontSize = 24.sp,
                color    = if (badge.earned) Color.Unspecified
                else Color.Gray.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                badge.label,
                fontSize  = 10.sp,
                textAlign = TextAlign.Center,
                color     = if (badge.earned) AppColors.TextPrimary else AppColors.TextMuted,
                lineHeight= 14.sp
            )
        }
    }
}

@Composable
fun SettingsRow(icon: String, label: String) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 15.sp, color = AppColors.TextPrimary, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, null, tint = AppColors.TextMuted, modifier = Modifier.size(18.dp))
    }
}

// ─── Utilities ───────────────────────────────────────────────────────────────

fun Float.format1() = "%.1f".format(this)
