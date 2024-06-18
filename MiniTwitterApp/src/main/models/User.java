package main.models;

import java.util.ArrayList;
import java.util.List;
import main.views.UserView;

public class User implements Component {
   private String id; // Unique identifier for the user
   private List<String> tweets = new ArrayList<>();
   private List<User> followers = new ArrayList<>();
   private List<String> followings = new ArrayList<>(); // List to store the IDs of users this user is following
   private List<UserView> observers = new ArrayList<>();

   public User(String id) {
      this.id = id;
   }

   public String getId() {
      return this.id;
   }

   public List<String> getFollowings() {
      List<String> followingIds = new ArrayList<>();
      for (String following : this.followings) {
         followingIds.add(following);
      }
      return followingIds;
   }

   public void postTweet(String tweet) {
      tweets.add(tweet);
      notifyFollowers(tweet);
      notifyObservers(tweet);
   }

   private void notifyFollowers(String tweet) {
      for (User follower : followers) {
         follower.receiveTweet(this.id, tweet);
      }
   }

   public void addObserver(UserView observer) {
      if (!observers.contains(observer)) {
         observers.add(observer);
      }
   }

   private void notifyObservers(String tweet) {
      for (UserView observer : observers) {
         observer.updateNewsFeed("Tweet from " + id + ": " + tweet);
      }
   }

   public void receiveTweet(String userId, String tweet) {
      // This method can be used to handle received tweets, if needed
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
