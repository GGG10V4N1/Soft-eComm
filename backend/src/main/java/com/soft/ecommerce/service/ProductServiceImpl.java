package com.soft.ecommerce.service;

import com.soft.ecommerce.exception.APIException;
import com.soft.ecommerce.exception.ResourceNotFoundException;
import com.soft.ecommerce.model.Category;
import com.soft.ecommerce.model.Product;
import com.soft.ecommerce.payload.ProductDTO;
import com.soft.ecommerce.repository.CategoryRepository;
import com.soft.ecommerce.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDTO add(Long categoryId, ProductDTO productDTO) {
        Category category = this.categoryRepository.findById(categoryId)
                            .orElseThrow( () -> new ResourceNotFoundException("Category", "id", categoryId) );

        List<Product> products = category.getProducts();

        boolean isProductPresent = products.stream()
                                           .anyMatch(p -> p.getName().equalsIgnoreCase(productDTO.getName()) );

        if(isProductPresent) {
            throw new APIException("Product with name " + productDTO.getName() + " already exists in category " + category.getName());
        }

        Product product = modelMapper.map(productDTO, Product.class);
        product.setImage("default.png");
        product.setCategory(category);
        //setUser
        Double specialPrice = product.getPrice() - ( product.getDiscount() * 0.01 * product.getPrice() );
        product.setSpecialPrice(specialPrice); //could be set on the requestbody
        Product addedProduct = productRepository.save(product);
        return modelMapper.map(addedProduct, ProductDTO.class);
    }
}
