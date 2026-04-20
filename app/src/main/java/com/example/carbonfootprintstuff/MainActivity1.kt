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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
    // Shared goal state — single source of truth for dashboard and daily goal screen
    var dailyGoalKg by remember { mutableStateOf(15f) }

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
                composable("dashboard") { DashboardScreen(dailyGoalKg) }
                composable("log")       { LogScreen() }
                composable("insights")  { InsightsScreen() }
                composable("profile")    { ProfileScreen(navController) }
                composable("friends")    { FriendsScreen(navController) }
                composable("dailygoal") { DailyGoalScreen(navController, dailyGoalKg) { dailyGoalKg = it } }
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
fun DashboardScreen(dailyGoalKg: Float = 15f) {
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
            else -> HeroCard(totalKg, dailyGoalKg)
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
fun HeroCard(totalKg: Float, dailyGoalKg: Float = 15f) {
    val animVal  by animateFloatAsState(totalKg,
        animationSpec = tween(1200, easing = EaseOutCubic), label = "hero")
    val progress = (totalKg / dailyGoalKg).coerceIn(0f, 1f)

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
            Text("Daily goal: ${dailyGoalKg.toInt()} kg", fontSize = 12.sp,
                color = AppColors.TextSecondary)
            Spacer(Modifier.height(6.dp))
            LinearProgressBar(progress)
            Spacer(Modifier.height(8.dp))
            val remaining = dailyGoalKg - totalKg
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

data class FoodItem(val name: String, val emoji: String, val kgCo2: Float)

data class TransportOption(
    val name: String,
    val emoji: String,
    val kgCo2PerMin: Float,
    val rateLabel: String
)

val foodItems = listOf(
    FoodItem("Beef",    "🥩", 7.70f),
    FoodItem("Fish",    "🐟", 1.80f),
    FoodItem("Pork",    "🥓", 1.80f),
    FoodItem("Chicken", "🍗", 1.36f),
    FoodItem("Pasta",   "🍝", 0.117f),
    FoodItem("Rice",    "🍚", 0.33f),
    FoodItem("Tomato",  "🍅", 0.16f),
    FoodItem("Banana",  "🍌", 0.07f),
    FoodItem("Apple",   "🍎", 0.04f),
    FoodItem("Potato",  "🥔", 0.04f),
)

val transportOptions = listOf(
    TransportOption("Driving",          "🚗", 0.056f,  "0.056 kg CO2/min"),
    TransportOption("Public Transport", "🚌", 0.0122f, "0.0122 kg CO2/min"),
)

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
    var loggedKg           by remember { mutableStateOf(0f) }
    var loggedTransport    by remember { mutableStateOf("") }
    var loggedFoodSnapshot by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    // Food state
    var foodQuantities by remember { mutableStateOf(foodItems.associate { it.name to 0 }) }
    val foodTotalKg = foodItems.sumOf { item ->
        (foodQuantities[item.name] ?: 0) * item.kgCo2.toDouble()
    }.toFloat()

    // Transport state
    var selectedTransport by remember { mutableStateOf(transportOptions[0]) }
    var transportMinutes  by remember { mutableStateOf(10f) }
    val transportTotalKg  = selectedTransport.kgCo2PerMin * transportMinutes

    val actualKg = when (selected) {
        "Food"      -> foodTotalKg
        "Transport" -> transportTotalKg
        else        -> sliderVal
    }

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
                        onClick  = { selected = cat; submitted = false; errorMsg = "" },
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

        when (selected) {

            // ── Transport picker ──────────────────────────────────────────────
            "Transport" -> {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("How did you travel?", fontSize = 14.sp,
                        color = AppColors.TextSecondary)
                    Text("Total: ${"%.3f".format(transportTotalKg)} kg CO2",
                        fontSize   = 13.sp, fontWeight = FontWeight.SemiBold,
                        color      = if (transportTotalKg > 0) AppColors.Primary
                        else AppColors.TextMuted)
                }
                Spacer(Modifier.height(10.dp))

                // Transport type card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
                    border   = BorderStroke(1.dp, AppColors.CardBorder)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        transportOptions.forEachIndexed { index, option ->
                            val isSelected = selectedTransport.name == option.name
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) AppColors.Primary.copy(0.1f)
                                        else Color.Transparent
                                    )
                                    .clickable { selectedTransport = option }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(option.emoji, fontSize = 24.sp)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(option.name, fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) AppColors.Primary
                                        else AppColors.TextPrimary)
                                    Text(option.rateLabel,
                                        fontSize = 11.sp, color = AppColors.TextMuted)
                                }
                                if (isSelected) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(22.dp).clip(CircleShape)
                                            .background(AppColors.Primary)
                                    ) {
                                        Text("✓", fontSize = 12.sp,
                                            color      = AppColors.Background,
                                            fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (index < transportOptions.size - 1) {
                                Divider(
                                    color     = AppColors.CardBorder.copy(alpha = 0.5f),
                                    thickness = 0.5.dp,
                                    modifier  = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Minutes slider
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Duration", fontSize = 14.sp, color = AppColors.TextSecondary)
                    Text("${transportMinutes.toInt()} minutes",
                        fontSize   = 14.sp, fontWeight = FontWeight.SemiBold,
                        color      = AppColors.Primary)
                }
                Spacer(Modifier.height(8.dp))
                Slider(
                    value         = transportMinutes,
                    onValueChange = { transportMinutes = it },
                    valueRange    = 1f..180f,
                    colors        = SliderDefaults.colors(
                        thumbColor         = AppColors.Primary,
                        activeTrackColor   = AppColors.Primary,
                        inactiveTrackColor = AppColors.CardBorder
                    )
                )

                // Quick time buttons
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10, 20, 30, 60, 90).forEach { mins ->
                        val isActive = transportMinutes.toInt() == mins
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isActive) AppColors.Primary.copy(0.2f)
                                    else AppColors.Card
                                )
                                .border(1.dp,
                                    if (isActive) AppColors.Primary else AppColors.CardBorder,
                                    RoundedCornerShape(10.dp))
                                .clickable { transportMinutes = mins.toFloat() }
                                .padding(vertical = 8.dp)
                        ) {
                            Text("${mins}m", fontSize = 12.sp,
                                color      = if (isActive) AppColors.Primary
                                else AppColors.TextSecondary,
                                fontWeight = if (isActive) FontWeight.Bold
                                else FontWeight.Normal)
                        }
                    }
                }
            }

            // ── Food picker ───────────────────────────────────────────────────
            "Food" -> {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("What did you eat?", fontSize = 14.sp,
                        color = AppColors.TextSecondary)
                    Text("Total: ${"%.3f".format(foodTotalKg)} kg CO2",
                        fontSize   = 13.sp, fontWeight = FontWeight.SemiBold,
                        color      = if (foodTotalKg > 0) AppColors.Primary
                        else AppColors.TextMuted)
                }
                Spacer(Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
                    border   = BorderStroke(1.dp, AppColors.CardBorder)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        foodItems.forEachIndexed { index, item ->
                            FoodItemRow(
                                item     = item,
                                quantity = foodQuantities[item.name] ?: 0,
                                onIncrease = {
                                    foodQuantities = foodQuantities.toMutableMap().also {
                                        it[item.name] = (it[item.name] ?: 0) + 1
                                    }
                                },
                                onDecrease = {
                                    foodQuantities = foodQuantities.toMutableMap().also {
                                        it[item.name] = maxOf(0, (it[item.name] ?: 0) - 1)
                                    }
                                }
                            )
                            if (index < foodItems.size - 1) {
                                Divider(
                                    color     = AppColors.CardBorder.copy(alpha = 0.5f),
                                    thickness = 0.5.dp,
                                    modifier  = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }
                if (foodTotalKg > 0) {
                    Spacer(Modifier.height(8.dp))
                    Text("↺ Reset all", fontSize = 12.sp, color = AppColors.TextMuted,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { foodQuantities = foodItems.associate { it.name to 0 } }
                            .padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            // ── Slider for Energy / Shopping / Flights ────────────────────────
            else -> {
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
            }
        }

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
                        val amountToLog  = actualKg
                        val activityDesc = when (selected) {
                            "Transport" -> "${selectedTransport.name} for ${transportMinutes.toInt()} mins"
                            "Food"      -> foodQuantities.filter { it.value > 0 }
                                .entries.joinToString(", ") { "${it.value}x ${it.key}" }
                                .ifEmpty { "Food" }
                            else        -> selected
                        }
                        api.addEntry(
                            EntryRequest(
                                user_name      = userName,
                                category       = selected,
                                activity       = activityDesc,
                                carbon_kg_co2e = amountToLog,
                                notes          = note
                            )
                        )
                        loggedKg = amountToLog
                        loggedTransport = selectedTransport.name
                        loggedFoodSnapshot = foodQuantities.filter { it.value > 0 }
                        when (selected) {
                            "Food"      -> foodQuantities = foodItems.associate { it.name to 0 }
                            "Transport" -> transportMinutes = 10f
                        }
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
            enabled = !submitting && actualKg > 0f
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
                    Text("Logged ${"%.3f".format(loggedKg)} kg CO2 for $selected!",
                        color = AppColors.TextPrimary, fontSize = 14.sp)
                }
            }

            val suggestion = when (selected) {
                "Transport" -> when (loggedTransport) {
                    "Driving" -> Pair("🚲", "Next time, try cycling or walking — zero emissions and great for your health!")
                    "Public Transport" -> Pair("✅", "Great choice! Public transport is already one of the lowest-emission ways to travel.")
                    else -> null
                }
                "Food" -> {
                    val heaviestItem = loggedFoodSnapshot.entries
                        .filter { it.value > 0 }
                        .maxByOrNull { entry -> foodItems.find { it.name == entry.key }?.kgCo2 ?: 0f }
                    when (heaviestItem?.key) {
                        "Beef"    -> Pair("🐔", "Beef is the highest-emission food — swapping for chicken saves ~6 kg CO2 per serving!")
                        "Pork"    -> Pair("🍗", "Try swapping pork for chicken — similar protein but ~25% less CO2.")
                        "Fish"    -> Pair("🥦", "Fish is moderate — plant proteins like lentils or tofu have up to 90% less CO2.")
                        "Chicken" -> Pair("🥗", "Good choice! For even lower emissions, try plant-based proteins like lentils.")
                        "Rice"    -> Pair("🥔", "Rice has moderate emissions — potatoes or pasta produce less CO2.")
                        "Pasta"   -> Pair("✅", "Pasta is a low-emission carb — great choice!")
                        "Banana"  -> Pair("✅", "Bananas are one of the lowest-emission fruits — keep it up!")
                        "Apple"   -> Pair("✅", "Apples are very low emission — great snack choice!")
                        "Potato"  -> Pair("✅", "Potatoes are super low emission — excellent!")
                        "Tomato"  -> Pair("🥗", "Tomatoes are low emission! Pair with other veggies for a nearly carbon-neutral meal.")
                        else -> null
                    }
                }
                else -> null
            }
            if (suggestion != null) {
                Spacer(Modifier.height(10.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Card),
                    border = BorderStroke(1.dp, AppColors.CardBorder)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Text(suggestion.first, fontSize = 22.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Next time tip", fontSize = 11.sp,
                                color = AppColors.TextMuted, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(3.dp))
                            Text(suggestion.second, fontSize = 13.sp,
                                color = AppColors.TextSecondary, lineHeight = 19.sp)
                        }
                    }
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

// ─── Food Item Row ────────────────────────────────────────────────────────────

@Composable
fun FoodItemRow(
    item: FoodItem,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    val rowKg = item.kgCo2 * quantity
    Row(
        modifier          = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.emoji, fontSize = 22.sp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontSize = 14.sp,
                fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
            Text("${item.kgCo2} kg CO2 each",
                fontSize = 11.sp, color = AppColors.TextMuted)
        }
        if (quantity > 0) {
            Text("${"%.3f".format(rowKg)} kg",
                fontSize   = 12.sp, color = AppColors.Primary,
                fontWeight = FontWeight.SemiBold,
                modifier   = Modifier.padding(end = 10.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(28.dp).clip(CircleShape)
                    .background(
                        if (quantity > 0) AppColors.CardBorder
                        else AppColors.CardBorder.copy(alpha = 0.3f))
                    .clickable(enabled = quantity > 0) { onDecrease() }
            ) {
                Text("−", fontSize = 16.sp,
                    color = if (quantity > 0) AppColors.TextPrimary else AppColors.TextMuted)
            }
            Text("$quantity", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color    = if (quantity > 0) AppColors.Primary else AppColors.TextMuted,
                modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(28.dp).clip(CircleShape)
                    .background(AppColors.Primary.copy(alpha = 0.2f))
                    .clickable { onIncrease() }
            ) {
                Text("+", fontSize = 16.sp, color = AppColors.Primary)
            }
        }
    }
}

// ─── Insights Screen ──────────────────────────────────────────────────────────

@Composable
fun InsightsScreen() {
    var allEntries by remember { mutableStateOf<List<ApiEntry>>(emptyList()) }
    var totalKg    by remember { mutableStateOf(0f) }
    var loading    by remember { mutableStateOf(true) }
    var errorMsg   by remember { mutableStateOf("") }
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(refreshKey) {
        loading  = true
        errorMsg = ""
        try {
            val entriesResp = api.getEntries()
            val totalResp   = api.getTotal()
            totalKg    = totalResp.total_kg
            allEntries = entriesResp.entries
        } catch (e: Exception) {
            errorMsg = "Could not reach server"
        } finally {
            loading = false
        }
    }

    // Extract all months present in the data (e.g. "2026-03")
    val allMonths = allEntries
        .map { it.entry_date.substring(0, 7) }
        .distinct()
        .sorted()

    var selectedMonth by remember { mutableStateOf("") }
    if (selectedMonth.isEmpty() && allMonths.isNotEmpty()) {
        selectedMonth = allMonths.last()
    }

    val monthEntries = allEntries.filter { it.entry_date.startsWith(selectedMonth) }

    val dailyTotals = monthEntries
        .groupBy { it.entry_date }
        .mapValues { (_, entries) -> entries.sumOf { it.carbon_kg_co2e.toDouble() }.toFloat() }
        .entries
        .sortedBy { it.key }

    val categoryTotals = monthEntries
        .groupBy { it.category }
        .mapValues { (_, entries) -> entries.sumOf { it.carbon_kg_co2e.toDouble() }.toFloat() }

    val worstCategory = categoryTotals.maxByOrNull { it.value }?.key ?: ""
    val avgPerDay = if (dailyTotals.isNotEmpty())
        dailyTotals.sumOf { it.value.toDouble() }.toFloat() / dailyTotals.size
    else 0f

    // Smart tips based on real data
    val smartTips: List<Pair<String, String>> = buildList {
        when (worstCategory) {
            "Transport" -> {
                add("🚲" to "Transport is your biggest source — try cycling or public transit for short trips to cut it by up to 40%")
                add("🚗" to "Consider carpooling — sharing a ride halves your per-trip transport emissions instantly")
            }
            "Food" -> {
                add("🥗" to "Food is your top category — swapping one meal a day to plant-based could save ~1.5 kg CO2 daily")
                add("🛒" to "Buying local and seasonal food reduces transport emissions in your supply chain significantly")
            }
            "Energy" -> {
                add("💡" to "Energy is your biggest footprint — turning off standby devices overnight can save ~0.5 kg CO2 daily")
                add("🌡️" to "Lowering your thermostat by just 1 degree can reduce heating emissions by around 8%")
            }
            "Shopping" -> {
                add("🛍️" to "Shopping is your top category — buying secondhand or borrowing instead of buying new cuts emissions by up to 70%")
                add("📦" to "Avoid fast fashion — one fewer new clothing item per month saves around 2 kg CO2")
            }
            "Flights" -> {
                add("✈️" to "Flights dominate your footprint — one short-haul flight can emit more CO2 than a week of driving")
                add("🚆" to "Consider trains for journeys under 500km — they emit up to 90% less CO2 than flying")
            }
            else -> {
                add("🌱" to "Keep logging your activities — the more data you add, the better your personalised tips will be")
            }
        }
        if (avgPerDay > 10f)
            add("⚠️" to "Your daily average of ${"%.1f".format(avgPerDay)} kg is above the 10 kg sustainable target — small daily changes add up fast")
        else if (avgPerDay > 0f)
            add("✅" to "Great work! Your daily average of ${"%.1f".format(avgPerDay)} kg is within the sustainable range — keep it up")
        add("🌍" to "The global average is ~12 kg CO2 per person per day — every entry you log helps you track progress toward less")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(52.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text("Insights", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary)
                Text("Your impact over time", fontSize = 14.sp,
                    color = AppColors.TextSecondary)
            }
            Text("↻ Refresh", fontSize = 13.sp,
                color    = AppColors.Primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { refreshKey++ }
                    .padding(8.dp))
        }

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
                // Stats row
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Total",   "%.1f kg".format(totalKg), "all time",
                        AppColors.Primary, Modifier.weight(1f))
                    StatCard("Entries", "${allEntries.size}", "logged",
                        AppColors.Accent,  Modifier.weight(1f))
                    StatCard("Avg/day", "%.1f kg".format(avgPerDay), "per day",
                        AppColors.Chart2,  Modifier.weight(1f))
                }

                if (allMonths.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text("Monthly Trend", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                        Text("${dailyTotals.size} days logged",
                            fontSize = 12.sp, color = AppColors.TextMuted)
                    }
                    Spacer(Modifier.height(10.dp))
                    if (allMonths.size > 1) {
                        val monthNames = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(allMonths) { month ->
                                val isActive = month == selectedMonth
                                val parts = month.split("-")
                                val label = "${monthNames.getOrElse((parts.getOrNull(1)?.toIntOrNull() ?: 1) - 1) { month }} ${parts.getOrNull(0) ?: ""}"
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isActive) AppColors.Primary.copy(0.2f) else AppColors.Card)
                                        .border(1.dp, if (isActive) AppColors.Primary else AppColors.CardBorder, RoundedCornerShape(20.dp))
                                        .clickable { selectedMonth = month }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(label, fontSize = 13.sp,
                                        color      = if (isActive) AppColors.Primary else AppColors.TextSecondary,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                    if (dailyTotals.isNotEmpty()) {
                        MonthlyBarChart(dailyTotals)
                    } else {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = AppColors.Card),
                            border = BorderStroke(1.dp, AppColors.CardBorder)) {
                            Box(contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                                Text("No entries for this month", fontSize = 14.sp, color = AppColors.TextMuted)
                            }
                        }
                    }
                }

                if (categoryTotals.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    val _mnNames = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
                    val _parts = selectedMonth.split("-")
                    val _monthLabel = "${_mnNames.getOrElse((_parts.getOrNull(1)?.toIntOrNull() ?: 1) - 1) { selectedMonth }} ${_parts.getOrNull(0) ?: ""}"
                    Text("By Category — $_monthLabel", fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    val maxKg = categoryTotals.values.maxOrNull() ?: 1f
                    categoryTotals.entries
                        .sortedByDescending { it.value }
                        .forEach { (cat, kg) ->
                            val (icon, color) = categoryIcons[cat]
                                ?: Pair("📦", AppColors.TextSecondary)
                            RealCategoryRow(cat, icon, kg, color, maxKg)
                            Spacer(Modifier.height(10.dp))
                        }
                }
            }
        }

        // Smart tips — always shown
        Spacer(Modifier.height(24.dp))
        Text("Tips for You", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary)
        if (worstCategory.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("Based on your $worstCategory activity",
                fontSize = 12.sp, color = AppColors.TextMuted)
        }
        Spacer(Modifier.height(12.dp))
        smartTips.forEach { (emoji, tip) ->
            TipCard(emoji, tip)
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(100.dp))
    }
}

