package com.eska.evenity.service;

import java.util.List;
import java.util.Optional;

import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.request.VendorRequest;
import com.eska.evenity.dto.response.VendorResponse;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.entity.Vendor;
import org.springframework.data.domain.Page;

public interface VendorService {
    Vendor createVendor(Vendor vendor, UserCredential userCredential);
    Page<VendorResponse> getAllVendor(PagingRequest pagingRequest);
    Page<VendorResponse> getAllActiveVendor(PagingRequest pagingRequest);
    Page<VendorResponse> getApprovedVendor(PagingRequest pagingRequest);
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
