package com.arturober.springeventos.controladores;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arturober.springeventos.entidades.Evento;
import com.arturober.springeventos.entidades.User;
import com.arturober.springeventos.entidades.dto.EventoDto;
import com.arturober.springeventos.entidades.dto.InsertEventoDto;
import com.arturober.springeventos.servicios.EventosService;

@RestController
@RequestMapping("/eventos")
public class EventosController {
	@Autowired
	private EventosService eventosService;
	
	@GetMapping() 
    public Map<String,Object> getEventos() {
		Map<String, Object> resp = new HashMap<>();
		resp.put("eventos", eventosService.findAll().stream().map(e -> EventoDto.fromEntity(e)).collect(Collectors.toList()));
        return resp;
    }
	
	@GetMapping("{id}")
    public ResponseEntity<Map<String, Object>> getEvento(@PathVariable Long id) {
		try {
			EventoDto evento = EventoDto.fromEntity(eventosService.find(id));
			Map<String, Object> resp = new HashMap<>();
			resp.put("evento", evento);
			return ResponseEntity.ok().body(resp);
		} catch(NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
    }
	
	@PostMapping()
	public ResponseEntity<EventoDto> postEvento(Authentication authentication, @RequestBody InsertEventoDto eventoDto) {
		Long idCreator = ((Integer)authentication.getCredentials()).longValue();
		Evento e = eventosService.insert(eventoDto, idCreator);
		return ResponseEntity.status(HttpStatus.CREATED).body(EventoDto.fromEntity(e));
	}
	
	@DeleteMapping("{id}")
	 public ResponseEntity<Void> deleteEvento(@PathVariable Long id) {
		try {
			eventosService.delete(id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} catch (EmptyResultDataAccessException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
    }
}
