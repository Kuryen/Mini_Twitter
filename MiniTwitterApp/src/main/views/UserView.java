package main.views;

import javax.swing.*;
import java.awt.*;
import main.models.*;

public class UserView extends JFrame {
   private User currentUser;
   private DefaultListModel<String> followingListModel = new DefaultListModel<>();
   private DefaultListModel<String> newsFeedListModel = new DefaultListModel<>();
   private JList<String> followingList = new JList<>(followingListModel);
   private JList<String> newsFeedList = new JList<>(newsFeedListModel);
   private JTextArea tweetTextArea = new JTextArea(5, 20);
   private JButton postTweetButton = new JButton("Post Tweet");

   public UserView(User user) {
      this.currentUser = user;
      setTitle("User View - " + user.getId());
      setSize(300, 500);
      setLayout(new BorderLayout());
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      currentUser.addObserver(this); // Ensure this method exists and works as intended in User
      initializeComponents();
   }

   private void initializeComponents() {
      // Panel for following users
      JPanel followingPanel = new JPanel(new BorderLayout());
      followingPanel.setBorder(BorderFactory.createTitledBorder("Following"));
      followingPanel.add(new JScrollPane(followingList), BorderLayout.CENTER);

      // Panel for tweet posting
      JPanel tweetPanel = new JPanel(new BorderLayout());
      tweetPanel.add(new JScrollPane(tweetTextArea), BorderLayout.CENTER);
      postTweetButton.addActionListener(e -> postTweet());
      tweetPanel.add(postTweetButton, BorderLayout.SOUTH);

      // Panel for news feed
      JPanel newsFeedPanel = new JPanel(new BorderLayout());
      newsFeedPanel.setBorder(BorderFactory.createTitledBorder("News Feed"));
      newsFeedPanel.add(new JScrollPane(newsFeedList), BorderLayout.CENTER);

      add(followingPanel, BorderLayout.NORTH);
      add(tweetPanel, BorderLayout.CENTER);
      add(newsFeedPanel, BorderLayout.SOUTH);
   }

   private void postTweet() {
      String tweet = tweetTextArea.getText();
      if (!tweet.isEmpty()) {
         currentUser.postTweet(tweet);
         tweetTextArea.setText(""); // Clear the text area after posting
      }
   }

   public void updateFollowingList() {
      followingListModel.clear();
      for (String userId : currentUser.getFollowings()) {
         followingListModel.addElement(userId);
      }
   }

   // This method should update asynchronously on the UI thread
   public void updateNewsFeed(String tweet) {
      SwingUtilities.invokeLater(() -> {
         newsFeedListModel.addElement(tweet);
      });
   }
}
