package com.example.todoApp

import io.awspring.cloud.dynamodb.DynamoDbTemplate
import org.springframework.beans.factory.annotation.Autowired
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.util.UUID

@DynamoDbBean
data class TodoDynamoEntity(val id: UUID = UUID.randomUUID(), val text: String = "") {

    @DynamoDbPartitionKey
    fun getPartitionKey(): UUID {
        return id
    }
}

//@Repository
class DynamoDBTodoRepository(
    @Autowired val dynamoDbClient: DynamoDbClient,
    @Autowired val dynamoDbTemplate: DynamoDbTemplate,
    ) : TodoRepository {

    override fun getTodos(): List<Todo> {
        val todos = dynamoDbTemplate.scanAll(TodoDynamoEntity::class.java)
        return todos.items().stream().map { todo: TodoDynamoEntity -> Todo(todo.id, todo.text) }.toList()
    }

    override fun getTodo(id: UUID): Todo? {
        val key = Key.builder().partitionValue(id.toString()).build()
        val todo = dynamoDbTemplate.load(key, TodoDynamoEntity::class.java)
        return if (todo == null) null else Todo(id, todo.text)
    }

    override fun saveTodo(todoRequest: TodoRequest): UUID {
        val todo = TodoDynamoEntity(text=todoRequest.text)
        dynamoDbTemplate.save(todo)
        return todo.id
    }

    override fun deleteTodo(id: UUID) {
        dynamoDbTemplate.delete(id)
    }

}