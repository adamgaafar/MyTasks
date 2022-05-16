package com.agaafar.jetdo2.ui.theme.todo_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agaafar.jetdo2.data.Todo
import com.agaafar.jetdo2.ui.theme.nabeti300

@Composable
fun TodoItem(
    todo: Todo,
    onEvent: (TodoListEvent) -> Unit,
    modifier: Modifier = Modifier,
    themeMode: Int
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable {
                onEvent(TodoListEvent.OnTodoClick(todo))
            }
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (themeMode == 0) {
                    nabeti300
                }else{
                    Color(0xFF454343)
                }
            )
            .padding(5.dp),

        ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
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
                        modifier = modifier.padding(start = 5.dp, bottom = 2.dp),
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
