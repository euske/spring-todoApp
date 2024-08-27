package com.example.todoApp

import java.util.UUID

interface TodoRepository {
    fun getTodos(): List<Todo>

    fun getTodo(id: UUID): Todo?

    fun saveTodo(todoRequest: TodoRequest): UUID

    fun deleteTodo(id: UUID)
}
