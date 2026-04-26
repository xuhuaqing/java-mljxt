package com.example.springbootdemo.hardware.dao;

import com.example.springbootdemo.hardware.ProjectCategory;
import com.example.springbootdemo.hardware.ProjectItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryProjectCatalogDao implements ProjectCatalogDao {

    private static final List<ProjectItem> PROJECTS = List.of(
            new ProjectItem(1, "0x01", "肩颈深度解压"),
            new ProjectItem(2, "0x02", "背脊通衡养护"),
            new ProjectItem(3, "0x03", "腰骶温养呵护"),
            new ProjectItem(4, "0x04", "胸肋呼吸舒展"),
            new ProjectItem(5, "0x05", "腹区温蕴舒压"),
            new ProjectItem(6, "0x06", "臀腿活力焕新"),
            new ProjectItem(7, "0x07", "小腿轻盈舒缓"),
            new ProjectItem(8, "0x08", "足踝稳泰调理"),
            new ProjectItem(9, "0x09", "上臂肱桡释能"),
            new ProjectItem(10, "0x0A", "前臂腕指松解"),
            new ProjectItem(11, "0x0B", "直角肩养成"),
            new ProjectItem(12, "0x0C", "天鹅臂精雕"),
            new ProjectItem(13, "0x0D", "手臂纤细雕刻"),
            new ProjectItem(14, "0x0E", "美背塑形"),
            new ProjectItem(15, "0x0F", "腰际线精雕"),
            new ProjectItem(16, "0x10", "马甲线雕刻"),
            new ProjectItem(17, "0x11", "蜜桃臀塑造"),
            new ProjectItem(18, "0x12", "大腿内侧紧致"),
            new ProjectItem(19, "0x13", "小腿线条优化"),
            new ProjectItem(20, "0x14", "跟腱显现雕刻"),
            new ProjectItem(21, "0x15", "力量重塑训练"),
            new ProjectItem(22, "0x16", "爆发力激活训练"),
            new ProjectItem(23, "0x17", "耐力强化训练"),
            new ProjectItem(24, "0x18", "协调敏捷训练"),
            new ProjectItem(25, "0x19", "稳定柔韧训练")
    );

    private static final List<ProjectCategory> PROJECT_CATEGORIES = List.of(
            new ProjectCategory("日常训练", PROJECTS.subList(0, 10)),
            new ProjectCategory("塑形紧致", PROJECTS.subList(10, 20)),
            new ProjectCategory("运动表现", PROJECTS.subList(20, 25))
    );

    @Override
    public List<ProjectItem> findAllProjects() {
        return PROJECTS;
    }

    @Override
    public List<ProjectCategory> findAllProjectCategories() {
        return PROJECT_CATEGORIES;
    }

    @Override
    public Optional<ProjectItem> findByProjectName(String rawName) {
        if (rawName == null) {
            return Optional.empty();
        }
        String target = rawName.strip();
        return PROJECTS.stream()
                .filter(p -> p.name().equals(target))
                .findFirst();
    }
}
