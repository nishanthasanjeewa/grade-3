package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.FilterListOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.QuestionCard
import com.example.ui.components.QuizHeader
import com.example.ui.components.ResultSummaryCard
import com.example.ui.theme.LocalQuizColors
import com.example.viewmodel.QuizViewModel
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showResetDialog by remember { mutableStateOf(false) }

    val handleResetRequest: () -> Unit = {
        if (uiState.answeredCount > 0) {
            showResetDialog = true
        } else {
            viewModel.resetQuiz()
            coroutineScope.launch { listState.animateScrollToItem(0) }
        }
    }

    val handleToggleWrongFilter: () -> Unit = {
        val turningOn = !uiState.showWrongOnly
        viewModel.toggleWrongOnlyFilter()
        if (turningOn) {
            coroutineScope.launch { listState.animateScrollToItem(0) }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            QuizHeader(
                answeredCount = uiState.answeredCount,
                correctCount = uiState.correctCount,
                totalCount = uiState.totalCount,
                onBackClick = onBackClick,
                onScrollToSummary = {
                    coroutineScope.launch {
                        val lastIndex = (listState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
                        listState.animateScrollToItem(lastIndex)
                    }
                },
                onResetClick = handleResetRequest
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 850.dp)
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "hero_banner") {
                    HeroBannerCard(
                        showWrongOnly = uiState.showWrongOnly,
                        wrongCount = uiState.wrongCount,
                        answeredCount = uiState.answeredCount,
                        onToggleWrongFilter = handleToggleWrongFilter
                    )
                }

                if (uiState.showWrongOnly && uiState.visibleQuestions.isEmpty()) {
                    item(key = "empty_wrong_filter") {
                        EmptyWrongFilterCard(
                            onShowAll = { viewModel.showAllQuestions() }
                        )
                    }
                } else {
                    items(
                        items = uiState.visibleQuestions,
                        key = { it.index }
                    ) { question ->
                        QuestionCard(
                            question = question,
                            selectedIndex = uiState.userAnswers[question.index],
                            onSelectOption = { optionIdx ->
                                viewModel.selectAnswer(question.index, optionIdx)
                            }
                        )
                    }
                }

                item(key = "result_summary") {
                    Spacer(modifier = Modifier.height(4.dp))
                    ResultSummaryCard(
                        uiState = uiState,
                        onToggleWrongFilter = handleToggleWrongFilter,
                        onResetClick = handleResetRequest
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.reset_dialog_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.reset_dialog_body),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetQuiz()
                        coroutineScope.launch { listState.animateScrollToItem(0) }
                    },
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text(stringResource(R.string.reset_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false },
                    modifier = Modifier.testTag("cancel_reset_button")
                ) {
                    Text(stringResource(R.string.reset_cancel))
                }
            }
        )
    }
}

@Composable
private fun HeroBannerCard(
    showWrongOnly: Boolean,
    wrongCount: Int,
    answeredCount: Int,
    onToggleWrongFilter: () -> Unit
) {
    val quizColors = LocalQuizColors.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, quizColors.cardBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_temple),
                    contentDescription = "බුද්ධ ධර්මය ඉගෙනුම් චිත්‍රය",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xB82C1600)
                                )
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFFFE082),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "සාදු සිතෙන් ඉගෙන ගනිමු • ප්රශ්න 50 ක්",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showWrongOnly) {
                        "වැරදි වූ ප්රශ්න ($wrongCount)"
                    } else {
                        "සියලුම ප්රශ්න (50) • පිළිතුරු $answeredCount"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FilterChip(
                    selected = showWrongOnly,
                    onClick = onToggleWrongFilter,
                    modifier = Modifier.testTag("top_wrong_filter_chip"),
                    label = {
                        Text(
                            text = if (showWrongOnly) "සියල්ල පෙන්වන්න" else "වැරදි පමණක් ($wrongCount)",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (showWrongOnly) Icons.Rounded.FilterListOff else Icons.Rounded.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
private fun EmptyWrongFilterCard(
    onShowAll: () -> Unit
) {
    val quizColors = LocalQuizColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("empty_wrong_filter_card"),
        shape = RoundedCornerShape(16.dp),
        color = quizColors.correctContainer,
        border = BorderStroke(1.dp, quizColors.onCorrectContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = quizColors.onCorrectContainer,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "දැනට වැරදි වූ ප්රශ්න කිසිවක් නොමැත!",
                style = MaterialTheme.typography.titleMedium,
                color = quizColors.onCorrectContainer,
                textAlign = TextAlign.Center
            )
            TextButton(onClick = onShowAll) {
                Text(
                    text = "සියලුම ප්රශ්න පෙන්වන්න",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
