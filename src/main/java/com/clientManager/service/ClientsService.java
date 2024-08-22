package com.clientManager.service;


import com.clientManager.entity.Clients;
import com.clientManager.repository.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ClientsService {
    @Autowired
    private ClientsRepository clientsRepository;

    public void save(Clients clients){
        clientsRepository.save(clients);
    }

    public List<Clients> getAllClients(){
        return clientsRepository.findAll();
    }

    public Clients getClientById(int id){
        return clientsRepository.findById(id).get();
    }
    public void deleteClient(int id) {
        Clients client = getClientById(id);
        if (client != null) {
            // Delete the image file if it exists
            String profileImage = client.getProfile();
            if (profileImage != null && !profileImage.isEmpty()) {
                Path imagePath = Paths.get("public/images/" + profileImage);
                try {
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Delete the client record
            clientsRepository.deleteById(id);
        }
    }
}
