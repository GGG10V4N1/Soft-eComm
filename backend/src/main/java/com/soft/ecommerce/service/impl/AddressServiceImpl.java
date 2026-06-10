package com.soft.ecommerce.service.impl;

import com.soft.ecommerce.payload.AddressDTO;
import com.soft.ecommerce.service.api.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {


    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        return null;
    }

    @Override
    public List<AddressDTO> getAddresses() {
        return List.of();
    }

    @Override
    public AddressDTO getAddressesById(Long addressId) {
        return null;
    }

    @Override
    public List<AddressDTO> getUserAddresses() {
        return List.of();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        return null;
    }

    @Override
    public String deleteAddress(Long addressId) {
        return "";
    }
}
