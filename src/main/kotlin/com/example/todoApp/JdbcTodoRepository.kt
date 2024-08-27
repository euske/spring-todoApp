package com.example.todoApp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID

@Component
class TodoRowMapper : RowMapper<Todo> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Todo {
        return Todo(rs.getObject(1, UUID::class.java), rs.getString(2))
    }
}

@Repository
@Profile("jdbc")
class JdbcTodoRepository (
    @Autowired val jdbcClient: JdbcClient,
    @Autowired val todoRowMapper: TodoRowMapper
) : TodoRepository {

    override fun getTodos(): List<Todo> {
        return jdbcClient.sql("SELECT id, text FROM todos").query(todoRowMapper).list()
    }

    override fun getTodo(id: UUID): Todo? {
        val todos = jdbcClient.sql("SELECT id, text FROM todos WHERE id=:id").param("id", id).query(todoRowMapper).list()
        if (todos.isEmpty()) {
            return null
        } else {
            return todos[0]
        }
    }

    override fun saveTodo(todoRequest: TodoRequest): UUID {
        val keyHolder = GeneratedKeyHolder()
        jdbcClient.sql("INSERT INTO todos (text) VALUES (:text)").param("text", todoRequest.text).update(keyHolder, "id")
        val newId = keyHolder.getKeyAs(UUID::class.java)
        return newId!!
    }

    override fun deleteTodo(id: UUID) {
        jdbcClient.sql("DELETE FROM todos WHERE id=:id").param("id", id).update()
    }
}