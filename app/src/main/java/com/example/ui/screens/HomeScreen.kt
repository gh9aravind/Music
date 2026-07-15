package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Track
import com.example.ui.MusicViewModel
import com.example.ui.components.VibeAlbumArt
import com.example.ui.theme.SpotifyGreen
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: MusicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val trendingTracks by viewModel.trendingTracks.collectAsState()
    val hipHopTracks by viewModel.hipHopTracks.collectAsState()
    val melodyTracks by viewModel.melodyTracks.collectAsState()
    val activeTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 120.dp, top = 16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "Ready for some beautiful soundtracks?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        if (recentlyPlayed.isNotEmpty()) {
            item {
                HomeTrackRow(
                    title = "Recently Played",
                    tracks = recentlyPlayed,
                    onTrackClick = { track -> viewModel.selectAndPlayTrack(track, recentlyPlayed) }
                )
            }
        }

        if (trendingTracks.isNotEmpty()) {
            item {
                HomeTrackRow(
                    title = "🔥 Trending Now",
                    tracks = trendingTracks,
                    onTrackClick = { track -> viewModel.selectAndPlayTrack(track, trendingTracks) }
                )
            }
        }

        if (hipHopTracks.isNotEmpty()) {
            item {
                HomeTrackRow(
                    title = "🎤 Hip-Hop",
                    tracks = hipHopTracks,
                    onTrackClick = { track -> viewModel.selectAndPlayTrack(track, hipHopTracks) }
                )
            }
        }

        if (melodyTracks.isNotEmpty()) {
            item {
                HomeTrackRow(
                    title = "🎵 Melody",
                    tracks = melodyTracks,
                    onTrackClick = { track -> viewModel.selectAndPlayTrack(track, melodyTracks) }
                )
            }
        }

        if (trendingTracks.isEmpty() && hipHopTracks.isEmpty() && melodyTracks.isEmpty() && recentlyPlayed.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SpotifyGreen)
                }
            }
        }
    }
}

@Composable
fun HomeTrackRow(
    title: String,
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(tracks) { track ->
                RecentTrackCard(track) { onTrackClick(track) }
            }
        }
    }
}

@Composable
fun RecentTrackCard(
    track: Track,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(110.dp)
            .clickable { onClick() }
    ) {
        VibeAlbumArt(
            vibeCode = track.vibeCode,
            thumbnailUrl = track.thumbnailUrl,
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = track.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = track.artist,
            fontSize = 10.sp,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TrackListItem(
    track: Track,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDownload: () -> Unit,
    onAddToNext: () -> Unit = {},
    onAddToQueue: () -> Unit = {}
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            VibeAlbumArt(
                vibeCode = track.vibeCode,
                thumbnailUrl = track.thumbnailUrl,
                modifier = Modifier.size(52.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    text = track.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isCurrent) SpotifyGreen else Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = track.artist,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (isCurrent && isPlaying) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Playing",
                    tint = SpotifyGreen,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(horizontal = 4.dp)
                )
            }

            IconButton(
                onClick = { onFavoriteToggle() },
                modifier = Modifier.testTag("fav_${track.id}")
            ) {
                Icon(
                    imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite status",
                    tint = if (track.isFavorite) SpotifyGreen else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.testTag("more_${track.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    containerColor = Color(0xFF1E1E1E)
                ) {
                    DropdownMenuItem(
                        text = { Text("Play", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White) },
                        onClick = {
                            showMenu = false
                            onClick()
                        },
                        modifier = Modifier.testTag("menu_play_${track.id}")
                    )
                    DropdownMenuItem(
                        text = { Text("Add to next", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.PlaylistPlay, contentDescription = null, tint = Color.White) },
                        onClick = {
                            showMenu = false
                            onAddToNext()
                        },
                        modifier = Modifier.testTag("menu_add_next_${track.id}")
                    )
                    DropdownMenuItem(
                        text = { Text("Add to queue", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = Color.White) },
                        onClick = {
                            showMenu = false
                            onAddToQueue()
                        },
                        modifier = Modifier.testTag("menu_add_queue_${track.id}")
                    )
                    DropdownMenuItem(
                        text = { Text(if (track.isDownloaded) "Remove download" else "Download", color = Color.White) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                tint = if (track.isDownloaded) SpotifyGreen else Color.White
                            )
                        },
                        onClick = {
                            showMenu = false
                            onDownload()
                        },
                        modifier = Modifier.testTag("menu_download_${track.id}")
                    )
                    DropdownMenuItem(
                        text = { Text("Share", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = Color.White) },
                        onClick = {
                            showMenu = false
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${track.title} - ${track.artist}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share song"))
                        },
                        modifier = Modifier.testTag("menu_share_${track.id}")
                    )
                }
            }
        }
    }
}
