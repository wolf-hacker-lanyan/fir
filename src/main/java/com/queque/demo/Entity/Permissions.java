package com.queque.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Permissions {
    @Id
    private Long id;
    private String name;  // 权限名称
    private String description;  // 权限描述
    private String permissions;  // 权限字符串，如 "user:create"

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPermission() {
        return permissions;
    }

    public void setPermission(String permissions) {
        this.permissions = permissions;
    }
}
