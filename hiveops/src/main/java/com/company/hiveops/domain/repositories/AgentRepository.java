package com.company.hiveops.domain.repositories;

import com.company.hiveops.domain.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentRepository extends JpaRepository<Agent, UUID> {
    List<Agent> findAllByCompanyId(UUID companyId);
    Optional<Agent> findByIdAndCompanyId(UUID id, UUID companyId);
}
