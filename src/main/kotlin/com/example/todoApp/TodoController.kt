package com.example.todoApp

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @PostMapping("/todos", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun postTodoUrlEncoded(@RequestParam text: String): Long {
        return todoRepository.saveTodo(TodoRequest(text))
    }

    @DeleteMapping("/todos/{id}")
    fun deleteTodo(@PathVariable id: Long) {
        todoRepository.deleteTodo(id)
    }

}