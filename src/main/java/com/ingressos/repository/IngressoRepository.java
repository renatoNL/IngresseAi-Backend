package com.ingressos.repository;

import com.ingressos.model.Ingresso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngressoRepository extends JpaRepository<Ingresso, Long> {
	java.util.Optional<Ingresso> findByEventoId(Long eventoId);
}