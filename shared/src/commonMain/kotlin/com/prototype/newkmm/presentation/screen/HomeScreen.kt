package com.prototype.newkmm.presentation.screen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prototype.newkmm.presentation.JournyNavigationRoute
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun HomeScreen(
    navigator: Navigator
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(top = 16.dp, start = 16.dp, end = 10.dp), text = "Journy", fontSize = 18.sp
                )
                IconButton(modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 10.dp), onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Main menu")
                }
            }

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 32.dp),
                text = "Today's date",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(modifier = Modifier.padding(start = 16.dp), text = "Day")

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f).padding(top = 20.dp)
            ) {
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
                item { JournyEntryItemView() }
            }

            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .height(65.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        navigator.navigate(JournyNavigationRoute.RecordScreen.route)
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(170.dp)
                        .border(
                            shape = RoundedCornerShape(40.dp),
                            color = Color.Magenta,
                            width = 2.dp
                        )
                ) {

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(color = Color.Magenta, shape = CircleShape)
                                .clip(CircleShape)
                                .align(Alignment.CenterVertically)
                        ) {}

                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 12.dp), text = "RECORD"
                        )

                    }

                }
            }
        }

    }
}

@Composable
fun JournyEntryItemView() {
    Surface(modifier = Modifier.height(60.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp),
                text = "02:47 PM",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp), text = "Journy #00"
            )

            Row(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .padding(start = 50.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.Gray
                    )
                }
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 10.dp),
                    onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}
