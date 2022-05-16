package com.agaafar.mytasks.ui.theme.todo_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agaafar.mytasks.data.Todo
import com.agaafar.mytasks.ui.theme.nabeti300

@Composable
fun TodoGridItem(
    todo: Todo,
    onEvent: (TodoListEvent) -> Unit,
    modifier: Modifier = Modifier,
    themeMode: Int
) {
    Card(
        modifier = modifier.fillMaxWidth()
            .clickable {
                onEvent(TodoListEvent.OnTodoClick(todo))
            },
        shape = RoundedCornerShape(15.dp),
        elevation = 5.dp

    ) {

        Box(
            modifier = Modifier
                .height(200.dp)
                .background(
                    if (themeMode == 0) {
                        nabeti300
                    } else {
                        Color(0xFF454343)
                    }
                )
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        modifier = Modifier.padding(top = 1.dp),
                        text = todo.title,
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (todo.isDone) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                    Spacer(modifier = modifier.width(115.dp))
                }
                todo.description?.let {
                    Text(
                        modifier = modifier.padding(start = 10.dp, bottom = 5.dp),
                        text = it,
                        color = Color.White,
                        textDecoration = if (todo.isDone) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )

                }
            }


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val Notchecked = !todo.isDone
                        onEvent(TodoListEvent.OnDoneChange(todo, Notchecked))

                    }) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            modifier = Modifier.size(33.dp),
                            contentDescription = "Check",
                            tint = if (todo.isDone) {
                                Color.Green
                            } else {
                                Color.White
                            }
                        )
                    }
                    IconButton(onClick = {
                        onEvent(TodoListEvent.OnDeleteTodoClick(todo))

                    }) {
                        Icon(
                            modifier = Modifier.size(33.dp),
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        }

    }

}

