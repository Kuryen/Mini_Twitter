package main.models;

public interface Component {
    void add(Component component);
    void remove(Component component);
    Component getChild(int i);
}