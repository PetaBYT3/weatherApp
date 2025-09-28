package com.weatherapp.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfilePageCore(

) {
    ProfilePage()
}
@Composable
private fun ProfilePage(

) {
    Column(
        modifier = Modifier
            .fillMaxSize().padding(horizontal = 10.dp),
    ) {
        Card (
            modifier = Modifier.padding(15.dp)
        ) {
            Column(
                modifier = Modifier
            ) {
                var isExpand by rememberSaveable {
                    mutableStateOf(false)
                }
                var btnTitle by rememberSaveable {
                    mutableStateOf("Expand")
                }
                Row {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Title"
                    )
                    Button(
                        onClick = {
                            if (isExpand) {
                                isExpand = false
                                btnTitle = "Collapse"
                            } else {
                                isExpand = true
                                btnTitle = "Expand"
                            }
                        },
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(text = btnTitle)
                    }
                }
                AnimatedVisibility(visible = isExpand) {
                    Text(
                        modifier = Modifier.height(100.dp),
                        text = "Lorem Ipsum Dolor Sit Amet"
                    )
                }
            }
        }
    }
}