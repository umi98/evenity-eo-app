package com.eska.evenity.service;

import com.eska.evenity.dto.response.VendorResponse;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.entity.Vendor;

import java.util.List;

public interface VendorService {
    Vendor createVendor(Vendor vendor, UserCredential userCredential);
    List<VendorResponse> getAllVendor();
    VendorResponse getVendorById(String id);
    VendorResponse getVendorByUserId(String id);
}
