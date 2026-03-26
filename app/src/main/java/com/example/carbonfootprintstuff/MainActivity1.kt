package com.example.carbonfootprintstuff

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import kotlin.math.abs

// ─── Color Palette ───────────────────────────────────────────────────────────

object AppColors {
    val Background    = Color(0xFF0D1B12)
    val Surface       = Color(0xFF132018)
    val Card          = Color(0xFF1A2D20)
    val CardBorder    = Color(0xFF2A4A30)
    val Primary       = Color(0xFF4ADE80)
    val PrimaryDim    = Color(0xFF22C55E)
    val Accent        = Color(0xFFBEF264)
    val TextPrimary   = Color(0xFFF0FDF4)
    val TextSecondary = Color(0xFF86EFAC)
    val TextMuted     = Color(0xFF4ADE80).copy(alpha = 0.5f)
    val Danger        = Color(0xFFF87171)
    val Warning       = Color(0xFFFBBF24)
    val Chart1        = Color(0xFF4ADE80)
    val Chart2        = Color(0xFF34D399)
    val Chart3        = Color(0xFF6EE7B7)
    val Gold          = Color(0xFFFFD700)
    val Silver        = Color(0xFFC0C0C0)
    val Bronze        = Color(0xFFCD7F32)
}

// ─── Data Models ─────────────────────────────────────────────────────────────

data class WeeklyData(val day: String, val kg: Float)
data class BadgeItem(val emoji: String, val label: String, val earned: Boolean)

data class FeedPost(
    val userName: String,
    val avatar: String,
    val category: String,
    val activity: String,
    val kg: Float,
    val timeAgo: String,
    val likes: Int,
    val comment: String
)

data class LeaderEntry(
    val rank: Int,
    val userName: String,
    val avatar: String,
    val totalKg: Float,
    val isMe: Boolean = false
)

// ─── Static Data ─────────────────────────────────────────────────────────────

val categoryIcons = mapOf(
    "Transport" to Pair("🚗", AppColors.Danger),
    "Food"      to Pair("🥦", AppColors.Warning),
    "Energy"    to Pair("⚡", AppColors.Chart2),
    "Shopping"  to Pair("🛍️", AppColors.Chart3),
    "Flights"   to Pair("✈️", AppColors.TextMuted),
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

val sampleFeed = listOf(
    FeedPost("Jordan", "🧑", "Transport", "Cycled to work instead of driving",
        1.8f, "2m ago",  12, "Way to go! 🚴"),
    FeedPost("Maya",   "👩", "Food",      "Had a fully plant-based day",
        2.5f, "18m ago", 24, "Inspiring! 🥗"),
    FeedPost("Liam",   "👦", "Energy",    "Switched off all standby devices",
        0.6f, "1h ago",   8, "Every bit counts 💡"),
    FeedPost("Sofia",  "👧", "Shopping",  "Bought secondhand clothes only",
        1.2f, "3h ago",  31, "Thrift queen 👑"),
    FeedPost("Noah",   "🧔", "Transport", "Took the train instead of flying",
        4.1f, "5h ago",  19, "Big win!"),
)

val sampleLeaderboard = listOf(
    LeaderEntry(1, "Maya",   "👩", 18.4f),
    LeaderEntry(2, "Sofia",  "👧", 22.1f),
    LeaderEntry(3, "Alex",   "🌿", 27.3f, isMe = true),
    LeaderEntry(4, "Jordan", "🧑", 31.6f),
    LeaderEntry(5, "Liam",   "👦", 38.9f),
    LeaderEntry(6, "Noah",   "🧔", 44.2f),
)

// ─── MainActivity ─────────────────────────────────────────────────────────────

class MainActivity1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CarbonApp() }
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

// ─── Bottom Navigation ────────────────────────────────────────────────────────

