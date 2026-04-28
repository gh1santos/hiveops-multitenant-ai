package com.company.hiveops.domain.repositories;

import com.company.hiveops.domain.model.Execution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExecutionRepository extends JpaRepository<Execution, UUID> {

    List<Execution> findAllByCompanyIdOrderByCreatedAtDesc(UUID companyId);
}