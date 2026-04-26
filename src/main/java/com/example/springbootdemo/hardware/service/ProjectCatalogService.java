package com.example.springbootdemo.hardware.service;

import com.example.springbootdemo.hardware.ProjectCategory;
import com.example.springbootdemo.hardware.ProjectItem;
import com.example.springbootdemo.hardware.dao.ProjectCatalogDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectCatalogService {

    private final ProjectCatalogDao projectCatalogDao;

    public ProjectCatalogService(ProjectCatalogDao projectCatalogDao) {
        this.projectCatalogDao = projectCatalogDao;
    }

    public List<ProjectItem> getProjects() {
        return projectCatalogDao.findAllProjects();
    }

    public List<ProjectCategory> getProjectCategories() {
        return projectCatalogDao.findAllProjectCategories();
    }

    public Optional<ProjectItem> findByProjectName(String rawName) {
        return projectCatalogDao.findByProjectName(rawName);
    }
}