@Composable
fun BottomNav(nav: NavHostController) {
    val current by nav.currentBackStackEntryAsState()
    val route   = current?.destination?.route
    NavigationBar(
        containerColor = AppColors.Surface,
        tonalElevation = 0.dp,
        modifier = Modifier
            .border(1.dp, AppColors.CardBorder,
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        listOf(
            Triple("dashboard", Icons.Filled.Home,     "Home"),
            Triple("log",       Icons.Filled.Add,      "Log"),
            Triple("insights",  Icons.Filled.BarChart, "Insights"),
            Triple("profile",   Icons.Filled.Person,   "Profile"),
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
                                if (selected) AppColors.Primary.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                    ) {
                        Icon(icon, label,
                            tint     = if (selected) AppColors.Primary else AppColors.TextMuted,
                            modifier = Modifier.size(22.dp))
                    }
                },
                label  = {
                    Text(label,
                        color    = if (selected) AppColors.Primary else AppColors.TextMuted,
                        fontSize = 11.sp)
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
        }
    }
}

// ─── Dashboard Screen ─────────────────────────────────────────────────────────

@Composable
fun DashboardScreen() {
    var totalKg  by remember { mutableStateOf(0f) }
    var loading  by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val response = api.getTotal()
            totalKg = response.total_kg
        } catch (e: Exception) {
            errorMsg = "Could not reach server"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(52.dp))

        // Header
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text("Good morning,", fontSize = 14.sp, color = AppColors.TextSecondary)
                Text("Alex 🌿", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp).clip(CircleShape)
                    .background(AppColors.Primary.copy(alpha = 0.15f))
                    .border(1.dp, AppColors.Primary.copy(alpha = 0.4f), CircleShape)
            ) { Text("🔔", fontSize = 20.sp) }
        }

        Spacer(Modifier.height(28.dp))

        // Hero card
        when {
            loading -> Box(Modifier.fillMaxWidth().height(160.dp),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppColors.Primary)
            }
            errorMsg.isNotEmpty() -> Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(20.dp),
                colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
                border   = BorderStroke(1.dp, AppColors.Danger)
            ) {
                Text("⚠️ $errorMsg — is your server running?",
                    modifier = Modifier.padding(20.dp),
                    color    = AppColors.Danger, fontSize = 14.sp)
            }
            else -> HeroCard(totalKg)
        }

        Spacer(Modifier.height(24.dp))

        // Weekly chart
        Text("This Week", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary)
        Spacer(Modifier.height(12.dp))
        WeeklyBarChart(sampleWeekly)

        Spacer(Modifier.height(28.dp))

        // ── Leaderboard ──────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("Leaderboard", fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            Text("This month", fontSize = 12.sp, color = AppColors.TextMuted)
        }
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(20.dp),
            colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
            border   = BorderStroke(1.dp, AppColors.CardBorder)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                sampleLeaderboard.forEachIndexed { index, entry ->
                    LeaderboardRow(entry)
                    if (index < sampleLeaderboard.size - 1) {
                        Divider(
                            color     = AppColors.CardBorder.copy(alpha = 0.5f),
                            thickness = 0.5.dp,
                            modifier  = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Friend Activity Feed ──────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("Friend Activity", fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            Text("Recent", fontSize = 12.sp, color = AppColors.TextMuted)
        }
        Spacer(Modifier.height(12.dp))
        sampleFeed.forEach { post ->
            FeedCard(post)
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(100.dp))
    }
}

// ─── Leaderboard Row ─────────────────────────────────────────────────────────

@Composable
fun LeaderboardRow(entry: LeaderEntry) {
    val rankLabel = when (entry.rank) {
        1    -> "🥇"
        2    -> "🥈"
        3    -> "🥉"
        else -> "#${entry.rank}"
    }
    val rankColor = when (entry.rank) {
        1    -> AppColors.Gold
        2    -> AppColors.Silver
        3    -> AppColors.Bronze
        else -> AppColors.TextMuted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (entry.isMe) AppColors.Primary.copy(alpha = 0.08f)
                else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.width(32.dp)) {
            if (entry.rank <= 3)
                Text(rankLabel, fontSize = 18.sp)
            else
                Text(rankLabel, fontSize = 13.sp,
                    fontWeight = FontWeight.Bold, color = rankColor)
        }

        Spacer(Modifier.width(12.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp).clip(CircleShape)
                .background(
                    if (entry.isMe) AppColors.Primary.copy(0.25f)
                    else AppColors.CardBorder
                )
                .border(
                    1.5.dp,
                    if (entry.isMe) AppColors.Primary else AppColors.CardBorder,
                    CircleShape
                )
        ) { Text(entry.avatar, fontSize = 18.sp) }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    entry.userName,
                    fontSize   = 14.sp,
                    fontWeight = if (entry.isMe) FontWeight.Bold else FontWeight.Medium,
                    color      = if (entry.isMe) AppColors.Primary else AppColors.TextPrimary
                )
                if (entry.isMe) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AppColors.Primary.copy(0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("You", fontSize = 10.sp, color = AppColors.Primary,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text("${entry.totalKg} kg CO2 this month",
                fontSize = 11.sp, color = AppColors.TextSecondary)
        }

        val maxKg = sampleLeaderboard.maxOf { it.totalKg }
        val frac  = entry.totalKg / maxKg
        Box(
            modifier = Modifier
                .width(50.dp).height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(AppColors.CardBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(frac).fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (entry.isMe) AppColors.Primary else AppColors.TextMuted
                    )
            )
        }
    }
}

