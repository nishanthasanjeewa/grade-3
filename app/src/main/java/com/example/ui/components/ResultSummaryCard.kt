package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.FilterListOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.EvaluationLevel
import com.example.model.QuizUiState
import com.example.ui.theme.LocalQuizColors
import com.example.ui.theme.OutfitFontFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultSummaryCard(
    uiState: QuizUiState,
    onToggleWrongFilter: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizColors = LocalQuizColors.current
    val isCompleted = uiState.isCompleted

    val borderColor = if (isCompleted) {
        quizColors.onCorrectContainer
    } else {
        MaterialTheme.colorScheme.secondary
    }
    val containerColor = if (isCompleted) {
        quizColors.completedSummaryBg
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("result_summary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = isCompleted) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.img_result_lotus),
                        contentDescription = "ප්රතිඵල උපහාර ලාංඡනය",
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Text(
                text = "අවසාන ප්රතිඵල පත්රිකාව (Result Summary)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val fourColumns = maxWidth >= 540.dp
                if (fourColumns) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatTile(
                            value = "${uiState.answeredCount} / ${uiState.totalCount}",
                            label = "පිළිතුරු සැපයූ ගණන",
                            valueColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            value = "${uiState.correctCount}",
                            label = "නිවැරදි පිළිතුරු",
                            valueColor = quizColors.onCorrectContainer,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            value = "${uiState.wrongCount}",
                            label = "වැරදි පිළිතුරු",
                            valueColor = quizColors.onWrongContainer,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            value = "${uiState.percentage}%",
                            label = "ලකුණු ප්රතිශතය",
                            valueColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatTile(
                                value = "${uiState.answeredCount} / ${uiState.totalCount}",
                                label = "පිළිතුරු සැපයූ ගණන",
                                valueColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                value = "${uiState.correctCount}",
                                label = "නිවැරදි පිළිතුරු",
                                valueColor = quizColors.onCorrectContainer,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatTile(
                                value = "${uiState.wrongCount}",
                                label = "වැරදි පිළිතුරු",
                                valueColor = quizColors.onWrongContainer,
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                value = "${uiState.percentage}%",
                                label = "ලකුණු ප්රතිශතය",
                                valueColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val evaluationMessage = when (uiState.evaluationLevel) {
                EvaluationLevel.NOT_STARTED ->
                    "ප්රශ්න සඳහා පිළිතුරු සැපයීම ආරම්භ කරන්න!"
                EvaluationLevel.IN_PROGRESS ->
                    "තව ප්රශ්න ${uiState.totalCount - uiState.answeredCount} ක් ඉතිරිව ඇත. දිගටම උත්සාහ කරන්න!"
                EvaluationLevel.EXCELLENT ->
                    "විශිෂ්ටයි! ඔබ බුද්ධ ධර්මය පිළිබඳව ඉතා හොඳ දැනුමක් පෙන්වා ඇත. 🌟"
                EvaluationLevel.VERY_GOOD ->
                    "ඉතා හොඳයි! තවදුරටත් වැරදුණු ප්රශ්න පරීක්ෂා කර බලන්න. 👍"
                EvaluationLevel.GOOD_EFFORT ->
                    "හොඳ උත්සාහයක්! වැරදුණු ප්රශ්න නැවත පාඩම් කරන්න. 📖"
                EvaluationLevel.KEEP_PRACTICING ->
                    "තවත් උත්සාහ කරන්න! වැරදුණු ප්රශ්න බලා නැවත පුහුණු වන්න. 💪"
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("evaluation_badge"),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = evaluationMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onToggleWrongFilter,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("filter_wrong_button"),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = if (uiState.showWrongOnly) Icons.Rounded.FilterListOff else Icons.Rounded.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = if (uiState.showWrongOnly) {
                            "සියලුම ප්රශ්න පෙන්වන්න"
                        } else {
                            "වැරදි වූ ප්රශ්න පමණක් පෙන්වන්න"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Button(
                    onClick = onResetClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("summary_reset_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "නැවත මුල සිට ආරම්භ කරන්න (Reset)",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    val quizColors = LocalQuizColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, quizColors.cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
