package com.soft.ecommerce.service.api;

import com.soft.ecommerce.payload.AddressDTO;

import java.util.List;

public interface AddressService {

    AddressDTO addAddress(AddressDTO addressDTO);
    List<AddressDTO> findAllAddresses();
    AddressDTO findAddressByAdressId(Long addressId);
    List<AddressDTO> findAllUserAddresses();
    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);
    String deleteAddress(Long addressId);
}
