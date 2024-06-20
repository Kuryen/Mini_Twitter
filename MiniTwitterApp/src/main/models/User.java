package main.models;

import java.util.ArrayList;
import java.util.List;

public class User implements Component, Observer, Subject {
   private String id; // Unique identifier for the user
   private List<String> tweets = new ArrayList<>();
   private List<User> followers = new ArrayList<>();
   private List<String> followings = new ArrayList<>(); // List to store the IDs of users this user is following
   private List<Observer> observers = new ArrayList<>();
   private List<String> newsFeed = new ArrayList<>();

   public User(String id) {
      this.id = id;
   }

   public String getId() {
      return this.id;
   }

   public List<String> getFollowings() {
      return new ArrayList<>(followings); // Directly return followings as they are strings
   }

   public void postTweet(String tweet) {
      tweets.add(tweet);
      notifyFollowers(tweet); // Notify followers about the tweet
      notifyObservers(tweet); // Notify views or other observers
   }

   private void notifyFollowers(String tweet) {
      for (User follower : followers) {
         follower.update("Tweet from " + this.id + ": " + tweet); // Call update directly if followers are also observers
      }
   }

   public void addObserver(Observer  observer) {
      if (!observers.contains(observer)) {
         observers.add(observer);
      }
   }

   // Subject interface methods
   @Override
   public void attach(Observer observer) {
      if (!observers.contains(observer)) {
         observers.add(observer);
      }
   }

   @Override
   public void detach(Observer observer) {
      observers.remove(observer);
   }

   public void notifyObservers(String message) {
      for (Observer observer : observers) {
         observer.update(message);
      }
   }

   // Observer interface method
   @Override
   public void update(String message) {
      newsFeed.add(message);
   }

   public void accept(Visitor visitor) {
      visitor.visit(this);  // Calls visit method for a User object
   }

   // Component interface methods
   @Override
   public void add(Component component) {
      throw new UnsupportedOperationException("Not supported operation");
   }

   @Override
   public void remove(Component component) {
      throw new UnsupportedOperationException("Not supported operation");
   }

   @Override
   public Component getChild(int i) {
      throw new UnsupportedOperationException("Not supported operation");
   }
}
