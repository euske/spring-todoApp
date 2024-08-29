package com.example.todoApp

import io.awspring.cloud.dynamodb.DynamoDbTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.util.UUID

@DynamoDbBean
data class TodoDynamoEntity(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("Id")
    var id: UUID = UUID.randomUUID(),

    @get:DynamoDbAttribute("Text")
    var text: String = ""
)

@Repository
@Profile("dynamo")
class DynamoDBTodoRepository(
    @Autowired val dynamoDbTemplate: DynamoDbTemplate,
    ) : TodoRepository {

    override fun getTodos(): List<Todo> {
        val todos = dynamoDbTemplate.scanAll(TodoDynamoEntity::class.java)
        return todos.items().stream().map { todo: TodoDynamoEntity -> Todo(todo.id, todo.text) }.toList()
    }

    override fun getTodo(id: UUID): Todo? {
        val todo = dynamoDbTemplate.load(getKeyFor(id), TodoDynamoEntity::class.java)
        return if (todo == null) null else Todo(id, todo.text)
    }

    override fun saveTodo(todoRequest: TodoRequest): UUID {
        val todo = TodoDynamoEntity(text=todoRequest.text)
        dynamoDbTemplate.save(todo)
        return todo.id
    }

    override fun deleteTodo(id: UUID) {
        dynamoDbTemplate.delete(getKeyFor(id), TodoDynamoEntity::class.java)
    }

    private fun getKeyFor(id: UUID): Key {
        return Key.builder().partitionValue(id.toString()).build()
    }
}