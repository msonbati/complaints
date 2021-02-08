package com.dash.complaints.controller;


import com.dash.complaints.entity.Complaint;
import com.dash.complaints.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Controller
@RequestMapping("/c")
public class ComplaintController {
    @Autowired
    ComplaintService service;


    @GetMapping("/home")
    public String home() {

        return "add.html";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Complaint> add(@RequestBody Complaint complaint) {
        System.out.println(complaint);
        complaint = service.create(complaint);
        //return   ResponseEntity.ok().build();

        return ResponseEntity.created(
                URI.create(
                        String.format("/complaint/%s", complaint.getId())
                )
        ).body(complaint);
    }
    @GetMapping("/add2")
    @ResponseBody
    public ResponseEntity<Complaint> add() {
        Complaint complaint = new Complaint("محمود","Mahmoud");
        complaint = service.create(complaint);
        //return   ResponseEntity.ok().build();

        return ResponseEntity.created(
                URI.create(
                        String.format("/complaint/%s", complaint.getId())
                )
        ).body(complaint);
    }

}
