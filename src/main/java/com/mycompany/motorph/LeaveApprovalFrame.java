package com.mycompany.motorph;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class LeaveApprovalFrame extends JFrame {
    private static final Path LEAVE_REQUESTS_CSV = Paths.get("src/main/resources/leave_requests.csv");

    private final DefaultTableModel model;
    private final JTable table;
    private List<String[]> rows = new ArrayList<>();

    public LeaveApprovalFrame() {
        super("Admin Leave Approval");
        setSize(980, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Leave Requests (Submitted / Pending can be approved or rejected)"));

        String[] cols = {"Emp #", "Name", "Position", "Type", "Date From", "Date To", "Reason", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refresh = new JButton("Refresh");
        JButton approve = new JButton("Approve");
        JButton reject = new JButton("Reject");
        actions.add(refresh);
        actions.add(approve);
        actions.add(reject);

        refresh.addActionListener(e -> loadRows());
        approve.addActionListener(e -> updateSelectedStatus("Approved"));
        reject.addActionListener(e -> updateSelectedStatus("Rejected"));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        loadRows();
    }

    private void loadRows() {
        rows.clear();
        model.setRowCount(0);
        if (Files.notExists(LEAVE_REQUESTS_CSV)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(LEAVE_REQUESTS_CSV);
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                String[] cols = splitCsv(line);
                if (cols.length >= 8) {
                    rows.add(cols);
                    model.addRow(new Object[]{cols[0], cols[1], cols[2], cols[3], cols[4], cols[5], cols[6], cols[7]});
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load leave requests: " + ex.getMessage());
        }
    }

    private void updateSelectedStatus(String status) {
        int rowIndex = table.getSelectedRow();
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            JOptionPane.showMessageDialog(this, "Select a leave request first.");
            return;
        }
        rows.get(rowIndex)[7] = status;

        try {
            saveAll();
            loadRows();
            JOptionPane.showMessageDialog(this, "Leave request marked as " + status + ".");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update leave request: " + ex.getMessage());
        }
    }

    private void saveAll() throws IOException {
        String header = "employee_number,employee_name,employee_position,leave_type,date_from,date_to,reason,status";
        try (BufferedWriter bw = Files.newBufferedWriter(LEAVE_REQUESTS_CSV, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            bw.write(header);
            for (String[] row : rows) {
                bw.newLine();
                bw.write(toCsv(row));
            }
        }
    }

    private String[] splitCsv(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    private String toCsv(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            String f = fields[i] == null ? "" : fields[i];
            if (f.contains(",") || f.contains("\"") || f.contains("\n")) {
                sb.append('"').append(f.replace("\"", "\"\"")).append('"');
            } else {
                sb.append(f);
            }
        }
        return sb.toString();
    }
}
