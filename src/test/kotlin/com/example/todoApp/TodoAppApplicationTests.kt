package com.example.todoApp

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.*
import java.net.URI
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["test", "dynamo"])
@Sql("classpath:/insert_test_data.sql")
class TodoAppApplicationTests(@LocalServerPort val port: Int) {

	val restClient = RestClient.create()
	val todoId1: UUID = UUID.fromString("75C431BF-E5EC-4253-8D96-E9BB2C6CAF8E")

	@Test
	fun contextLoads() {
	}

	@Test
	fun `GETリクエストはOKステータスを返す`() {
		// localhost/todos に GETリクエストを送る。
		val response = restClient.get().uri("http://localhost:$port/todos").retrieve().toEntity<String>()
		// レスポンスのステータスコードは OK であること。
		assertThat(response.statusCode, equalTo(HttpStatus.OK))
	}

	@Test
	fun `GETリクエストはTodoオブジェクトのリストを返す`() {
		// localhost/todos に GETリクエストを送り、レスポンスを Todoオブジェクトの配列として解釈する。
		val response = restClient.get().uri("http://localhost:$port/todos").retrieve().toEntity<Array<Todo>>()
		// レスポンスの Content-Type は application/json であること。
		assertThat(response.headers.contentType, equalTo(MediaType.APPLICATION_JSON))
		// 配列は2つの要素をもつこと。
		val todos = response.body!!
		assertThat(todos.size, equalTo(2))
		// 各要素のtextが "foo", "bar" (順不同) であること。
		assertThat(todos.map { todo: Todo -> todo.text }, containsInAnyOrder("foo", "bar"))
	}

	@Test
	fun `POSTリクエストはCREATEDステータスを返す`() {
		// localhost/todos に POSTリクエストを送る。このときのボディは {"text": "hello"}
		val request = TodoRequest("hello")
		val response = restClient.post().uri("http://localhost:$port/todos").body(request).retrieve().toEntity<String>()
		// レスポンスのステータスコードは OK であること。
		assertThat(response.statusCode, equalTo(HttpStatus.CREATED))
	}

	@Test
	fun `POSTリクエストはTodoオブジェクトを格納する`() {
		// localhost/todos に GETリクエストを送り、レスポンスを Todoオブジェクトの配列として解釈する。
		val response1 = restClient.get().uri("http://localhost:$port/todos").retrieve().toEntity<Array<Todo>>()
		// このときのレスポンスを todos1 として記憶。
		val todos1 = response1.body!!

		// localhost/todos に POSTリクエストを送る。このときのボディは {"text": "hello"}
		val request = TodoRequest("hello")
		restClient.post().uri("http://localhost:$port/todos").body(request).retrieve().toEntity<String>()

		// ふたたび localhost/todos に GETリクエストを送り、レスポンスを Todoオブジェクトの配列として解釈する。
		val response2 = restClient.get().uri("http://localhost:$port/todos").retrieve().toEntity<Array<Todo>>()
		// このときのレスポンスを todos2 として記憶。
		val todos2 = response2.body!!
		// 配列 todos2 は、配列 todos1 よりも 1 要素だけ多い。
		assertThat(todos2.size, equalTo(todos1.size + 1))
		// 配列 todos2 には "hello" をもつTodoオブジェクトが含まれている。
		assertThat(todos2.map { todo: Todo -> todo.text }, hasItem("hello"))
	}

	@Test
	fun `POSTリクエストは新しいTodoオブジェクトのLocationを返す`() {
		// localhost/todos に POSTリクエストを送る。このときのボディは {"text": "hello"}
		val request = TodoRequest("hello")
		val response1 = restClient.post().uri("http://localhost:$port/todos").body(request).retrieve().toBodilessEntity()
		// そのとき返されたidを記憶しておく。
		val location = URI(response1.headers.getFirst(HttpHeaders.LOCATION)!!)
		val id: UUID = UUID.fromString(location.path.split("/").last())

		// ふたたび localhost/todos に GETリクエストを送り、レスポンスを Todoオブジェクトの配列として解釈する。
		val response2 = restClient.get().uri("http://localhost:$port/todos").retrieve().toEntity<Array<Todo>>()
		// このときのレスポンスを todos として記憶。
		val todos = response2.body!!
		// 配列 todos には返された id をもつTodoオブジェクトが含まれており、そのテキストは hello。
		assertThat(todos.find { todo: Todo -> todo.id == id }!!.text, equalTo("hello"))
	}

