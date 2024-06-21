package main.models;

import java.util.Arrays;

public class AnalyticsVisitor implements Visitor {
   private int userCount = 0;
   private int groupCount = 0;
   private int tweetCount = 0;
   private int positiveTweetCount = 0;

   @Override
   public void visit(User user) {
      userCount++;  // Increment count when visiting a user
      tweetCount += user.getNewsFeed().size();
      positiveTweetCount += (int) user.getNewsFeed().stream().filter(tweet -> isPositiveTweet(tweet)).count();
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

   public int getTweetCount() {
      return tweetCount;
   }

   public double getPositiveTweetPercentage() {
      return tweetCount > 0 ? 100.0 * positiveTweetCount / tweetCount : 0;
   }

   private boolean isPositiveTweet(String tweet) {
      String[] positiveWords = {"good", "great", "excellent", "nice", "awesome", "fantastic"};
      return Arrays.stream(positiveWords).anyMatch(tweet.toLowerCase()::contains);
   }
}