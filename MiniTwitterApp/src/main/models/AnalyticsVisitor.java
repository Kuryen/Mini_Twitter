package main.models;

public class AnalyticsVisitor implements Visitor {
   private int userCount = 0;
   private int groupCount = 0;

   @Override
   public void visit(User user) {
      userCount++;  // Increment count when visiting a user
   }

   @Override
   public void visit(Group group) {
      groupCount++;  // Increment count when visiting a group
      for (Component component : group.getChildren()) {
         component.accept(this);  // Recursively visit all children
      }
   }

   public int getUserCount() {
      return userCount;
   }

   public int getGroupCount() {
      return groupCount;
   }
}