package com.ecommerce.project.controller;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/cart/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity){
        CartDTO addedProduct = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(addedProduct, HttpStatus.CREATED);
    }


    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        List<CartDTO> allCarts = cartService.getAllCarts();
        return new ResponseEntity<>(allCarts, HttpStatus.FOUND);
    }


    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId, cartId);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }


    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable String operation){
        CartDTO cartDTO = cartService.updateCartProductQuantity(productId,
                operation.equalsIgnoreCase("delete")? -1: 1);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }


    @DeleteMapping("/carts/{cartId}/products/{productId}")
    public ResponseEntity<String> deleteCartProduct(@PathVariable Long cartId,
                                                     @PathVariable Long productId){
        String status = cartService.deleteCartProduct(cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
