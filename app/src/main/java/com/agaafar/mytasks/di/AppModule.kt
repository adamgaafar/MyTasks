package com.agaafar.mytasks.di

import android.app.Application
import androidx.room.Room
import com.agaafar.mytasks.data.TodoDatabase
import com.agaafar.mytasks.data.TodoRepository
import com.agaafar.mytasks.data.TodoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(app: Application): TodoDatabase {
        return Room.databaseBuilder(
            app,
            TodoDatabase::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(db:TodoDatabase):TodoRepository{
        return TodoRepositoryImpl(db.dao)
    }
}