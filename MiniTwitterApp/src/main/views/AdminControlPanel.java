package main.views;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

public class AdminControlPanel extends JFrame {
private static AdminControlPanel instance = null;
    private DefaultTreeModel treeModel;

    // Private constructor to prevent instantiation outside this class
    private AdminControlPanel() {
        setTitle("Admin Control Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
    }

   public static AdminControlPanel getInstance() {
        if (instance == null) {
            instance = new AdminControlPanel();
        }
        return instance;
    }

    private void initializeComponents() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root);
        JTree tree = new JTree(treeModel);
        JScrollPane treeScrollPane = new JScrollPane(tree);
        getContentPane().add(treeScrollPane, BorderLayout.CENTER);

        // Setup the layout and the basic components for managing users/groups
        setupManagementComponents();
    }

    private void setupManagementComponents() {
        JButton addUserButton = new JButton("Add User");
        JButton addGroupButton = new JButton("Add Group");
        JTextField userIdField = new JTextField(10);
        JTextField groupIdField = new JTextField(10);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(new JLabel("User ID:"));
        controlPanel.add(userIdField);
        controlPanel.add(addUserButton);
        controlPanel.add(new JLabel("Group ID:"));
        controlPanel.add(groupIdField);
        controlPanel.add(addGroupButton);

        getContentPane().add(controlPanel, BorderLayout.SOUTH);

        // Listeners for the buttons
        addUserButton.addActionListener(e -> {
            String userId = userIdField.getText();
            if (!userId.isEmpty()) {
                addUserToTree(userId);
            }
        });
        addGroupButton.addActionListener(e -> {
            String groupId = groupIdField.getText();
            if (!groupId.isEmpty()) {
                addGroupToTree(groupId);
            }
        });
    }

    private void addUserToTree(String userId) {
        // Implement logic to add user to the tree and model
    }

    private void addGroupToTree(String groupId) {
        // Implement logic to add group to the tree and model
    }
}