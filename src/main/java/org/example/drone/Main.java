package org.example.drone;



import java.io.IOException;

import org.example.drone.api.Dto.DroneDTO;
import org.example.drone.api.Factory.DroneFactory;
import org.example.drone.api.Misc.Archiver;

import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var storage = DroneFactory.getInstance();
        Scanner scanner = new Scanner(System.in);

        boolean t1 = false;

        do {
            System.out.println("Из какого файла прочитать данные? (txt, xml, json)");
            String fileToRead = scanner.nextLine();
            fileToRead = fileToRead.toLowerCase();
            switch (fileToRead) {
                case "txt":
                    storage.readFromFile("drone.txt");
                    t1 = true;
                    break;

                case "xml":
                    storage.setListStorage(storage.readFromXml("drone.xml"));
                    t1 = true;
                    break;

                case "json":
                    storage.setListStorage(storage.readDataFromJsonFile("drone.json"));
                    t1 = true;
                    break;

                default:
                    System.out.println("Неправильный формат файла. Попробуйте снова.");
                    break;
            }
        } while (!t1);
        System.out.println("Список дронов получен.");
        for (DroneDTO dto : storage.getList()) {
            System.out.println(dto.toString());
        }
        System.out.println();
        int id = -1;
        String name = "";
        String description = "";
        boolean t = true;
        do {
            System.out.println("Введите данные о дронах в формате cost,name,description:");
            try {
                String input = scanner.nextLine();
                String[] parts = input.split(",");
                id = Integer.parseInt(parts[0]);
                name = parts[1];
                description = parts[2];
                int finalId = id;
                String finalDescription = description;
                String finalName = name;
                if (storage.getList().stream().anyMatch(parachuteDTO -> parachuteDTO.getCost() == finalId) &&
                        storage.getList().stream().anyMatch(ParachuteDTO -> ParachuteDTO.getDescription().equals(finalDescription)) &&
                        storage.getList().stream().anyMatch(CategoryDto -> CategoryDto.getName().equals(finalName))
                ) {
                    System.out.println("Такой дрон уже получен!");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Попробуйте снова");
                t = false;
            }
        } while (t != true);
        System.out.println(storage.getList());

        var newParachute = new DroneDTO(id, name, description);
        storage.addToListStorage(newParachute);
        storage.addToMapStorage(id, newParachute);

        storage.writeToFile("drone.txt");
        storage.writeToXml("drone.xml", storage.getList());
        storage.writeDataToJsonFile("drone.json", storage.getList());

        System.out.println("Обновленный список дронов" + storage.getList());
        boolean ans = false;

        do {
            System.out.println("Выберете поле для сортировки(cost,name,description):");
            String typeSort = scanner.nextLine();
            typeSort = typeSort.toLowerCase();

            switch (typeSort) {

                case "cost":
                    storage.getList().sort(Comparator.comparing(DroneDTO::getCost));
                    System.out.println("Дроны сортированные по cost: ");
                    for (DroneDTO dto : storage.getList()) {
                        System.out.println(dto.toString());
                    }
                    ans = true;
                    break;

                case "name":
                    storage.getList().sort(Comparator.comparing(DroneDTO::getName));
                    System.out.println("Дроны сортированные по названию: " + storage.getList());
                    ans = true;
                    break;

                case "description":
                    storage.getList().sort(Comparator.comparing(DroneDTO::getDescription));
                    System.out.println("Дроны сортированные по описанию: " + storage.getList());
                    ans = true;
                    break;
                default:
                    System.out.println("Введено неверное поле");
                    break;
            }
        } while (!ans);

        String[] files = new String[]{
                "drone.txt",
                "drone.json",
                "drone.xml"
        };

        Archiver archiver = new Archiver();
        try {
            archiver.createZipArchive("zipResult.zip", files);
            archiver.createJarArchive("jarResult.jar", files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
