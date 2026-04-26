package com.example.springbootdemo.hardware;

import java.util.List;

public record ProjectCategory(
        String categoryName,
        List<ProjectItem> projects
) {
}
