package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address  = modelMapper.map(addressDTO, Address.class);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddress() {
        List<Address> addresses = addressRepository.findAll();
        if(addresses.isEmpty()){
            throw new ApiException("No address is present");
        }
        List<AddressDTO> addressDTOS = addresses.stream()
                .map(addr -> modelMapper.map(addr, AddressDTO.class)).toList();
        return addressDTOS;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
    Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address","addressId",addressId));
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressesByLoggedUser(User user) {
        List<Address> addressList = addressRepository.findAddressByUser(user);
        if(addressList.isEmpty()){
            throw new ApiException("No address are present for current user");
        }
        List<AddressDTO> addressDTOS = addressList.stream()
                .map(addr -> modelMapper.map(addr, AddressDTO.class)).toList();
        return addressDTOS;
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address addressInDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address","addressId",addressId));

        addressInDB.setCity(addressDTO.getCity());
        addressInDB.setPincode(addressDTO.getPincode());
        addressInDB.setCountry(addressDTO.getCountry());
        addressInDB.setState(addressDTO.getState());
        addressInDB.setStreet(addressDTO.getStreet());
        addressInDB.setBuildingName(addressDTO.getBuildingName());
        Address updatedAddr = addressRepository.save(addressInDB);

        User user = addressInDB.getUser();
        user.getAddresses().removeIf(addr -> addr.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddr);
        userRepository.save(user);
        return modelMapper.map(updatedAddr, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId",addressId));

        User user = address.getUser();
        user.getAddresses().removeIf(addr -> addr.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(address);
        return "Address with id "+addressId+" is deleted successfully";
    }
}
