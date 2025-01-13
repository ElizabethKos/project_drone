package org.example.drone.api.Factory;
import org.example.drone.api.Dto.DroneDTO;
import org.example.drone.api.Factory.DroneFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DroneFactoryTest {

    private DroneFactory droneFactory;

    @BeforeEach
    void setUp() {
        droneFactory = DroneFactory.getInstance();
    }

    @Test
    void testReadFromFile() throws IOException {
        // Создание тестового файла
        File testFile = new File("test_drones.txt");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("100,Drone1,Description1\n");
            writer.write("200,Drone2,Description2\n");
        }

        // Чтение из файла
        droneFactory.readFromFile(testFile.getAbsolutePath());

        // Проверка содержимого
        List<DroneDTO> drones = droneFactory.getList();
        assertEquals(2, drones.size());
        assertEquals("Drone1", drones.get(0).getName());
        assertEquals("Description2", drones.get(1).getDescription());

        // Удаление тестового файла
        testFile.delete();
    }

    @Test
    void testWriteToFile() throws IOException {
        droneFactory.addToListStorage(new DroneDTO(100, "Drone1", "Description1"));
        droneFactory.writeToFile("output.txt");

        // Проверка, что файл создан и не пустой
        File outputFile = new File("output.txt");
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);

        // Удаление выходного файла
        outputFile.delete();
    }

    @Test
    void testFindByName() {
        droneFactory.addToListStorage(new DroneDTO(100, "Drone1", "Description1"));
        droneFactory.addToListStorage(new DroneDTO(200, "Drone2", "Description2"));

        DroneDTO foundDrone = droneFactory.findByName("Drone1");
        assertNotNull(foundDrone);
        assertEquals(100, foundDrone.getCost());

        DroneDTO notFoundDrone = droneFactory.findByName("Drone3");
        assertEquals(-1, notFoundDrone.getCost()); // Проверка на значение по умолчанию
    }
}