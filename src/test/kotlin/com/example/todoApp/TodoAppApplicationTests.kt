package com.example.todoApp

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoAppApplicationTests(@Autowired val restTemplate: TestRestTemplate, @LocalServerPort val port: Int) {

	@Test
	fun contextLoads() {
	}

	@Test
	fun `GETリクエストはOKステータスを返す`() {
		val response = restTemplate.getForEntity("http://localhost:$port/todos", Any::class.java)
		assertThat(response.statusCode, equalTo(HttpStatus.OK))
		assertThat(response.headers.contentType, equalTo(MediaType.APPLICATION_JSON))
	}

	@Test
	@Sql("/insert_test_data.sql")
	fun `GETリクエストはTodoオブジェクトのリストを返す`() {
		val response = restTemplate.getForEntity("http://localhost:$port/todos", Array<Todo>::class.java)
		val todos = response.body!!
		assertThat(todos.size, equalTo(1))
		assertThat(todos[0].id, equalTo(1))
		assertThat(todos[0].text, equalTo("foo"))
	}

	@Test
	fun `POSTリクエストはOKステータスを返す`() {
		val request = TodoRequest("hello")
		val response = restTemplate.postForEntity("http://localhost:$port/todos", request, Any::class.java)
		assertThat(response.statusCode, equalTo(HttpStatus.OK))
	}

	@Test
	fun `POSTリクエストはTodoオブジェクトを格納する`() {
		val response1 = restTemplate.getForEntity("http://localhost:$port/todos", Array<Todo>::class.java)
		val todos1 = response1.body!!

		val request = TodoRequest("hello")
		restTemplate.postForEntity("http://localhost:$port/todos", request, Any::class.java)

		val response2 = restTemplate.getForEntity("http://localhost:$port/todos", Array<Todo>::class.java)
		val todos2 = response2.body!!
		assertThat(todos2.size, equalTo(todos1.size + 1))
		assertThat(todos2.map { todo: Todo -> todo.text }, hasItem("hello"))
	}
}
