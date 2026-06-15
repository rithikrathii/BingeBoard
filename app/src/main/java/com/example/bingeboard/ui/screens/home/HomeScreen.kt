package com.example.bingeboard.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bingeboard.R
import com.example.bingeboard.ui.components.GenreChip
import com.example.bingeboard.ui.components.MovieCard
import com.example.bingeboard.ui.theme.Background
import com.example.bingeboard.ui.theme.CardSurface
import com.example.bingeboard.ui.theme.GoldAccent
import com.example.bingeboard.ui.theme.SecondaryText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleFilterSheet() },
            containerColor = CardSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Filter Movies",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Year Range", color = SecondaryText, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = uiState.yearMin,
                        onValueChange = { viewModel.onYearMinChanged(it) },
                        placeholder = { Text("From", color = SecondaryText) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Background,
                            unfocusedContainerColor = Background,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = GoldAccent
                        ),
                        singleLine = true
                    )
                    TextField(
                        value = uiState.yearMax,
                        onValueChange = { viewModel.onYearMaxChanged(it) },
                        placeholder = { Text("To", color = SecondaryText) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Background,
                            unfocusedContainerColor = Background,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = GoldAccent
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Rating (e.g. PG-13, R, G)", color = SecondaryText, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = uiState.selectedRated,
                    onValueChange = { viewModel.onRatedChanged(it) },
                    placeholder = { Text("e.g. PG-13", color = SecondaryText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Background,
                        unfocusedContainerColor = Background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = GoldAccent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Language", color = SecondaryText, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = uiState.selectedLanguage,
                    onValueChange = { viewModel.onLanguageChanged(it) },
                    placeholder = { Text("e.g. English", color = SecondaryText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Background,
                        unfocusedContainerColor = Background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = GoldAccent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.clearFilters() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryText)
                    ) {
                        Text("Clear")
                    }
                    Button(
                        onClick = { viewModel.applyAdvancedFilter() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldAccent,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Apply", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(35.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.movie_frame),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "BingeBoard",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFilterSheet() }) {
                        Icon(
                            Icons.Rounded.FilterList,
                            contentDescription = "Filter",
                            tint = GoldAccent
                        )
                    }
                    if (uiState.currentUser != null) {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = uiState.currentUser?.fullName?.split(" ")
                                ?.mapNotNull { it.firstOrNull()?.toString() }
                                ?.take(2)
                                ?.joinToString("") ?: ""
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = Color.Black
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.genres) { genre ->
                    GenreChip(
                        genre = genre,
                        isSelected = uiState.selectedGenre == genre,
                        onClick = { viewModel.onGenreSelected(genre) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading && uiState.displayedMovies.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldAccent)
                }
            } else {
                val listState = rememberLazyListState()

                val shouldLoadMore = remember {
                    derivedStateOf {
                        val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                        val totalItems = listState.layoutInfo.totalItemsCount
                        lastVisibleItem != null && lastVisibleItem.index >= totalItems - 3
                    }
                }

                LaunchedEffect(shouldLoadMore.value) {
                    if (shouldLoadMore.value) {
                        viewModel.loadMore()
                    }
                }

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.displayedMovies, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie.id) }
                        )
                    }
                    if (uiState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = GoldAccent,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 46.dp)
            .clip(RoundedCornerShape(28.dp)),
        placeholder = {
            Text(
                "Search movies, genres...",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
        },
        leadingIcon = {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = SecondaryText)
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CardSurface,
            unfocusedContainerColor = CardSurface,
            disabledContainerColor = CardSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = GoldAccent
        ),
        singleLine = true
    )
}