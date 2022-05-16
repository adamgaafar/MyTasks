package com.agaafar.jetdo2.ui.theme.todo_list

import com.agaafar.jetdo2.data.Todo


sealed class TodoListEvent{
    data class OnDeleteTodoClick(val todo: Todo):TodoListEvent()
    data class OnDoneChange(val todo:Todo,val isDone:Boolean):TodoListEvent()
    object OnUndoDeleteClick:TodoListEvent()
    data class OnTodoClick(val todo:Todo):TodoListEvent()
    data class onTitleChange(val title:String): TodoListEvent()
    data class onDescriptionChange(val description:String): TodoListEvent()
    data class getTodo(val todo:Todo): TodoListEvent()

    data class onIdChange(val id:Int): TodoListEvent()
    data class onTodoChange(val todo:Todo): TodoListEvent()

    object OnSaveTodoClick: TodoListEvent()
}
