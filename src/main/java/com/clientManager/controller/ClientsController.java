package com.clientManager.controller;


import com.clientManager.entity.Clients;
import com.clientManager.entity.ClientsDto;
import com.clientManager.service.ClientsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
public class ClientsController {

    @Autowired
    private ClientsService service;

    @GetMapping("/")
    public String homePage(){
        return "home";
    }

    @GetMapping("/clients_list")
    public ModelAndView clientsListPage(){
        List<Clients> list = service.getAllClients();
        return new ModelAndView("clientsList", "clients", list);
    }

    @GetMapping("/add_client")
    public String addClientPage(Model model){
        model.addAttribute("clientsDto", new ClientsDto());
        return "addClient";
    }

    @PostMapping("/save")
    public String addClient(@Valid @ModelAttribute ClientsDto clientsDto, BindingResult result, RedirectAttributes redirectAttributes) {
        MultipartFile profile = clientsDto.getProfile();

        // Check if profile image is null or empty
        if (profile == null || profile.isEmpty()) {
            result.addError(new FieldError("clientsDto", "profile", "Image file is required!"));
        }

        if (result.hasErrors()) {
            return "addClient";
        }

        // Save image
        Date date = new Date();
        String storageFileName = date.getTime() + "_" + profile.getOriginalFilename();
        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = profile.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        // Save client data
        Clients clients = new Clients();
        clients.setName(clientsDto.getName());
        clients.setPhone(clientsDto.getPhone());
        clients.setProfile(storageFileName);

        service.save(clients);
        redirectAttributes.addFlashAttribute("message", "Client added successfully!");

        return "redirect:/clients_list";
    }

    @GetMapping("/editClient/{id}")
    public String editClientPage(@PathVariable("id") int id, Model model) {
        Clients clients = service.getClientById(id);
        ClientsDto clientsDto = new ClientsDto();

        // Populate ClientsDto with existing data
        clientsDto.setName(clients.getName());
        clientsDto.setPhone(clients.getPhone());

        model.addAttribute("clientsDto", clientsDto);
        model.addAttribute("existingProfile", clients.getProfile());
        model.addAttribute("id", id);
        return "editClient"; // The view name for editing the client
    }

    @PostMapping("/update_client/{id}")
    public String updateClient(@PathVariable("id") int id, @Valid @ModelAttribute ClientsDto clientsDto,
                               BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        Clients clients = service.getClientById(id);
        MultipartFile newProfile = clientsDto.getProfile();


        // Add existing profile image to the model for the view
        model.addAttribute("existingProfile", clients.getProfile());

        if (result.hasErrors()) {
            model.addAttribute("id", id);
            return "editClient"; // Return to the edit view with validation errors
        }

        // Check if the profile image is not null or empty
        if (newProfile != null && !newProfile.isEmpty()) {
            // Delete the old image
            String existingProfilePath = "public/images/" + clients.getProfile();
            try {
                Path path = Paths.get(existingProfilePath);
                Files.deleteIfExists(path);
            } catch (Exception ex) {
                System.out.println("Failed to delete old image: " + ex.getMessage());
            }

            // Save the new image
            Date date = new Date();
            String storageFileName = date.getTime() + "_" + newProfile.getOriginalFilename();
            try {
                String uploadDir = "public/images/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = newProfile.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }
                clients.setProfile(storageFileName);
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }
        }

        // Update other fields
        clients.setName(clientsDto.getName());
        clients.setPhone(clientsDto.getPhone());

        // Save the updated client
        service.save(clients);
        redirectAttributes.addFlashAttribute("message", "Client updated successfully!");
        return "redirect:/clients_list";
    }

    @GetMapping("/deleteClient/{id}")
    public String deleteClient(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        service.deleteClient(id);
        redirectAttributes.addFlashAttribute("message", "Client deleted successfully!");
        return "redirect:/clients_list";
    }
}
