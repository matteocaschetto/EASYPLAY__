package com.epicode.EASYPLAY.repository;

import com.epicode.EASYPLAY.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByCreatoreId(Long creatoreId);
}
