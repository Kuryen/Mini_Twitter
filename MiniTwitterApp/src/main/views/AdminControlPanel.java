package main.views;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

import main.models.AnalyticsVisitor;
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
    private JButton analyticsButton;
    private User currentUser;
    private Group currentGroup;

    // Private constructor to prevent instantiation outside this class
    private AdminControlPanel() {
        setTitle("Admin Control Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
    }

    private DefaultMutableTreeNode findNodeForGroup(Group group) {
        Enumeration<?> enumeration = rootNode.breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) enumeration.nextElement();
            if (currentNode.getUserObject() instanceof Group && currentNode.getUserObject().equals(group)) {
                return currentNode;
            }
        }
        return null; // Return null if no node is found
    }

    // Singleton pattern to ensure only one instance of this panel
    public static AdminControlPanel getInstance() {
        if (instance == null) {
            instance = new AdminControlPanel();
        }
        return instance;
    }

    private void initializeComponents() {
        // Setup the layout and the basic components for managing users/groups
        setupTree();
        setupManagementComponents();
        setupButtons();  // Ensure this method sets up all your buttons and their listeners
    }

    private void setupTree() {
        Group rootGroup = new Group("root", "Root Group");
        rootNode = new DefaultMutableTreeNode(rootGroup);
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getContentPane().add(treeScrollPane, BorderLayout.CENTER);
        setupTreeSelectionListener();
    }

    public void actionPerformed(ActionEvent e) {
        Object nodeInfo = rootNode.getUserObject();
        if (nodeInfo instanceof Group) {
            Group group = (Group) nodeInfo;
            // proceed with your action
            JOptionPane.showMessageDialog(this, "Group ID: " + group.getId() + "\nGroup Name: " + group.getName(), "Group Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid object type.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupTreeSelectionListener() {
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                Object userObject = selectedNode.getUserObject();
                if (userObject instanceof Group) {
                    currentGroup = (Group) userObject;
                    updateUserList(currentGroup.getUsers());
                } else {
                    userList.setModel(new DefaultListModel<>()); // Clear the list if no group is selected
                }
                currentUser = null; // Reset currentUser if it's not a user selection
            }
        });
    }

    private void setupManagementComponents() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        userIdTextField = new JTextField(10);
        JButton addUserButton = new JButton("Add User");
        groupIdTextField = new JTextField(10);
        JButton addGroupButton = new JButton("Add Group");
        analyticsButton = new JButton("Show Analytics");

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

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3; // Span across all columns
        controlPanel.add(analyticsButton, gbc);

        getContentPane().add(controlPanel, BorderLayout.SOUTH);

        addUserButton.addActionListener(e -> handleAddUser());
        addGroupButton.addActionListener(e -> handleAddGroup());
        analyticsButton.addActionListener(e -> performAnalytics());
    }

    private void setupButtons() {
        JButton removeUserButton = new JButton("Remove User");
        JButton updateGroupButton = new JButton("Update Group");

        removeUserButton.addActionListener(e -> {
            if (currentUser != null && currentGroup != null) {
                removeUser(currentUser, currentGroup);
                updateTree();
                currentUser = null;  // Optionally reset the current user
            } else {
                JOptionPane.showMessageDialog(this, "No user or group selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateGroupButton.addActionListener(e -> {
            if (currentGroup != null) {
                String newName = JOptionPane.showInputDialog(this, "Enter new group name:", currentGroup.getName());
                if (newName != null && !newName.trim().isEmpty()) {
                    currentGroup.setName(newName);
                    updateTree();
                }
            } else {
                JOptionPane.showMessageDialog(this, "No group selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void handleAddUser() {
        String userId = userIdTextField.getText().trim();
        if (!userId.isEmpty() && !groupContainsUser((Group) rootNode.getUserObject(), userId)) {
            addUser(userId, (Group) rootNode.getUserObject());
            userIdTextField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "User ID already exists or is empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddGroup() {
        String groupId = groupIdTextField.getText().trim();
        if (!groupId.isEmpty() && !groupContainsGroup((Group) rootNode.getUserObject(), groupId)) {
            addGroup(groupId, (Group) rootNode.getUserObject());
            groupIdTextField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Group ID already exists or is empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addUser(String userId, Group parentGroup) {
        if (!groupContainsUser(parentGroup, userId)) {
            User newUser = new User(userId);
            parentGroup.add(newUser);
            updateTree();
            DefaultMutableTreeNode groupNode = findNodeForGroup(parentGroup);
            if (groupNode != null) {
                DefaultMutableTreeNode newUserNode = new DefaultMutableTreeNode(newUser);
                groupNode.add(newUserNode);
                treeModel.nodesWereInserted(groupNode, new int[]{groupNode.getIndex(newUserNode)});
                selectAndScrollToNode(newUserNode);
            } else {
                JOptionPane.showMessageDialog(this, "Group node not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "User ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addGroup(String groupId, Group parentGroup) {
        if (!groupContainsGroup(parentGroup, groupId)) {
            Group newGroup = new Group(groupId, groupId);
            parentGroup.add(newGroup);
            updateTree();
            DefaultMutableTreeNode parentNode = findNodeForGroup(parentGroup);
            if (parentNode != null) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newGroup);
                parentNode.add(newNode);
                treeModel.reload(parentNode); // Only reload the part of the tree that has changed
                selectAndScrollToNode(newNode);
            } else {
                JOptionPane.showMessageDialog(this, "Parent group not found in the tree.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Group ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeUser(User user, Group group) {
        if (group != null && user != null) {
            group.remove(user);
            updateTree();
            JOptionPane.showMessageDialog(this, "User removed successfully.");
        }
    }

    private void performAnalytics() {
        // Assuming `Group` and `User` classes have methods to accept and process a visitor
        if (currentGroup != null) {
            AnalyticsVisitor visitor = new AnalyticsVisitor();
            currentGroup.accept(visitor); // Ensure your Group class implements the accept method for visitor

            String message = String.format("Total Users: %d\nTotal Groups: %d\nTotal Tweets: %d\nPositive Tweets: %.2f%%",
                                           visitor.getUserCount(), visitor.getGroupCount(),
                                           visitor.getTweetCount(), visitor.getPositiveTweetPercentage());

            JOptionPane.showMessageDialog(this, message, "Analytics Summary", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error: Root is not a group or no group selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        treeModel.reload(rootNode);
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

    private void selectAndScrollToNode(Object identifier) {
        DefaultMutableTreeNode node = null;

        if (identifier instanceof DefaultMutableTreeNode) {
            // If the identifier is already a TreeNode, use it directly.
            node = (DefaultMutableTreeNode) identifier;
        } else if (identifier instanceof String) {
            // If the identifier is a String, search for the node.
            String id = (String) identifier;
            Enumeration<?> enumeration = rootNode.breadthFirstEnumeration();
            while (enumeration.hasMoreElements()) {
                DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) enumeration.nextElement();
                if (currentNode.getUserObject() instanceof Component && 
                    ((Component) currentNode.getUserObject()).getId().equals(id)) {
                    node = currentNode;
                    break;
                }
            }
        }

        // Scroll to the found node, if any.
        if (node != null) {
            TreePath path = new TreePath(node.getPath());
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
        } else {
            System.err.println("Node not found for identifier: " + identifier);
        }
    }
}