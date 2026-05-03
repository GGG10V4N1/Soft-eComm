package com.soft.ecommerce.service;

import com.soft.ecommerce.exception.APIException;
import com.soft.ecommerce.exception.ResourceNotFoundException;
import com.soft.ecommerce.model.Category;
import com.soft.ecommerce.model.Product;
import com.soft.ecommerce.model.User;
import com.soft.ecommerce.payload.ProductDTO;
import com.soft.ecommerce.payload.ProductResponse;
import com.soft.ecommerce.repository.CategoryRepository;
import com.soft.ecommerce.repository.ProductRepository;
import com.soft.ecommerce.utils.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;
    private final AuthUtil authUtil;

    @Value("${image.base.url}")
    private String imageBaseUrl;

    @Value("${project.image}")
    private String path;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
                              ModelMapper modelMapper, FileService fileService, AuthUtil authUtil) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
        this.authUtil = authUtil;
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
        product.setUser(authUtil.loggedInUser());
        product.setImage("default.png");
        product.setCategory(category);
        if(product.getDiscount() == null) product.setDiscount(0.0);
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
    public ProductResponse findProductsByCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, Long categoryId) {
        Category category = categoryRepository
                            .findById(categoryId)
                            .orElseThrow( () -> new ResourceNotFoundException("Category", "id", categoryId) );

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                              Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        List<Product> products = productPage.getContent();

        if(products.isEmpty())  throw new APIException("Category" + category.getName() + " doesn't have any products yet");

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

    @Override
    public ProductResponse findProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                              Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageDetails);
        List<Product> products = productPage.getContent();

        if(products.isEmpty())  throw new APIException("No products found with keyword: " + keyword);

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

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product foundedProduct = productRepository
                                 .findById(productId)
                                 .orElseThrow( () -> new ResourceNotFoundException("Product", "id", productId) );

        Product prodChanges = modelMapper.map(productDTO, Product.class);

        foundedProduct.setName(prodChanges.getName());
        foundedProduct.setDescription(prodChanges.getDescription());
        foundedProduct.setStock(prodChanges.getStock());
        foundedProduct.setPrice(prodChanges.getPrice());
        foundedProduct.setDiscount(prodChanges.getDiscount());
        foundedProduct.setSpecialPrice(prodChanges.getSpecialPrice());

        Product updatedProduct = productRepository.save(foundedProduct);
        //carts

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product foundedProduct = productRepository
                                 .findById(productId)
                                 .orElseThrow( () -> new ResourceNotFoundException("Product", "id", productId) );
        //carts

        productRepository.delete(foundedProduct);
        return modelMapper.map(foundedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product foundedProduct = productRepository
                                 .findById(productId)
                                 .orElseThrow( () -> new ResourceNotFoundException("Product", "id", productId) );
        String fileName = fileService.uploadImage(path, image);
        foundedProduct.setImage(fileName);
        Product updatedProduct = productRepository.save(foundedProduct);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse findAllProductsForAdmin(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortBy.equalsIgnoreCase("asc") ?
                              Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
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
    public ProductResponse findAllProductsForSeller(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                              Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        User user = authUtil.loggedInUser();
        Page<Product> productPage = productRepository.findByUser(user, pageDetails);
        List<Product> products = productPage.getContent();
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

}
