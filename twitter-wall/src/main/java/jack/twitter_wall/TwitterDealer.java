package jack.twitter_wall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterDealer {

	private final String APIKEY = "2pNMVDF6WTXNluSTfOmZrg";
	private final String APISECRETKEY = "6OpNBSKwYA2iumS4AVRb29gVW5QPOPBuJka7cNX0";
	private final String ACCESSTOKEN = "850171040-yymC3qxYnHMZn5aWKb8rvziaDgJO9dCvkgJgkpCD";
	private final String ACCESSTOKENSECRET = "kYiIx9YMmeqqZSLGuzBNO6GUCYPrdgNtU16lOmsYc";

	private Twitter twitter;
	private QueryResult result;
	private Query query;
	private Iterator<Status> iterator;
	private ArrayList<String> tweet = new ArrayList<String>();

	public TwitterDealer() {

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(APIKEY)
				.setOAuthConsumerSecret(APISECRETKEY)
				.setOAuthAccessToken(ACCESSTOKEN)
				.setOAuthAccessTokenSecret(ACCESSTOKENSECRET);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
		query = new Query("management");
		result = null;
		iterator = null;
		try {
			result = twitter.search(query);
			iterator = result.getTweets().iterator();
		}
		catch (TwitterException e) {
			e.printStackTrace();
		}

		// for (int i = 0; i < result.getTweets().size(); i++) {
		// System.out.println(
		// "@" + result.getTweets().get(i).getUser().getScreenName()
		// + " - " + result.getTweets().get(i).getText());
		// }
	}

	public String nextTweetString() throws TwitterException {
		if (!iterator.hasNext()) {
			searchQuery();
		}
		if (tweet.isEmpty()) {
			String tweetInString = iterator.next().getText();
			String[] temp = tweetInString.split(" ");
			System.out.println(tweetInString);
			for (String str : temp) {
				tweet.add(str);
			}
		}
		int randomIndex = (int) Math.random() * (tweet.size() - 1);
		String result = tweet.get(randomIndex);
		tweet.remove(randomIndex);
		return result;

	}


	public void searchQuery() throws TwitterException {
		result = twitter.search(query);
		iterator = result.getTweets().iterator();
	}
}
