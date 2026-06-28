package com.aibilling.catalog.model;

import com.aibilling.audit.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing Catalog Category master data.
 * Supports self-referencing recursive parent-child hierarchy.
 */
@Entity
@Table(name = "catalog_categories")
@SQLDelete(sql = "UPDATE catalog_categories SET status = 'DELETED' WHERE id = ? and version = ?")
@SQLRestriction("status != 'DELETED'")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogCategory extends BaseEntity {

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_category_id")
    private CatalogCategory parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CatalogCategory> subCategories = new ArrayList<>();

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CatalogCategory getParentCategory() { return parentCategory; }
    public void setParentCategory(CatalogCategory parentCategory) { this.parentCategory = parentCategory; }
    public List<CatalogCategory> getSubCategories() { return subCategories; }
    public void setSubCategories(List<CatalogCategory> subCategories) { this.subCategories = subCategories; }
}
