package com.company.hiveops.api.controllers;

import com.company.hiveops.api.dtos.CreateCompanyRequest;
import com.company.hiveops.domain.model.Company;
import com.company.hiveops.domain.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyRepository companyRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Company createCompany(@RequestBody CreateCompanyRequest request) {
        Company company = Company.builder()
                .name(request.name())
                .build();
        return companyRepository.save(company);
    }
}
