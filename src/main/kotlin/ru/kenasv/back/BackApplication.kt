package ru.kenasv.back

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
//import org.springframework.data.annotation.Id
import org.springframework.stereotype.Service
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PostMapping
import java.util.UUID
import org.springframework.jdbc.core.query
import org.springframework.web.bind.annotation.*

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import java.util.*


@SpringBootApplication
class BackApplication

fun main(args: Array<String>) {
	runApplication<BackApplication>(*args)
}

@RestController
class MessageController(val service: MessageService) {
	@GetMapping("/")
	fun index(): List<Message> = service.findMessages()

	@GetMapping("/{id}")
	fun index(@PathVariable id: String): List<Message> =
			service.findMessageById(id)

	@PostMapping("/")
	fun post(@RequestBody message: Message) {
		service.save(message)
	}
}

data class Message(val id: String?, val text: String)


@Service
class MessageService(val db: JdbcTemplate) {

	fun findMessages(): List<Message> = db.query("select * from messages") { response, _ ->
		Message(response.getString("id"), response.getString("text"))
	}

	fun findMessageById(id: String): List<Message> = db.query("select * from messages where id = ?", id) { response, _ ->
		Message(response.getString("id"), response.getString("text"))
	}

	fun save(message: Message) {
		val id = message.id ?: UUID.randomUUID().toString()
		db.update("insert into messages values ( ?, ? )",
				id, message.text)
	}
}


@Table("PEOPLE")
data class People(@Id var id: String?, val text: String)

interface PeopleRepository : CrudRepository<People, String>

@Service
class PeopleService(val db2: PeopleRepository) {
	fun findPeoples(): List<People> = db2.findAll().toList()

	fun findPeopleById(id: String): List<People> = db2.findById(id).toList()

	fun save(people: People) {
		db2.save(people)
	}

	fun <T : Any> Optional<out T>.toList(): List<T> =
			if (isPresent) listOf(get()) else emptyList()
}