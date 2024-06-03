package com.example.todoApp

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.sql.ResultSet

class TodoRowMapper : RowMapper<Todo> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Todo {
        return Todo(rs.getLong(1), rs.getString(2))
    }
}

@Repository
class TodoRepository(val jdbcTemplate: JdbcTemplate) {

    val todoRowMapper = TodoRowMapper()

    fun getTodos(): List<Todo> {
        return jdbcTemplate.query("SELECT id, text FROM todos", todoRowMapper)
    }

    fun getTodo(id: Long): Todo? {
        val todos = jdbcTemplate.query("SELECT id, text FROM todos WHERE id=?", todoRowMapper, id)
        if (todos.isEmpty()) {
            return null
        } else {
            return todos[0]
        }
    }

    fun saveTodo(todoRequest: TodoRequest): Long {
        val simpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate).withTableName("todos").usingGeneratedKeyColumns("id")
        val parameters = mapOf("text" to todoRequest.text)
        return simpleJdbcInsert.executeAndReturnKey(parameters).toLong()
    }

    fun deleteTodo(id: Long) {
        jdbcTemplate.update("DELETE FROM todos WHERE id=?", id)
    }
}