package com.example.todoApp

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

class TodoRowMapper : RowMapper<Todo> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Todo {
        return Todo(rs.getInt(1), rs.getString(2))
    }
}

@Repository
class TodoRepository(val jdbcTemplate: JdbcTemplate) {

    val todoRowMapper = TodoRowMapper()

    fun getTodos(): List<Todo> {
        return jdbcTemplate.query("SELECT id, text FROM todos", todoRowMapper)
    }

    fun saveTodo(todoRequest: TodoRequest) {
        jdbcTemplate.update("INSERT INTO todos (text) VALUES (?)", todoRequest.text)
    }
}