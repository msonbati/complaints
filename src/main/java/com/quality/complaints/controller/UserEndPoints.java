package com.quality.complaints.controller;

import com.quality.complaints.exceptions.InvalidComplaintException;
import com.quality.complaints.model.Complaint;
import com.quality.complaints.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class UserEndPoints {
    @Autowired
    ComplaintService complaintService;

    @GetMapping("/submit")
    public String doView(Model model){
        Complaint complaint = new Complaint();
        model.addAttribute("complaint" ,complaint);
        return "submit";
    }

    @GetMapping("/audio")
    public String audio(Model model){

        return "audio";
    }
    @PostMapping("/upload")
    public String upload(@RequestParam("audio_data") MultipartFile file){
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());
        System.out.println("upload called!");
        return "audio";
    }


    @PostMapping("/submit")
    public String submitComplaint(@ModelAttribute Complaint complaint , Model model){
        try {
            complaint =    complaintService.add(complaint);
            model.addAttribute("complaint",complaint);
            System.out.println(complaint);
        } catch (InvalidComplaintException e) {
            e.printStackTrace();
        }
        return "submit";
    }
}
