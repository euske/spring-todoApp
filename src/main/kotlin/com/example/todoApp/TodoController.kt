package com.example.todoApp

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

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
    fun postTodo(@RequestBody todoRequest: TodoRequest): ResponseEntity<Void> {
        val id = todoRepository.saveTodo(todoRequest)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(id).toUri()
        return ResponseEntity.created(location).build()
    }

    @PostMapping("/todos", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun postTodoUrlEncoded(@RequestParam text: String): ResponseEntity<Void> {
        val id = todoRepository.saveTodo(TodoRequest(text))
        val location = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(id).toUri()
        return ResponseEntity.created(location).build()
    }

    @DeleteMapping("/todos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTodo(@PathVariable id: Long) {
        todoRepository.deleteTodo(id)
    }

}