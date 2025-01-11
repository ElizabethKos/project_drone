package org.example.drone.GUI;

import org.example.drone.api.Dto.DroneDTO;
import org.example.drone.api.Factory.DroneFactory;
import org.example.drone.api.Misc.Archiver;
import org.example.drone.persistence.Repositories.AbstractStorage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Comparator;

public class DroneGUI {

    private AbstractStorage<DroneDTO> storage;
    private JFrame frame;
    private JTextField costField, nameField, descriptionField;
    private JTable table;
    private DefaultTableModel tableModel;

    public DroneGUI() {
        storage = DroneFactory.getInstance();
    }

    public void createAndShowGUI() {
        frame = new JFrame("Drone Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);

        // Основная панель с градиентным фоном
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color startColor = new Color(135, 206, 250);
                Color endColor = new Color(70, 130, 180);
                GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Панель для ввода данных
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBackground(new Color(245, 245, 245));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Drone Details"));

        inputPanel.add(new JLabel("Cost:"));
        costField = new JTextField();
        inputPanel.add(costField);

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        JButton addButton = new JButton("Add Drone");
        addButton.setBackground(new Color(50, 205, 50));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.addActionListener(new AddButtonListener());
        inputPanel.add(addButton);

        // Таблица для отображения данных
        tableModel = new DefaultTableModel(new Object[]{"Cost", "Name", "Description"}, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setBackground(new Color(240, 255, 255));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Панель кнопок управления
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton readButton = new JButton("Read Data");
        readButton.setBackground(new Color(30, 144, 255));
        readButton.setForeground(Color.WHITE);
        readButton.setFont(new Font("Arial", Font.BOLD, 14));
        readButton.addActionListener(new ReadButtonListener());

        JButton writeButton = new JButton("Write Data");
        writeButton.setBackground(new Color(255, 140, 0));
        writeButton.setForeground(Color.WHITE);
        writeButton.setFont(new Font("Arial", Font.BOLD, 14));
        writeButton.addActionListener(new WriteButtonListener());

        JButton sortButton = new JButton("Sort Data");
        sortButton.setBackground(new Color(186, 85, 211));
        sortButton.setForeground(Color.WHITE);
        sortButton.setFont(new Font("Arial", Font.BOLD, 14));
        sortButton.addActionListener(new SortButtonListener());

        JButton archiveButton = new JButton("Create Archive");
        archiveButton.setBackground(new Color(220, 20, 60));
        archiveButton.setForeground(Color.WHITE);
        archiveButton.setFont(new Font("Arial", Font.BOLD, 14));
        archiveButton.addActionListener(new ArchiveButtonListener());

        buttonPanel.add(readButton);
        buttonPanel.add(writeButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(archiveButton);

        // Расположение компонентов на основной панели
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cost = costField.getText();
            String name = nameField.getText();
            String description = descriptionField.getText();

            if (!cost.isEmpty() && !name.isEmpty() && !description.isEmpty()) {
                try {
                    int costInt = Integer.parseInt(cost);
                    DroneDTO drone = new DroneDTO(costInt, name, description);

                    boolean isDuplicate = storage.getList().stream()
                            .anyMatch(d -> d.getName().equalsIgnoreCase(name));

                    if (isDuplicate) {
                        JOptionPane.showMessageDialog(frame, "Drone with the same name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        storage.addToListStorage(drone);
                        storage.addToMapStorage(costInt, drone);

                        tableModel.addRow(new Object[]{drone.getCost(), drone.getName(), drone.getDescription()});
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid cost format", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ReadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] options = {"drone.txt", "drone.xml", "drone.json"};
            String fileType = (String) JOptionPane.showInputDialog(frame,
                    "Select file to read from", "Select File",
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (fileType != null) {
                new Thread(() -> {
                    try {
                        tableModel.setRowCount(0);
                        storage.getList().clear();

                        switch (fileType) {
                            case "drone.txt":
                                storage.readFromFile(fileType);
                                break;
                            case "drone.xml":
                                storage.setListStorage(storage.readFromXml(fileType));
                                break;
                            case "drone.json":
                                storage.setListStorage(storage.readDataFromJsonFile(fileType));
                                break;
                            default:
                                throw new IOException("Unsupported file format");
                        }

                        updateTable();

                        JOptionPane.showMessageDialog(frame, "Data successfully loaded from " + fileType,
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error reading file: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }).start();
            }
        }
    }

    private class WriteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                Thread txtWriter = new Thread(() -> storage.writeToFile("drone.txt"));
                Thread xmlWriter = new Thread(() -> storage.writeToXml("drone.xml", storage.getList()));
                Thread jsonWriter = new Thread(() -> storage.writeDataToJsonFile("drone.json", storage.getList()));

                txtWriter.start();
                xmlWriter.start();
                jsonWriter.start();

                try {
                    txtWriter.join();
                    xmlWriter.join();
                    jsonWriter.join();
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(frame, "Error during file writing: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(frame, "Data written to all files", "Success", JOptionPane.INFORMATION_MESSAGE);
            }).start();
        }
    }

    private class SortButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] options = {"cost", "name", "description"};
            String field = (String) JOptionPane.showInputDialog(frame,
                    "Select sorting field", "Sort by",
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (field != null) {
                new Thread(() -> {
                    switch (field) {
                        case "cost":
                            storage.getList().sort(Comparator.comparingInt(DroneDTO::getCost));
                            break;
                        case "name":
                            storage.getList().sort(Comparator.comparing(DroneDTO::getName));
                            break;
                        case "description":
                            storage.getList().sort(Comparator.comparing(DroneDTO::getDescription));
                            break;
                    }
                    updateTable();
                }).start();
            }
        }
    }

    private class ArchiveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Archiver archiver = new Archiver();
            String[] files = {"drone.txt", "drone.json", "drone.xml"};

            new Thread(() -> {
                try {
                    archiver.createZipArchive("DroneArchive.zip", files);
                    archiver.createJarArchive("DroneArchive.jar", files);
                    JOptionPane.showMessageDialog(frame, "Archive Created", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (DroneDTO drone : storage.getList()) {
            tableModel.addRow(new Object[]{drone.getCost(), drone.getName(), drone.getDescription()});
        }
    }
}
