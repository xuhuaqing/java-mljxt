package com.example.springbootdemo.hardware.dao;

import com.example.springbootdemo.hardware.ProjectCategory;
import com.example.springbootdemo.hardware.ProjectItem;

import java.util.List;
import java.util.Optional;

public interface ProjectCatalogDao {
    List<ProjectItem> findAllProjects();

    List<ProjectCategory> findAllProjectCategories();

    Optional<ProjectItem> findByProjectName(String rawName);
}
