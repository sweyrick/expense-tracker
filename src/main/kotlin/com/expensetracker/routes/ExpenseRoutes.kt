package com.expensetracker.routes

import com.expensetracker.models.ExpenseRequest
import com.expensetracker.models.ExpenseCategory
import com.expensetracker.repository.ExpenseRepository
import com.expensetracker.repository.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.put
import io.ktor.server.routing.delete
import java.time.LocalDate

fun Route.expenseRoutes(expenseRepository: ExpenseRepository, userRepository: UserRepository) {
    route("/expenses") {
        withAuth {
            get("/categories") {
                // Return all categories as their serial names
                val categories = ExpenseCategory.values().map {
                    it::class.java.getField(it.name).getAnnotation(kotlinx.serialization.SerialName::class.java)?.value ?: it.name
                }
                call.respond(categories)
            }
            
            get {
                try {
                    // Get query parameters for date filtering
                    val startDateParam = call.request.queryParameters["startDate"]
                    val endDateParam = call.request.queryParameters["endDate"]
                    
                    // Parse dates if provided
                    val startDate = startDateParam?.let { LocalDate.parse(it) }
                    val endDate = endDateParam?.let { LocalDate.parse(it) }
                    
                    // Get all expenses for the family (both users can see all) with optional date filtering
                    val user = getAuthenticatedUser(call, userRepository) ?: return@get
                    val expenses = expenseRepository.getExpenses(user.id, startDate, endDate)
                    call.respond(expenses)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to fetch expenses"))
                }
            }
            
            post {
                try {
                    val request = call.receive<ExpenseRequest>()
                    val user = getAuthenticatedUser(call, userRepository) ?: return@post
                    
                    val expenseId = expenseRepository.createExpense(user.id, request)
                    
                    call.respond(HttpStatusCode.Created, mapOf("id" to expenseId))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request data"))
                }
            }

            put("/{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<ExpenseRequest>()
                val user = getAuthenticatedUser(call, userRepository) ?: return@put
                val updated = expenseRepository.updateExpense(id, user.id, request)
                if (updated) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
            }
            
            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val user = getAuthenticatedUser(call, userRepository) ?: return@delete
                val deleted = expenseRepository.deleteExpense(id, user.id)
                if (deleted) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
