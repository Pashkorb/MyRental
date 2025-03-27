package com.example.rental.property;

import com.example.rental.property.Property;
import com.example.rental.property.PropertyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepo propertyRepo;

    // Создание нового объекта недвижимости
    public Property createProperty(Property property) {
        return propertyRepo.save(property);
    }
    // Получение объекта недвижимости по UUID
    public Property getProperty(UUID uuid) {
        return propertyRepo.findById(uuid).orElse(null);
    }

    // Получение всех объектов недвижимости
    public List<Property> getAllProperties() {
        return propertyRepo.findAll();
    }

    // Обновление объекта недвижимости
    public Property updateProperty(Property property) {
        if (propertyRepo.existsById(property.getUuid())) {
            return propertyRepo.save(property);
        } else {
            return null;
        }
    }

    // Удаление объекта недвижимости
    public void deleteProperty(UUID uuid) {
        propertyRepo.deleteById(uuid);
    }
}