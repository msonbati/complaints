package com.dash.complaints.controller;

import com.dash.complaints.entity.Complaint;
import com.dash.complaints.service.ComplaintService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ComplaintController.class)
public class TestComplaintController {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper; //maps object to json string

    @MockBean
    ComplaintService service;

    @Test
    @WithMockUser("/test")
    public void addComplaint() throws Exception {
        Complaint complaint = new Complaint("محمود","Mahmoud");
        complaint.setId(1234);
     given(service.create(Mockito.any())).willReturn(complaint);// BDD style given willReturn



        mockMvc.perform(MockMvcRequestBuilders
                .post("/c/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(complaint))
        ).andExpect(status().isCreated());
    }
}
