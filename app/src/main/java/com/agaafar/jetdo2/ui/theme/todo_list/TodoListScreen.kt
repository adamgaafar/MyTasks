package com.agaafar.jetdo2.ui.theme.todo_list


import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agaafar.jetdo2.R
import com.agaafar.jetdo2.Util.SearchWidgetState
import com.agaafar.jetdo2.Util.UiEvent
import com.agaafar.jetdo2.data.Todo
import com.agaafar.jetdo2.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.round

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val todos = viewModel.todos.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val searchWidgetState by viewModel.searchWidgetState
    val searchTextState by viewModel.searchTextState

    val emptytodo = Todo("", "", false)

    var isSearchBarVisible by remember {
        mutableStateOf(false)
    }

    val updateSearchBarVisibility: (Boolean) -> Unit = {
        isSearchBarVisible = it
    }

    //for switching between gridview and listview
    var GridOrList by remember {
        mutableStateOf(false)
    }
    //for updating GridOrList state
    val updateGridOrList: (Boolean) -> Unit = {
        GridOrList = it
    }

    val SelectedNav = remember {
            mutableStateOf("")
    }
    var ThemeModeSelected by remember {
        mutableStateOf(0)
    }
    ThemeModeSelected = if (isSystemInDarkTheme()) {
        //dark
        1
    } else {
        //light
        0
    }

    var searchedList by remember {
        mutableStateOf(listOf(Todo("", "", false)))
    }

    val updatesearchedList: (List<Todo>) -> Unit = {
        searchedList = it
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action,
                        duration = SnackbarDuration.Short
                    ).let {
                        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                        if (it == SnackbarResult.ActionPerformed) {
                            viewModel.onEvent(TodoListEvent.OnUndoDeleteClick)
                        }
                    }

                }
                is UiEvent.getTodo -> {
                    viewModel.todo = viewModel.todo
                }

                is UiEvent.DeleteGridTodo -> {

                    val list = searchedList.toMutableList()
                    list.remove(event.todo)
                    updatesearchedList(list)

                }

                is UiEvent.UndoDeleteGridTodo -> {

                    val list = searchedList.toMutableList()
                    list.add(event.todo)
                    updatesearchedList(list)

                }

                is UiEvent.OnDoneChange -> {
                    val list = searchedList.toMutableList()
                    list.remove(event.todo)
                    list.add(event.todo.copy(isDone = event.isDone))
                    updatesearchedList(list)
                }

                is UiEvent.AddTodo -> {
                    val list = searchedList.toMutableList()
                    if (list.contains(event.todo)) {
                        list.remove(event.todo)
                        updatesearchedList(list)
                    } else {
                        list.remove(event.todo)
                        list.add(event.todo)
                        updatesearchedList(list)
                    }
                    updatesearchedList(list)

                }

                else -> Unit
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scrollState = rememberScrollState()
    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 15.dp),
        sheetState = sheetState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .defaultMinSize(minHeight = 50.dp)
                    .fillMaxHeight(0.5f)
                    .padding(15.dp)

            ) {


                /* sheet content */
                Column(
                    modifier = Modifier
                        .scrollable(
                            state = scrollState,
                            orientation = Orientation.Vertical
                        )
                        .verticalScroll(rememberScrollState())
                ) {
                    if (viewModel.id == -1) {
                        viewModel.id = -1
                        //creating new todo
                        Text(
                            text = "Title:",
                            modifier = Modifier.padding(start = 15.dp, bottom = 2.dp)
                        )
                        OutlinedTextField(
                            value = viewModel.title,
                            onValueChange = {
                                viewModel.onEvent(TodoListEvent.onTitleChange(it))
                            },
                            placeholder = {
                                Text(text = "Title")
                            },
                            modifier = Modifier.padding(10.dp, bottom = 12.dp)
                        )
                        Text(
                            text = "Description:",
                            modifier = Modifier.padding(start = 15.dp, bottom = 2.dp)
                        )
                        OutlinedTextField(
                            value = viewModel.description,
                            onValueChange = {
                                viewModel.onEvent(TodoListEvent.onDescriptionChange(it))
                            },
                            placeholder = {
                                Text(text = "Description")
                            },
                            modifier = Modifier.padding(10.dp, bottom = 12.dp)
                        )


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    if (viewModel.title.isBlank()) {
                                        scope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                "Title is empty pls enter anything",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    } else {
                                        viewModel.onEvent(TodoListEvent.OnSaveTodoClick)
                                    }
                                    viewModel.id = -1
                                    scope.launch {
                                        sheetState.hide()
                                    }
                                },
                                Modifier
                                    .size(349.dp, 57.dp)
                                    .graphicsLayer {
                                        round(50f)
                                    }
                                    .background(
                                        if (ThemeModeSelected == 0) {
                                            lightPink
                                        } else {
                                            nabeti300
                                        }
                                    ),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (ThemeModeSelected == 0) {
                                        lightPink
                                    } else {
                                        nabeti300
                                    }
                                )

                            ) {
                                Text(text = "Save", fontSize = 18.sp)
                            }
                        }

                    } else if (viewModel.id != -1) {
                        // edit here
                        //updating todo

                        Text(
                            text = "Title:",
                            modifier = Modifier.padding(start = 15.dp, bottom = 2.dp)
                        )
                        OutlinedTextField(
                            value = viewModel.title,
                            onValueChange = {
                                viewModel.onEvent(TodoListEvent.onTitleChange(it))
                            },

                            modifier = Modifier.padding(10.dp, bottom = 12.dp)
                        )
                        Text(
                            text = "Description:",
                            modifier = Modifier.padding(start = 15.dp, bottom = 2.dp)
                        )
                        OutlinedTextField(
                            value = viewModel.description,
                            onValueChange = {
                                viewModel.onEvent(TodoListEvent.onDescriptionChange(it))
                            },

                            modifier = Modifier.padding(10.dp, bottom = 12.dp)
                        )


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    if (viewModel.title.isBlank()) {
                                        scope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                "Title is empty pls enter anything",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    } else {
                                        val newlist = searchedList.toMutableList()
                                        newlist.remove(viewModel.todo)
                                        updatesearchedList(newlist)
                                        viewModel.onEvent(TodoListEvent.OnSaveTodoClick)
                                    }

                                    viewModel.id = -1
                                    scope.launch {
                                        sheetState.hide()
                                    }
                                },
                                Modifier
                                    .size(349.dp, 57.dp)
                                    .graphicsLayer {
                                        round(50f)
                                    }
                                    .background(
                                        if (ThemeModeSelected == 0) {
                                            lightPink
                                        } else {
                                            nabeti300
                                        }
                                    ),
                                colors = if (ThemeModeSelected == 0) {
                                    ButtonDefaults.buttonColors(backgroundColor = lightPink)
                                } else {
                                    ButtonDefaults.buttonColors(backgroundColor = nabeti300)
                                }

                            ) {
                                Text(text = "Save", fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                handleAppBars(
                    searchWidgetState = searchWidgetState,
                    searchTextState = searchTextState,
                    onTextChange = {
                        viewModel.updateSearchTextState(newValue = it)

                    },
                    onCloseClicked = {
                        viewModel.updateSearchTextState(newValue = "")
                        viewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
                        isSearchBarVisible = false
                    },
                    onSearchClicked = {
                        val filtered = filter(todos.value, it)
                        if (filtered.isEmpty()) {
                            //when there is nothing found in the search
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    "Nothing Found",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            val newlist = emptyList<Todo>()
                            updatesearchedList(newlist)

                        } else {
                            searchedList = filtered
                        }

                    },
                    onSearchTriggered = {
                        viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                        isSearchBarVisible = true
                    },
                    isSearchBarVisible = isSearchBarVisible,
                    updateSearchBarVisibility = updateSearchBarVisibility,
                    ThemeModeSelected = ThemeModeSelected,
                    GridOrListController = GridOrList,
                    updateGridOrList = updateGridOrList
                )
            },
            scaffoldState = scaffoldState,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        viewModel.todo = emptytodo
                        viewModel.onEvent(TodoListEvent.onTodoChange(emptytodo))
                        viewModel.onEvent(TodoListEvent.onIdChange(-1))
                        scope.launch { sheetState.show() }
                    },
                    shape = RoundedCornerShape(50),
                    backgroundColor =
                    if (ThemeModeSelected == 0) {
                        Pink200
                    } else {
                        Color(0xFFBF0000)
                    },
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Todo"
                    )
                }
            },
            isFloatingActionButtonDocked = true,
            floatingActionButtonPosition = FabPosition.Center,
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth(2f),
                    backgroundColor = if (ThemeModeSelected == 0) {
                        Color.White
                    } else {
                        Color(0xFF1C1C1C)
                    },
                    cutoutShape = RoundedCornerShape(50),
                    elevation = 20.dp,
                    content = {
                        BottomNavigationItem(
                            selected = SelectedNav.value == "home",
                            onClick = {

                                SelectedNav.value = "home"
                                viewModel.updateSearchTextState(newValue = "")
                                viewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
                                isSearchBarVisible = false
                            },
                            icon = {
                                Icon(
                                    Icons.Filled.Home,
                                    contentDescription = "home",
                                    tint = if (ThemeModeSelected == 0) {
                                        Color.Black
                                    } else {
                                        Color.White
                                    }
                                )
                            },
                            label = {
                                Text(
                                    text = "Home", color = if (ThemeModeSelected == 0) {
                                        Color.Black
                                    } else {
                                        Color.White
                                    }
                                )
                            },
                            alwaysShowLabel = true
                        )
                        BottomNavigationItem(
                            selected = SelectedNav.value == "Night Mode",
                            onClick = {
                                SelectedNav.value = "Night Mode"
                                // 0 = day mode || 1 = night mode
                                ThemeModeSelected = if (ThemeModeSelected == 0) {
                                    //theme = Day then reverse it to night
                                    1
                                } else {
                                    //theme = Night then reverse it to Day
                                    0
                                }

                            },
                            icon = {
                                Icon(
                                    if (ThemeModeSelected == 0) {
                                        painterResource(id = R.drawable.ic_baseline_wb_sunny_24)
                                    } else {
                                        painterResource(id = R.drawable.ic_baseline_nightlight_24)
                                    }, contentDescription = "Night",
                                    tint = if (ThemeModeSelected == 0) {
                                        Color.Black
                                    } else {
                                        Color.White
                                    }
                                )
                            },
                            label = {
                                Text(
                                    text = if (ThemeModeSelected == 0) {
                                        "Night Mode"
                                    } else {
                                        "Day Mode"
                                    }, color = if (ThemeModeSelected == 0) {
                                        Color.Gray
                                    } else {
                                        Color.White
                                    }
                                )
                            },
                            alwaysShowLabel = true
                        )
                    }
                )
            },
            backgroundColor = if (ThemeModeSelected == 0) {
                Color.White
            } else {
                Color(0xFF0E0D0E)
            }
        ) {

            Column {

                if (!isSearchBarVisible) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, top = 15.dp, bottom = 25.dp)
                    ) {
                        // Text(text = "Whats on your mind?", fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = lightPink)
                        Text(
                            text = "Whats on your mind!",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            fontStyle = FontStyle.Italic,
                            color = if (ThemeModeSelected == 0) {
                                textdarkred
                            } else {
                                Color(0xFFFBFBFB)
                            }
                        )
                    }
                } else {
                    //SearchBarWidget()
                    //onSearchWidgetClicked()
                }
                handleTodoView(
                    if (!isSearchBarVisible) {
                        todos.value
                    } else {
                        searchedList.filterNotNull().toList()
                    },
                    GridOrList,
                    ThemeModeSelected,
                    isSearchBarVisible,
                    viewModel,
                    sheetState,
                    scope
                )

            }

        }
    }
}

