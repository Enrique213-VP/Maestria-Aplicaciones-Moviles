package com.svape.masterunalapp.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svape.masterunalapp.R
import com.svape.masterunalapp.ui.theme.MasterUnalAppTheme
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MasterUnalAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashScreen {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = EaseInOut),
        label = "alpha"
    )

    val scaleAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutBack),
        label = "scale"
    )

    val slideAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 100f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 500, easing = EaseOutCubic),
        label = "slide"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alphaAnimation)
        ) {
            Card(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scaleAnimation)
                    .clip(CircleShape),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = "Logo de la aplicación UNAL",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "¡Bienvenido!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003366),
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = slideAnimation.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Universidad Nacional\nde Colombia",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4A90E2),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier
                    .offset(y = slideAnimation.dp)
                    .alpha(if (slideAnimation < 50f) 1f else 0f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Maestría en Ingeniería de Software\nDesarrollo de Aplicaciones Móviles",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF718096),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier
                    .offset(y = slideAnimation.dp)
                    .alpha(if (slideAnimation < 50f) 1f else 0f)
            )

            Spacer(modifier = Modifier.height(50.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = Color(0xFF003366),
                strokeWidth = 3.dp
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(alphaAnimation),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sede Bogotá",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF003366).copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Estudiante Sergio Vargas",
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF718096)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MasterUnalAppTheme {
        SplashScreen {}
    }
}