package com.example.todoApp

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Entity
@Table(name = "todos")
class TodoJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),

    val text: String = ""
)

@Repository
interface JpaTodoRepositoryBase : JpaRepository<TodoJpaEntity, UUID> {

}

@Repository
@Profile("jpa")
class JpaTodoRepositoryImpl(
    @Autowired val todoRepositoryBase: JpaTodoRepositoryBase
) : TodoRepository {

    override fun getTodos(): List<Todo> {
        val todos = todoRepositoryBase.findAll().map { todo: TodoJpaEntity -> Todo(todo.id, todo.text) }
        return todos
    }

    override fun getTodo(id: UUID): Todo? {
        val todo = todoRepositoryBase.findById(id).orElse(null)
        return if (todo == null) null else Todo(todo.id, todo.text)
    }

    override fun saveTodo(todoRequest: TodoRequest): UUID {
        val todo = TodoJpaEntity(text=todoRequest.text)
        todoRepositoryBase.save(todo)
        return todo.id
    }

    override fun deleteTodo(id: UUID) {
        todoRepositoryBase.deleteById(id)
    }

}