package com.quality.complaints.service;

import com.quality.complaints.model.Complaint;
import com.quality.complaints.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplaintServiceImpl implements ComplaintService{
    @Autowired
    ComplaintRepository repository;

    @Override
    public Complaint add(Complaint complaint) {
        return null;
    }
}
