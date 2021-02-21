package com.quality.complaints.service;

import com.quality.complaints.exceptions.InvalidComplaintException;
import com.quality.complaints.model.Complaint;

public interface ComplaintService {
    public Complaint add(Complaint complaint)throws InvalidComplaintException;
}
