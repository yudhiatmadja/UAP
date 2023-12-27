package com.example.uap;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PeminjamanApp extends Application {

    private static final String FILE_PATH = "borrowed_books.txt";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aplikasi Peminjaman Buku");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Label dan TextField untuk input judul buku
        Label titleLabel = new Label("Judul Buku:");
        TextField titleTextField = new TextField();
        grid.add(titleLabel, 0, 0);
        grid.add(titleTextField, 1, 0);

        // Label dan TextField untuk input nama peminjam
        Label borrowerLabel = new Label("Peminjam:");
        TextField borrowerTextField = new TextField();
        grid.add(borrowerLabel, 0, 1);
        grid.add(borrowerTextField, 1, 1);

        // Tombol untuk peminjaman
        Button borrowButton = new Button("Pinjam");
        grid.add(borrowButton, 0, 2);

        // Tombol untuk pengembalian
        Button returnButton = new Button("Kembalikan");
        grid.add(returnButton, 1, 2);



        // Tombol untuk delete data
        Button deleteButton = new Button("Delete");
        grid.add(deleteButton, 1, 5);

        // Tabel untuk menampilkan data peminjaman
        TableView<Book> tableView = new TableView<>();
        TableColumn<Book, String> titleColumn = new TableColumn<>("Judul Buku");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> borrowerColumn = new TableColumn<>("Peminjam");
        borrowerColumn.setCellValueFactory(new PropertyValueFactory<>("borrower"));

        tableView.getColumns().addAll(titleColumn, borrowerColumn);
        grid.add(tableView, 0, 4, 2, 1);

        // Output area untuk hasil peminjaman/pengembalian
        TextArea outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        grid.add(outputTextArea, 0, 3, 2, 1);

        // Memuat data dari file
        loadDataFromFile(outputTextArea, tableView);

        // Event handling untuk tombol pinjam
        borrowButton.setOnAction(e -> {
            String title = titleTextField.getText();
            String borrower = borrowerTextField.getText();
            String output = "Buku '" + title + "' dipinjam oleh " + borrower;
            outputTextArea.setText(output);

            // Simpan data ke file
            saveToFile(title, borrower);
            tableView.getItems().add(new Book(title, borrower));
        });

        // Event handling untuk tombol kembali
        returnButton.setOnAction(e -> {
            String title = titleTextField.getText();
            String borrower = borrowerTextField.getText();
            String output = "Buku '" + title + "' dikembalikan oleh " + borrower;
            outputTextArea.setText(output);

            // Hapus data dari file
            removeFromFile(title, borrower);
            tableView.getItems().removeIf(book -> book.getTitle().equals(title) && book.getBorrower().equals(borrower));
        });

        // Event handling untuk tombol update



        // Event handling untuk tombol delete
        deleteButton.setOnAction(e -> {
            Book selectedBook = tableView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                String title = selectedBook.getTitle();
                String borrower = selectedBook.getBorrower();

                String output = "Data '" + title + "' oleh " + borrower + " dihapus.";
                outputTextArea.setText(output);

                removeFromFile(title, borrower);
                tableView.getItems().remove(selectedBook);
            } else {
                outputTextArea.setText("Pilih buku yang ingin dihapus.");
            }
        });

        Scene scene = new Scene(grid, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveToFile(String title, String borrower) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(title + "," + borrower + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeFromFile(String title, String borrower) {
        try {
            Path path = Paths.get(FILE_PATH);
            List<String> lines = Files.readAllLines(path);
            List<String> updatedLines = lines.stream()
                    .filter(line -> !(line.startsWith(title + ",") && line.endsWith(borrower)))
                    .collect(Collectors.toList());
            Files.write(path, updatedLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromFile(TextArea outputTextArea, TableView<Book> tableView) {
        try {
            Path path = Paths.get(FILE_PATH);
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                outputTextArea.setText("Data Peminjaman:\n");
                List<Book> books = new ArrayList<>();
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String title = parts[0];
                        String borrower = parts[1];
                        outputTextArea.appendText(line + "\n");
                        books.add(new Book(title, borrower));
                    }
                }
                tableView.getItems().addAll(books);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static class Book {
        private String title;
        private String borrower;

        public Book(String title, String borrower) {
            this.title = title;
            this.borrower = borrower;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBorrower() {
            return borrower;
        }

        public void setBorrower(String borrower) {
            this.borrower = borrower;
        }
    }
}
