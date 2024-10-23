package com.droid.colorcodex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: ColorViewModel by viewModels { ColorViewModelFactory(application) }

            ColorCodeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        AddColorButton { viewModel.addColor() }
                    },
                    topBar = {

                        TopBar(
                            pendingSyncCount = viewModel.colorList.collectAsStateWithLifecycle().value.count { !it.isSynced },
                            onSyncClick = { viewModel.syncColors() }
                        )
                    }
                ) { innerPadding ->
                    ColorListScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ColorCodeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(pendingSyncCount: Int, onSyncClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Color App", color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF545B9F)
        ),
        actions = {
            Box(modifier = Modifier.padding(end = 16.dp)) {
                SyncButton(onClick = onSyncClick, pendingSyncCount = pendingSyncCount)
            }
        }
    )
}

@Composable
fun ColorListScreen(viewModel: ColorViewModel, modifier: Modifier = Modifier) {
    val colorList by viewModel.colorList.collectAsStateWithLifecycle(initialValue = emptyList())

    LazyVerticalGrid(
        modifier = modifier.padding(16.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(colorList) { color ->
            ColorItem(colorData = color)
        }
    }
}

@Composable
fun ColorItem(colorData: ColorData) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(colorData.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(android.graphics.Color.parseColor(colorData.colorCode)))
            .padding(8.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {

            Text(
                text = colorData.colorCode,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )


            Divider(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(1.dp),
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Created at  $formattedDate",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun AddColorButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFFBDB7FF),
        contentColor = Color(0xFF5E6DB1),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(text = "Add Color", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))


            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color = Color(0xFF5E6DB1)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}


@Composable
fun SyncButton(onClick: () -> Unit, pendingSyncCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Circular Counter with a larger size
        Box(
            modifier = Modifier
                .height(30.dp)
                .width(70.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFD4D4FF)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "  $pendingSyncCount",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Sync Icon
                IconButton(onClick = onClick) {
                    Icon(Icons.Default.Sync, contentDescription = "Sync", tint = Color(0xFF5E6DB1)) // Matching bluish tint
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ColorCodeTheme {
        ColorItem(
            colorData = ColorData(
                colorCode = "#FFAABB",
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
