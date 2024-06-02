package com.example.todoApp

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TodoController(val todoRepository: TodoRepository) {

    @GetMapping("/todos")
    fun getTodos(): List<Todo> {
        return todoRepository.getTodos()
    }

    @GetMapping("/todos/{id}")
    fun getTodo(@PathVariable id: Long): ResponseEntity<Todo> {
        val todo = todoRepository.getTodo(id)
        if (todo == null) {
            return ResponseEntity.notFound().build()
        } else {
            return ResponseEntity.ok(todo)
        }
    }

    @PostMapping("/todos")
    fun postTodo(@RequestBody todoRequest: TodoRequest): Long {
        return todoRepository.saveTodo(todoRequest)
    }

}