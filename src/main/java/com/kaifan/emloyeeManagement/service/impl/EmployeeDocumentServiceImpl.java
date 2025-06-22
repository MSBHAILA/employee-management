package com.kaifan.emloyeeManagement.service.impl;

import com.kaifan.emloyeeManagement.dto.EmployeeDocumentDto;
import com.kaifan.emloyeeManagement.entity.EmployeeDocument;
import com.kaifan.emloyeeManagement.mapper.EmployeeDocumentMapper;
import com.kaifan.emloyeeManagement.repository.EmployeeDocumentRepository;
import com.kaifan.emloyeeManagement.service.EmployeeDocumentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeDocumentServiceImpl implements EmployeeDocumentService {

    @Value("${app.document.upload-dir}")
    private String uploadDir;

    @Autowired
    private EmployeeDocumentRepository documentRepository;

    @Autowired
    private EmployeeDocumentMapper employeeDocumentMapper;

    @Override
    public EmployeeDocumentDto uploadDocument(String employeeId, MultipartFile file, EmployeeDocumentDto metadata) {
        try {
            String folderPath = uploadDir + "/" + employeeId + "/" + metadata.getDocumentType();
            Path dirPath = Paths.get(folderPath);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String originalName = file.getOriginalFilename();
            String savedFileName = "_" + originalName;
            Path filePath = dirPath.resolve(savedFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            metadata.setEmployeeId(employeeId);
            metadata.setDocumentType(metadata.getDocumentType());
            metadata.setFileName(savedFileName);
            metadata.setOriginalName(originalName);
            metadata.setUploadPath(filePath.toString());
            metadata.setUploadTime(LocalDateTime.now());

            return employeeDocumentMapper.employeeDocumentToEmployeeDocumentDto(
                    documentRepository.save(employeeDocumentMapper.employeeDocumentDtoToEmployeeDocument(metadata)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to store document", e);
        }
    }

    @Override
    public Resource downloadDocument(String documentId) {
        EmployeeDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        try {
            Path path = Paths.get(doc.getUploadPath());
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) throw new FileNotFoundException("File not found");
            return resource;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    @Override
    public void deleteDocument(String documentId) {
        EmployeeDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
        try {
            Files.deleteIfExists(Paths.get(doc.getUploadPath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
        documentRepository.deleteById(documentId);
    }

    @Override
    public List<EmployeeDocumentDto> getDocumentsByEmployee(String employeeId) {
        return employeeDocumentMapper.employeeDocumentsToEmployeeDocumentDtos(documentRepository.findByEmployeeId(employeeId));
    }
}

