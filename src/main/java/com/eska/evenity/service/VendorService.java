package com.eska.evenity.service;

import java.util.List;
import java.util.Optional;

import com.eska.evenity.dto.request.VendorRequest;
import com.eska.evenity.dto.response.VendorResponse;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.entity.Vendor;

public interface VendorService {
    Vendor createVendor(Vendor vendor, UserCredential userCredential);
    List<VendorResponse> getAllVendor();
    List<VendorResponse> getAllActiveVendor();
    List<VendorResponse> getApprovedVendor();
    VendorResponse getVendorById(String id);
    Vendor getVendorByUserId(String id);
    Vendor getVendorUsingId(String id);
//    VendorWithProductsResponse getVendorWithProducts(String id);
    VendorResponse updateVendor(String id, VendorRequest request);
    VendorResponse approveStatusVendor(String id);
    VendorResponse rejectStatusVendor(String id);
    void softDeleteById(String id);
    void upVoteVendor(String id);
    void downVoteVendor(String id);
    List<Vendor> searchVendor(String name);
}
