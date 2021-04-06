package by.bsuir;

import by.bsuir.entity.Container;
import by.bsuir.entity.FileInfo;
import by.bsuir.entity.Type;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class MainWindowController {

    @FXML
    private TableView<DTO> table;

    @FXML
    private TableColumn<DTO, String> nameColumn;

    @FXML
    private TableColumn<DTO, String> typeColumn;

    @FXML
    private Button openButton;

    @FXML
    private Button copyButton;

    @FXML
    private Button pasteButton;

    @FXML
    private Button cutButton;

    @FXML
    private Button addButton;

    @FXML
    private Button backButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TextField nameField;

    @FXML
    private Button renameButton;

    private long currentId;

    private long copyId;

    private long cutId;

    private FileInfo selectedFile;

    private Container container;

    private final RestTemplate template = new RestTemplate();

    private final Stack<Long> stack = new Stack<>();

    private final String GET_DIRECTORY_ADDRESS = "http://localhost:8082/dir/{id}";

    private final String GET_FILE_ADDRESS = "http://localhost:8082/files/{id}";

    private final String POST_DIRECTORY_ADDRESS = "http://localhost:8082/dir/{id}/upload";

    private final String POST_FILE_ADDRESS = "http://localhost:8082/files/{id}/upload";

    private final String PUT_ADDRESS = "http://localhost:8082/storage/{id}/put";

    private final String DELETE_ADDRESS = "http://localhost:8082/storage/{id}/delete";

    private final String COPY_ADDRESS = "http://localhost:8082/storage/copy?source={source}&target={target}";

    private final String MOVE_ADDRESS = "http://localhost:8082/storage/move?source={source}&target={target}";

    private final String SAVE_DIRECTORY = "C:\\Users\\kolod\\Downloads\\";

    @FXML
    void initialize() {
        nameColumn.setCellValueFactory(name -> name.getValue().name);
        typeColumn.setCellValueFactory(type -> type.getValue().type);

        try {
            container = template.getForObject(GET_DIRECTORY_ADDRESS, Container.class, 4);
            currentId = 4;
            showTable();
        } catch (ResourceAccessException e) {
            printServerNotConnectedError();
        }

        table.setOnMouseClicked(mouseEvent -> {
            DTO selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                long selectedItemId = selectedItem.id.get();
                for (FileInfo info : container.getFiles()) {
                    if (info.getId() == selectedItemId) {
                        selectedFile = info;
                        showButtons(true);
                        break;
                    }
                }
            }
        });

        openButton.setOnAction(actionEvent -> {
            if (selectedFile.getType() == Type.DIRECTORY) {
                try {
                    container = template.getForObject(GET_DIRECTORY_ADDRESS, Container.class, selectedFile.getId());
                    showTable();
                    stack.push(currentId);
                    currentId = selectedFile.getId();
                    selectedFile = null;
                    showButtons(false);
                } catch (ResourceAccessException e) {
                    printServerNotConnectedError();
                }
            } else {
                try {
                    File file = template.getForObject(GET_FILE_ADDRESS, File.class, selectedFile.getId());
                    if (file == null) {
                        printResourceRemoved();
                    } else {
                        File newFIle = new File(SAVE_DIRECTORY + file.getName());
                        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFIle));
                             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                            bos.write(bis.readAllBytes());
                            bos.flush();
                            printFileSavedSuccessfully();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (ResourceAccessException e) {
                    printServerNotConnectedError();
                }
            }
        });

        backButton.setOnAction(actionEvent -> {
            if (!stack.empty()) {
                try {
                    Long backId = stack.peek();
                    container = template.getForObject(GET_DIRECTORY_ADDRESS, Container.class, backId);
                    currentId = backId;
                    showTable();
                    selectedFile = null;
                    showButtons(false);
                    stack.pop();
                } catch (ResourceAccessException e) {
                    printServerNotConnectedError();
                }
            }
        });

        copyButton.setOnAction(actionEvent -> {
            cutId = 0;
            copyId = selectedFile.getId();
            pasteButton.setDisable(false);
        });

        addButton.setOnAction(actionEvent -> {
            String dirName = nameField.getText().trim();
            try {
                if (!dirName.isEmpty()) {
                    template.postForObject(POST_DIRECTORY_ADDRESS, dirName, Boolean.class, currentId);
                    container = template.getForObject(GET_DIRECTORY_ADDRESS, Container.class, currentId);
                    showTable();
                } else {
                    FileChooser chooser = new FileChooser();
                    File file = chooser.showOpenDialog(App.getStage());
                    if (file != null) {
                        template.postForObject(POST_FILE_ADDRESS, file, Boolean.class, currentId);
                        container = template.getForObject(GET_DIRECTORY_ADDRESS, Container.class, currentId);
                        showTable();
                    }
                }
            } catch (ResourceAccessException e) {
                printServerNotConnectedError();
            }
        });

        cutButton.setOnAction(actionEvent -> {
            copyId = 0;
            cutId = selectedFile.getId();
            pasteButton.setDisable(false);
        });

        pasteButton.setOnAction(actionEvent -> {
            try {
                if (copyId != 0) {
                    template.getForObject(COPY_ADDRESS, boolean.class, copyId, currentId);
                }
                if (cutId != 0) {
                    template.getForObject(MOVE_ADDRESS, boolean.class, cutId, currentId);
                }
                container = template.getForObject(GET_DIRECTORY_ADDRESS, Container.class, currentId);
                showTable();
                pasteButton.setDisable(true);
            } catch (ResourceAccessException e) {
                printServerNotConnectedError();
            } catch (HttpServerErrorException e) {
                printResourceRemoved();
            }
        });

        deleteButton.setOnAction(actionEvent -> {
            try {
                template.delete(DELETE_ADDRESS, selectedFile.getId());
                container = template.getForObject(GET_DIRECTORY_ADDRESS, Container.class, currentId);
                showTable();
                selectedFile = null;
                showButtons(false);
            } catch (ResourceAccessException e) {
                printServerNotConnectedError();
            } catch (HttpServerErrorException e) {
                printResourceRemoved();
                container = template.getForObject(GET_DIRECTORY_ADDRESS, Container.class, currentId);
                selectedFile = null;
                showButtons(false);
                showTable();
            }
        });

        renameButton.setOnAction(actionEvent -> {
            try {
                String name = nameField.getText().trim();
                selectedFile.setName(name);
                template.put(PUT_ADDRESS, selectedFile, selectedFile.getId());
                container = template.getForObject(GET_DIRECTORY_ADDRESS, Container.class, currentId);
                showTable();
                selectedFile = null;
                showButtons(false);
            } catch (ResourceAccessException e) {
                printServerNotConnectedError();
            } catch (HttpServerErrorException e) {
                printResourceRemoved();
                container = template.getForObject(GET_DIRECTORY_ADDRESS, Container.class, currentId);
                selectedFile = null;
                showButtons(false);
                showTable();
            }
        });
    }

    private void printFileSavedSuccessfully() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("File Saved");
        alert.setHeaderText(null);
        alert.setContentText("The File has been saved to 'Downloads' successfully");
        alert.show();
    }

    private void printServerNotConnectedError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("The Server is temporarily unavailable");
        alert.show();
    }

    private void printResourceRemoved() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("This resource has been moved to other url");
        alert.show();
    }

    private void showButtons(boolean show) {
        copyButton.setDisable(!show);
        cutButton.setDisable(!show);
        openButton.setDisable(!show);
        deleteButton.setDisable(!show);
        renameButton.setDisable(!show);
        //nameField.setDisable(!show);
    }

    private void showTable() {
        List<DTO> dto = new LinkedList<>();
        for (FileInfo info : container.getFiles()) {
            String name = info.getName();
            if (info.getType() == Type.DIRECTORY) name = name.substring(0, name.length() - 1);
            dto.add(new DTO(info.getId(), name, info.getType().toString()));
        }
        ObservableList<DTO> list = FXCollections.observableList(dto);
        table.setItems(list);
    }

    private static class DTO {

        LongProperty id;

        StringProperty name;

        StringProperty type;

        public DTO(Long id, String name, String type) {
            this.id = new SimpleLongProperty(id);
            this.name = new SimpleStringProperty(name);
            this.type = new SimpleStringProperty(type);
        }
    }

}
