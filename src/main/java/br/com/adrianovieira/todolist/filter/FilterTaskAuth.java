package br.com.adrianovieira.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.adrianovieira.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * Essa classe de Filtro server para fazer filtragens e validações ANTES da execução dos Controllers. 
*/

@Component // Classes do tipo 'Component' servem para sinalizar ao Spring que ele precisa "passar por aqui" e levá-las em consideração durante a execução
public class FilterTaskAuth extends OncePerRequestFilter{

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

         var servletPath = request.getServletPath(); // De onde está sendo chamado o filtro...
         System.out.println("Entrei no filtro a partir de  '"+servletPath+"'' ... "); 

         if(servletPath.startsWith("/tasks/")){ // startWith para contemplar os paths '/tasks/' que também tiverem os IDs passados como parâmetros para atualizações de tarefas 
                // PEGA A AUTENTICAÇÃO 

            var authorization = request.getHeader("Authorization");
            var user_password = authorization.substring(authorization.indexOf(" "), authorization.length()).trim();
            System.out.println("AUTH USADO (CRIPTORGRAFADO): "+user_password);

            byte[] authDecode = Base64.getDecoder().decode(user_password);

            String authString = new String(authDecode);
            System.out.println("AUTH USADO: "+authString);

                // dividindo usuário e senha...
                String[] dadosAuth = authString.split(":");
                System.out.println("Usuário: "+dadosAuth[0]);
                System.out.println("Senha: "+dadosAuth[1]); 

            // VALIDAR A AUTENTICAÇÃO 
                var user = this.userRepository.findByUsername(dadosAuth[0]);

                if(user == null){
                    response.sendError(401, "Usuário sem autorização");
                }else{
                    // Validar senha...
                    var passwordVerify = BCrypt.verifyer().verify(dadosAuth[1].toCharArray(), user.getPassword());
                    if(!passwordVerify.verified){
                        response.sendError(401);
                    }else{
                        request.setAttribute("idUser", user.getId());
                        request.setAttribute("name", user.getName());
                        filterChain.doFilter(request, response);
                    }

                  
                }
         }else{
            System.out.println("PATH NÃO ELEGÍVEL A FILTRAGEM");
            filterChain.doFilter(request, response);
         }

            
    }

    
}