	@Test
	fun `POSTリクエストはwww-form-urlencoded型のリクエストも受け付ける`() {
		// localhost/todos に POSTリクエストを送る。このときのボディは text=hello
		val params = LinkedMultiValueMap(mapOf("text" to listOf("hello")))
		val response1 = restClient.post().uri("http://localhost:$port/todos").contentType(MediaType.APPLICATION_FORM_URLENCODED).body(params).retrieve().toBodilessEntity()
		// そのとき返されたidを記憶しておく。
		val location = URI(response1.headers.getFirst(HttpHeaders.LOCATION)!!)
		val id: UUID = UUID.fromString(location.path.split("/").last())

		// ふたたび localhost/todos に GETリクエストを送り、レスポンスを Todoオブジェクトの配列として解釈する。
		val response2 = restClient.get().uri("http://localhost:$port/todos").retrieve().toEntity<Array<Todo>>()
		// このときのレスポンスを todos として記憶。
		val todos = response2.body!!
		// 配列 todos には返された id をもつTodoオブジェクトが含まれており、そのテキストは hello。
		assertThat(todos.find { todo: Todo -> todo.id == id }!!.text, equalTo("hello"))
	}

	@Test
	fun `GETリクエストは単一のオブジェクトに対してOKステータスを返す`() {
		// localhost/todos に GETリクエストを送る。
		val response = restClient.get().uri("http://localhost:$port/todos/$todoId1").retrieve().toEntity<Todo>()
		// レスポンスのステータスコードは OK であること。
		assertThat(response.statusCode, equalTo(HttpStatus.OK))
	}

	@Test
	fun `GETリクエストはひとつのTodoオブジェクトを返す`() {
		// localhost/todos に GETリクエストを送り、レスポンスを Todoオブジェクトとして解釈する。
		val response = restClient.get().uri("http://localhost:$port/todos/$todoId1").retrieve().toEntity<Todo>()
		val todo = response.body!!
		// id=2 の Todoオブジェクトが取得されている。
		assertThat(todo.id, equalTo(todoId1))
		assertThat(todo.text, equalTo("bar"))
	}

	@Test
	fun `GETリクエストは存在しないIDに対してNot Foundを返す`() {
		// localhost/todos に GETリクエストを送り、レスポンスを Todoオブジェクトとして解釈する。
		val id = UUID.randomUUID()
		assertThrows(
			HttpClientErrorException.NotFound::class.java,
			{ restClient.get().uri("http://localhost:$port/todos/$id").retrieve().toBodilessEntity() }
		)
	}

	@Test
	fun `DELETEリクエストはTodoオブジェクトを削除する`() {
		// localhost/todos に POSTリクエストを送る。このときのボディは {"text": "hello"}
		val request = TodoRequest("hello")
		val response1 = restClient.post().uri("http://localhost:$port/todos").body(request).retrieve().toBodilessEntity()
		// そのとき返されたlocationを記憶しておく。
		val location = URI(response1.headers.getFirst(HttpHeaders.LOCATION)!!)
		val id: UUID = UUID.fromString(location.path.split("/").last())

		// localhost/todos にその id の DELETEリクエストを送る。
		restClient.delete().uri(location).retrieve().toBodilessEntity()

		// ふたたび localhost/todos に GETリクエストを送り、レスポンスを Todoオブジェクトの配列として解釈する。
		val response2 = restClient.get().uri("http://localhost:$port/todos").retrieve().toEntity<Array<Todo>>()
		// このときのレスポンスを todos として記憶。
		val todos = response2.body!!
		// 配列 todos にはその id をもつオブジェクトは含まれていない。
		assertThat(todos.find { todo: Todo -> todo.id == id }, nullValue())
	}
}
