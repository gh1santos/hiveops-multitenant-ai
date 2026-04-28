package com.company.hiveops.domain.exceptions;

import java.util.UUID;

public class BudgetExceededException extends DomainException {
    public BudgetExceededException(UUID agentId) {
        super(String.format("O agente [%s] excedeu seu orçamento mensal e foi bloqueado.", agentId));
    }
}