package com.dash.complaints.repository;

import com.dash.complaints.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint,Integer> {

}
