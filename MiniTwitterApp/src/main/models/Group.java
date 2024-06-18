package main.models;

import java.util.ArrayList;
import java.util.List;

public class Group implements Component {
   private List<Component> members = new ArrayList<>();

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
}
