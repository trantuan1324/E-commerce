package com.rabbyte.sbecom.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {
    private Long categoryId;

    @NotBlank(message = "Category name is required")
    @Size(min = 3, max = 40, message = "Category name must be between 3 and 40 characters")
    private String categoryName;
}
