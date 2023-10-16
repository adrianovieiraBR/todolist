package br.com.adrianovieira.todolist.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // Anotação do Spring usada para definir que essa classe é do tipo GLOBAL para tratamento de exceções. TODA exceção lançada passará por aqui antes
public class ExceptionHandlerController {
    
    @ExceptionHandler(HttpMessageNotReadableException.class) // Neste método, a anotação 'ExceptionHandler' indica que toda a exceção do tipo 'HttpMessageNotReadableException' passará por aqui! A mensagem para o usuário vem na variável 'e', abaixo...
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMostSpecificCause().getMessage());
    }

}
