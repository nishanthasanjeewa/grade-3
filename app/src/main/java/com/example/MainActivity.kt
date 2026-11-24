package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.GradeCoverScreen
import com.example.ui.QuizScreen
import com.example.ui.SubjectsMenuScreen
import com.example.ui.theme.BuddhismQuizTheme
import com.example.viewmodel.QuizViewModel

private object AppRoutes {
    const val GRADE_COVER = "grade_cover"
    const val SUBJECTS_MENU = "subjects_menu"
    const val BUDDHISM_QUIZ = "buddhism_quiz"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuddhismQuizTheme {
                val navController = rememberNavController()
                val quizViewModel: QuizViewModel = viewModel()
                val quizUiState by quizViewModel.uiState.collectAsStateWithLifecycle()

                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.GRADE_COVER,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(260)
                        ) + fadeIn(animationSpec = tween(260))
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(260)
                        ) + fadeOut(animationSpec = tween(200))
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(260)
                        ) + fadeIn(animationSpec = tween(260))
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(260)
                        ) + fadeOut(animationSpec = tween(200))
                    }
                ) {
                    composable(route = AppRoutes.GRADE_COVER) {
                        GradeCoverScreen(
                            onContinueClick = {
                                navController.navigate(AppRoutes.SUBJECTS_MENU) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(route = AppRoutes.SUBJECTS_MENU) {
                        SubjectsMenuScreen(
                            quizUiState = quizUiState,
                            onBuddhismClick = {
                                navController.navigate(AppRoutes.BUDDHISM_QUIZ) {
                                    launchSingleTop = true
                                }
                            },
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(route = AppRoutes.BUDDHISM_QUIZ) {
                        QuizScreen(
                            viewModel = quizViewModel,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
