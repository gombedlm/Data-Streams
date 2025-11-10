import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsApp extends JFrame {

    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton, searchButton, quitButton;
    private Path filePath;

    public DataStreamsApp() {
        super("Java Data Streams Lab");

        // -------------------- GUI COMPONENTS --------------------
        originalTextArea = new JTextArea(20, 30);
        originalTextArea.setEditable(false);
        originalTextArea.setBorder(BorderFactory.createTitledBorder("Original File"));

        filteredTextArea = new JTextArea(20, 30);
        filteredTextArea.setEditable(false);
        filteredTextArea.setBorder(BorderFactory.createTitledBorder("Filtered Results"));

        JScrollPane originalScroll = new JScrollPane(originalTextArea);
        JScrollPane filteredScroll = new JScrollPane(filteredTextArea);

        searchField = new JTextField(20);

        loadButton = new JButton("Load File");
        searchButton = new JButton("Search File");
        searchButton.setEnabled(false); // enabled after file is loaded
        quitButton = new JButton("Quit");

        // -------------------- ACTION LISTENERS --------------------
        loadButton.addActionListener(this::loadFile);
        searchButton.addActionListener(this::searchFile);
        quitButton.addActionListener(e -> System.exit(0));

        // -------------------- TOP PANEL --------------------
        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(loadButton);
        topPanel.add(searchButton);
        topPanel.add(quitButton);

        // -------------------- CENTER PANEL --------------------
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        centerPanel.add(originalScroll);
        centerPanel.add(filteredScroll);

        // -------------------- FRAME SETTINGS --------------------
        this.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);

        this.setSize(900, 600);  // Explicit window size
        this.setLocationRelativeTo(null); // Center on screen
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    // -------------------- LOAD FILE --------------------
    private void loadFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            filePath = fileChooser.getSelectedFile().toPath();
            try (Stream<String> lines = Files.lines(filePath)) {
                List<String> allLines = lines.collect(Collectors.toList());
                originalTextArea.setText(String.join("\n", allLines));
                filteredTextArea.setText(""); // clear filtered results
                searchButton.setEnabled(true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error reading file: " + ex.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -------------------- SEARCH FILE --------------------
    private void searchFile(ActionEvent e) {
        if (filePath == null) return;

        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search string.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Stream<String> lines = Files.lines(filePath)) {
            List<String> filtered = lines
                    .filter(line -> line.contains(searchTerm)) // Stream filter with lambda
                    .collect(Collectors.toList());
            filteredTextArea.setText(String.join("\n", filtered));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error reading file: " + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------- MAIN --------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DataStreamsApp::new);
    }
}
