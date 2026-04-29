package com.soft.ecommerce.service;

import com.soft.ecommerce.exception.APIException;
import com.soft.ecommerce.exception.ResourceNotFoundException;
import com.soft.ecommerce.model.Category;
import com.soft.ecommerce.model.Product;
import com.soft.ecommerce.payload.ProductDTO;
import com.soft.ecommerce.payload.ProductResponse;
import com.soft.ecommerce.repository.CategoryRepository;
import com.soft.ecommerce.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        //product.setImage("default.png");
        product.setCategory(category);
        //setUser
        //Double specialPrice = product.getPrice() - ( product.getDiscount() * 0.01 * product.getPrice() );
        //product.setSpecialPrice(specialPrice); //could be set on the requestbody
        Product addedProduct = productRepository.save(product);
        return modelMapper.map(addedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                              Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage =  productRepository.findAll(pageDetails);
        List<Product> products = productPage.getContent();

        if(products.isEmpty()){
            throw new APIException("NO PRODUCTS HAVE BEEN ADDED YET");
        }

        List<ProductDTO> productDTOS = products.stream()
                                               .map(p -> modelMapper.map(p, ProductDTO.class))
                                               .toList();

        return new ProductResponse(productDTOS,
                                   productPage.getTotalElements(),
                                   productPage.getSize(),
                                   productPage.getNumber(),
                                   productPage.getTotalPages(),
                                   productPage.isLast());
    }
}
