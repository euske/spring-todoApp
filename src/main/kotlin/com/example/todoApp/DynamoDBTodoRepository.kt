package com.example.todoApp

import io.awspring.cloud.dynamodb.DynamoDbTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.util.UUID

@DynamoDbBean
class TodoDynamoEntity {

    private var id: UUID
    private var text: String

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    fun getId(): UUID {
        return id
    }
    fun setId(id: UUID) {
        this.id = id
    }

    @DynamoDbAttribute("text")
    fun getText(): String {
        return text
    }
    fun setText(text: String) {
        this.text = text
    }

    constructor() : this(UUID.randomUUID(), "")

    constructor(id: UUID = UUID.randomUUID(), text: String = "") {
        this.id = id
        this.text = text
    }

}

@Repository
@Profile("dynamo")
class DynamoDBTodoRepository(
    @Autowired val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    @Autowired val dynamoDbTemplate: DynamoDbTemplate,
    ) : TodoRepository {

    override fun getTodos(): List<Todo> {
        val todos = dynamoDbTemplate.scanAll(TodoDynamoEntity::class.java)
        return todos.items().stream().map { todo: TodoDynamoEntity -> Todo(todo.getId(), todo.getText()) }.toList()
    }

    override fun getTodo(id: UUID): Todo? {
        val key = Key.builder().partitionValue(id.toString()).build()
        val todo = dynamoDbTemplate.load(key, TodoDynamoEntity::class.java)
        return if (todo == null) null else Todo(id, todo.getText())
    }

    override fun saveTodo(todoRequest: TodoRequest): UUID {
        val todo = TodoDynamoEntity(text=todoRequest.text)
        dynamoDbTemplate.save(todo)
        return todo.getId()
    }

    override fun deleteTodo(id: UUID) {
        dynamoDbTemplate.delete(id)
    }

}