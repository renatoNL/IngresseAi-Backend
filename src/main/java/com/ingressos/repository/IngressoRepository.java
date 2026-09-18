package com.ingressos.repository;

import com.ingressos.model.Ingresso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngressoRepository extends JpaRepository<Ingresso, Long> {
    List<Ingresso> findByEventoIdOrderByIdAsc(Long eventoId);
}