package com.quality.complaints.service;

import com.quality.complaints.exceptions.InvalidComplaintException;
import com.quality.complaints.model.Complaint;
import com.quality.complaints.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

public class ComplaintServiceTest {
@Mock
   private ComplaintRepository repository;

@InjectMocks
   private ComplaintServiceImpl service;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
     //  service =  new ComplaintServiceImpl(repository);
    }
    @Test
    public void addComplaintWithDescription()throws Exception{
        Complaint complaint = new Complaint("Test complaint");
        given(repository.save(complaint))
                .willReturn(new Complaint("test"));
        Complaint complaint1 = service.add(complaint);
        assertNotNull(complaint1);
        assertEquals("test",complaint1.getDescription());

    }
    @Test
    public void addComplaintWithoutDescription() {
        Complaint complaint = new Complaint();
   //     given(repository.save(complaint))
     //           .willReturn(new Complaint(""));
      Exception e =  assertThrows(InvalidComplaintException.class ,()-> service.add(complaint));
      assertEquals("Invalid Complaint Description!",e.getMessage());


    }
}
