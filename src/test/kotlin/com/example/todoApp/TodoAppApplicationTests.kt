package com.example.todoApp

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class TodoAppApplicationTests() {

	@Autowired
	lateinit var mockMvc: MockMvc
	@Autowired
	lateinit var objectMapper: ObjectMapper

	@Test
	fun contextLoads() {
	}

	@Test
	fun `GETリクエストはOKステータスを返す`() {
		mockMvc.perform(get("/todos"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
	}

	@Test
	@Sql("/insert_test_data.sql")
	fun `GETリクエストはTodoオブジェクトのリストを返す`() {
		mockMvc.perform(get("/todos"))
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].text").value("foo"))
	}

	@Test
	fun `POSTリクエストはOKステータスを返す`() {
		val json = "{\"text\": \"hello\"}"
		mockMvc.perform(post("/todos")
			.contentType(MediaType.APPLICATION_JSON)
			.content(json))
			.andExpect(status().isOk())
	}

	@Test
	fun `POSTリクエストはTodoオブジェクトを格納する`() {
		val result = mockMvc.perform(get("/todos")).andReturn()
		val json1 = result.response.contentAsString
		val todos = objectMapper.readValue(json1, List::class.java)
		val length = todos.count()
		val json = "{\"text\": \"hello\"}"
		mockMvc.perform(post("/todos")
			.contentType(MediaType.APPLICATION_JSON)
			.content(json))
			.andReturn()
		mockMvc.perform(get("/todos"))
			.andExpect(jsonPath("$.length()").value(length+1))
			.andExpect(jsonPath("$[*].text", hasItem("hello")))
	}
}
