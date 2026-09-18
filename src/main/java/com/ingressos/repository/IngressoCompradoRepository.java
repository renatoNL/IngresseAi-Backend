package com.ingressos.repository;

import com.ingressos.model.IngressoComprado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngressoCompradoRepository extends JpaRepository<IngressoComprado, Long> {
    List<IngressoComprado> findByCompradorIdOrderByIdAsc(Long compradorId);

    Optional<IngressoComprado> findByIdAndCompradorId(Long id, Long compradorId);
}