package main.models;

import java.util.ArrayList;
import java.util.List;

public class Group implements Component {
   private String id;
   private List<Component> members = new ArrayList<>();

   public Group(String id) {
      this.id = id;
   }

   @Override
   public void add(Component component) {
   members.add(component);
   }

   @Override
   public void remove(Component component) {
   members.remove(component);
   }

   @Override
   public Component getChild(int i) {
   return members.get(i);
   }

   // Method to retrieve all user instances from this group
    public List<User> getUsers() {
      List<User> users = new ArrayList<>();
      for (Component member : members) {
         if (member instanceof User) {
               users.add((User) member);
         } else if (member instanceof Group) {
               users.addAll(((Group) member).getUsers()); // Recursively get users from subgroups
         }
      }
      return users;
   }

   @Override
   public void accept(Visitor visitor) {
      visitor.visit(this);  // Allow the visitor to perform operations specific to Group
      for (Component component : getChildren()) {
         component.accept(visitor);  // Recursively accept visitors for all children
      }
   }

   @Override
   public String getId() {
      return id;
   }

   // Method to return all children, useful for UI operations
   public List<Component> getChildren() {
      return new ArrayList<>(members);
   }
}
