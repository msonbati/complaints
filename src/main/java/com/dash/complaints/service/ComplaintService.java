package com.dash.complaints.service;

import com.dash.complaints.entity.Complaint;
import com.dash.complaints.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplaintService {
@Autowired
ComplaintRepository repository;
    public Complaint create(Complaint complaint) {

      return   repository.save(complaint);

    }
}
