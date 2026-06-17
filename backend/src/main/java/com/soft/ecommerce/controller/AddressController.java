package com.soft.ecommerce.controller;

import com.soft.ecommerce.payload.AddressDTO;
import com.soft.ecommerce.service.api.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ecomApi")
public class AddressController { //finished

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> addAddress(@Valid @RequestBody AddressDTO addressDTO){

        AddressDTO savedAddressDTO = addressService.addAddress(addressDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAddressDTO);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> findAllAddresses(){

        List<AddressDTO> addressList = addressService.findAllAddresses();
        return ResponseEntity.status(HttpStatus.OK).body(addressList);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> findAddressByAddressId(@PathVariable Long addressId){

        AddressDTO addressDTO = addressService.findAddressByAdressId(addressId);
        return ResponseEntity.status(HttpStatus.OK).body(addressDTO);
    }


    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> findAllUserAddresses(){

        List<AddressDTO> addressList = addressService.findAllUserAddresses();
        return ResponseEntity.status(HttpStatus.OK).body(addressList);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId
            , @RequestBody AddressDTO addressDTO){

        AddressDTO updatedAddress = addressService.updateAddress(addressId, addressDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedAddress);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){

        String status = addressService.deleteAddress(addressId);
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }
}
