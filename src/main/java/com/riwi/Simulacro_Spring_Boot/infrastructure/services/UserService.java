package com.riwi.Simulacro_Spring_Boot.infrastructure.services;

import java.util.ArrayList;
import java.util.stream.Collectors;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.riwi.Simulacro_Spring_Boot.api.dto.request.UserReq;
import com.riwi.Simulacro_Spring_Boot.api.dto.response.CourseBasicResp;
import com.riwi.Simulacro_Spring_Boot.api.dto.response.UserResp;
import com.riwi.Simulacro_Spring_Boot.domain.entities.Course;
import com.riwi.Simulacro_Spring_Boot.domain.entities.UserEntity;
import com.riwi.Simulacro_Spring_Boot.domain.repositories.UserRepository;
import com.riwi.Simulacro_Spring_Boot.infrastructure.abstract_services.IUserService;
//import com.riwi.Simulacro_Spring_Boot.utils.enums.SortType;
import com.riwi.Simulacro_Spring_Boot.utils.exceptions.BadRequestException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements IUserService{

    // Inyeccion de dependencia
    @Autowired
    private final UserRepository userRepository;

    // Crear
    @Override
    public UserResp create(UserReq request) {

         // Convertir la solicitud a entidad UserEntity
        UserEntity userEntity = this.requestToUser(request);

        // Guardar el usuario en el repositorio y devolver la respuesta
        return this.entityToResponse(this.userRepository.save(userEntity));
    }

    // Obtener solo uno
    @Override
    public UserResp get(Long id) {

        // Buscar el usuario y convertirlo a respuesta
        return this.entityToResponse(this.findId(id));
    }

    // Actualizar
    @Override
    public UserResp update(UserReq request, Long id) {

        // Buscar el usuario existente por ID
        UserEntity userEntity = this.findId(id);

        // Actualizar el usuario con los datos de la solicitud
        userEntity = this.requestToUser(request);
        userEntity.setId(id);

         // Guardar el usuario actualizado en el repositorio y devolver la respuesta
        return this.entityToResponse(this.userRepository.save(userEntity));
    }

    // Eliminar
    @Override
    public void delete(Long id) {

        // Eliminar el usuario del repositorio
        this.userRepository.delete(this.findId(id));
    }

    // Obtener todo
    @Override
    public Page<UserResp> getAll(int page, int size) {

        // Ajustar el número de página si es necesario
        if (page < 0) page = 0;

        // Crear objeto de paginación
        PageRequest pagination = PageRequest.of(page, size);

        // Obtener todos los usuarios paginados y convertirlos a respuestas
        return this.userRepository.findAll(pagination)
                .map(user -> this.entityToResponse(user));     
    }

    // Obtener por id
    @Override
    public UserResp getById(Long id) {

         // Buscar el usuario y convertirlo a respuesta
        return this.entityToResponse(findId(id));
    }

    // Metodos privados
    
    // Convertir entidad UserEntity a respuesta UserResp
    private UserResp entityToResponse(UserEntity entity) {

        UserResp response = new UserResp();

        // Copiar propiedades del usuario a la respuesta
        BeanUtils.copyProperties(entity, response);
        
         // Convertir y asignar cursos si existen
        if (entity.getCourses() != null) {
            
            response.setCourses(entity.getCourses().stream()
                .map(course -> this.courseToResponse(course))
                .collect(Collectors.toList()));

        } else {

            response.setCourses(new ArrayList<>());
        }
        
        return response;
    }
    

     // Para convertir CourseBasicResp a cursos
    private CourseBasicResp courseToResponse(Course entity) {

        return CourseBasicResp.builder()
                .id(entity.getId())
                .courseName(entity.getCourseName())
                .description(entity.getDescription())
                .build();
    }

    // Convertir solicitud UserReq a entidad UserEntity
    private UserEntity requestToUser(UserReq request) {

        return UserEntity.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole())
                .build();
    }

    // Buscar usuario por ID y lanzar excepción si no se encuentra
    private UserEntity findId(Long id) {

        return this.userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("No hay usuarios con ese id"));
    }
}
