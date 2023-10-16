package br.com.adrianovieira.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired // Anotação SPRING para que o ciclo de vida desse repositório seja gerenciado pelo Spring (semelhante ao EJB) 
    private IUserRepository userRepository;

    /**
     * @RequestBody - anotação que define que o conteúdo esperado para a classe (no caso, UserModel) virá do corpo da requisição
     */
    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){ // Classes do tipo 'ResponseEntity' (Spring) permitem retornar da base detalhes da operação feita. No caso em questão, detalhes do sucesso ou erro da operação de salvamente do usuário
        System.out.println("Salvando usuário: "+userModel.getName()); 

        var user =  this.userRepository.findByUsername(userModel.getUsername());

        if(user != null){
                System.out.println("USUÁRIO JÁ EXISTE!!!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário Já Existe!"); 
        }

        // Criptografando a senha do usuário com o BCrypt
            var passwordHashred = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
            userModel.setPassword(passwordHashred); 

        var userCreated = this.userRepository.save(userModel);

        System.out.println("Usuario salvo: "+userCreated.getName()); 
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

}