// ─── Feed Card ────────────────────────────────────────────────────────────────

@Composable
fun FeedCard(post: FeedPost) {
    val (icon, catColor) = categoryIcons[post.category]
        ?: Pair("📦", AppColors.TextSecondary)
    var liked     by remember { mutableStateOf(false) }
    val likeCount  = if (liked) post.likes + 1 else post.likes

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
        border   = BorderStroke(1.dp, AppColors.CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Top row: avatar + name + time + category pill
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp).clip(CircleShape)
                        .background(AppColors.CardBorder)
                        .border(1.dp, AppColors.Primary.copy(0.3f), CircleShape)
                ) { Text(post.avatar, fontSize = 20.sp) }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(post.userName, fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                    Text(post.timeAgo, fontSize = 11.sp, color = AppColors.TextMuted)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(catColor.copy(alpha = 0.15f))
                        .border(1.dp, catColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("$icon ${post.category}",
                        fontSize = 11.sp, color = catColor,
                        fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Activity text
            Text(post.activity, fontSize = 14.sp, color = AppColors.TextPrimary,
                lineHeight = 20.sp)

            Spacer(Modifier.height(10.dp))

            // CO2 saved banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.Primary.copy(alpha = 0.08f))
                    .border(1.dp, AppColors.Primary.copy(0.2f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌱", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Saved ", fontSize = 13.sp, color = AppColors.TextSecondary)
                    Text("${post.kg} kg CO2",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = AppColors.Primary)
                    Text(" today", fontSize = 13.sp, color = AppColors.TextSecondary)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Like + comment row
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { liked = !liked }
                        .background(
                            if (liked) AppColors.Danger.copy(0.12f) else Color.Transparent
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Like",
                        tint     = if (liked) AppColors.Danger else AppColors.TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("$likeCount", fontSize = 12.sp,
                        color = if (liked) AppColors.Danger else AppColors.TextMuted)
                }

                Spacer(Modifier.width(12.dp))

                Text("💬 ${post.comment}",
                    fontSize = 12.sp,
                    color    = AppColors.TextSecondary,
                    modifier = Modifier.weight(1f))
            }
        }
    }
}

// ─── Hero Card ────────────────────────────────────────────────────────────────

