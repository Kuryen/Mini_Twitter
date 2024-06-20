package main.views;

import main.models.*;

public class Application {
    public static void main(String[] args) {
        // Initialize the root group and other components
        Group root = new Group("root");
        // Optionally add some users or groups for testing
        root.add(new User("User1"));
        root.add(new Group("Subgroup1"));

        // Use visitor to analyze the structure
        AnalyticsVisitor visitor = new AnalyticsVisitor();
        root.accept(visitor);
        System.out.println("Total Users: " + visitor.getUserCount());
        System.out.println("Total Groups: " + visitor.getGroupCount());

        // Start the admin control panel
        AdminControlPanel adminPanel = AdminControlPanel.getInstance();
        adminPanel.setVisible(true);
    }
}
