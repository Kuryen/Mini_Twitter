package main.views;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import main.models.Component;
import main.models.Group;
import main.models.User;

public class AdminControlPanel extends JFrame {
    private static AdminControlPanel instance = null;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JTree tree;
    private JTextField userIdTextField;
    private JTextField groupIdTextField;
    private DefaultListModel<String> userModel = new DefaultListModel<>();
    private JList<String> userList = new JList<>(userModel);

    // Private constructor to prevent instantiation outside this class
    private AdminControlPanel() {
        setTitle("Admin Control Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);
        JScrollPane scrollPane = new JScrollPane(userList);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        initializeComponents();
    }

    // Singleton pattern to ensure only one instance of this panel
    public static AdminControlPanel getInstance() {
        if (instance == null) {
            instance = new AdminControlPanel();
        }
        return instance;
    }

    private void initializeComponents() {
        rootNode = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel); // use the class field instead of declaring a new local variable
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding around the tree
        getContentPane().add(treeScrollPane, BorderLayout.CENTER);

        // Setup the layout and the basic components for managing users/groups
        setupManagementComponents();
    }

    private void setupManagementComponents() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        userIdTextField = new JTextField(10);
        JButton addUserButton = new JButton("Add User");
        groupIdTextField = new JTextField(10);
        JButton addGroupButton = new JButton("Add Group");

        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("User ID: "), gbc);
        gbc.gridx = 1;
        controlPanel.add(userIdTextField, gbc);
        gbc.gridx = 2;
        controlPanel.add(addUserButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Group ID: "), gbc);
        gbc.gridx = 1;
        controlPanel.add(groupIdTextField, gbc);
        gbc.gridx = 2;
        controlPanel.add(addGroupButton, gbc);

        getContentPane().add(controlPanel, BorderLayout.SOUTH);

        // Listeners for the buttons
        addUserButton.addActionListener(e -> {
            String userId = userIdTextField.getText().trim();
            if (!userId.isEmpty() && !groupContainsUser((Group) rootNode.getUserObject(), userId)) {
                addUser(userId, (Group) rootNode.getUserObject());
                userIdTextField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "User ID already exists or is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addGroupButton.addActionListener(e -> {
            String groupId = groupIdTextField.getText().trim();
            if (!groupId.isEmpty() && !groupContainsGroup((Group) rootNode.getUserObject(), groupId)) {
                addGroup(groupId, (Group) rootNode.getUserObject());
                groupIdTextField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Group ID already exists or is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void addUser(String userId, Group group) {
        User newUser = new User(userId);
        group.add(newUser);
        updateTree();
        selectAndScrollToNode(userId);
    }

    private void addGroup(String groupId, Group parentGroup) {
        Group newGroup = new Group(groupId);
        parentGroup.add(newGroup);
        updateTree();
        selectAndScrollToNode(groupId);
    }

    private boolean groupContainsUser(Group group, String userId) {
        return group.getChildren().stream()
            .anyMatch(component -> component instanceof User && ((User) component).getId().equals(userId));
    }

    private boolean groupContainsGroup(Group group, String groupId) {
        return group.getChildren().stream()
            .anyMatch(component -> component instanceof Group && ((Group) component).getId().equals(groupId));
    }

    private void updateTree() {
        rootNode.removeAllChildren();
        addGroupNodes((Group) rootNode.getUserObject(), rootNode);
        treeModel.reload();
    }

    // Method to update the user list in the UI
    public void updateUserList(List<User> users) {
        SwingUtilities.invokeLater(() -> {
            userModel.clear(); // Clear the existing contents
            for (User user : users) {
                userModel.addElement(user.getId()); // Add user ID to the list model
            }
        });
    }

    private void addGroupNodes(Group group, DefaultMutableTreeNode node) {
        for (Component member : group.getChildren()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(member.getId());
            node.add(childNode);
            if (member instanceof Group) {
                addGroupNodes((Group) member, childNode);
            }
        }
    }

    private void selectAndScrollToNode(String id) {
        Enumeration<?> enumeration = rootNode.breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            if (node.getUserObject().toString().equals(id)) {
                TreeNode[] nodes = treeModel.getPathToRoot(node);
                TreePath path = new TreePath(nodes);
                tree.setSelectionPath(path);
                tree.scrollPathToVisible(path);
                break;
            }
        }
    }
}