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
		// localhost/todos に GETリクエストを投げる。
		val response = restTemplate.getForEntity("http://localhost:$port/todos", Any::class.java)
		// レスポンスのステータスコードは OK であること。
		assertThat(response.statusCode, equalTo(HttpStatus.OK))
		// レスポンスの Content-Type は application/json であること。
		assertThat(response.headers.contentType, equalTo(MediaType.APPLICATION_JSON))
	}

	@Test
	@Sql("/insert_test_data.sql")
	fun `GETリクエストはTodoオブジェクトのリストを返す`() {
		// localhost/todos に GETリクエストを投げ、レスポンスを Todoオブジェクトの配列として解釈する。
		val response = restTemplate.getForEntity("http://localhost:$port/todos", Array<Todo>::class.java)
		// 配列は1つの要素をもつこと。
		val todos = response.body!!
		assertThat(todos.size, equalTo(1))
		// 最初の要素は id=1 であり、text が "foo" であること。
		assertThat(todos[0].id, equalTo(1))
		assertThat(todos[0].text, equalTo("foo"))
	}

	@Test
	fun `POSTリクエストはOKステータスを返す`() {
		// localhost/todos に POSTリクエストを投げる。このときのボディは {"text": "hello"}
		val request = TodoRequest("hello")
		val response = restTemplate.postForEntity("http://localhost:$port/todos", request, Any::class.java)
		// レスポンスのステータスコードは OK であること。
		assertThat(response.statusCode, equalTo(HttpStatus.OK))
	}

	@Test
	fun `POSTリクエストはTodoオブジェクトを格納する`() {
		// localhost/todos に GETリクエストを投げ、レスポンスを Todoオブジェクトの配列として解釈する。
		val response1 = restTemplate.getForEntity("http://localhost:$port/todos", Array<Todo>::class.java)
		// このときのレスポンスを todos1 として記憶。
		val todos1 = response1.body!!

		// localhost/todos に POSTリクエストを投げる。このときのボディは {"text": "hello"}
		val request = TodoRequest("hello")
		restTemplate.postForEntity("http://localhost:$port/todos", request, Any::class.java)

		// ふたたび localhost/todos に GETリクエストを投げ、レスポンスを Todoオブジェクトの配列として解釈する。
		val response2 = restTemplate.getForEntity("http://localhost:$port/todos", Array<Todo>::class.java)
		// このときのレスポンスを todos2 として記憶。
		val todos2 = response2.body!!
		// 配列 todos2 は、配列 todos1 よりも 1 要素だけ多い。
		assertThat(todos2.size, equalTo(todos1.size + 1))
		// 配列 todos2 には "hello" をもつTodoオブジェクトが含まれている。
		assertThat(todos2.map { todo: Todo -> todo.text }, hasItem("hello"))
	}

	@Test
	fun `POSTリクエストは新しいTodoオブジェクトのidを返す`() {
		// localhost/todos に POSTリクエストを投げる。このときのボディは {"text": "hello"}
		val request = TodoRequest("hello")
		val response1 = restTemplate.postForEntity("http://localhost:$port/todos", request, Long::class.java)
		val id = response1.body!!

		// ふたたび localhost/todos に GETリクエストを投げ、レスポンスを Todoオブジェクトの配列として解釈する。
		val response2 = restTemplate.getForEntity("http://localhost:$port/todos", Array<Todo>::class.java)
		// このときのレスポンスを todos として記憶。
		val todos = response2.body!!
		// 配列 todos2 には "hello" をもつTodoオブジェクトが含まれている。
		assertThat(todos.find { todo: Todo -> todo.id == id }!!.text, equalTo("hello"))
	}
}
