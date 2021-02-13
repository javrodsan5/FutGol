package org.springframework.samples.futgol.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.samples.futgol.system.CustomErrorController
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletRequest


@ControllerAdvice
class ExceptionHandlerConfiguration{

    @Autowired
    var customErrorController: CustomErrorController?= null

    @ExceptionHandler()
    fun defaultErrorHandler(request: HttpServletRequest, ex: Exception): String{
        request.setAttribute("javax.servlet.error.request_uri", request.getPathInfo());
        request.setAttribute("javax.servlet.error.status_code", 400);
        request.setAttribute("exeption", ex);
        return "exception";
    }

    fun getErrorController(): CustomErrorController? {
        return customErrorController;
    }

    fun setErrorController(errorController: CustomErrorController) {
        this.customErrorController = errorController;
    }
}
