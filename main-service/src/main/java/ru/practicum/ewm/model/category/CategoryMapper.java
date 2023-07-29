package ru.practicum.ewm.model.category;

import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class CategoryMapper {
    public Category toEntityFromDto(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setId(categoryDto.getId());
        return category;
    }

    public CategoryDto toDtoFromEntity(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(category.getName());
        categoryDto.setId(category.getId());
        return categoryDto;
    }

    public List<CategoryDto> toListDtoFromListEntity(List<Category> listEntity) {
        List<CategoryDto> listDto = new ArrayList<>();
        for (Category category:listEntity
        ) {
            listDto.add(toDtoFromEntity(category));
        }
        return listDto;
    }
}