// ─── Monthly Bar Chart ────────────────────────────────────────────────────────

@Composable
fun MonthlyBarChart(dailyTotals: List<Map.Entry<String, Float>>) {
    val maxKg = dailyTotals.maxOf { it.value }.coerceAtLeast(1f)
    val goalKg = 10f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
        border   = BorderStroke(1.dp, AppColors.CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Goal line label
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Daily emissions", fontSize = 11.sp, color = AppColors.TextMuted)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape)
                        .background(AppColors.Accent))
                    Spacer(Modifier.width(4.dp))
                    Text("10 kg goal", fontSize = 11.sp, color = AppColors.TextMuted)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Chart area
            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                // Goal line
                val goalFrac = (goalKg / maxKg).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .align(Alignment.BottomStart)
                        .padding(bottom = (goalFrac * 120).dp)
                        .background(AppColors.Accent.copy(alpha = 0.4f))
                )

                // Bars
                Row(
                    modifier              = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment     = Alignment.Bottom
                ) {
                    dailyTotals.forEach { (date, kg) ->
                        val frac   by animateFloatAsState(kg / maxKg, tween(700), label = "mb")
                        val overGoal = kg > goalKg
                        val day    = date.takeLast(2).trimStart('0').ifEmpty { "0" }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height((frac * 100).dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(
                                        if (overGoal)
                                            Brush.verticalGradient(
                                                listOf(AppColors.Danger, AppColors.Danger.copy(0.5f)))
                                        else
                                            Brush.verticalGradient(
                                                listOf(AppColors.Primary, AppColors.Primary.copy(0.4f)))
                                    )
                            )
                            Spacer(Modifier.height(4.dp))
                            if (dailyTotals.size <= 14) {
                                Text(day, fontSize = 8.sp, color = AppColors.TextMuted,
                                    textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Summary row
            val overDays  = dailyTotals.count { it.value > goalKg }
            val underDays = dailyTotals.size - overDays
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$underDays", fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, color = AppColors.Primary)
                    Text("Under goal", fontSize = 10.sp, color = AppColors.TextMuted)
                }
                Box(modifier = Modifier.width(1.dp).height(32.dp)
                    .background(AppColors.CardBorder))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$overDays", fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, color = AppColors.Danger)
                    Text("Over goal", fontSize = 10.sp, color = AppColors.TextMuted)
                }
                Box(modifier = Modifier.width(1.dp).height(32.dp)
                    .background(AppColors.CardBorder))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("%.1f".format(dailyTotals.sumOf { it.value.toDouble() }.toFloat() / dailyTotals.size),
                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.Accent)
                    Text("Avg kg/day", fontSize = 10.sp, color = AppColors.TextMuted)
                }
            }
        }
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
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Text(sub,   fontSize = 11.sp, color = AppColors.TextSecondary)
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
fun ProfileScreen(nav: NavHostController) {
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
        SettingsRow("🔔", "Notifications") {}
        Divider(color = AppColors.CardBorder, thickness = 0.5.dp)
        SettingsRow("👤", "Friends") { nav.navigate("friends") }
        Divider(color = AppColors.CardBorder, thickness = 0.5.dp)
        SettingsRow("🎯", "Daily Goal") { nav.navigate("dailygoal") }
        Divider(color = AppColors.CardBorder, thickness = 0.5.dp)
        SettingsRow("🌍", "Region") {}
        Divider(color = AppColors.CardBorder, thickness = 0.5.dp)
        SettingsRow("❓", "Help & FAQ") {}
        Divider(color = AppColors.CardBorder, thickness = 0.5.dp)
        SettingsRow("🚪", "Sign Out") {}
        Divider(color = AppColors.CardBorder, thickness = 0.5.dp)
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
fun SettingsRow(icon: String, label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 16.dp),
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


// ─── Friends Screen ─────────────────────────────────────────────

data class Friend(val name: String, val avatar: String, val code: String)

@Composable
fun FriendsScreen(nav: NavHostController) {
    var friends       by remember { mutableStateOf(listOf(
        Friend("Maya",   "👩", "GRN-4821"),
        Friend("Jordan", "🧑", "BLU-7743"),
    )) }
    var inputCode  by remember { mutableStateOf("") }
    var addError   by remember { mutableStateOf("") }
    var addSuccess by remember { mutableStateOf("") }

    val codeToName = mapOf(
        "GRN-1234" to Pair("Liam",  "👦"),
        "GRN-5678" to Pair("Sofia", "👧"),
        "BLU-9999" to Pair("Noah",  "🧔"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(52.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp).clip(CircleShape)
                    .background(AppColors.Card)
                    .border(1.dp, AppColors.CardBorder, CircleShape)
                    .clickable { nav.popBackStack() }
            ) { Text("←", fontSize = 18.sp, color = AppColors.TextPrimary) }
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Friends", fontSize = 26.sp,
                    fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                Text("${friends.size} friends connected",
                    fontSize = 13.sp, color = AppColors.TextSecondary)
            }
        }

        Spacer(Modifier.height(28.dp))

        // Your friend code card
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Primary.copy(0.1f)),
            border = BorderStroke(1.dp, AppColors.Primary.copy(0.4f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Your Friend Code", fontSize = 12.sp,
                    color = AppColors.TextSecondary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GRN-0042", fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.Primary, letterSpacing = 2.sp)
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppColors.Primary.copy(0.2f))
                            .border(1.dp, AppColors.Primary.copy(0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) { Text("Copy", fontSize = 13.sp, color = AppColors.Primary,
                        fontWeight = FontWeight.SemiBold) }
                }
                Spacer(Modifier.height(8.dp))
                Text("Share this code with friends so they can add you",
                    fontSize = 12.sp, color = AppColors.TextMuted)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("Add a Friend", fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
        Spacer(Modifier.height(10.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Card),
            border = BorderStroke(1.dp, AppColors.CardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value         = inputCode,
                    onValueChange = { inputCode = it.uppercase(); addError = ""; addSuccess = "" },
                    placeholder   = { Text("e.g. GRN-1234", color = AppColors.TextMuted) },
                    label         = { Text("Friend Code", color = AppColors.TextSecondary) },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(14.dp),
                    singleLine    = true,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AppColors.Primary,
                        unfocusedBorderColor = AppColors.CardBorder,
                        focusedTextColor     = AppColors.TextPrimary,
                        unfocusedTextColor   = AppColors.TextPrimary,
                        cursorColor          = AppColors.Primary,
                        focusedLabelColor    = AppColors.Primary,
                        unfocusedLabelColor  = AppColors.TextSecondary
                    )
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        val trimmed = inputCode.trim()
                        when {
                            trimmed.isEmpty()                       -> addError = "Please enter a friend code"
                            trimmed == "GRN-0042"                   -> addError = "That’s your own code!"
                            friends.any { it.code == trimmed }      -> addError = "You are already friends with this person"
                            codeToName.containsKey(trimmed) -> {
                                val (name, avatar) = codeToName[trimmed]!!
                                friends    = friends + Friend(name, avatar, trimmed)
                                addSuccess = "Added $name as a friend!"
                                inputCode  = ""
                            }
                            else -> addError = "Friend code not found. Ask your friend to check it."
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary,
                        contentColor   = AppColors.Background)
                ) { Text("Add Friend", fontWeight = FontWeight.Bold) }

                if (addError.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text("⚠️ $addError", fontSize = 13.sp, color = AppColors.Danger)
                }
                if (addSuccess.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text("✅ $addSuccess", fontSize = 13.sp, color = AppColors.Primary)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("Your Friends", fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
        Spacer(Modifier.height(10.dp))

        if (friends.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Card),
                border = BorderStroke(1.dp, AppColors.CardBorder)) {
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                    Text("No friends added yet. Share your code!",
                        fontSize = 14.sp, color = AppColors.TextMuted,
                        textAlign = TextAlign.Center)
                }
            }
        } else {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Card),
                border = BorderStroke(1.dp, AppColors.CardBorder)) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    friends.forEachIndexed { index, friend ->
                        Row(modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Box(contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp).clip(CircleShape)
                                    .background(AppColors.CardBorder)
                                    .border(1.dp, AppColors.Primary.copy(0.3f), CircleShape)
                            ) { Text(friend.avatar, fontSize = 20.sp) }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(friend.name, fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                                Text(friend.code, fontSize = 11.sp, color = AppColors.TextMuted)
                            }
                            Box(contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppColors.Danger.copy(0.1f))
                                    .border(1.dp, AppColors.Danger.copy(0.3f), RoundedCornerShape(8.dp))
                                    .clickable { friends = friends.filter { it.code != friend.code } }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text("Remove", fontSize = 11.sp, color = AppColors.Danger) }
                        }
                        if (index < friends.size - 1) {
                            Divider(color = AppColors.CardBorder.copy(0.5f), thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}


// ─── Daily Goal Screen ───────────────────────────────────────────

@Composable
fun DailyGoalScreen(nav: NavHostController, initialGoal: Float = 15f, onGoalChanged: (Float) -> Unit = {}) {
    var goalKg        by remember { mutableStateOf(initialGoal) }
    var saved         by remember { mutableStateOf(false) }
    val presets       = listOf(5f, 8f, 10f, 12f, 15f, 20f)

    // Goal level description
    val goalLevel = when {
        goalKg <= 5f  -> Triple("🏆", "Carbon Hero",    "Extremely ambitious — well below global targets")
        goalKg <= 8f  -> Triple("🌟", "Eco Champion",   "Well below the sustainable 10 kg threshold")
        goalKg <= 10f -> Triple("✅", "On Target",          "Right at the sustainable daily target")
        goalKg <= 15f -> Triple("💪", "Making Progress", "Above sustainable but a solid starting goal")
        else          -> Triple("⚠️", "Room to Improve", "Above average — try lowering your goal over time")
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
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp).clip(CircleShape)
                    .background(AppColors.Card)
                    .border(1.dp, AppColors.CardBorder, CircleShape)
                    .clickable { nav.popBackStack() }
            ) { Text("←", fontSize = 18.sp, color = AppColors.TextPrimary) }
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Daily Goal", fontSize = 26.sp,
                    fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                Text("Set your daily CO2 target", fontSize = 13.sp,
                    color = AppColors.TextSecondary)
            }
        }

        Spacer(Modifier.height(28.dp))

        // Current goal hero card
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
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()) {
                Text("Daily Goal", fontSize = 13.sp, color = AppColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center) {
                    Text("%.0f".format(goalKg), fontSize = 56.sp,
                        fontWeight = FontWeight.ExtraBold, color = AppColors.Primary)
                    Text(" kg CO2", fontSize = 18.sp, color = AppColors.TextSecondary,
                        modifier = Modifier.padding(bottom = 10.dp))
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppColors.Primary.copy(0.15f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("${goalLevel.first}  ${goalLevel.second}",
                        fontSize = 14.sp, color = AppColors.Primary,
                        fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(6.dp))
                Text(goalLevel.third, fontSize = 12.sp,
                    color = AppColors.TextSecondary, textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.height(28.dp))

        // Slider
        Text("Adjust Goal", fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text("Drag to set your daily CO2 target in kg",
            fontSize = 12.sp, color = AppColors.TextMuted)
        Spacer(Modifier.height(16.dp))

        Slider(
            value         = goalKg,
            onValueChange = { goalKg = it; saved = false },
            valueRange    = 1f..30f,
            steps         = 28,
            colors        = SliderDefaults.colors(
                thumbColor         = AppColors.Primary,
                activeTrackColor   = AppColors.Primary,
                inactiveTrackColor = AppColors.CardBorder
            )
        )

        Spacer(Modifier.height(16.dp))

        // Preset buttons
        Text("Quick Presets", fontSize = 14.sp, color = AppColors.TextSecondary)
        Spacer(Modifier.height(10.dp))
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                val isActive = goalKg == preset
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isActive) AppColors.Primary.copy(0.2f) else AppColors.Card)
                        .border(1.dp,
                            if (isActive) AppColors.Primary else AppColors.CardBorder,
                            RoundedCornerShape(12.dp))
                        .clickable { goalKg = preset; saved = false }
                        .padding(vertical = 10.dp)
                ) {
                    Text("${preset.toInt()} kg", fontSize = 12.sp,
                        color      = if (isActive) AppColors.Primary else AppColors.TextSecondary,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        textAlign  = TextAlign.Center)
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // Reference card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(20.dp),
            colors   = CardDefaults.cardColors(containerColor = AppColors.Card),
            border   = BorderStroke(1.dp, AppColors.CardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Reference Points", fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                Spacer(Modifier.height(12.dp))
                listOf(
                    Triple(AppColors.Danger,   "~20 kg", "Global average per person per day"),
                    Triple(AppColors.Warning,  "~12 kg", "Average in developed countries"),
                    Triple(AppColors.Accent,   "~10 kg", "Sustainable target by 2030"),
                    Triple(AppColors.Primary,  "~5 kg",  "Target for 1.5°C climate goal"),
                ).forEach { (color, value, label) ->
                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                        Spacer(Modifier.width(10.dp))
                        Text(value, fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary,
                            modifier = Modifier.width(52.dp))
                        Text(label, fontSize = 12.sp, color = AppColors.TextSecondary)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Save button
        Button(
            onClick = { onGoalChanged(goalKg); saved = true },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape    = RoundedCornerShape(16.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = if (saved) AppColors.CardBorder else AppColors.Primary,
                contentColor   = AppColors.Background
            )
        ) {
            Text(
                if (saved) "✅  Goal saved — ${goalKg.toInt()} kg/day"
                else "Save Goal",
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = if (saved) AppColors.Primary else AppColors.Background
            )
        }

        Spacer(Modifier.height(100.dp))
    }
}

// ─── Utilities ────────────────────────────────────────────────────────────────

fun Float.format1() = "%.1f".format(this)