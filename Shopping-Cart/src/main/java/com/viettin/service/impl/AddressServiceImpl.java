package com.viettin.service.impl;

import com.viettin.dto.AddressDto;
import com.viettin.entity.Address;
import com.viettin.entity.User;
import com.viettin.mapper.MapperConfigs;
import com.viettin.repository.AddressRepository;
import com.viettin.response.Response;
import com.viettin.service.interfaces.AddressService;
import com.viettin.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;
    private final MapperConfigs mapperConfigs;

    @Override
    public Response saveAndUpdateAddress(AddressDto addressDto) {
        User user = userService.getLoginUser();
        Address address = user.getAddress();

        if (address == null) {
            address = new Address();
            address.setUser(user);
        }

        // Log giá trị của addressDto trước khi set vào address
        System.out.println("AddressDto: " + addressDto);

        if (addressDto.getStreet() != null) address.setStreet(addressDto.getStreet());
        if (addressDto.getCity() != null) address.setCity(addressDto.getCity());
        if (addressDto.getState() != null) address.setState(addressDto.getState());
        if (addressDto.getZipCode() != null) address.setZipCode(addressDto.getZipCode());
        if (addressDto.getCountry() != null) address.setCountry(addressDto.getCountry());

        // Log đối tượng Address sau khi được cập nhật
        System.out.println("Updated Address: " + address);

        addressRepository.save(address);

        String message = (address.getId() == null) ? "Address successfully created" : "Address successfully updated";
        return Response.builder()
                .status(200)
                .message(message)
                .build();
    }
}
