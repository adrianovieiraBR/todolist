package br.com.adrianovieira.todolist.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {
    
    /**
     * Método para identificar, dentre todas as variáveis de um determinado objeto (qualquer objeto de qualquer classe), quais aquelas que estão 
     * nulas. 
    */
    public static String[] getNullPropertyNames(Object source){
        System.out.println("Analisando valores do objeto em 'getNullPropertyNames'... "); 
        final BeanWrapper src = new BeanWrapperImpl(source); // Classe BeanWrapper, utilizada para ler o conteúdo de um objeto e mostrá-lo de diferentes formas

        PropertyDescriptor[] pds = src.getPropertyDescriptors(); // Obtendo do objeto passado dos os descriptors (nomes de variável?)

        Set<String> emptyNames = new HashSet<>();

        for(PropertyDescriptor pd : pds){
            Object srcValue = src.getPropertyValue(pd.getName());
            if(srcValue == null)
             emptyNames.add(pd.getName()); 
        }

        return emptyNames.toArray(new String[emptyNames.size()]); 
    }

    public static void copyNonNullProperties(Object source, Object target){
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source)); 
    }
}
