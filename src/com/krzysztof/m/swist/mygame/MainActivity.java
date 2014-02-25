package com.krzysztof.m.swist.mygame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.krzysztof.m.swist.basegameutils.BaseGameActivity;

public class MainActivity extends BaseGameActivity implements
		EndGameFragment.Listener, GameplayFragment.Listener,
		MainMenuFragment.Listener {

	private GameLogic gameLogic;
	private GoogleAPILogic googleAPILogic;

	/*
	 * API INTEGRATION SECTION. This section contains the code that integrates
	 * the game with the Google Play game services API.
	 */
	// Debug tag
	final static boolean ENABLE_DEBUG = true;
	final static String TAG = "MyGame";

	// Request codes for the UIs that we show with startActivityForResult:
	private final static int RC_SELECT_PLAYERS = 10000;
	private final static int RC_INVITATION_INBOX = 10001;
	private final static int RC_WAITING_ROOM = 10002;

	// request codes we use when invoking an external activity
	final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

	Intent intent;

	EndGameFragment endGameFragment;
	GameplayFragment gameplayFragment;
	MainMenuFragment mainMenuFragment;
	Outbox outbox;

	/*
	 * COMMUNICATIONS SECTION. Methods that implement the game's network
	 * protocol.
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		enableDebugLog(ENABLE_DEBUG);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// create fragments
		endGameFragment = new EndGameFragment();
		gameplayFragment = new GameplayFragment();
		mainMenuFragment = new MainMenuFragment();

		gameLogic = new GameLogic();
		googleAPILogic = new GoogleAPILogic();

		gameLogic.setGoogleApiClient(getApiClient());
		gameLogic.setGoogleAPILogic(googleAPILogic);
		gameLogic.setMainActivity(this);

		googleAPILogic.setGameLogic(gameLogic);
		googleAPILogic.setMainActivity(this);
		googleAPILogic.setMainMenuFragment(mainMenuFragment);
		googleAPILogic.setGoogleApiClient(getApiClient());
		
		gameplayFragment.setGameLogic(gameLogic);
		// set listeners
		endGameFragment.setListener(this);
		gameplayFragment.setListener(this);
		mainMenuFragment.setListener(this);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, mainMenuFragment).commit();
		/*
		 * / set up a click listener for everything we care about for (int id :
		 * CLICKABLES) { findViewById(id).setOnClickListener(this); }
		 */

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    //No call for super(). Bug on API Level > 11.
	}
	
	/**
	 * Called by the base class (BaseGameActivity) when sign-in has failed. For
	 * example, because the user hasn't authenticated yet. We react to this by
	 * showing the sign-in button.
	 */


	/**
	 * Called by the base class (BaseGameActivity) when sign-in succeeded. We
	 * react by going to our main screen.
	 */

	@Override
	public void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		super.onActivityResult(requestCode, responseCode, intent);

		switch (requestCode) {
		case RC_SELECT_PLAYERS:
			// we got the result from the "select players" UI -- ready to create
			// the room
			googleAPILogic.handleSelectPlayersResult(responseCode, intent);
			break;
		case RC_INVITATION_INBOX:
			// we got the result from the "select invitation" UI (invitation
			// inbox). We're
			// ready to accept the selected invitation:
			googleAPILogic.handleInvitationInboxResult(responseCode, intent);
			break;
		case RC_WAITING_ROOM:
			// ignore result if we dismissed the waiting room from code:
			if (googleAPILogic.waitRoomDismissedFromCode)
				break;

			// we got the result from the "waiting room" UI.
			if (responseCode == Activity.RESULT_OK) {
				// player wants to start playing
				Log.d(TAG,
						"Starting game because user requested via waiting room UI.");

				// let other players know we're starting.
				// broadcastStart();

				// start the game!
				// gameplayFragment.startGame(true);
				switchToFragment(gameplayFragment);
			} else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
				// player actively indicated that they want to leave the room
				googleAPILogic.leaveRoom();
			} else if (responseCode == Activity.RESULT_CANCELED) {
				/*
				 * Dialog was cancelled (user pressed back key, for instance).
				 * In our game, this means leaving the room too. In more
				 * elaborate games,this could mean something else (like
				 * minimizing the waiting room UI but continue in the handshake
				 * process).
				 */
				googleAPILogic.leaveRoom();
			}

			break;
		}
	}

	// Activity is going to the background. We have to leave the current room.
	@Override
	public void onStop() {
		Log.d(TAG, "**** got onStop");

		// if we're in a room, leave it.
		googleAPILogic.leaveRoom();

		// stop trying to keep the screen on
		stopKeepingScreenOn();

		switchToFragment(mainMenuFragment);
		super.onStop();
	}

	// Activity just got to the foreground. We switch to the wait screen because
	// we will now
	// go through the sign-in flow (remember that, yes, every time the Activity
	// comes back to the
	// foreground we go through the sign-in flow -- but if the user is already
	// authenticated,
	// this flow simply succeeds and is imperceptible).
	@Override
	public void onStart() {
		// switchToScreen(R.id.screen_wait);
		super.onStart();
		switchToFragment(mainMenuFragment);
	}

	// Handle back key to make sure we cleanly leave a game if we are in the
	// middle of one
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent e) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && mCurScreen == R.id.screen_game) {
	// leaveRoom();
	// return true;
	// }
	// return super.onKeyDown(keyCode, e);
	// }

	/*
	 * CALLBACKS SECTION. This section shows how we implement the several games
	 * API callbacks.
	 */

	// Broadcast a message indicating that we're starting to play. Everyone else
	// will react
	// by dismissing their waiting room UIs and starting to play too.
	// private void broadcastStart() {
	// if (gameplayFragment.getGameType() != GameType.MULTIPLAYER)
	// return; // playing single-player mode
	//
	// msgBuf[0] = 'S';
	// msgBuf[1] = (byte) 0;
	// for (Participant p : participants) {
	// if (p.getParticipantId().equals(myId))
	// continue;
	// if (p.getStatus() != Participant.STATUS_JOINED)
	// continue;
	// getGamesClient().sendReliableRealTimeMessage(null, msgBuf, roomId,
	// p.getParticipantId());
	// }
	// }

	/*
	 * MISC SECTION. Miscellaneous methods.
	 */

	// Sets the flag to keep this screen on. It's recommended to do that during
	// the
	// handshake when setting up a game, because if the screen turns off, the
	// game will be
	// cancelled.
	void keepScreenOn() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	// Clears the flag that keeps the screen on.
	void stopKeepingScreenOn() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onEndGameScreenDismissed() {
		switchToFragment(mainMenuFragment);
	}

	void switchToFragment(Fragment newFragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, newFragment).commit();
	}

	@Override
	public void onGameEnd() {
		int score = gameLogic.getScore();

		outbox = new Outbox();
		outbox.setScore(score);

		// check for achievements
		checkForAchievements();

		// update leaderboards
		updateLeaderboards();

		// push those accomplishments to the cloud, if signed in
		if (isSignedIn()) {
			pushAccomplishments();
		}
		endGameFragment.setScore(score);
		switchToFragment(endGameFragment);
	}

	@Override
	public void onStartGameRequested(GameType gameType) {
		if (gameType == GameType.SINGLEPLAYER) {
			gameLogic.setGameType(gameType);
			switchToFragment(gameplayFragment);
		} else {
			gameLogic.startQuickGame();
		}

	}

	@Override
	public void onShowAchievementsRequested() {
		if (isSignedIn()) {
			startActivityForResult(
					Games.Achievements.getAchievementsIntent(getApiClient()),
					RC_UNUSED);
		} else {
			showAlert(getString(R.string.achievements_not_available));
		}
	}

	@Override
	public void onShowLeaderboardsRequested() {
		if (isSignedIn()) {
			startActivityForResult(
					Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()),
					RC_UNUSED);
		} else {
			showAlert(getString(R.string.leaderboards_not_available));
		}

	}

	@Override
	public void onSignInButtonClicked() {
		beginUserInitiatedSignIn();
	}

	@Override
	public void onSignOutButtonClicked() {
		signOut();
		mainMenuFragment.setShowSignIn(true);
	}

	@Override
	public void onInvatePlayersButtonClicked() {
		intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(
				getApiClient(), 1, 3);
		// switchToScreen(R.id.screen_wait);
		startActivityForResult(intent, RC_SELECT_PLAYERS);

	}

	@Override
	public void onSeeInvitationsButtonClicked() {
		intent = Games.Invitations.getInvitationInboxIntent(getApiClient());
		// switchToScreen(R.id.screen_wait);
		startActivityForResult(intent, RC_INVITATION_INBOX);
	}

	// private void unlockAchievement(int achievementId, String fallbackString)
	// {
	// if (isSignedIn()) {
	// Games.Achievements.unlock(getApiClient(),getString(achievementId));
	// } else {
	// Toast.makeText(this,
	// getString(R.string.achievement) + ": " + fallbackString,
	// Toast.LENGTH_LONG).show();
	// }
	// }

	private void achievementToast(String achievement) {
		// Only show toast if not signed in. If signed in, the standard Google
		// Play
		// toasts will appear, so we don't need to show our own.
		if (!isSignedIn()) {
			Toast.makeText(this,
					getString(R.string.achievement) + ": " + achievement,
					Toast.LENGTH_LONG).show();
		}
	}

	private void pushAccomplishments() {
		if (outbox.getOver10points()) {
			Games.Achievements.unlock(getApiClient(),
					getString(R.string.achievement_over_10_points));
			outbox.setOver10points(false);
		}
		if (outbox.getScore() >= 0) {
			Games.Leaderboards.submitScore(getApiClient(),
					getString(R.string.leaderboard), outbox.getScore());
			outbox.setScore(-1);
		}

		outbox.saveLocal(this);
	}

	private void checkForAchievements() {
		// Check if each condition is met; if so, unlock the corresponding
		// achievement.
		if (gameLogic.getScore() > 10) {
			outbox.setOver10points(true);
			achievementToast(getString(R.string.over_10_points));
		}

	}

	private void updateLeaderboards() {
		if (outbox.getScore() < gameLogic.getScore()) {
			outbox.setScore(gameLogic.getScore());

		}
	}

	@Override
	public void onSignInSucceeded() {

		Log.d(TAG, "Sign-in succeeded.");
		mainMenuFragment.setShowSignIn(false);

		// install invitation listener so we get notified if we receive an
		// invitation to play
		// a game.
		Games.Invitations.registerInvitationListener(getApiClient(), googleAPILogic);
		//
		// // if we received an invite via notification, accept it; otherwise,
		// go
		// // to main screen
		if (getInvitationId() != null) {
			googleAPILogic.acceptInviteToRoom(getInvitationId());
			return;
		}
		// switchToMainScreen();

	}
	
	@Override
	public void onSignInFailed() {
		Log.d(TAG, "Sign-in failed.");
		mainMenuFragment.setShowSignIn(true);
		// switchToScreen(R.id.screen_sign_in);
	}
	
	// Show error message about game being cancelled and return to main screen.
		void showGameError() {
			showAlert(getString(R.string.error), getString(R.string.game_problem));
			switchToFragment(mainMenuFragment);
		}

}
