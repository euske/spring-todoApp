package com.example.todoApp

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

class TodoRowMapper : RowMapper<Todo> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Todo {
        return Todo(rs.getLong(1), rs.getString(2))
    }
}

@Repository
class TodoRepository(val jdbcTemplate: JdbcTemplate, val dataSource: DataSource) {

    val todoRowMapper = TodoRowMapper()

    fun getTodos(): List<Todo> {
        return jdbcTemplate.query("SELECT id, text FROM todos", todoRowMapper)
    }

    fun saveTodo(todoRequest: TodoRequest): Long {
        val simpleJdbcInsert = SimpleJdbcInsert(dataSource).withTableName("todos").usingGeneratedKeyColumns("id")
        val parameters = mapOf("text" to todoRequest.text)
        return simpleJdbcInsert.executeAndReturnKey(parameters).toLong()
    }
}