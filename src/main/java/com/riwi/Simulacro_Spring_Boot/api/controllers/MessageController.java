package com.riwi.Simulacro_Spring_Boot.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.Simulacro_Spring_Boot.api.dto.request.MessageReq;
import com.riwi.Simulacro_Spring_Boot.api.dto.response.MessageResp;
import com.riwi.Simulacro_Spring_Boot.infrastructure.abstract_services.IMessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@Tag(name = "Messages")
@RequestMapping(path = "/message")
public class MessageController {

    // Inyeccion de dependencia 
    @Autowired
    private final IMessageService messageService;

    // Obtener todo
    @GetMapping
    public ResponseEntity<Page<MessageResp>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
            ) {
        return ResponseEntity.ok(this.messageService.getAll(page, size));
    }

    // Obtener por id
    @GetMapping(path = "/{message_id}")
    public ResponseEntity<MessageResp> get(
            @PathVariable Long message_id) {

        return ResponseEntity.ok(this.messageService.get(message_id));
    }

    // Crear 
    @PostMapping
    @Operation(
        summary = "Send Message",
        description = "Send a message from one user to another within a course."
    )
    public ResponseEntity<MessageResp> create(
            @Validated @RequestBody MessageReq request) {

        return ResponseEntity.ok(this.messageService.create(request));
    }

    // Actualizar
    @PutMapping(path = "/{message_id}")
    public ResponseEntity<MessageResp> update(
            @Validated @RequestBody MessageReq request,
            @PathVariable Long message_id) {

        return ResponseEntity.ok(this.messageService.update(request, message_id));
    }

    // Eliminar 
    @DeleteMapping(path = "/{message_id}")
    public ResponseEntity<Void> delete(@PathVariable Long message_id) {
        
        this.messageService.delete(message_id);
        return ResponseEntity.noContent().build();
    }
    
}
