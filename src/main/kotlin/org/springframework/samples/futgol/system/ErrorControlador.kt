package org.springframework.samples.futgol.system

import org.springframework.stereotype.Controller
import org.springframework.http.HttpStatus

import javax.servlet.RequestDispatcher

import javax.servlet.http.HttpServletRequest

import org.springframework.web.bind.annotation.RequestMapping

import org.springframework.boot.web.servlet.error.ErrorController

@Controller
class ErrorControlador : ErrorController {
    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): String {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
        if (status != null) {
            val statusCode = status.toString().toInt()
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "errores/error-404"
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "errores/error-500"
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "errores/error-403"
            } else if (statusCode == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                return "errores/error-503"
            }
        }
        // display generic error
        return "errores/error"
    }

    override fun getErrorPath(): String {
        return "/error"
    }
}