@Composable
fun HeroCard(totalKg: Float) {
    val animVal by animateFloatAsState(totalKg,
        animationSpec = tween(1200, easing = EaseOutCubic), label = "hero")
    val goal     = 15f
    val progress = (totalKg / goal).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF14532D), Color(0xFF052E16))))
            .border(1.dp,
                Brush.linearGradient(listOf(AppColors.Primary.copy(0.6f), Color.Transparent)),
                RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            Text("Total Footprint", fontSize = 13.sp, color = AppColors.TextSecondary)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("%.1f".format(animVal), fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold, color = AppColors.Primary)
                Text(" kg CO2", fontSize = 18.sp, color = AppColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("Daily goal: ${goal.toInt()} kg", fontSize = 12.sp,
                color = AppColors.TextSecondary)
            Spacer(Modifier.height(6.dp))
            LinearProgressBar(progress)
            Spacer(Modifier.height(8.dp))
            val remaining = goal - totalKg
            Text(
                if (remaining > 0) "Under goal by ${remaining.format1()} kg!"
                else "Over goal by ${(-remaining).format1()} kg",
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
        modifier = Modifier.fillMaxWidth().height(8.dp)
            .clip(RoundedCornerShape(4.dp)).background(AppColors.CardBorder)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(animProgress).fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(Brush.horizontalGradient(
                    listOf(AppColors.PrimaryDim, AppColors.Accent)))
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
            modifier              = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom
        ) {
            data.forEach { d -> BarColumn(d, maxKg, data.indexOf(d) == 5) }
        }
    }
}

@Composable
fun BarColumn(d: WeeklyData, maxKg: Float, highlight: Boolean) {
    val animH by animateFloatAsState(d.kg / maxKg, tween(800), label = "bar")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.width(32.dp)
    ) {
        Text("${d.kg.toInt()}", fontSize = 9.sp,
            color = if (highlight) AppColors.Accent else AppColors.TextMuted)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier.width(18.dp).height((animH * 80).dp)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(
                    if (highlight)
                        Brush.verticalGradient(listOf(AppColors.Accent, AppColors.PrimaryDim))
                    else
                        Brush.verticalGradient(listOf(
                            AppColors.Primary.copy(0.7f), AppColors.Primary.copy(0.3f)))
                )
        )
        Spacer(Modifier.height(6.dp))
        Text(d.day, fontSize = 11.sp,
            color = if (highlight) AppColors.Primary else AppColors.TextSecondary)
    }
}

// ─── Log Screen ───────────────────────────────────────────────────────────────

