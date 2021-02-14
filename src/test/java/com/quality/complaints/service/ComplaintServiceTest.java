package com.quality.complaints.service;

import com.quality.complaints.model.Complaint;
import com.quality.complaints.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

public class ComplaintServiceTest {
    @Mock
    ComplaintRepository repository;

    @InjectMocks
    ComplaintService service;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void addComplaint()throws Exception{
        Complaint complaint = new Complaint();
        given(repository.save(complaint))
                .willReturn(new Complaint("test"));
        Complaint complaint1 = service.add(new Complaint());
        assertNotNull(complaint1);
        assertEquals("test",complaint1.getDescription());

    }
}
