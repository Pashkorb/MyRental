package com.example.rental.property;

import com.example.rental.users.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rental/property")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    // Создание нового объекта недвижимости
    @PostMapping("/create")
    public ResponseEntity<Property> createProperty(@RequestBody Property property) {
        Property createdProperty = propertyService.createProperty(property);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProperty);
    }

    // Получение объекта недвижимости по UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<Property> getProperty(@PathVariable UUID uuid) {
        Property property = propertyService.getProperty(uuid);
        if (property != null) {
            return ResponseEntity.ok(property);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Получение всех объектов недвижимости
    @GetMapping("/all")
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    // Обновление объекта недвижимости
    @PutMapping("/update/{uuid}")
    public ResponseEntity<Property> updateProperty(@PathVariable UUID uuid, @RequestBody Property property) {
        if (propertyService.getProperty(uuid) != null) {
            property.setUuid(uuid);
            Property updatedProperty = propertyService.updateProperty(property);
            return ResponseEntity.ok(updatedProperty);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Удаление объекта недвижимости
    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID uuid) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userPrincipal.getUserId().equals(propertyService.getProperty(uuid).getLandlord())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        };
        if (propertyService.getProperty(uuid) != null) {
            propertyService.deleteProperty(uuid);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}