package br.com.adrianovieira.todolist.task;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
    List<TaskModel> findByIdUser(UUID idUser);
    // TaskModel findByIdAndByIdUser(UUID id, UUID idUser);     // Só um exemplo de como é possível escrever um método na interface sem precisar implementar esse método, contanto que os nomes das variáveis na nomenclatura do método estejam corretos... LINDO
}
