package br.insper.iam.evento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> getEventos() {
        return eventoRepository.findAll();
    }

    public Evento saveEvento(Evento evento) {
        return eventoRepository.save(evento);
    }
}
