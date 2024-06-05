package com.example.todoApp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Component
class TodoRowMapper : RowMapper<Todo> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Todo {
        return Todo(rs.getLong(1), rs.getString(2))
    }
}

@Repository
class TodoRepository(
    @Autowired val jdbcClient: JdbcClient,
    @Autowired val todoRowMapper: TodoRowMapper
) {

    fun getTodos(): List<Todo> {
        return jdbcClient.sql("SELECT id, text FROM todos").query(todoRowMapper).list()
    }

    fun getTodo(id: Long): Todo? {
        val todos = jdbcClient.sql("SELECT id, text FROM todos WHERE id=?").param(id).query(todoRowMapper).list()
        if (todos.isEmpty()) {
            return null
        } else {
            return todos[0]
        }
    }

    fun saveTodo(todoRequest: TodoRequest): Long {
        val keyHolder = GeneratedKeyHolder()
        jdbcClient.sql("INSERT INTO todos (text) VALUES (?)").param(todoRequest.text).update(keyHolder, "id")
        val newId = keyHolder.key!!.toLong()
        return newId
    }

    fun deleteTodo(id: Long) {
        jdbcClient.sql("DELETE FROM todos WHERE id=?").param(id).update()
    }
}