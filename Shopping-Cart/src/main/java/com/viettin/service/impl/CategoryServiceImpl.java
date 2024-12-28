package com.viettin.service.impl;

import com.viettin.dto.CategoryDto;
import com.viettin.entity.Category;
import com.viettin.exception.InvalidCredentialsException;
import com.viettin.exception.NotFoundException;
import com.viettin.mapper.MapperConfigs;
import com.viettin.repository.CategoryRepository;
import com.viettin.response.Response;
import com.viettin.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final MapperConfigs mapperConfigs;

    @Override
    public Response createCategory(CategoryDto categoryRequest) {
        Category existingCategory = categoryRepository.findByName(categoryRequest.getName());
        if(existingCategory != null){
            throw new InvalidCredentialsException("Category Existed");
        }
        Category category = mapperConfigs.mapToCategory(categoryRequest);
        Category savedCategory = categoryRepository.save(category);
        CategoryDto categoryDto = mapperConfigs.mapCategoryToDtoBasic(savedCategory);
        return Response.builder()
                .status(200)
                .message("Created Category Successfully")
                .categoryDto(categoryDto)
                .build();
    }

    @Override
    public Response updateCategory(Long categoryId, CategoryDto categoryRequest) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                ()->new NotFoundException("Category Not Found")
        );
        category.setName(categoryRequest.getName());
        Category updatedCategory = categoryRepository.save(category);
        CategoryDto categoryDto = mapperConfigs.mapCategoryToDtoBasic(updatedCategory);
        return Response.builder()
                .status(200)
                .message("Updated Category Successfully")
                .categoryDto(categoryDto)
                .build();
    }

    @Override
    public Response getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new NotFoundException("Category Not Found");
        }
        List<CategoryDto> categoryDtos = categories.stream().map(mapperConfigs::mapCategoryToDtoBasic).collect(Collectors.toList());
        return Response.builder()
                .status(200)
                .categoryDtoList(categoryDtos)
                .build();
    }

    @Override
    public Response getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                ()-> new NotFoundException("Category Not Found By Id "+categoryId)
        );
        CategoryDto categoryDto = mapperConfigs.mapCategoryToDtoBasic(category);
        return Response.builder()
                .status(200)
                .message("Category Found")
                .categoryDto(categoryDto)
                .build();
    }

    @Override
    public Response deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                ()-> new NotFoundException("Category Not Found By Id "+categoryId)
        );
        categoryRepository.delete(category);
        return Response.builder()
                .status(200)
                .message("Deleted Category Successfully")
                .build();
    }
}
