package fr.agitex.climax.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import fr.agitex.climax.entity.Client;
import fr.agitex.climax.entity.ClientWrapper;
import fr.agitex.climax.mapper.ClientMapper;
import fr.agitex.climax.repository.ClientRepository;
import fr.agitex.climax.service.dto.ClientDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static fr.agitex.climax.utils.Constants.*;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public ClientDTO save(ClientDTO clientDTO) {
        Client client = clientMapper.toEntity(clientDTO);
        client = clientRepository.save(client);
        return clientMapper.toDto(client);
    }


    public List<ClientDTO> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        return clientMapper.toDto(clients);
    }

    public Double averageOfSalaryByProfession(String profession) {
        return averageOfSalaryByProfession(clientRepository.findAllByProfession(profession));
    }

    public String importFromFile(MultipartFile file) throws IOException {
        switch (Objects.requireNonNull(file.getContentType())) {
            case JSON -> {
               clientRepository.saveAll(loadFromJson(file));
                return "JSON imported successfully";
            }

            case XML -> {
                clientRepository.saveAll(loadFromXML(file));
                return "Xml Imported Successfully";
            }

            case CSV -> {
                String csv = new String(file.getBytes(), StandardCharsets.UTF_8);
                clientRepository.saveAll(loadFromCsv(file));
                return csv;
            }

            default -> {
                return "Format de fichier non accepté, veuillez utiliser des fichier .csv, .xml, .json";
            }

        }
    }

    private List<Client> loadFromCsv(MultipartFile file) throws IOException {
        String[] HEADERS = { "lastName", "firstName", "age", "profession", "salary"};
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(new InputStreamReader(file.getInputStream()));
        List<Client> clients = new ArrayList<>();
        for (CSVRecord record : records) {
            String lastName = record.get("lastName");
            String fistName = record.get("firstName");
            String age = record.get("age");
            String profession = record.get("profession");
            String salary = record.get("salary");
            Client client = new Client();
            client.setLastName(lastName);
            client.setFirstName(fistName);
            client.setAge(Long.parseLong(age.trim()));
            client.setProfession(profession);
            client.setSalary(Long.parseLong(salary.trim()));
            clients.add(client);
        }
        return clients;
    }

    private List<Client> loadFromJson(MultipartFile file) throws IOException {
        String json = new String(file.getBytes(), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Client>>() {}.getType();

        return gson.fromJson(json, listType);

    }

    private List<Client> loadFromXML(MultipartFile file) throws IOException {
        XStream xStream = new XStream();
        xStream.alias("clients", List.class);
        xStream.alias("client", Client.class);
        xStream.addPermission(AnyTypePermission.ANY);
        return (List<Client>) xStream.fromXML(file.getInputStream());
    }

    private double averageOfSalaryByProfession(List<Client> clients) {
        return clients.stream().mapToDouble(Client::getSalary)
                .average()
                .orElse(0.0);
    }
}
