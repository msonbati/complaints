package com.quality.complaints.controller;

import com.daralshifa.util.DashMailer;
import com.quality.complaints.exceptions.InvalidComplaintException;
import com.quality.complaints.model.Complaint;
import com.quality.complaints.service.ComplaintService;
import com.quality.complaints.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Date;


@Controller
public class UserEndPoints {
    @Autowired
    ComplaintService complaintService;

    @Autowired
    private StorageService storageService;

    @GetMapping("/submit")
    public String doView(Model model) {
        Complaint complaint = new Complaint();
        model.addAttribute("complaint", complaint);
        return "submit";
    }

    @GetMapping("/audio")
    public String audio(Model model) {

        return "audio";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("audio_data") MultipartFile file, HttpServletRequest req) {
        final String fileName = file.getOriginalFilename();
        HttpSession session = req.getSession();

        session.setAttribute("fileName", fileName);
        System.out.println(fileName);

        System.out.println(file.getSize());
        System.out.println("upload called!");
        if (file.getSize() > 0) {
            storageService.store(file);
        }
        return "audio";
    }


    @PostMapping("/submit")
    public String submitComplaint(@ModelAttribute Complaint complaint,
                                  Model model,
                                  HttpServletRequest req
    ) {
        final String base64Audio = req.getParameter("recording");

        if (!base64Audio.equals("")) {
            final Base64.Decoder decoder = Base64.getDecoder();
            final byte[] decodedByte = decoder.decode(base64Audio.split(",")[1]);
            final String fileName = new Date().getTime() + ".webm";
            storageService.store(fileName, decodedByte);
            complaint.setVoiceFileName(fileName);
        }


        try {
            complaint = complaintService.add(complaint);
            try {
                final String message = "Complaint id = "+complaint.getId()+" from "+complaint.getNameAr() +" description: "+complaint.getDescription()
                        + "phone: "+complaint.getMobile()+  complaint.getVoiceFileName()==null?"":" , voice file name"+complaint.getVoiceFileName();
                DashMailer.sendMail("complaints@daralshifa.com" ,"naeemj@daralshifa.com","complaint",message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } catch (InvalidComplaintException e) {
            e.printStackTrace();
        }
        model.addAttribute("complaint", complaint);
        System.out.println(complaint);

        return "submit";
    }
}
