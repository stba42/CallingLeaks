package de.majug.callingleaks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

/**
 * Die Klasse muss für das Setup in ein Java Projekt kopiert werden, dann kann
 * man ein AccessToken erzeugen.
 * 
 * @author sba
 * 
 */
public class TwitterConnector {

	/**
	 * Muss auf http://twitter.com/apps/new angefordert werden
	 */
	private static String consumerKey = "XXX";
	private static String consumerSecret = "XXX";

	public void updateStatus(String message, Double lang, Double longitude)
			throws Exception {
		// Unsere App: http://twitter.com/oauth_clients/details/643189
		GeoLocation geo = null;
		if (lang != null && longitude != null) {
			geo = new GeoLocation(lang, longitude);
		}
		AccessToken accessToken = loadAccessToken(1);
		TwitterFactory factory = new TwitterFactory();

		Twitter twitter = factory.getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		//
		twitter.setOAuthAccessToken(accessToken);
		Status status;
		if (geo != null) {
			Log.d("TwitterConnector", "Tweeting with GeoLocation");
			status = twitter.updateStatus(message, geo);
		} else {
			Log.d("TwitterConnector", "Tweeting without GeoLocation");
			status = twitter.updateStatus(message);
		}
		System.out.println("Successfully updated the status to ["
				+ status.getText() + "].");

	}

	/**
	 * Methode zum Request einer Berechtigung bei Twitter. Muss einmal
	 * ausgeführt werden
	 * 
	 * @throws TwitterException
	 * @throws IOException
	 */
	private static void setup() throws TwitterException, IOException {
		// TODO Implementieren, sodass das in den Settings erledigt werden kann.
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (null == accessToken) {
			System.out
					.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out
					.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			String pin = br.readLine();
			try {
				if (pin.length() > 0) {
					accessToken = twitter
							.getOAuthAccessToken(requestToken, pin);
				} else {
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					te.printStackTrace();
				}
			}
		}
		// Diese Beiden Werte müssen in der loadAccessToken eingepflegt werden
		System.out.println(accessToken.getToken());
		System.out.println(accessToken.getTokenSecret());
		// persist to the accessToken for future reference.
	}

	/**
	 * Gibt momentan immer den gleichen Token zurück Der Token sollte später in
	 * setup() ordentlich gespeichert werden und hier ausgelesen werden
	 * 
	 * @param useId
	 * @return
	 */
	private AccessToken loadAccessToken(int useId) {
		String token = "XXX";
		String tokenSecret = "XXX";
		return new AccessToken(token, tokenSecret);
	}

	public static void main(String[] args) throws TwitterException, IOException {
		TwitterConnector.setup();
	}

}
