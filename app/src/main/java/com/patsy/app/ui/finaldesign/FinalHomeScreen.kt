package com.patsy.app.ui.finaldesign

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.R

enum class FinalHomeDestination { HOME, THYNK, CREATE, PATSY_DMS, PROFILE }

private data class FinalDesignCard(
    val title: String,
    val edited: String,
    val type: String,
    val background: Brush,
)

private val finalDesignCards = listOf(
    FinalDesignCard("Happy dog post", "Edited 2h ago", "Image", Brush.linearGradient(listOf(Color(0xFF173527), Color(0xFF395A35)))),
    FinalDesignCard("Beach vibes", "Edited yesterday", "Video", Brush.linearGradient(listOf(Color(0xFF6F335D), Color(0xFFFF9A55)))),
    FinalDesignCard("Adopt don't shop", "Edited 2d ago", "Post", Brush.linearGradient(listOf(Color(0xFF111111), Color(0xFF292929)))),
    FinalDesignCard("Dog tip template", "Edited 3d ago", "Template", Brush.linearGradient(listOf(Color(0xFFE8D0AE), Color(0xFF85A9B2)))),
)

@Composable
fun FinalHomeScreen(
    onNavigate: (FinalHomeDestination) -> Unit,
    onAskPatsy: () -> Unit = {},
    onCreatePost: () -> Unit = {},
    onOpenThynkMusic: () -> Unit = {},
    onOpenThynkIt: () -> Unit = {},
) {
    Box(Modifier.fillMaxSize().background(FinalCharcoal)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { FinalHomeTopBar() }
            item {
                Box(
                    Modifier
                        .padding(horizontal = 22.dp)
                        .fillMaxWidth()
                        .border(2.dp, FinalRainbow, RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF101014))
                        .clickable(onClick = onAskPatsy)
                        .padding(horizontal = 18.dp, vertical = 15.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✦", style = TextStyle(brush = FinalRainbow, fontSize = 23.sp))
                        Spacer(Modifier.width(10.dp))
                        Text("Ask Patsy anything...", color = Color(0xFFB4B4BD), fontSize = 17.sp, modifier = Modifier.weight(1f))
                        Text("◉", color = FinalWhite, fontSize = 24.sp)
                    }
                }
            }
            item {
                Text(
                    "Hey! What can I help with today? 💜",
                    color = FinalWhite,
                    fontSize = 17.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(horizontal = 28.dp),
                )
            }
            item {
                VisibleThynkLaunchSection(
                    onOpenThynkMusic = onOpenThynkMusic,
                    onOpenThynkIt = onOpenThynkIt,
                )
            }
            item { ContinueDesignsSection(onNewDesign = { onNavigate(FinalHomeDestination.THYNK) }) }
            item { TodaySection() }
            item { CreatePostSection(onCreatePost) }
            item { FeedTabs() }
            item { SocialFeedPreview() }
            item { Spacer(Modifier.height(18.dp)) }
        }
    }
}

