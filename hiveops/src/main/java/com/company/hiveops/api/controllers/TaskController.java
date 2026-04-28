package com.company.hiveops.api.controllers;

import com.company.hiveops.api.dtos.CreateTaskRequest;
import com.company.hiveops.application.services.TaskService;
import com.company.hiveops.domain.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody CreateTaskRequest request) {
        return taskService.createTask(
                request.title(),
                request.description(),
                request.priority(),
                request.agentId()
        );
    }

    @GetMapping
    public List<Task> listTasks() {
        return taskService.listCompanyTasks();
    }
}
