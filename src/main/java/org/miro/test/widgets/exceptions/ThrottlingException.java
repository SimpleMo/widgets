package org.miro.test.widgets.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS, reason = "Too many request. Service is temporary not available")
public class ThrottlingException extends RuntimeException {
}
