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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Value("${image.base.url}")
    private String imageBaseUrl;


    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    private String constructImageUrl(String imageName) {
        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
    }

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository
                            .findById(categoryId)
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
        if(product.getDiscount() == null) product.setDiscount(0.0);
        //setUser
        if(product.getSpecialPrice() == null){ //auto specialPrice
            Double specialPrice = product.getPrice() - ( product.getDiscount() * 0.01 * product.getPrice() );
            product.setSpecialPrice(specialPrice);
        }

        Product addedProduct = productRepository.save(product);
        return modelMapper.map(addedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse findAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                              Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Specification<Product> spec = (root, cq, cb) -> cb.conjunction();

        if ( category != null && !category.isEmpty() ) {
            spec = spec.and((root, cq, cb) ->
                            cb.like(root.get("category").get("name"), category));
        }
        if( keyword != null && !keyword.isEmpty() ) {
            spec = spec.and( (root, cq, cb) ->
                              cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
        }

        Page<Product> productPage =  productRepository.findAll(spec, pageDetails);
        List<Product> products = productPage.getContent();

        if(products.isEmpty())  throw new APIException("NO PRODUCTS HAVE BEEN ADDED YET");

        List<ProductDTO> productDTOS = products.stream()
                                               .map(p -> {
                                                   ProductDTO dto = modelMapper.map(p, ProductDTO.class);
                                                   dto.setImage(constructImageUrl(p.getImage()));
                                                   return dto;
                                               })
                                               .toList();

        return ProductResponse.builder()
                              .totalElements(productPage.getTotalElements())
                              .pageSize(productPage.getSize())
                              .pageNumber(productPage.getNumber())
                              .totalPages(productPage.getTotalPages())
                              .lastPage(productPage.isLast())
                              .content(productDTOS)
                              .build();
    }

    @Override
    public ProductResponse findProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
                                                  String sortOrder) {
        Category category = categoryRepository
                            .findById(categoryId)
                            .orElseThrow( () -> new ResourceNotFoundException("Category", "id", categoryId) );

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                              Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        List<Product> products = productPage.getContent();

        if(products.isEmpty())  throw new APIException(category.getName() + " doesn't have any products yet");

        List<ProductDTO> productDTOS = products.stream()
                                               .map( p -> modelMapper.map(p, ProductDTO.class) )
                                               .toList();

        return ProductResponse.builder()
                              .totalElements(productPage.getTotalElements())
                              .pageSize(productPage.getSize())
                              .pageNumber(productPage.getNumber())
                              .totalPages(productPage.getTotalPages())
                              .lastPage(productPage.isLast())
                              .content(productDTOS)
                              .build();
    }

}
