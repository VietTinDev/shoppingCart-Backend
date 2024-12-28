package com.viettin.service.interfaces;

import com.viettin.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public interface ProductService {
    Response createProduct(Long categoryId, MultipartFile image, String name, String description, BigDecimal price) throws SQLException, IOException;
    Response updateProduct(Long productId, Long categoryId, MultipartFile image, String name, String description, BigDecimal price) throws SQLException, IOException;
    Response deleteProduct(Long productId);
    Response getProductById(Long productId);
    Response getAllProducts();
    Response getProductsByCategory(Long categoryId);
    Response searchProduct(String searchValue);
}
