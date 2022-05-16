package com.agaafar.mytasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    val title:String,
    val description:String?,
    val isDone:Boolean,
    @PrimaryKey var id:Int? = null
)