@Composable
private fun VisibleThynkLaunchSection(
    onOpenThynkMusic: () -> Unit,
    onOpenThynkIt: () -> Unit,
) {
    Column(
        Modifier
            .padding(horizontal = 22.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            "CREATE IN THyNK-IN!",
            style = TextStyle(brush = FinalRainbow, fontSize = 13.sp, fontWeight = FontWeight.Black),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(
                Modifier
                    .weight(1f)
                    .height(126.dp)
                    .border(2.dp, FinalRainbow, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0A0A0F))
                    .clickable(onClick = onOpenThynkMusic)
                    .padding(14.dp),
            ) {
                Text("THyNK Music", color = FinalWhite, fontSize = 19.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                Text("MIX • RECORD • DJ • MASTER", color = FinalMuted, fontSize = 10.sp, lineHeight = 14.sp)
                Spacer(Modifier.weight(1f))
                Text("OPEN STUDIO →", style = TextStyle(brush = FinalRainbow, fontSize = 11.sp, fontWeight = FontWeight.Bold))
            }
            Column(
                Modifier
                    .weight(1f)
                    .height(126.dp)
                    .border(2.dp, FinalRainbow, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0A0A0F))
                    .clickable(onClick = onOpenThynkIt)
                    .padding(14.dp),
            ) {
                Text("THyNK-IT", color = FinalWhite, fontSize = 19.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                Text("PUBLISH • FASHION • PHOTO • DESIGN", color = FinalMuted, fontSize = 10.sp, lineHeight = 14.sp)
                Spacer(Modifier.weight(1f))
                Text("OPEN STUDIO →", style = TextStyle(brush = FinalRainbow, fontSize = 11.sp, fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun FinalHomeTopBar() {
    Box(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 10.dp)) {
        ThynkDestinationLogo(
            destination = ThynkPanelDestination.IN,
            modifier = Modifier.align(Alignment.Center).width(120.dp).height(68.dp),
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                Modifier.size(45.dp).border(1.dp, Color(0xFF55555D), CircleShape).clip(CircleShape).background(Color(0xFF111115)),
                contentAlignment = Alignment.Center,
            ) {
                Text("♢", color = FinalWhite, fontSize = 23.sp)
                Box(Modifier.align(Alignment.TopEnd).size(10.dp).background(Color(0xFFFF3D82), CircleShape))
            }
            Box(
                Modifier.size(45.dp).border(1.4.dp, FinalRainbow, CircleShape).clip(CircleShape).background(Color(0xFF111115)),
                contentAlignment = Alignment.Center,
            ) {
                Text("•••", color = FinalWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun ContinueDesignsSection(onNewDesign: () -> Unit) {
    Column(
        Modifier
            .padding(horizontal = 22.dp)
            .fillMaxWidth()
            .border(1.dp, Color(0xFF303036), RoundedCornerShape(24.dp))
            .padding(vertical = 12.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("CONTINUE DESIGNS", color = FinalWhite, fontSize = 13.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            Text("View all ›", style = TextStyle(brush = FinalRainbow, fontSize = 12.sp, fontWeight = FontWeight.SemiBold))
        }
        Spacer(Modifier.height(10.dp))
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(finalDesignCards) { card -> DesignPreviewCard(card) }
            item { NewDesignCard(onNewDesign) }
        }
    }
}

@Composable
private fun DesignPreviewCard(card: FinalDesignCard) {
    Column(
        Modifier
            .width(142.dp)
            .border(1.dp, Color(0xFF38383E), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF101014)),
    ) {
        Box(Modifier.fillMaxWidth().height(115.dp).background(card.background)) {
            if (card.title == "Happy dog post" || card.title == "Dog tip template") {
                Image(
                    painter = painterResource(R.drawable.patsy_generated_main),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Box(
                Modifier.align(Alignment.BottomStart).padding(7.dp).background(FinalRainbow, RoundedCornerShape(9.dp)).padding(horizontal = 7.dp, vertical = 3.dp),
            ) {
                Text(card.type, color = FinalWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Column(Modifier.padding(8.dp)) {
            Text(card.title, color = FinalWhite, fontSize = 11.sp, maxLines = 1)
            Text(card.edited, color = FinalMuted, fontSize = 9.sp)
        }
    }
}

@Composable
private fun NewDesignCard(onClick: () -> Unit) {
    Box(
        Modifier
            .width(122.dp)
            .height(160.dp)
            .border(1.dp, Color(0xFF38383E), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF101014))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(54.dp).border(2.dp, FinalRainbow, CircleShape), contentAlignment = Alignment.Center) {
                Text("+", color = FinalWhite, fontSize = 33.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text("New Design", color = FinalWhite, fontSize = 11.sp)
        }
    }
}

@Composable
private fun TodaySection() {
    Column(
        Modifier
            .padding(horizontal = 22.dp)
            .fillMaxWidth()
            .border(1.dp, Color(0xFF303036), RoundedCornerShape(24.dp))
            .padding(12.dp),
    ) {
        Text("TODAY", color = FinalWhite, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            TodayItem("▣", "2 scheduled posts", "Next one at 2:00 PM", Modifier.weight(1f))
            TodayItem("♢", "1 reminder", "Vet appointment at 4:30 PM", Modifier.weight(1f))
            TodayItem("🐾", "Pet Awareness Day", "National Dog Day – 26 Aug", Modifier.weight(1f))
        }
    }
}

@Composable
private fun TodayItem(icon: String, title: String, detail: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).border(1.4.dp, FinalRainbow, CircleShape), contentAlignment = Alignment.Center) {
            Text(icon, color = FinalWhite, fontSize = 17.sp)
        }
        Spacer(Modifier.width(7.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = FinalWhite, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 2)
            Text(detail, color = FinalMuted, fontSize = 9.sp, maxLines = 2)
        }
        Text("›", color = FinalMuted, fontSize = 17.sp)
    }
}

@Composable
private fun CreatePostSection(onCreatePost: () -> Unit) {
    Column(
        Modifier
            .padding(horizontal = 22.dp)
            .fillMaxWidth()
            .border(1.dp, Color(0xFF303036), RoundedCornerShape(24.dp))
            .padding(12.dp),
    ) {
        Text("CREATE POST", color = FinalWhite, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(45.dp).clip(CircleShape).background(FinalRainbow).padding(2.dp)) {
                Image(
                    painter = painterResource(R.drawable.patsy_generated_main),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                )
            }
            Spacer(Modifier.width(10.dp))
            Box(
                Modifier.weight(1f).border(1.dp, Color(0xFF3A3A40), RoundedCornerShape(18.dp)).padding(horizontal = 14.dp, vertical = 11.dp),
            ) {
                Text("Share something with the community...", color = FinalMuted, fontSize = 12.sp)
            }
            Spacer(Modifier.width(8.dp))
            Text("☺", color = FinalWhite, fontSize = 25.sp)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CreateChip("▧", "Image", Modifier.weight(1f))
            CreateChip("●", "Video", Modifier.weight(1f))
            CreateChip("▤", "Document", Modifier.weight(1.1f))
            CreateChip("▦", "Template", Modifier.weight(1.1f))
            CreateChip("•••", "More", Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CreateChip("@", "Tag people")
            CreateChip("#", "Add hashtag")
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .background(FinalRainbow, RoundedCornerShape(16.dp))
                    .clickable(onClick = onCreatePost)
                    .padding(horizontal = 28.dp, vertical = 9.dp),
            ) {
                Text("POST", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun CreateChip(icon: String, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .border(1.dp, Color(0xFF45454D), RoundedCornerShape(14.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(icon, style = TextStyle(brush = FinalRainbow, fontSize = 13.sp, fontWeight = FontWeight.Black))
        Spacer(Modifier.width(5.dp))
        Text(text, color = FinalWhite, fontSize = 10.sp, maxLines = 1)
    }
}

@Composable
private fun FeedTabs() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 30.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("For You", color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Box(Modifier.width(42.dp).height(2.dp).background(FinalRainbow))
        }
        Text("Following", color = FinalMuted, fontSize = 12.sp)
        Text("Questions", color = FinalMuted, fontSize = 12.sp)
        Text("Latest", color = FinalMuted, fontSize = 12.sp)
        Text("≡", color = FinalMuted, fontSize = 20.sp)
    }
}

@Composable
private fun SocialFeedPreview() {
    Box(Modifier.padding(horizontal = 22.dp).fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(end = 105.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(Color(0xFF7A5A33)))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Sarah & Buddy", color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("2h ago · ◉", color = FinalMuted, fontSize = 9.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text("morning for a walk with my boy Buddy 🐾☀", color = FinalWhite, fontSize = 10.sp, maxLines = 1)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(120.dp)
                            .border(1.5.dp, FinalRainbow, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (index == 1) Color(0xFF28402C) else Color(0xFF37251A)),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.patsy_generated_main),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(35.dp).clip(CircleShape).background(Color(0xFF27363E)))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Pawsome Life", color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("5h ago · ◉", color = FinalMuted, fontSize = 9.sp)
                }
            }
            Spacer(Modifier.height(5.dp))
            Text("Show me the funniest thing your dog has ever done! 😂👍", color = FinalWhite, fontSize = 12.sp)
            Spacer(Modifier.height(7.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("♥ 42", color = Color(0xFFFF4C7D), fontSize = 12.sp)
                Text("◯ 32", color = FinalWhite, fontSize = 12.sp)
                Text("↗ 12", color = FinalWhite, fontSize = 12.sp)
                Text("▱", color = FinalWhite, fontSize = 12.sp)
            }
        }
        FinalPatsyActor(
            action = FinalPatsyAction.IDLE,
            modifier = Modifier.align(Alignment.BottomEnd).size(142.dp),
        )
    }
}
