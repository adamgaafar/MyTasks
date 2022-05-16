package com.agaafar.jetdo2.ui.theme.todo_list

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agaafar.jetdo2.Util.SearchWidgetState
import com.agaafar.jetdo2.Util.UiEvent
import com.agaafar.jetdo2.data.Todo
import com.agaafar.jetdo2.data.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    var todo by mutableStateOf<Todo?>(null)

    var title by mutableStateOf("")

    var id by mutableStateOf(0)

    var description by mutableStateOf("")

    val todos = repository.getTodos()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deletedTodo: Todo? = null

    fun onEvent(event: TodoListEvent) {
        //handling events
        when (event) {
            is TodoListEvent.OnTodoClick -> {
                sendUiEvent(UiEvent.AddOrEditTodoById("?todoId=${event.todo.id}"))
            }
            is TodoListEvent.getTodo -> {
                if (event.todo.id != -1) {
                    viewModelScope.launch {
                        event.todo.id?.let {
                            repository.getTodoById(it)?.let { todo ->
                                title = todo.title
                                description = todo.description ?: ""
                                this@TodoListViewModel.todo = todo
                            }
                        }
                    }
                } else if (event.todo.id == -1) {
                    id = -1
                }
            }

            is TodoListEvent.onTodoChange -> {
                todo = event.todo
                title = event.todo.title
                description = event.todo.description!!
            }

            is TodoListEvent.OnUndoDeleteClick -> {
                deletedTodo?.let { todo ->
                    sendUiEvent(UiEvent.UndoDeleteGridTodo(todo))
                    viewModelScope.launch(Dispatchers.IO) {
                        repository.insertTodo(todo)
                    }
                }
            }

            is TodoListEvent.OnDeleteTodoClick -> {
                sendUiEvent(UiEvent.DeleteGridTodo(event.todo))
                viewModelScope.launch(Dispatchers.IO) {
                    deletedTodo = event.todo
                    repository.deleteTodo(event.todo)
                    sendUiEvent(
                        UiEvent.ShowSnackBar(
                            message = "Todo deleted",
                            action = "Undo"
                        )
                    )
                }
            }

            is TodoListEvent.OnDoneChange -> {
                sendUiEvent(UiEvent.OnDoneChange(event.todo, event.isDone))
                viewModelScope.launch(Dispatchers.IO) {
                    repository.insertTodo(
                        event.todo.copy(
                            isDone = event.isDone
                        )
                    )
                }
            }


            is TodoListEvent.onTitleChange -> {
                title = event.title
            }
            is TodoListEvent.onIdChange -> {
                id = event.id
            }
            is TodoListEvent.onDescriptionChange -> {
                description = event.description
            }

            is TodoListEvent.OnSaveTodoClick -> {
                todo?.let {
                    Todo(
                        title, description,
                        it.isDone, todo?.id
                    )
                }?.let { UiEvent.AddTodo(it) }?.let { sendUiEvent(it) }

                viewModelScope.launch(Dispatchers.IO) {
                    repository.insertTodo(
                        Todo(
                            title = title,
                            description = description,
                            isDone = todo?.isDone ?: false,
                            id = todo?.id
                        )
                    )
                }
            }
        }
    }

    //for sending ui events to main screen
    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiEvent.send(event)
        }
    }

    //for searchWidget
    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)

    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> = mutableStateOf(value = "")

    val searchTextState: State<String> = _searchTextState

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }


}