@Composable
fun LogScreen() {
    var selected   by remember { mutableStateOf("Transport") }
    val categories = listOf("Transport", "Food", "Energy", "Shopping", "Flights")
    var sliderVal  by remember { mutableStateOf(5f) }
    var note       by remember { mutableStateOf("") }
    var userName   by remember { mutableStateOf("Alex") }
    var submitted  by remember { mutableStateOf(false) }
    var submitting by remember { mutableStateOf(false) }
    var errorMsg   by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

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

        Spacer(Modifier.height(20.dp))

        Text("Your Name", fontSize = 14.sp, color = AppColors.TextSecondary)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value         = userName,
            onValueChange = { userName = it },
            placeholder   = { Text("e.g. Alex", color = AppColors.TextMuted) },
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

        Spacer(Modifier.height(20.dp))

        Text("Category", fontSize = 14.sp, color = AppColors.TextSecondary)
        Spacer(Modifier.height(10.dp))
        categories.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { cat ->
                    val active = selected == cat
                    FilterChip(
                        selected = active,
                        onClick  = { selected = cat },
                        label    = { Text(cat, fontSize = 13.sp) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppColors.Primary.copy(alpha = 0.2f),
                            selectedLabelColor     = AppColors.Primary,
                            containerColor         = AppColors.Card,
                            labelColor             = AppColors.TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled             = true,
                            selected            = active,
                            borderColor         = AppColors.CardBorder,
                            selectedBorderColor = AppColors.Primary
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(20.dp))

        Text("Amount: ${"%.1f".format(sliderVal)} kg CO2",
            fontSize = 14.sp, color = AppColors.TextSecondary)
        Spacer(Modifier.height(8.dp))
        Slider(
            value         = sliderVal,
            onValueChange = { sliderVal = it },
            valueRange    = 0f..30f,
            colors        = SliderDefaults.colors(
                thumbColor         = AppColors.Primary,
                activeTrackColor   = AppColors.Primary,
                inactiveTrackColor = AppColors.CardBorder
            )
        )

        Spacer(Modifier.height(20.dp))

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

        Button(
            onClick = {
                scope.launch {
                    submitting = true
                    errorMsg   = ""
                    try {
                        api.addEntry(
                            EntryRequest(
                                user_name      = userName,
                                category       = selected,
                                activity       = selected,
                                carbon_kg_co2e = sliderVal,
                                notes          = note
                            )
                        )
                        submitted = true
                    } catch (e: Exception) {
                        errorMsg = "Could not save — is your server running?"
                    } finally {
                        submitting = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape    = RoundedCornerShape(16.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                contentColor   = AppColors.Background
            ),
            enabled = !submitting
        ) {
            if (submitting)
                CircularProgressIndicator(color = AppColors.Background,
                    modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            else
                Text("Log Activity", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (submitted) {
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = AppColors.Primary.copy(0.15f)),
                border   = BorderStroke(1.dp, AppColors.Primary.copy(0.4f))
            ) {
                Row(modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("✅", fontSize = 22.sp)
                    Spacer(Modifier.width(12.dp))
                    Text("Logged ${"%.1f".format(sliderVal)} kg for $selected!",
                        color = AppColors.TextPrimary, fontSize = 14.sp)
                }
            }
        }

        if (errorMsg.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = AppColors.Danger.copy(0.15f)),
                border   = BorderStroke(1.dp, AppColors.Danger.copy(0.4f))
            ) {
                Text("⚠️ $errorMsg", modifier = Modifier.padding(16.dp),
                    color = AppColors.Danger, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

// ─── Insights Screen ──────────────────────────────────────────────────────────

@Composable
fun InsightsScreen() {
    var entries  by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var totalKg  by remember { mutableStateOf(0f) }
    var loading  by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val entriesResp = api.getEntries()
            val totalResp   = api.getTotal()
            totalKg = totalResp.total_kg
            entries = entriesResp.entries.map { row ->
                mapOf(
                    "category" to (row.getOrNull(2)?.toString() ?: ""),
                    "kg"       to ((row.getOrNull(5) as? Double)?.toFloat() ?: 0f)
                )
            }
        } catch (e: Exception) {
            errorMsg = "Could not reach server"
        } finally {
            loading = false
        }
    }

    val categoryTotals = entries
        .groupBy { it["category"] as String }
        .mapValues { (_, rows) -> rows.sumOf { (it["kg"] as Float).toDouble() }.toFloat() }

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
        Text("Your impact over time", fontSize = 14.sp, color = AppColors.TextSecondary)
        Spacer(Modifier.height(28.dp))

        when {
            loading -> Box(Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppColors.Primary)
            }
            errorMsg.isNotEmpty() -> Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
                border   = BorderStroke(1.dp, AppColors.Danger)
            ) {
                Text("⚠️ $errorMsg", modifier = Modifier.padding(16.dp),
                    color = AppColors.Danger, fontSize = 14.sp)
            }
            else -> {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Total",   "%.1f kg".format(totalKg), "all time",
                        AppColors.Primary, Modifier.weight(1f))
                    StatCard("Entries", "${entries.size}", "logged",
                        AppColors.Accent,  Modifier.weight(1f))
                }
                if (categoryTotals.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    Text("By Category", fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    categoryTotals.forEach { (cat, kg) ->
                        val (icon, color) = categoryIcons[cat]
                            ?: Pair("📦", AppColors.TextSecondary)
                        val maxKg = categoryTotals.values.maxOrNull() ?: 1f
                        RealCategoryRow(cat, icon, kg, color, maxKg)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Tips for You", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary)
        Spacer(Modifier.height(12.dp))
        listOf(
            "🚲" to "Try cycling short distances to cut transport emissions by up to 30%",
            "🥗" to "One plant-based day per week saves ~2.5 kg CO2",
            "💡" to "Switch to LED bulbs — saves ~0.5 kg CO2 per bulb/month",
        ).forEach { (emoji, tip) ->
            TipCard(emoji, tip)
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun RealCategoryRow(category: String, icon: String, kg: Float,
                    color: Color, maxKg: Float) {
    val progress by animateFloatAsState(kg / maxKg.coerceAtLeast(1f),
        tween(900), label = "cat")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
        border   = BorderStroke(1.dp, AppColors.CardBorder)
    ) {
        Row(modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(category, fontSize = 14.sp,
                        fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
                    Text("%.2f kg".format(kg), fontSize = 13.sp,
                        color = AppColors.TextSecondary)
                }
                Spacer(Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth().height(5.dp)
                    .clip(RoundedCornerShape(3.dp)).background(AppColors.CardBorder)
                ) {
                    Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp)).background(color))
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, sub: String,
             color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
        border   = BorderStroke(1.dp, AppColors.CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = AppColors.TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Text(sub,   fontSize = 12.sp, color = AppColors.TextSecondary)
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
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.width(12.dp))
            Text(tip, fontSize = 13.sp, color = AppColors.TextSecondary, lineHeight = 20.sp)
        }
    }
}

// ─── Profile Screen ───────────────────────────────────────────────────────────

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
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp).clip(CircleShape)
                .background(Brush.radialGradient(listOf(
                    AppColors.Primary.copy(0.4f), AppColors.PrimaryDim.copy(0.1f))))
                .border(2.dp, AppColors.Primary.copy(0.6f), CircleShape)
        ) { Text("🌿", fontSize = 40.sp) }

        Spacer(Modifier.height(12.dp))
        Text("Alex Johnson", fontSize = 22.sp, fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary)
        Text("Member since Jan 2024", fontSize = 13.sp, color = AppColors.TextSecondary)
        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            ProfileStat("38", "Day Streak")
            ProfileStat("3",  "Badges")
            ProfileStat("#3", "Rank")
        }

        Spacer(Modifier.height(28.dp))
        Text("Badges", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
            color    = AppColors.TextPrimary,
            modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(14.dp))

        badges.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()) {
                row.forEach { badge -> BadgeCard(badge, Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(28.dp))
        listOf(
            "🔔" to "Notifications",
            "\uD83D\uDC64" to "Friends",
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
        Text(label, fontSize = 11.sp, color = AppColors.TextSecondary,
            textAlign = TextAlign.Center)
    }
}

@Composable
fun BadgeCard(badge: BadgeItem, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = if (badge.earned) AppColors.Primary.copy(0.12f)
            else AppColors.Card),
        border = BorderStroke(1.dp,
            if (badge.earned) AppColors.Primary.copy(0.5f) else AppColors.CardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(badge.emoji, fontSize = 24.sp,
                color = if (badge.earned) Color.Unspecified
                else Color.Gray.copy(alpha = 0.4f))
            Spacer(Modifier.height(4.dp))
            Text(badge.label, fontSize = 10.sp, textAlign = TextAlign.Center,
                color      = if (badge.earned) AppColors.TextPrimary else AppColors.TextMuted,
                lineHeight = 14.sp)
        }
    }
}

@Composable
fun SettingsRow(icon: String, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 15.sp, color = AppColors.TextPrimary,
            modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, null, tint = AppColors.TextMuted,
            modifier = Modifier.size(18.dp))
    }
}

// ─── Utilities ────────────────────────────────────────────────────────────────

fun Float.format1() = "%.1f".format(this)
