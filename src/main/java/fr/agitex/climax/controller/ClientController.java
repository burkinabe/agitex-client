package fr.agitex.climax.controller;

import fr.agitex.climax.entity.Client;
import fr.agitex.climax.service.ClientService;
import fr.agitex.climax.service.dto.ClientDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }


    @GetMapping("/clients")
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<ClientDTO> clients = clientService.getAllClients();
        return ResponseEntity.ok().body(clients);
    }

    @PostMapping("/clients/upload-file")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        String response = clientService.importFromFile(file);
        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/clients/salary-average")
    public ResponseEntity<Double> averageOfSalary(@RequestParam String profession) {
        double average = clientService.averageOfSalaryByProfession(profession);
        return ResponseEntity.ok()
                .body(average);
    }
}
