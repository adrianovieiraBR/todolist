package br.com.adrianovieira.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.adrianovieira.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        System.out.println("Chegou no controller de tasks. ... id passado "+request.getAttribute("idUser"));
        taskModel.setIdUser((UUID) request.getAttribute("idUser")); 

        // Fazendo algumas validações antes do cadastro da tarefa... 

            // Validando a data de início da tarefa...
            if(taskModel.getStartAt().isBefore(LocalDateTime.now())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser maior que a data atual!");
            }

            // Passada a validação, validando agora a data de fim (que deve ser maior que a de início)
            if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de término deve ser maior que a data de início!");
            }


        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/") // Chamado quando o usuário precisa ler as tarefas que possui... Trazendo tudo o que for relacionado ao usuário autenticado... 
    public List<TaskModel> list(HttpServletRequest request){

        System.out.println("Obtendo todas as tarefas de "+request.getAttribute("name")+" "); 
        return this.taskRepository.findByIdUser((UUID)request.getAttribute("idUser")); 
    }

    @PutMapping("/{id}") // ID passado por parâmetro na URL
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){ 

        System.out.println("ID PASSADO COMO PARAMETRO: "+id.toString());

        var taskExistente = this.taskRepository.findById(id).orElse(null); // Por padrão, o 'findById' retorna um Optional, ou seja, pode existir ou não o objeto... sendo assim, caso não exista retornamos um null mesmo

        if(taskExistente == null){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa inexistente!");
        }else{
            System.out.println("TAREFA EXISTE... verificando...");
                // Caso a tarefa exista mas não seja pertencente ao id do usuário logado...
                if(!taskExistente.getIdUser().equals(request.getAttribute("idUser"))){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão de alterar esta tarefa!");
                }

                Utils.copyNonNullProperties(taskModel, taskExistente); // Alterando APENAS as informações novas obtidas do usuário (em taskModel) no objeto existente no banco (taskExistente)... 

                this.taskRepository.save(taskExistente); // Salvando o objeto existente (agora modificado)
             }
            return ResponseEntity.status(HttpStatus.OK).body(taskExistente); 
    }

    
}
