package com.soft.ecommerce.service.impl;

import com.soft.ecommerce.exception.ResourceNotFoundException;
import com.soft.ecommerce.model.Address;
import com.soft.ecommerce.model.User;
import com.soft.ecommerce.payload.AddressDTO;
import com.soft.ecommerce.repository.AddressRepository;
import com.soft.ecommerce.repository.UserRepository;
import com.soft.ecommerce.service.api.AddressService;
import com.soft.ecommerce.utils.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper modelMapper, UserRepository userRepository, AuthUtil authUtil) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.authUtil = authUtil;
    }

    @Override
    public AddressDTO addAddress(AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        Address address = modelMapper.map(addressDTO, Address.class);
        user.addAddress(address);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> findAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
                        .map(address -> modelMapper.map(address, AddressDTO.class))
                        .toList();
    }

    @Override
    public AddressDTO findAddressByAdressId(Long addressId) {
        Address address = addressRepository.findById(addressId)
                                           .orElseThrow(() -> new ResourceNotFoundException("Address","AddressId",addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> findAllUserAddresses() {
        User user = authUtil.loggedInUser();
        List<Address> addresses = user.getAddresses();
        return addresses.stream()
                        .map(address -> modelMapper.map(address, AddressDTO.class))
                        .toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address savedAddress = addressRepository.findById(addressId)
                                                .orElseThrow(() -> new ResourceNotFoundException("Address","AddressId",addressId));

        savedAddress.setCity(addressDTO.getCity());
        savedAddress.setState(addressDTO.getState());
        savedAddress.setCountry(addressDTO.getCountry());
        savedAddress.setStreet(addressDTO.getStreet());
        savedAddress.setPincode(addressDTO.getPincode());
        savedAddress.setBuildingName(addressDTO.getBuildingName());

        Address updatedAddress = addressRepository.save(savedAddress);

        User user = savedAddress.getUser();

        user.getAddresses().removeIf(address -> address.getId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address savedAddress = addressRepository.findById(addressId)
                                                .orElseThrow(() -> new ResourceNotFoundException("Address","AddressId",addressId));
        User user = savedAddress.getUser();
        user.getAddresses().removeIf(address -> address.getId().equals(addressId));
        userRepository.save(user);
        addressRepository.delete(savedAddress);

        return "Address deleted successfully with addressId: " + addressId;
    }
}
