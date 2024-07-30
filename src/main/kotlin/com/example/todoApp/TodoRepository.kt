package com.example.todoApp

interface TodoRepository {
    fun getTodos(): List<Todo>

    fun getTodo(id: Long): Todo?

    fun saveTodo(todoRequest: TodoRequest): Long

    fun deleteTodo(id: Long)
}
