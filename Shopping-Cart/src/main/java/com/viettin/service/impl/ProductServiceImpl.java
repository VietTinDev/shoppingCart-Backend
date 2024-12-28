package com.viettin.service.impl;

import com.viettin.dto.ProductDto;
import com.viettin.entity.Category;
import com.viettin.entity.Product;
import com.viettin.exception.NotFoundException;
import com.viettin.mapper.MapperConfigs;
import com.viettin.repository.CategoryRepository;
import com.viettin.repository.ProductRepository;
import com.viettin.response.Response;
import com.viettin.service.interfaces.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MapperConfigs mapperConfigs;

    @Override
    @Transactional
    public Response createProduct(Long categoryId, MultipartFile image, String name, String description, BigDecimal price)
            throws SQLException, IOException {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category not found")
        );
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setCategory(category);
        if (image != null && !image.isEmpty()) {
            product.setImageUrl(convertMultipartFileToBlob(image));
        }
        Product savedProduct = productRepository.save(product);
        ProductDto productDto = mapperConfigs.mapProductToDtoBasic(savedProduct);
        return Response.builder()
                .status(200)
                .message("Product successfully created")
                .productDto(productDto)
                .build();

    }

    @Override
    public Response updateProduct(Long productId, Long categoryId, MultipartFile image, String name, String description, BigDecimal price) throws SQLException, IOException {
        Product product = productRepository.findById(productId).orElseThrow(
                ()-> new NotFoundException("Product Not Found")
        );

        Category category = categoryRepository.findById(categoryId).orElseThrow(
                    ()-> new NotFoundException("Category not found")
        );

        if (category != null) product.setCategory(category);
        if (name != null) product.setName(name);
        if (price != null) product.setPrice(price);
        if (description != null) product.setDescription(description);
        if (image != null && !image.isEmpty()) {
            product.setImageUrl(convertMultipartFileToBlob(image));
        }
        Product updatedProduct = productRepository.save(product);
        ProductDto productDto = mapperConfigs.mapProductToDtoBasic(updatedProduct);
        return Response.builder()
                .status(200)
                .productDto(productDto)
                .message("Product updated successfully")
                .build();
    }

    @Override
    public Response deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                ()->new NotFoundException("Product not found")
        );
        productRepository.delete(product);
        return Response.builder()
                .status(200)
                .message("Deleted product successfully")
                .build();
    }

    @Override
    public Response getProductById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                ()->new NotFoundException("Product not found")
        );
        String base64Image = convertBlobToBase64(product.getImageUrl());
        ProductDto productDto = mapperConfigs.mapProductToDtoBasic(product);
        productDto.setImageUrl(base64Image);
        return Response.builder()
                .status(200)
                .message("Product successfully")
                .productDto(productDto)
                .build();
    }

    @Override
    public Response getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDto> productDtos = products.stream()
                .map(product -> {
                    ProductDto dto = mapperConfigs.mapProductToDtoBasic(product);
                    if (product.getImageUrl() != null) {
                        dto.setImageUrl(convertBlobToBase64(product.getImageUrl()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .message("Product list")
                .productDtoList(productDtos)
                .build();
    }

    @Override
    public Response getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        if (products.isEmpty()){
            throw new NotFoundException("No Products Found");
        }
        List<ProductDto> productDtos = products.stream()
                .map(product -> {
                    ProductDto dto = mapperConfigs.mapProductToDtoBasic(product);
                    if (product.getImageUrl() != null) {
                        dto.setImageUrl(convertBlobToBase64(product.getImageUrl()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return Response.builder()
                .status(200)
                .productDtoList(productDtos)
                .build();
    }

    @Override
    public Response searchProduct(String searchValue) {
        List<Product> products = productRepository.findByNameOrDescriptionContaining(searchValue,searchValue);
        if (products.isEmpty()){
            throw new NotFoundException("No Products Found");
        }
        List<ProductDto> productDtos = products.stream()
                .map(product -> {
                    ProductDto dto = mapperConfigs.mapProductToDtoBasic(product);
                    if (product.getImageUrl() != null) {
                        dto.setImageUrl(convertBlobToBase64(product.getImageUrl()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .productDtoList(productDtos)
                .build();
    }

    private String convertBlobToBase64(Blob blob) {
        try {
            if (blob != null) {
                byte[] imageBytes = blob.getBytes(1, (int) blob.length());
                return Base64.getEncoder().encodeToString(imageBytes);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error converting Blob to Base64", e);
        }
        return null;
    }

    private Blob convertMultipartFileToBlob(MultipartFile image) throws SQLException, IOException {
        byte[] imageBytes = image.getBytes();
        return new SerialBlob(imageBytes);
    }

}
