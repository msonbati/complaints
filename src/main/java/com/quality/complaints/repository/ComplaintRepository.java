package com.quality.complaints.repository;

import com.quality.complaints.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository  extends JpaRepository<Complaint ,Long> {
}