private fun filter(todos: List<Todo>, searchName: String): List<Todo> {
    val newList = mutableListOf<Todo>()
    todos.forEachIndexed { index, todo ->
        if (todo.title.lowercase(Locale.ROOT).trim().contains(
                searchName.lowercase(Locale.ROOT)
                    .trim()
            )
        ) {
            newList.add(todo)
        }
    }
    return newList.toList()
}


@Composable
fun SearchAppBarWidget(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    Row(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .border(2.dp, color = Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .height(60.dp)
                .graphicsLayer {
                    shadowElevation = 12.dp.toPx()
                    shape = RoundedCornerShape(8.dp)
                    clip = true
                }
                .background(color = Color.Transparent)

        ) {
            TextField(
                modifier = Modifier
                    .fillMaxSize(),
                value = text,
                onValueChange = {
                    onTextChange(it)
                },
                placeholder = {
                    Text(
                        modifier = Modifier.alpha(ContentAlpha.medium),
                        text = "Search",
                        color = Color.Black
                    )
                },
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.subtitle1.fontSize
                ),
                singleLine = true,
                leadingIcon = {
                    IconButton(
                        modifier = Modifier.alpha(ContentAlpha.medium),
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Black
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (text.isNotEmpty()) {
                                onTextChange("")
                            } else {
                                onCloseClicked()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_group_21),
                            contentDescription = "Close Icon",
                            tint = Color.Gray
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchClicked(text)
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    cursorColor = Color.Black.copy(alpha = ContentAlpha.high)
                )

            )
        }
    }
}


