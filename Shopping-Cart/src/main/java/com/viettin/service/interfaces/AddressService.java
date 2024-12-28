package com.viettin.service.interfaces;

import com.viettin.dto.AddressDto;
import com.viettin.response.Response;

public interface AddressService {
    Response saveAndUpdateAddress(AddressDto addressDto);

}
