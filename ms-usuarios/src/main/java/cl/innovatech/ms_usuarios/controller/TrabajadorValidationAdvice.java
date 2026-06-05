package cl.innovatech.ms_usuarios.controller;

import cl.innovatech.ms_usuarios.validation.TrabajadorValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class TrabajadorValidationAdvice {

	@ExceptionHandler(TrabajadorValidationException.class)
	public ResponseEntity<Map<String, String>> handleValidation(TrabajadorValidationException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("message", ex.getMessage()));
	}
}