@Composable
fun handleAppBars(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    isSearchBarVisible: Boolean,
    updateSearchBarVisibility: (Boolean) -> Unit,
    ThemeModeSelected: Int,
    GridOrListController: Boolean,
    updateGridOrList: (Boolean) -> Unit
) {
    when (searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            TopBar(
                onSearchClicked = onSearchTriggered,
                isSearchBarVisible = isSearchBarVisible,
                updateSearchBarVisibility = updateSearchBarVisibility,
                ThemeModeSelected,
                GridOrListController,
                updateGridOrList
            )
        }
        SearchWidgetState.OPENED -> {
            SearchAppBarWidget(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun handleTodoView(
    todos: List<Todo>,
    GridOrListController: Boolean,
    ThemeModeSelected: Int,
    isSearchBarVisible: Boolean,
    viewModel: TodoListViewModel,
    sheetState: ModalBottomSheetState,
    scope: CoroutineScope
) {
    //either grids or list
    //true = Grid
    //false = List
    if (!GridOrListController) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            items(todos) { todo ->
                if (todo.title.isBlank() && isSearchBarVisible) {

                } else {
                    TodoItem(
                        todo = todo,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.id = todo.id!!
                                viewModel.todo = todo
                                viewModel.onEvent(TodoListEvent.getTodo(todo))

                                scope.launch { sheetState.show() }
                            }
                            .padding(16.dp),
                        ThemeModeSelected
                    )
                }

            }
        }
    } else {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth(),
            cells = GridCells.Adaptive(minSize = 178.dp)
        ) {
            items(todos) { todo ->
                if (todo.title.isBlank() && isSearchBarVisible) {

                } else {
                    TodoGridItem(
                        todo = todo,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .clickable {
                                viewModel.id = todo.id!!
                                viewModel.todo = todo
                                viewModel.onEvent(TodoListEvent.getTodo(todo))
                                scope.launch { sheetState.show() }
                            }
                            .padding(16.dp),
                        ThemeModeSelected
                    )
                }

            }
        }
    }


}

