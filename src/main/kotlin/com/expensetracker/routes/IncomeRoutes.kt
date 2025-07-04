package com.expensetracker.routes

import com.expensetracker.models.IncomeRequest
import com.expensetracker.repository.IncomeRepository
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

fun Route.incomeRoutes(incomeRepository: IncomeRepository, userRepository: UserRepository) {
    route("/incomes") {
        withAuth {
            get {
                try {
                    // Get query parameters for date filtering
                    val startDateParam = call.request.queryParameters["startDate"]
                    val endDateParam = call.request.queryParameters["endDate"]
                    
                    // Parse dates if provided
                    val startDate = startDateParam?.let { LocalDate.parse(it) }
                    val endDate = endDateParam?.let { LocalDate.parse(it) }
                    
                    // Get all incomes for the family (both users can see all) with optional date filtering
                    val user = getAuthenticatedUser(call, userRepository) ?: return@get
                    val incomes = incomeRepository.getIncomes(user.id, startDate, endDate)
                    call.respond(incomes)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to fetch incomes"))
                }
            }
            
            post {
                try {
                    val request = call.receive<IncomeRequest>()
                    val user = getAuthenticatedUser(call, userRepository) ?: return@post
                    
                    val incomeId = incomeRepository.createIncome(user.id, request)
                    
                    call.respond(HttpStatusCode.Created, mapOf("id" to incomeId))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request data"))
                }
            }
            
            put("/{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<IncomeRequest>()
                val user = getAuthenticatedUser(call, userRepository) ?: return@put
                val updated = incomeRepository.updateIncome(id, user.id, request)
                if (updated) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
            }
            
            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val user = getAuthenticatedUser(call, userRepository) ?: return@delete
                val deleted = incomeRepository.deleteIncome(id, user.id)
                if (deleted) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
