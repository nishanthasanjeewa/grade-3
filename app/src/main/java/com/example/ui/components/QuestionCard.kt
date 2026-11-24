package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.BuddhismQuestions
import com.example.model.QuizQuestion
import com.example.ui.theme.LocalQuizColors
import com.example.ui.theme.OutfitFontFamily

@Composable
fun QuestionCard(
    question: QuizQuestion,
    selectedIndex: Int?,
    onSelectOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val quizColors = LocalQuizColors.current
    val isLocked = selectedIndex != null
    val isCorrect = selectedIndex == question.correctIndex

    val cardBorderColor by animateColorAsState(
        targetValue = when {
            !isLocked -> quizColors.cardBorder
            isCorrect -> quizColors.onCorrectContainer.copy(alpha = 0.55f)
            else -> quizColors.onWrongContainer.copy(alpha = 0.55f)
        },
        label = "card_border"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("question_card_${question.index}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${question.index + 1}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (isLocked) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "පිළිතුර අගුළු දමා ඇත",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier
                            .padding(start = 8.dp, top = 2.dp)
                            .size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val useTwoColumns = maxWidth >= 540.dp
                if (useTwoColumns) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (rowIndex in 0 until question.options.size step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OptionItem(
                                    questionIndex = question.index,
                                    optionIndex = rowIndex,
                                    optionText = question.options[rowIndex],
                                    selectedIndex = selectedIndex,
                                    correctIndex = question.correctIndex,
                                    isLocked = isLocked,
                                    onSelect = { onSelectOption(rowIndex) },
                                    modifier = Modifier.weight(1f)
                                )
                                if (rowIndex + 1 < question.options.size) {
                                    OptionItem(
                                        questionIndex = question.index,
                                        optionIndex = rowIndex + 1,
                                        optionText = question.options[rowIndex + 1],
                                        selectedIndex = selectedIndex,
                                        correctIndex = question.correctIndex,
                                        isLocked = isLocked,
                                        onSelect = { onSelectOption(rowIndex + 1) },
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        question.options.forEachIndexed { optIdx, optText ->
                            OptionItem(
                                questionIndex = question.index,
                                optionIndex = optIdx,
                                optionText = optText,
                                selectedIndex = selectedIndex,
                                correctIndex = question.correctIndex,
                                isLocked = isLocked,
                                onSelect = { onSelectOption(optIdx) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isLocked,
                enter = fadeIn() + expandVertically()
            ) {
                val correctLetter = BuddhismQuestions.optionLetters.getOrElse(question.correctIndex) { "" }
                val correctText = question.options.getOrElse(question.correctIndex) { "" }
                val feedbackText = if (isCorrect) {
                    "නිවැරදියි ✓"
                } else {
                    "වැරදියි ✗ — නිවැරදි පිළිතුර: $correctLetter. $correctText"
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .testTag("feedback_${question.index}"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isCorrect) quizColors.correctContainer else quizColors.wrongContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                            contentDescription = null,
                            tint = if (isCorrect) quizColors.onCorrectContainer else quizColors.onWrongContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = feedbackText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isCorrect) quizColors.onCorrectContainer else quizColors.onWrongContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionItem(
    questionIndex: Int,
    optionIndex: Int,
    optionText: String,
    selectedIndex: Int?,
    correctIndex: Int,
    isLocked: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizColors = LocalQuizColors.current
    val letter = BuddhismQuestions.optionLetters.getOrElse(optionIndex) { "" }

    val isSelected = selectedIndex == optionIndex
    val isCorrectOption = isLocked && optionIndex == correctIndex
    val isWrongSelection = isLocked && isSelected && optionIndex != correctIndex

    val containerColor by animateColorAsState(
        targetValue = when {
            isCorrectOption -> quizColors.correctContainer
            isWrongSelection -> quizColors.wrongContainer
            else -> quizColors.optionSurface
        },
        label = "option_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            isCorrectOption -> quizColors.onCorrectContainer
            isWrongSelection -> quizColors.onWrongContainer
            else -> quizColors.cardBorder
        },
        label = "option_border"
    )
    val textColor = when {
        isCorrectOption -> quizColors.onCorrectContainer
        isWrongSelection -> quizColors.onWrongContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    val shape = RoundedCornerShape(12.dp)
    Surface(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(shape)
            .clickable(
                enabled = !isLocked,
                role = Role.RadioButton,
                onClick = onSelect
            )
            .testTag("option_${questionIndex}_${optionIndex}"),
        shape = shape,
        color = containerColor,
        border = BorderStroke(if (isCorrectOption || isWrongSelection) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OptionTickBox(
                isSelected = isSelected,
                isCorrectOption = isCorrectOption,
                isWrongSelection = isWrongSelection
            )
            Text(
                text = "$letter. $optionText",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isCorrectOption || isWrongSelection) FontWeight.Bold else FontWeight.Normal
                ),
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OptionTickBox(
    isSelected: Boolean,
    isCorrectOption: Boolean,
    isWrongSelection: Boolean
) {
    val quizColors = LocalQuizColors.current
    val boxColor = when {
        isCorrectOption -> quizColors.onCorrectContainer
        isWrongSelection -> quizColors.onWrongContainer
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    val boxBorder = when {
        isCorrectOption -> quizColors.onCorrectContainer
        isWrongSelection -> quizColors.onWrongContainer
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
    }

    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(boxColor)
            .border(width = 1.5.dp, color = boxBorder, shape = RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected || isCorrectOption) {
            Icon(
                imageVector = if (isWrongSelection) Icons.Rounded.Close else Icons.Rounded.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}
