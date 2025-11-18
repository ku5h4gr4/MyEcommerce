package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;


    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        CartItem cartIem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if(cartIem != null){
            throw new ApiException("Product "+product.getProductName()+" already exists in cart");
        }

        if(product.getQuantity() ==0){
            throw new ApiException(product.getProductName()+" is not available");
        }

        if(product.getQuantity() < quantity){
            throw new ApiException("Please, make an order of "+product.getProductName()+" less than or equal to the quantity "+product.getQuantity());
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        cartItemRepository.save(newCartItem);

//        product.setQuantity(product.getQuantity()-quantity);
        cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));

        Cart savedCart = cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(savedCart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());
        return cartDTO;

    }


    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.isEmpty()){
            throw new ApiException("No Cart exist!!");
        }

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

                    List<ProductDTO> products = cart.getCartItems().stream()
                            .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                            .toList();
                    cartDTO.setProducts(products);
                    return cartDTO;
                }).toList();
        return cartDTOS;

    }


    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }


    @Override
    @Transactional
    public CartDTO updateCartProductQuantity(Long productId, Integer quantity) {
        String email = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(email);
        Long cartId = userCart.getCartId();

        Cart cart =  cartRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product =  productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Prouct", "productId", productId));

        if(product.getQuantity()==0){
            throw new ApiException(product.getProductName()+" is not available");
        }

        if(product.getQuantity()< quantity){
            throw new ApiException("Please, make an order of "+product.getProductName()+" less than or equal to the quantity "+product.getQuantity());
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem == null){
            throw new ApiException("Product "+product.getProductName()+" is not available in cart!!!");
        }

        int newQuantity = cartItem.getQuantity() + quantity;
        if(newQuantity < 0){
            throw new ApiException("The resulting quantity cannot be negative");
        }

        if(newQuantity ==0){
            deleteCartProduct(cartId, productId);
        }
        else{
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setQuantity(cartItem.getQuantity()+quantity);
        cartItem.setDiscount(product.getDiscount());
        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice()*quantity));
        cartRepository.save(cart);
        }

        CartItem updatedItem = cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity()==0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO prod = modelMapper.map(item.getProduct(), ProductDTO.class);
            prod.setQuantity(item.getQuantity());
            return prod;
                });
        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }


    @Transactional
    @Override
    public String deleteCartProduct(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem == null){
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice()- cartItem.getProductPrice() * cartItem.getQuantity());

        cartItemRepository.deleteCartItemByCartIdAndProductId(cartId, productId);

        return "Product "+cartItem.getProduct().getProductName()+" is removed from the cart!!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart","cartId",cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId",productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItem ==null){
            throw new ApiException("Product "+product.getProductName()+" is not available in the cart!!");
        }

        // if we update the product price then we need to remove the old price from cart and add new price
        double cartPrice = cart.getTotalPrice() - cartItem.getProductPrice() * cartItem.getQuantity();

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartItem.getProductPrice() * cartItem.getQuantity());

        cartItemRepository.save(cartItem);

    }


    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;

    }
}