@Composable
fun TopBar(
    onSearchClicked: () -> Unit,
    isSearchBarVisible: Boolean,
    updateSearchBarVisibility: (Boolean) -> Unit,
    themeMode: Int,
    GridOrListController: Boolean,
    updateGridOrList: (Boolean) -> Unit
) {

    TopAppBar(
        modifier = Modifier.padding(top = 9.dp, bottom = 15.dp),
        //Provide Title
        title = {

            Text(
                text = "My Tasks",
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 15.dp, bottom = 5.dp),
                color = if (themeMode == 0) {
                    nabeti400
                } else {
                    Color.White
                }
            )
        },
        actions = {
            IconButton(
                modifier =
                Modifier.padding(end = 10.dp),
                onClick = {
                    updateSearchBarVisibility(!isSearchBarVisible)
                    onSearchClicked()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "search",
                    Modifier.size(33.dp, 33.dp),
                    tint = if (themeMode == 0) {
                        Color.Gray
                    } else {
                        Color.White
                    }
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                updateGridOrList(!GridOrListController)
            }) {
                Icon(
                    modifier = Modifier
                        .padding(start = 5.dp, top = 0.dp)
                        .size(27.dp),

                    painter =
                    painterResource(
                        id = if (GridOrListController) {
                            //true = list
                            R.drawable.ic_baseline_list_24
                        } else {
                            //false = grid
                            R.drawable.gridview
                        }
                    ),
                    contentDescription = "view",
                    tint = if (themeMode == 0) {
                        gridiconcolor
                    } else {
                        Color.White
                    }
                )
            }
        },
        backgroundColor = if (themeMode == 0) {
            Color.White
        } else {
            Color.Black
        },
        elevation = 0.dp
    )
}
