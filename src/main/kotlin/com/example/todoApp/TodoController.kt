package com.example.todoApp

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TodoController(val todoRepository: TodoRepository) {

    @GetMapping("/todos")
    fun getTodos(): List<Todo> {
        return todoRepository.getTodos()
    }

    @PostMapping("/todos")
    fun postTodo(@RequestBody todoRequest: TodoRequest): Long {
        return todoRepository.saveTodo(todoRequest)
    }

}