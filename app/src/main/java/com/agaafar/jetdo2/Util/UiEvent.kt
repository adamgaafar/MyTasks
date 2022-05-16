package com.agaafar.jetdo2.Util

import com.agaafar.jetdo2.data.Todo

sealed class UiEvent{

    data class AddOrEditTodoById(val id:String):UiEvent()

    data class ShowSnackBar(
        val message:String,
        val action:String? = null
    ):UiEvent()

    data class getTodo(var id: Todo?):UiEvent()

    data class DeleteGridTodo(val todo: Todo): UiEvent()
    data class UndoDeleteGridTodo(val todo: Todo): UiEvent()
    data class AddTodo(val todo:Todo):UiEvent()
    data class OnDoneChange(val todo:Todo,val isDone:Boolean):UiEvent()

}
